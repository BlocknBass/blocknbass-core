package nl.blocknbass.core;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Lazy;
import nl.blocknbass.core.network.ControlClient;

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
	
	@Override
	public void onInitialize() {
		System.out.println("Initializing Block & Bass Core!");
		INSTANCE = this;
		controlClient = new ControlClient();
		dispatch = new BlocknBassPacketDispatcher();
		PLUGINS.get().forEach(provider -> provider.registerPacketSet(dispatch));
	}
}
