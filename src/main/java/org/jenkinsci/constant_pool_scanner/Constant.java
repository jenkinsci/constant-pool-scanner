package org.jenkinsci.constant_pool_scanner;

/**
 * Constant value.
 *
 * <p>
 * Many constant value refers to other constants in the pool, and because they can be a
 * forward reference, some of the referenced values may not be available when a constant
 * is found.
 *
 * <p>
 * {@link ConstantPoolScanner} creates a {@link Constant} object as soon as the index
 * is referenced from somewhere, and the actual value will be back-filled later when
 * the constant is actually parsed.
 *
 * <p>
 * This allows {@link ConstantPoolVisitor} to hold on to references without knowing their
 * actual values and makes the parsing easier.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Constant {
}
