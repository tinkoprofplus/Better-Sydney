package me.aidan.sydney.mixins;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.impl.EntitySpawnEvent;
import me.aidan.sydney.modules.impl.visuals.AtmosphereModule;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void getSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Integer> info) {
        if (Sydney.MODULE_MANAGER.getModule(AtmosphereModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(AtmosphereModule.class).modifyFog.getValue()) {
            info.setReturnValue(Sydney.MODULE_MANAGER.getModule(AtmosphereModule.class).fogColor.getColor().getRGB());
        }
    }

    @Inject(method = "addEntity", at = @At(value = "HEAD"))
    private void addEntity(Entity entity, CallbackInfo info) {
        Sydney.EVENT_HANDLER.post(new EntitySpawnEvent(entity));
    }
}
