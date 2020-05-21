package nl.blocknbass.core.mixins;

import net.minecraft.client.world.ClientWorld;
import nl.blocknbass.core.event.WorldLoadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method="disconnect()V", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        WorldLoadListener.get().onDisconnect();

    }
}
