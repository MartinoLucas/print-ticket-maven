package org.example.printer;

public interface Printer extends AutoCloseable {
    void write(byte[] data) throws Exception;
    void close() throws Exception;
}
