package org.jenkinsci.constant_pool_scanner;

/**
 * Constant that refers to a class name.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ClassConstant {
    private Utf8Constant value;

    /**
     * UTF-8 constant that holds the class' internal name.
     */
    public Utf8Constant getUTF8() {
        return value;
    }

    /**
     * Gets the actual class name.
     */
    public String get() {
        return getUTF8().get();
    }

    ClassConstant set(Utf8Constant v) {
        this.value = v;
        return this;
    }
}
