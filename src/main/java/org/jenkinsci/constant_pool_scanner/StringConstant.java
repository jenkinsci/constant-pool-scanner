package org.jenkinsci.constant_pool_scanner;

/**
 * String constant (which is separate from UTF-8 constant.)
 *
 * @author Kohsuke Kawaguchi
 */
public final class StringConstant implements Constant {
    private Utf8Constant actual;

    public String get() {
        if (actual==null)
            throw new IllegalStateException();
        return actual.get();
    }
}
