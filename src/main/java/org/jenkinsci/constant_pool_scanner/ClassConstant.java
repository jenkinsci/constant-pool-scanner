package org.jenkinsci.constant_pool_scanner;

/**
 * Constant that refers to a class name.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ClassConstant implements Constant {
    private Utf8Constant value;

    /**
     * UTF-8 constant that holds the class' internal name.
     */
    public Utf8Constant get() {
        return value;
    }

    /**
     * Gets the actual class name.
     */
    public String getInternalName() {
        return get().get();
    }

    ClassConstant set(Utf8Constant v) {
        this.value = v;
        return this;
    }
}
