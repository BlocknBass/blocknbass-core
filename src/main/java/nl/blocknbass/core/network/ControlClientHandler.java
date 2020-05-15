package nl.blocknbass.core.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.MinecraftClient;
import nl.blocknbass.core.BlocknBassCore;
import nl.blocknbass.core.BlocknBassPacketDispatcher;
import nl.blocknbass.core.proto.MessageProto;

public class ControlClientHandler extends SimpleChannelInboundHandler<MessageProto.Message> {
	private MinecraftClient client;
	
	public ControlClientHandler(MinecraftClient client) {
		this.client = client;
	}
	
	@Override
	public void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) {
		String key = msg.getKey();
		try {
			BlocknBassCore.INSTANCE.dispatch.dispatch(key, msg, client);
		} catch (BlocknBassPacketDispatcher.HandlerNotFoundException e) {
			System.err.println("[WARNING]: Unknown handler key " + key);
		}
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
