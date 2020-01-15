package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final StompMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private ConnectionsImpl connections;
    private static int currConnectionId = 100;
    private int currId;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, StompMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;

        currConnectionId = currConnectionId +1;
        currId = currConnectionId;
        connections = ConnectionsImpl.getInstance();
        connections.addHandler(currId, this);
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            System.out.println("is about to start process before while");
            protocol.start(currId, connections);

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    System.out.println("is about to start process NOW");
                    protocol.process(nextMessage);
                }
            }
            close();

        } catch (IOException ex) {
            connections.disconnect(currConnectionId);
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connections.disconnect(currConnectionId);
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) throws IOException {
        System.out.println("before writing");
        System.out.println(connections.toString());
        out.write(encdec.encode(msg));
        out.flush();
    }
}
