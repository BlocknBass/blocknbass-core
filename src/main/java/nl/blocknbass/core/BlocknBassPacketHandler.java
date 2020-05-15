package nl.blocknbass.core;

import net.minecraft.client.MinecraftClient;
import nl.blocknbass.core.proto.MessageProto;

public interface BlocknBassPacketHandler {

	void handle(MessageProto.Message message, MinecraftClient client);
}
