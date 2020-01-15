package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.frameObjects.FrameObject;
import bgu.spl.net.impl.frameObjects.clientsFrames.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class
MessageEncoderDecoderImpl implements MessageEncoderDecoder<FrameObject> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;

    @Override
    public FrameObject decodeNextByte(byte nextByte) {
        if (nextByte == '\u0000') {
            System.out.println("if");
            return bytesToFrame(popString());
        }
        pushByte(nextByte);
        return null; //not a message yet
    }

    /**
     * encodes the given message to bytes array
     *
     * @param message the message to encode
     * @return the encoded bytes
     */
    @Override
    public byte[] encode(FrameObject message) {
        return message.toString().getBytes();
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private FrameObject bytesToFrame(String stringBytes) {
        FrameObject frameObject = null;
        String [] message = stringBytes.split("\n");
        HashMap<String, String> headers = new HashMap<>();
        String body = "";

        //Get the headers and the body of the message
        for (int i = 1; i < message.length; i++) {
            if(message[i].contains(":")) {
                //TODO do we need to check : in body if we're creating it?
                //if(i != 1 && message[i - 1].length() > 0) //if the line before is empty, then this is not a header!
                    headers.put(message[i].split(":")[0], message[i].split(":")[1]);
            }
            else if(!message[i].isEmpty())
                body += message[i];
        }
        //body += '\u0000';
        //System.out.println(message[0]);

        //Get the type of the message
        if(message[0].equals("CONNECT"))
            frameObject = new ConnectClient(message[0], headers, body);

        else if(message[0].equals("DISCONNECT"))
            frameObject = new DisconnectClient(message[0], headers, body);

        else if(message[0].equals("SUBSCRIBE"))
            frameObject = new SubscribeClient(message[0], headers, body);

        else if(message[0].equals("UNSUBSCRIBE"))
            frameObject = new UnsubscribeClient(message[0], headers, body);

        else if(message[0].equals("SEND")) {
            frameObject = new SendClient(message[0], headers, body);

        }
        System.out.println(frameObject.toString());
        return frameObject;
    }
}
