package org.jenkinsci.constant_pool_scanner;

/**
 * Name of a field/method plus its descriptor.
 *
 * @author Kohsuke Kawaguchi
 */
public final class NameAndTypeConstant implements Constant {
    private Utf8Constant name;
    private Utf8Constant descriptor;

    /**
     * Name of the field/method.
     */
    public Utf8Constant getName() {
        if (name==null)
            throw new IllegalStateException();
        return name;
    }

    /**
     * Its type descriptor, a combination of field/method return type and parameter types.
     */
    public Utf8Constant getDescriptor() {
        if (descriptor==null)
            throw new IllegalStateException();
        return descriptor;
    }

    NameAndTypeConstant set(Utf8Constant name, Utf8Constant descriptor) {
        this.name = name;
        this.descriptor = descriptor;
        return this;
    }
}
