package nl.blocknbass.core.network;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.MessageLite.Builder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@Sharable
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {
    public ProtobufEncoder() {
    }

    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
        if (msg instanceof MessageLite) {
            out.add(Unpooled.wrappedBuffer(((MessageLite)msg).toByteArray()));
        } else {
            if (msg instanceof Builder) {
                out.add(Unpooled.wrappedBuffer(((Builder)msg).build().toByteArray()));
            }

        }
    }
}
