package me.aidan.sydney.mixins;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.impl.AttackBlockEvent;
import me.aidan.sydney.events.impl.AttackEntityEvent;
import me.aidan.sydney.events.impl.BreakBlockEvent;
import me.aidan.sydney.modules.impl.player.NoInteractModule;
import me.aidan.sydney.utils.minecraft.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        AttackBlockEvent event = new AttackBlockEvent(pos, direction);
        Sydney.EVENT_HANDLER.post(event);
        if (event.isCancelled()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "interactBlock", at = @At(value = "HEAD"), cancellable = true)
    private void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
        NoInteractModule noInteractModule = Sydney.MODULE_MANAGER.getModule(NoInteractModule.class);
        if (noInteractModule.isToggled() && noInteractModule.shouldNoInteract() && noInteractModule.mode.getValue().equalsIgnoreCase("Disable") && WorldUtils.RIGHT_CLICKABLE_BLOCKS.contains(client.world.getBlockState(hitResult.getBlockPos()).getBlock())) {
            info.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"))
    private void interactBlock$BEFORE(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        NoInteractModule noInteractModule = Sydney.MODULE_MANAGER.getModule(NoInteractModule.class);
        if (!client.player.isSneaking() && noInteractModule.isToggled() && noInteractModule.shouldNoInteract() && noInteractModule.mode.getValue().equalsIgnoreCase("Sneak") && WorldUtils.RIGHT_CLICKABLE_BLOCKS.contains(client.world.getBlockState(hitResult.getBlockPos()).getBlock())) {
            client.player.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
    }

    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", shift = At.Shift.AFTER))
    private void interactBlock$AFTER(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
        NoInteractModule noInteractModule = Sydney.MODULE_MANAGER.getModule(NoInteractModule.class);
        if (!client.player.isSneaking() && noInteractModule.isToggled() && noInteractModule.shouldNoInteract() && noInteractModule.mode.getValue().equalsIgnoreCase("Sneak") && WorldUtils.RIGHT_CLICKABLE_BLOCKS.contains(client.world.getBlockState(hitResult.getBlockPos()).getBlock())) {
            client.player.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
    }

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        Sydney.EVENT_HANDLER.post(new AttackEntityEvent(player, target));
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Sydney.EVENT_HANDLER.post(new BreakBlockEvent(pos));
    }
}
