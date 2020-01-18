package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.frameObjects.FrameObject;
import bgu.spl.net.srv.Server;

import java.util.function.Supplier;

public class StompServer {

    public static void main(String[] args) {

            if (args[0].equals("tpc"))
            {
                Server<FrameObject> tpc = Server.threadPerClient(
                        7777,
                        () -> new StompMessagingProtocolImpl<>(),
                        () -> new MessageEncoderDecoderImpl());
                tpc.serve();
            } else if (args[0].equals("reactor"))
            {
                Server<FrameObject> reactor = Server.reactor(
                        2,
                        7777,
                        () -> new StompMessagingProtocolImpl<>(),
                        () -> new MessageEncoderDecoderImpl());
                reactor.serve();
            }
    }
}
