/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.jenkinsci.constant_pool_scanner;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Scans Java bytecode for class references.
 * This might be used for dependency analysis, class loader optimizations, etc.
 * @see <a href="http://hg.netbeans.org/main-silver/raw-file/4a24ea1d4a94/nbbuild/antsrc/org/netbeans/nbbuild/VerifyClassLinkage.java">original sources</a>
 */
public class ConstantPoolScanner {

    /**
     * Examines the constant pool of a class file and looks for references to other classes.
     * @param data a Java class file
     * @return a (sorted) set of binary class names (e.g. {@code some.pkg.Outer$Inner})
     * @throws IOException in case of malformed bytecode
     */
    public static Set<String> dependencies(byte[] data) throws IOException {
        return dependencies(new ByteArrayInputStream(data));
    }

    /**
     * Examines the constant pool of a class file and looks for references to other classes.
     * @param in Stream that reads a Java class file
     * @return a (sorted) set of binary class names (e.g. {@code some.pkg.Outer$Inner})
     * @throws IOException in case of malformed bytecode
     */
    public static Set<String> dependencies(InputStream in) throws IOException {
        Set<String> result = new TreeSet<String>();
        DataInput input = new DataInputStream(in);
        skip(input, 8); // magic, minor_version, major_version
        int size = input.readUnsignedShort() - 1; // constantPoolCount
        String[] utf8Strings = new String[size];
        boolean[] isClassName = new boolean[size];
        boolean[] isDescriptor = new boolean[size];
        for (int i = 0; i < size; i++) {
            byte tag = input.readByte();
            switch (tag) {
                case 1: // CONSTANT_Utf8
                    utf8Strings[i] = input.readUTF();
                    break;
                case 7: // CONSTANT_Class
                    int index = input.readUnsignedShort() - 1;
                    if (index >= size) {
                        throw new IOException("@" + i + ": CONSTANT_Class_info.name_index " + index + " too big for size of pool " + size);
                    }
                    //log("Class reference at " + index, Project.MSG_DEBUG);
                    isClassName[index] = true;
                    break;
                case 3: // CONSTANT_Integer
                case 4: // CONSTANT_Float
                case 9: // CONSTANT_Fieldref
                case 10: // CONSTANT_Methodref
                case 11: // CONSTANT_InterfaceMethodref
                    skip(input, 4);
                    break;
                case 12: // CONSTANT_NameAndType
                    skip(input, 2);
                    index = input.readUnsignedShort() - 1;
                    if (index >= size || index < 0) {
                        throw new IOException("@" + i + ": CONSTANT_NameAndType_info.descriptor_index " + index + " too big for size of pool " + size);
                    }
                    isDescriptor[index] = true;
                    break;
                case 8: // CONSTANT_String
                    skip(input, 2);
                    break;
                case 5: // CONSTANT_Long
                case 6: // CONSTANT_Double
                    skip(input, 8);
                    i++; // weirdness in spec
                    break;
                default:
                    throw new IOException("Unrecognized constant pool tag " + tag + " at index " + i +
                            "; running UTF-8 strings: " + Arrays.asList(utf8Strings));
            }
        }
        //task.log("UTF-8 strings: " + Arrays.asList(utf8Strings), Project.MSG_DEBUG);
        for (int i = 0; i < size; i++) {
            String s = utf8Strings[i];
            if (isClassName[i]) {
                while (s.charAt(0) == '[') {
                    // array type
                    s = s.substring(1);
                }
                if (s.length() == 1) {
                    // primitive
                    continue;
                }
                String c;
                if (s.charAt(s.length() - 1) == ';' && s.charAt(0) == 'L') {
                    // Uncommon but seems sometimes this happens.
                    c = s.substring(1, s.length() - 1);
                } else {
                    c = s;
                }
                result.add(c.replace('/', '.'));
            } else if (isDescriptor[i]) {
                int idx = 0;
                while ((idx = s.indexOf('L', idx)) != -1) {
                    int semi = s.indexOf(';', idx);
                    if (semi == -1) {
                        throw new IOException("Invalid type or descriptor: " + s);
                    }
                    result.add(s.substring(idx + 1, semi).replace('/', '.'));
                    idx = semi;
                }
            }
        }
        return result;
    }

    private static void skip(DataInput input, int bytes) throws IOException {
        int skipped = input.skipBytes(bytes);
        if (skipped != bytes) {
            throw new IOException("Truncated class file");
        }
    }

    private ConstantPoolScanner() {}

}
