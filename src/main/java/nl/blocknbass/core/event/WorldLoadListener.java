package nl.blocknbass.core.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import nl.blocknbass.core.BlocknBassCore;

public class WorldLoadListener {

	public static WorldLoadListener INSTANCE;
	
	public static WorldLoadListener get() {
		if (INSTANCE == null)
			INSTANCE = new WorldLoadListener();
		
		return INSTANCE;
	}

	public void onLoadWorldPost(ClientWorld worldClientIn, MinecraftClient minecraftClient) {
		try {
			BlocknBassCore.INSTANCE.controlClient.run("control.blocknbass.nl", 6969, minecraftClient);
		} catch (Exception e) {
			System.err.println("Failed to connect to server " + "localhost");
			e.printStackTrace();
		}
	}
	
	public void onDisconnect() {
		BlocknBassCore.INSTANCE.controlClient.shutdownChannels();
	}
}
