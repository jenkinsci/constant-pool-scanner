package org.jenkinsci.constant_pool_scanner;

/**
 * Name of a field/method plus its descriptor.
 *
 * @author Kohsuke Kawaguchi
 */
public final class NameAndTypeConstant {
    private Utf8Constant name;
    private Utf8Constant descriptor;

    /**
     * Name of the field/method.
     */
    public String getName() {
        return name.get();
    }

    public Utf8Constant getNameUTF8() {
        return name;
    }

    /**
     * Its type descriptor, a combination of field/method return type and parameter types.
     */
    public String getDescriptor() {
        return descriptor.get();
    }

    /**
     * Its type descriptor, a combination of field/method return type and parameter types.
     */
    public Utf8Constant getDescriptorUTF8() {
        return descriptor;
    }

    NameAndTypeConstant set(Utf8Constant name, Utf8Constant descriptor) {
        this.name = name;
        this.descriptor = descriptor;
        return this;
    }
}
