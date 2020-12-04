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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nl.blocknbass.core.BlocknBassCore;
import nl.blocknbass.core.proto.MessageProto;

public class ControlClient {
	private Channel channel;
	
	public void run(String server, int port, MinecraftClient client) throws Exception {
		if (channel != null && channel.isOpen())
			return;
		Bootstrap b = new Bootstrap();
		b.group((EventLoopGroup)BlocknBassCore.getNettyLoop().get());
		b.channel(BlocknBassCore.getNettyLoopClass());
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
				ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
				ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
				ch.pipeline().addLast(new ProtobufEncoder());
				ch.pipeline().addLast(new ControlClientHandler(client));
			}
		});
		
		final ChannelFuture f = b.connect(server, port);
		f.addListener((ChannelFutureListener) future -> {
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
				MinecraftClient.getInstance().player.sendMessage(text, false);
			});
		});
		
		f.sync();
		channel = f.channel();
	}

	public Channel getChannel() {
		return channel;
	}
	
	public void shutdownChannels() {
		try {
			if (channel != null && channel.isOpen())
				channel.close().sync();
		} catch (InterruptedException e) {
			System.err.println("Channel shutdown failed!");
		}
	}

	public void reconnect(MinecraftClient client) {
		shutdownChannels();
		try {
			run("control.blocknbass.nl", 6969, client);
		} catch (Exception e) {
			client.execute(() -> {
				client.player.sendMessage(
						new LiteralText("Failed to reconnect to Block & Bass control server")
								.formatted(Formatting.RED), false
				);
			});
		}
	}
}
