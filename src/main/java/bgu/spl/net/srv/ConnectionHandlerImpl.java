package bgu.spl.net.srv;

import java.io.IOException;

public class ConnectionHandlerImpl<T> implements ConnectionHandler<T> {
    /**
     * Comment the following lines (both send methods) for the existing implementations to work.
     *
     * @param msg
     */
    @Override
    public void send(T msg) {

    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {

    }
}
