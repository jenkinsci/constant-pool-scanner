package org.jenkinsci.constant_pool_scanner.samples;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@SuppressWarnings({"NP_LOAD_OF_KNOWN_NULL_VALUE", "NP_ALWAYS_NULL"})
public class D {
    {
        A a = new B();
        C c = null;
        c.m(a);
    }
}
