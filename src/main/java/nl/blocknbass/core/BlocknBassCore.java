package nl.blocknbass.core;

import com.google.common.collect.ImmutableList;
import io.netty.channel.Channel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import nl.blocknbass.core.network.ControlClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BlocknBassCore implements ModInitializer {
	public static final String ENTRYPOINT_TYPE = "blocknbass-mod";
	public static BlocknBassCore INSTANCE;
	public ControlClient controlClient;
	public BlocknBassPacketDispatcher dispatch;

	private static final Lazy<ImmutableList<IBlocknBassMod>> PLUGINS = new Lazy<>(
			() -> ImmutableList.copyOf(FabricLoader.getInstance().getEntrypoints(
					ENTRYPOINT_TYPE, IBlocknBassMod.class
			))
	);

	public static Class<? extends Channel> getNettyLoopClass() {
		if (Epoll.isAvailable() && MinecraftClient.getInstance().options.shouldUseNativeTransport()) {
			return EpollSocketChannel.class;
		}
		return NioSocketChannel.class;
	}

	public static Lazy getNettyLoop() {
		if (Epoll.isAvailable() && MinecraftClient.getInstance().options.shouldUseNativeTransport()) {
			return ClientConnection.CLIENT_IO_GROUP_EPOLL;
		}
		return ClientConnection.CLIENT_IO_GROUP;
	}

	@Override
	public void onInitialize() {
		System.out.println("Initializing Block & Bass Core!");
		INSTANCE = this;
		controlClient = new ControlClient();
		dispatch = new BlocknBassPacketDispatcher();
		PLUGINS.get().forEach(provider -> provider.registerPacketSet(dispatch));
		ClientSidePacketRegistry.INSTANCE.register(new Identifier("blocknbass", "main"),
				((packetContext, packetByteBuf) -> {
					controlClient.reconnect(MinecraftClient.getInstance());
				}));
	}
}
