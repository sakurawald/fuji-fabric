package io.github.sakurawald.mixin._internal.server_instance;

import io.github.sakurawald.ServerMain;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.sakurawald.ServerMain.log;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void $init(CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        log.debug("MinecraftServerMixin: $init: " + server);
        ServerMain.SERVER = server;
    }
}