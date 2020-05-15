package nl.blocknbass.core.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nl.blocknbass.core.proto.MessageProto;

public class ControlClient {
	private EventLoopGroup nettyGroup = new NioEventLoopGroup();
	private Channel channel;
	
	public void run(String server, int port, MinecraftClient client) throws Exception {
		Bootstrap b = new Bootstrap();
		b.group(nettyGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
				ch.pipeline().addLast(new ControlClientHandler(client));
			}
		});
		
		final ChannelFuture f = b.connect(server, port);
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				Text text;
				if (future.isSuccess()) {
					text = new LiteralText("Successfully connected to the"
							+ " Block & Bass control server!").formatted(Formatting.GREEN);
				} else {
					text = new LiteralText("Could not connect to the"
							+ " Block & Bass control server!").formatted(Formatting.RED);
					f.channel().close();
				}
				
				client.execute(() -> {
					MinecraftClient.getInstance().player.addChatMessage(text, false);
				});
			}
		});
		
		f.sync();
		channel = f.channel();
	}

	public Channel getChannel() {
		return channel;
	}
	
	public void shutdown() {
		//TODO: fix this lol
		//nettyGroup.shutdownGracefully();
	}
}