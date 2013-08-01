package org.jenkinsci.constant_pool_scanner;

/**
 * String constant (which is separate from UTF-8 constant.)
 *
 * @author Kohsuke Kawaguchi
 */
public final class StringConstant {
    private final Utf8Constant actual;

    StringConstant(Utf8Constant actual) {
        this.actual = actual;
    }

    public String get() {
        return actual.get();
    }
}
