package org.jenkinsci.constant_pool_scanner;

/**
 * UTF-8 Constant (which is separate from string constant.)
 *
 * @author Kohsuke Kawaguchi
 */
public final class Utf8Constant implements CharSequence {
    String actual;

    public String get() {
        return actual;
    }

    public int length() {
        return get().length();
    }

    public char charAt(int index) {
        return get().charAt(index);
    }

    public String subSequence(int start, int end) {
        return get().substring(start,end);
    }

    @Override
    public String toString() {
        return actual;
    }
}
