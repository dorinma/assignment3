package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.frameObjects.FrameObject;
import bgu.spl.net.srv.Server;

import java.util.function.Supplier;

public class StompServer {

    public static void main(String[] args) {

        if (args[0].equals("tpc")) {

//            Server<FrameObject> tpcServer = Server.threadPerClient(
//                    7777,
//                    ()-> new StompMessagingProtocol(),
//                    ()-> new MessageEncoderDecoderImpl());
//            tpcServer.serve();
//
//
//
//        } else if (args[0].equals("reactor")) {
//            Server<FrameObject> recServer = Server.reactor(
//                    3,
//                    7777,
//                    ()-> new StompMessagingProtocol(),
//                    ()-> new MessageEncoderDecoderImpl());
//            recServer.serve();
        }

    }
}
