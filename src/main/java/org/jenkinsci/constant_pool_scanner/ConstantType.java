package org.jenkinsci.constant_pool_scanner;

import java.util.Arrays;
import java.util.List;

/**
 * Types of constants.
 *
 * @author Kohsuke Kawaguchi
 */
public enum ConstantType {
    UTF8(1,Utf8Constant.class),
    CLASS(7,ClassConstant.class,UTF8),
    NAME_AND_TYPE(12,NameAndTypeConstant.class,UTF8),
    FIELD_REF(9,FieldRefConstant.class,CLASS,NAME_AND_TYPE,UTF8),
    METHOD_REF(10,MethodRefConstant.class,CLASS,NAME_AND_TYPE,UTF8),
    INTERFACE_METHOD_REF(11,InterfaceMethodRefConstant.class,CLASS,NAME_AND_TYPE,UTF8),
    STRING(8,String.class,UTF8),
    INTEGER(3,Integer.class),
    FLOAT(4,Float.class),
    LONG(5,Long.class),
    DOUBLE(6,Double.class),
    // METHOD_HANDLE(15,
    // METHOD_TYPE(16,
    // INVOKE_DYNAMIC(18
    ;

    /**
     * The type of the constant object this kind will produce.
     */
    public final Class valueType;

    /**
     * Collection of this type of constant requires collecting them as well.
     * The set is transitive.
     */
    final List<ConstantType> implies;

    /**
     * Constant pool tag
     */
    public final int tag;

    ConstantType(int tag, Class valueType, ConstantType... implies) {
        this.tag = tag;
        this.valueType = valueType;
        this.implies = Arrays.asList(implies);
    }

    private static final ConstantType[] byTag = new ConstantType[20];

    static {
        for (ConstantType c : ConstantType.values()) {
            byTag[c.tag] = c;
        }
    }

    public static ConstantType fromTag(int tag) {
        return byTag[tag];
    }
}
