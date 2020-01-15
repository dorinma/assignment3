package bgu.spl.net.impl.newsfeed;

import bgu.spl.net.impl.frameObjects.FrameObject;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.impl.stomp.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.stomp.StompMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class NewsFeedServerMain {

    public static void main(String[] args) {
        NewsFeed feed = new NewsFeed(); //one shared object

        if (args[0].equals("tpc")) {
            Server<FrameObject> tpc = Server.threadPerClient(
                    7777,
                    () -> new StompMessagingProtocolImpl<>(),
                    () -> new MessageEncoderDecoderImpl());
            tpc.serve();
        } else if (args[0].equals("reactor")) {
            Server<FrameObject> reactor = Server.reactor(
                    2,
                    7777,
                    () -> new StompMessagingProtocolImpl<>(),
                    () -> new MessageEncoderDecoderImpl());
            reactor.serve();
        }

    }
}
