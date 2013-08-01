package org.jenkinsci.constant_pool_scanner;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Parsed constants.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ConstantPool {
    final Object[] constants;

    ConstantPool(int size) {
        this.constants = new Object[size];
    }

    /**
     * Lists up all the constants of the specified type (including subtypes if applicable.)
     */
    public <T> Iterable<T> list(final Class<T> type) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    int idx=-1;

                    {
                        seek();
                    }

                    public boolean hasNext() {
                        return idx<constants.length;
                    }

                    public T next() {
                        Object v = constants[idx];
                        seek();
                        return type.cast(v);
                    }

                    private void seek() {
                        do {
                            idx++;
                        } while (hasNext() && !type.isInstance(constants[idx]));
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public int size() {
        return constants.length;
    }

    NameAndTypeConstant nameAndTypeAt(int i) {
        if (constants[i]==null)
            constants[i] = new NameAndTypeConstant();
        return (NameAndTypeConstant)constants[i];
    }

    FieldRefConstant fieldRefAt(int i) {
        if (constants[i]==null)
            constants[i] = new FieldRefConstant();
        return (FieldRefConstant)constants[i];
    }

    MethodRefConstant methodRefAt(int i) {
        if (constants[i]==null)
            constants[i] = new MethodRefConstant();
        return (MethodRefConstant)constants[i];
    }

    InterfaceMethodRefConstant interfaceMethodRefAt(int i) {
        if (constants[i]==null)
            constants[i] = new InterfaceMethodRefConstant();
        return (InterfaceMethodRefConstant)constants[i];
    }

    ClassConstant classAt(int i) {
        if (constants[i]==null)
            constants[i] = new ClassConstant();
        return (ClassConstant)constants[i];
    }

    Utf8Constant utf8At(int i) {
        if (constants[i]==null)
            constants[i] = new Utf8Constant();
        return (Utf8Constant)constants[i];
    }

    void set(int i, Object value) {
        assert constants[i]==null;
        constants[i] = value;
    }

    @Override
    public String toString() {
        return Arrays.asList(constants).toString();
    }
}
