/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Jesse Glick. All rights reserved.
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
 *
 */

package org.jenkinsci.constant_pool_scanner;

import org.jenkinsci.constant_pool_scanner.samples.D;
import org.jenkinsci.constant_pool_scanner.samples.C;
import org.jenkinsci.constant_pool_scanner.samples.A;
import org.jenkinsci.constant_pool_scanner.samples.B;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.IOUtils;
import static org.junit.Assert.*;
import org.junit.Test;

public class ConstantPoolScannerTest {

    @Test public void basics() throws Exception {
        assertDependencies(A.class, Object.class);
        assertDependencies(B.class, A.class);
        assertDependencies(C.class, Object.class);
        assertDependencies(D.class, Object.class, A.class, B.class, C.class, String.class);
    }

    private static void assertDependencies(Class<?> from, Class<?>... to) throws IOException {
        Set<String> expected = new TreeSet<String>();
        for (Class<?> c : to) {
            expected.add(c.getName());
        }
        expected.add(from.getName());
        byte[] bytecode = IOUtils.toByteArray(from.getClassLoader().getResourceAsStream(from.getName().replace('.', '/') + ".class"));
        Set<String> actual = ConstantPoolScanner.dependencies(bytecode);
        assertEquals(expected.toString(), actual.toString());
    }

}
