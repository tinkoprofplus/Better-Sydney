package me.aidan.sydney.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.impl.PlayerTravelEvent;
import me.aidan.sydney.modules.impl.movement.KeepSprintModule;
import me.aidan.sydney.modules.impl.movement.SafeWalkModule;
import me.aidan.sydney.modules.impl.movement.SpeedModule;
import me.aidan.sydney.modules.impl.movement.VelocityModule;
import me.aidan.sydney.modules.impl.player.ReachModule;
import me.aidan.sydney.utils.IMinecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IMinecraft {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyReturnValue(method = "isPushedByFluids", at = @At("RETURN"))
    private boolean isPushedByFluids(boolean original) {
        if ((Object) this == mc.player && Sydney.MODULE_MANAGER.getModule(VelocityModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(VelocityModule.class).antiLiquidPush.getValue()) return false;
        return original;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift = At.Shift.AFTER))
    private void attack(CallbackInfo callbackInfo) {
        if (Sydney.MODULE_MANAGER.getModule(KeepSprintModule.class).isToggled()) {
            float multiplier = 0.6f + 0.4f * Sydney.MODULE_MANAGER.getModule(KeepSprintModule.class).motion.getValue().floatValue();
            mc.player.setVelocity(mc.player.getVelocity().x / 0.6 * multiplier, mc.player.getVelocity().y, mc.player.getVelocity().z / 0.6 * multiplier);
            mc.player.setSprinting(true);
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void travel(Vec3d movementInput, CallbackInfo info) {
        PlayerTravelEvent event = new PlayerTravelEvent(movementInput);
        Sydney.EVENT_HANDLER.post(event);

        if (event.isCancelled()) {
            move(MovementType.SELF, getVelocity());
            info.cancel();
        }
    }

    @Inject(method = "getBlockInteractionRange", at = @At("HEAD"), cancellable = true)
    private void getBlockInteractionRange(CallbackInfoReturnable<Double> info) {
        if (Sydney.MODULE_MANAGER.getModule(ReachModule.class).isToggled()) {
            info.setReturnValue(Sydney.MODULE_MANAGER.getModule(ReachModule.class).amount.getValue().doubleValue());
        }
    }

    @Inject(method = "getEntityInteractionRange", at = @At("HEAD"), cancellable = true)
    private void getEntityInteractionRange(CallbackInfoReturnable<Double> info) {
        if (Sydney.MODULE_MANAGER.getModule(ReachModule.class).isToggled()) {
            info.setReturnValue(Sydney.MODULE_MANAGER.getModule(ReachModule.class).amount.getValue().doubleValue());
        }
    }

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    private void clipAtLedge(CallbackInfoReturnable<Boolean> info) {
        if (Sydney.MODULE_MANAGER.getModule(SafeWalkModule.class).isToggled()) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "getMovementSpeed", at = @At("HEAD"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> info) {
        if (Sydney.MODULE_MANAGER.getModule(SpeedModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(SpeedModule.class).mode.getValue().equalsIgnoreCase("Vanilla")) {
            info.setReturnValue(Sydney.MODULE_MANAGER.getModule(SpeedModule.class).vanillaSpeed.getValue().floatValue());
        }
    }
}
