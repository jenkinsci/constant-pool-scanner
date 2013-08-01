package org.jenkinsci.constant_pool_scanner;

/**
 * Reference to a field/method of another class.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class MemberRefConstant {
    ClassConstant clazz;
    NameAndTypeConstant nameAndType;

    /**
     * Gets the internal name of the class that contains method/field in question.
     */
    public String getClazz() {
        return clazz.get();
    }

    /**
     * Class that contains the method/field in question.
     */
    public ClassConstant getClassConstant() {
        return clazz;
    }

    /**
     * Name of the field/method.
     */
    public String getName() {
        return nameAndType.getName();
    }

    /**
     * Its type descriptor, a combination of field/method return type and parameter types.
     */
    public String getDescriptor() {
        return nameAndType.getDescriptor();
    }

    /**
     * Signature of the method/field.
     */
    public NameAndTypeConstant getNameAndTypeConstant() {
        return nameAndType;
    }

    MemberRefConstant set(ClassConstant clazz, NameAndTypeConstant nameAndType) {
        this.clazz = clazz;
        this.nameAndType = nameAndType;
        return this;
    }
}
