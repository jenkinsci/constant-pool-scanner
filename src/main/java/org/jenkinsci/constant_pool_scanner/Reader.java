package org.jenkinsci.constant_pool_scanner;

import java.io.IOException;

/**
 * Lazily parses primitive value of the constant pool.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Reader<T> {
    T read() throws IOException;
}
