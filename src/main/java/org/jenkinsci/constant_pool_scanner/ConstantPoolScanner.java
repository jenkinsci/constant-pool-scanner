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
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Streaming parser of the constant pool in a Java class file.
 *
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
        final Set<Utf8Constant> classNames = new HashSet<Utf8Constant>();
        final Set<Utf8Constant> descriptors = new HashSet<Utf8Constant>();

        new ConstantPoolScanner().parse(in, new ConstantPoolVisitor() {
            @Override
            public void onClass(ClassConstant value) {
                classNames.add(value.get());
            }

            @Override
            public void onNameAndType(NameAndTypeConstant value) {
                descriptors.add(value.getDescriptor());
            }
        });

        Set<String> result = new TreeSet<String>();
        for (Utf8Constant utf : descriptors) {
            String s = utf.get();
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

        for (Utf8Constant utf : classNames) {
            String s = utf.get();
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
        }

        return result;
    }

    /**
     * Class file being parsed.
     */
    private DataInput source;
    /**
     * Constants that are discovered and pre-created at the point of reference.
     */
    private Constant[] constants;
    /**
     * Index of the current constant getting passed to the visitor.
     */
    private int index;
    /**
     * Constant type of the current constant getting passed to the visitor.
     */
    private byte tag;
    /**
     * Whether the current constant is read.
     */
    private boolean readTag;

    public ConstantPoolScanner() {
    }

    /**
     * Parses a class file and invokes the visitor with constants.
     */
    public void parse(byte[] source, ConstantPoolVisitor visitor) throws IOException {
        parse(new ByteArrayInputStream(source),visitor);
    }

    /**
     * Parses a class file and invokes the visitor with constants.
     */
    public void parse(InputStream source, ConstantPoolVisitor visitor) throws IOException {
        parse((DataInput)new DataInputStream(source),visitor);
    }

    /**
     * Parses a class file and invokes the visitor with constants.
     */
    public void parse(final DataInput source, ConstantPoolVisitor visitor) throws IOException {
        try {
            this.source = source;

            skip(8); // magic, minor_version, major_version
            int size = source.readUnsignedShort() - 1; // constantPoolCount
            constants = new Constant[size];

            /**
             * This object parses bytes into primitive values lazily.
             */
            final Reader r = new Reader() {
                public Object read() throws IOException {
                    if (readTag)
                        throw new IllegalStateException("Constant was already read");
                    readTag = true;

                    switch (tag) {
                    case 1: // CONSTANT_Utf8
                        return utf8At(index);
                    case 7: // CONSTANT_Class
                        return classAt(index);
                    case 3: // CONSTANT_Integer
                        return source.readInt();
                    case 4: // CONSTANT_Float
                        return source.readFloat();
                    case 9: // CONSTANT_Fieldref
                    case 10: // CONSTANT_Methodref
                    case 11: // CONSTANT_InterfaceMethodref
                        return memberRefAt(index);
                    case 12: // CONSTANT_NameAndType
                        return nameAndTypeAt(index);
                    case 8: // CONSTANT_String
                        return utf8At(readIndex());
                    case 5: // CONSTANT_Long
                        return source.readLong();
                    case 6: // CONSTANT_Double
                        return source.readDouble();
                    default:
                        throw new IllegalStateException();
                    }
                }
            };

            for (int i = 0; i < size; i++) {
                index = i;
                tag = source.readByte();
                readTag = false;

                switch (tag) {
                    case 1: // CONSTANT_Utf8
                        visitor.onUTF8(utf8At(i).actual = source.readUTF());
                        break;
                    case 7: // CONSTANT_Class
                        visitor.onClass(classAt(i).set(utf8At(readIndex())));
                        break;
                    case 3: // CONSTANT_Integer
                        visitor.onInteger(r);
                        consumeTag(4);
                        break;
                    case 4: // CONSTANT_Float
                        visitor.onFloat(r);
                        consumeTag(4);
                        break;
                    case 9: // CONSTANT_Fieldref
                        visitor.onFieldRef(
                               memberRefAt(i).set(classAt(readIndex()),nameAndTypeAt(readIndex())));
                        break;
                    case 10: // CONSTANT_Methodref
                        visitor.onMethodRef(
                                memberRefAt(i).set(classAt(readIndex()), nameAndTypeAt(readIndex())));
                        break;
                    case 11: // CONSTANT_InterfaceMethodref
                        visitor.onInterfaceMethodRef(
                                memberRefAt(i).set(classAt(readIndex()), nameAndTypeAt(readIndex())));
                        break;
                    case 12: // CONSTANT_NameAndType
                        visitor.onNameAndType(
                                nameAndTypeAt(i).set(utf8At(readIndex()),utf8At(readIndex())));
                        break;
                    case 8: // CONSTANT_String
                        visitor.onString(utf8At(readIndex()));
                        break;
                    case 5: // CONSTANT_Long
                        visitor.onLong(r);
                        consumeTag(8);
                        i++; // weirdness in spec
                        break;
                    case 6: // CONSTANT_Double
                        visitor.onDouble(r);
                        consumeTag(8);
                        i++; // weirdness in spec
                        break;
                    default:
                        throw new IOException("Unrecognized constant pool tag " + tag + " at index " + i +
                                "; running constants: " + Arrays.asList(constants));
                }
            }
        } finally {
            this.source = null;
            this.constants = null;
            this.tag = -1;
        }
    }

    private void consumeTag(int size) throws IOException {
        if (!readTag)
            skip(size);
    }

    private NameAndTypeConstant nameAndTypeAt(int i) {
        if (constants[i]==null)
            constants[i] = new NameAndTypeConstant();
        return (NameAndTypeConstant)constants[i];
    }

    private MemberRefConstant memberRefAt(int i) {
        if (constants[i]==null)
            constants[i] = new MemberRefConstant();
        return (MemberRefConstant)constants[i];
    }

    private ClassConstant classAt(int i) {
        if (constants[i]==null)
            constants[i] = new ClassConstant();
        return (ClassConstant)constants[i];
    }

    private Utf8Constant utf8At(int i) {
        if (constants[i]==null)
            constants[i] = new Utf8Constant();
        return (Utf8Constant)constants[i];
    }

    private void skip(int bytes) throws IOException {
        // skipBytes cannot be used reliably because 0 is a valid return value
        // and we can end up looping forever
        source.readFully(new byte[bytes]);
    }

    private int readIndex() throws IOException {
        return source.readUnsignedShort() - 1;
    }

}
