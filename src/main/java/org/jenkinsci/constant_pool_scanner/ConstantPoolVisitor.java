package org.jenkinsci.constant_pool_scanner;

import java.io.IOException;

/**
 * Receives callback for constants discovered in the class file.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ConstantPoolVisitor {
    /**
     * A class constant is found.
     *
     * @param value
     *      {@link ClassConstant#get()} will return a valid UTF-8 constant, but its value
     *      may not be available until all the constants are parsed.
     */
    public void onClass(ClassConstant value) throws IOException {}
    /**
     * A reference to a field is found.
     *
     * @param value
     *      {@link MemberRefConstant#getClazz()} and {@link MemberRefConstant#getNameAndType()}
     *      will return valid constant objects, but their values may not be available
     *      until all the constants are parsed.
     */
    public void onFieldRef(MemberRefConstant value) throws IOException {}
    /**
     * A reference to a method is found.
     *
     * @param value
     *      {@link MemberRefConstant#getClazz()} and {@link MemberRefConstant#getNameAndType()}
     *      will return valid constant objects, but their values may not be available
     *      until all the constants are parsed.
     */
    public void onMethodRef(MemberRefConstant value) throws IOException {}
    /**
     * A reference to an interface method is found.
     *
     * @param value
     *      {@link MemberRefConstant#getClazz()} and {@link MemberRefConstant#getNameAndType()}
     *      will return valid constant objects, but their values may not be available
     *      until all the constants are parsed.
     */
    public void onInterfaceMethodRef(MemberRefConstant value) throws IOException {}

    /**
     * A string literal is found.
     *
     * @param value
     *      Value of UTF-8 constant may not be available until all the constants are parsed.
     */
    public void onString(Utf8Constant value) throws IOException {}

    /**
     * An integer literal is found.
     *
     * @param reader
     *      If the caller is interested in obtaining the actual value, {@link Reader#read()}
     *      can be called once to obtain the value. Otherwise the parsing of the constant
     *      will be skipped.
     */
    public void onInteger(Reader<Integer> reader) throws IOException {}

    /**
     * A float literal is found.
     *
     * @param reader
     *      If the caller is interested in obtaining the actual value, {@link Reader#read()}
     *      can be called once to obtain the value. Otherwise the parsing of the constant
     *      will be skipped.
     */
    public void onFloat(Reader<Float> reader) throws IOException {}
    /**
     * A long literal is found.
     *
     * @param reader
     *      If the caller is interested in obtaining the actual value, {@link Reader#read()}
     *      can be called once to obtain the value. Otherwise the parsing of the constant
     *      will be skipped.
     */
    public void onLong(Reader<Long> reader) throws IOException {}
    /**
     * A double literal is found.
     *
     * @param reader
     *      If the caller is interested in obtaining the actual value, {@link Reader#read()}
     *      can be called once to obtain the value. Otherwise the parsing of the constant
     *      will be skipped.
     */
    public void onDouble(Reader<Double> reader) throws IOException {}

    /**
     * A field/method name + descriptor combo constant was found.
     *
     * @param value
     *      {@link NameAndTypeConstant#getName()} and {@link NameAndTypeConstant#getDescriptor()}
     *      will return valid constant objects, but their values may not be available
     *      until all the constants are parsed.
     */
    public void onNameAndType(NameAndTypeConstant value) throws IOException {}

    /**
     * UTF-8 constant is found.
     */
    public void onUTF8(String value) throws IOException {}
    // public void onMethodHandle(...);
    // public void onMethodType(...);
    // public void onInvokeDynamic(...);
}
