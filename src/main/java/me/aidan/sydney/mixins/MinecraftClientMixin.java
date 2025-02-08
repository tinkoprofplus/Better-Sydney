package me.aidan.sydney.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.impl.GameLoopEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.gui.special.MainMenuScreen;
import me.aidan.sydney.modules.impl.combat.NoHitDelayModule;
import me.aidan.sydney.modules.impl.core.MenuModule;
import me.aidan.sydney.modules.impl.miscellaneous.AutoRespawnModule;
import me.aidan.sydney.modules.impl.player.FastPlaceModule;
import me.aidan.sydney.modules.impl.player.MultiTaskModule;
import me.aidan.sydney.utils.IMinecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements IMinecraft {
    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow private int itemUseCooldown;

    @Shadow @Final public GameOptions options;

    @Shadow public int attackCooldown;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V", shift = At.Shift.BEFORE))
    private void init(RunArgs args, CallbackInfo info) {
        Sydney.onPostInitialize();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;runTasks()V", shift = At.Shift.AFTER))
    private void runTickHook(boolean tick, CallbackInfo info) {
        Sydney.EVENT_HANDLER.post(new GameLoopEvent());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo info) {
        Sydney.EVENT_HANDLER.post(new TickEvent());
    }

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isRiding()Z", ordinal = 0, shift = At.Shift.BEFORE))
    private void doItemUse(CallbackInfo info) {
        if (Sydney.MODULE_MANAGER != null && Sydney.MODULE_MANAGER.getModule(FastPlaceModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(FastPlaceModule.class).isValidItem(player.getMainHandStack().getItem())) {
            itemUseCooldown = Sydney.MODULE_MANAGER.getModule(FastPlaceModule.class).ticks.getValue().intValue();
        }
    }

    @ModifyExpressionValue(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean handleBlockBreaking(boolean original) {
        if (Sydney.MODULE_MANAGER != null && Sydney.MODULE_MANAGER.getModule(MultiTaskModule.class).isToggled()) return false;
        return original;
    }

    @ModifyExpressionValue(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
    private boolean handleInputEvents(boolean original) {
        if (Sydney.MODULE_MANAGER != null && Sydney.MODULE_MANAGER.getModule(MultiTaskModule.class).isToggled()) return false;
        return original;
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void setScreen(Screen screen, CallbackInfo info) {
        if (screen instanceof DeathScreen && player != null && Sydney.MODULE_MANAGER.getModule(AutoRespawnModule.class).isToggled()) {
            player.requestRespawn();
            info.cancel();
        }

        if (screen instanceof TitleScreen) {
            Sydney.checkForUpdates();

            if (Sydney.MODULE_MANAGER.getModule(MenuModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(MenuModule.class).mainMenu.getValue()) {
                this.setScreen(new MainMenuScreen());
                info.cancel();
            }
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void doAttack(CallbackInfoReturnable<Boolean> info) {
        if (Sydney.MODULE_MANAGER != null && Sydney.MODULE_MANAGER.getModule(NoHitDelayModule.class).isToggled()) {
            attackCooldown = 0;
        }
    }
}