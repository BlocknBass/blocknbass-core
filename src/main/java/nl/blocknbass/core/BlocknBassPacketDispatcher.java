package nl.blocknbass.core;

import net.minecraft.client.MinecraftClient;
import nl.blocknbass.core.proto.MessageProto;

import java.util.HashMap;

public class BlocknBassPacketDispatcher {
    public static class HandlerNotFoundException extends Exception {
    }

    private HashMap<String, BlocknBassPacketHandler> handlers = new HashMap<>();

    public void register(String key, BlocknBassPacketHandler handler) {
        handlers.put(key, handler);
    }

    public void dispatch(String key, MessageProto.Message message, MinecraftClient client)
            throws HandlerNotFoundException {
        BlocknBassPacketHandler handler = handlers.get(key);
        if (handler == null) {
            throw new HandlerNotFoundException();
        }
        handler.handle(message, client);
    }
}
