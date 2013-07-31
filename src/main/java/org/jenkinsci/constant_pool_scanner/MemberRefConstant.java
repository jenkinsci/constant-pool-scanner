package org.jenkinsci.constant_pool_scanner;

/**
 * Reference to a field/method of another class.
 *
 * @author Kohsuke Kawaguchi
 */
public final class MemberRefConstant implements Constant {
    ClassConstant clazz;
    NameAndTypeConstant nameAndType;

    /**
     * Class that contains the method/field in question.
     */
    public ClassConstant getClazz() {
        return clazz;
    }

    /**
     * Signature of the method/field.
     */
    public NameAndTypeConstant getNameAndType() {
        return nameAndType;
    }

    MemberRefConstant set(ClassConstant clazz, NameAndTypeConstant nameAndType) {
        this.clazz = clazz;
        this.nameAndType = nameAndType;
        return this;
    }
}
