package me.aidan.sydney.mixins;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.impl.RemoveFireworkEvent;
import me.aidan.sydney.utils.IMinecraft;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin implements IMinecraft {
    @Shadow private int life;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FireworkRocketEntity;updateRotation()V", shift = At.Shift.AFTER), cancellable = true)
    private void tick(CallbackInfo info) {
        FireworkRocketEntity entity = ((FireworkRocketEntity) (Object) this);

        RemoveFireworkEvent event = new RemoveFireworkEvent(entity);
        Sydney.EVENT_HANDLER.post(event);

        if (event.isCancelled()) {
            info.cancel();

            if (life == 0 && !entity.isSilent()) {
                mc.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0f, 1.0f);
            }

            ++life;
            if (mc.world.isClient && life % 2 < 2) {
                mc.world.addParticle(ParticleTypes.FIREWORK, entity.getX(), entity.getY(), entity.getZ(), mc.world.random.nextGaussian() * 0.05, -entity.getVelocity().y * 0.5, mc.world.random.nextGaussian() * 0.05);
            }
        }
    }
}
