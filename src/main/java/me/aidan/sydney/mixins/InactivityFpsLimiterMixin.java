package me.aidan.sydney.mixins;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.impl.miscellaneous.UnfocusedFPSModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.InactivityFpsLimiter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InactivityFpsLimiter.class)
public class InactivityFpsLimiterMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void getFramerateLimit(CallbackInfoReturnable<Integer> info) {
        if (Sydney.MODULE_MANAGER.getModule(UnfocusedFPSModule.class).isToggled() && !client.isWindowFocused()) {
            info.setReturnValue(Sydney.MODULE_MANAGER.getModule(UnfocusedFPSModule.class).limit.getValue().intValue());
        }
    }
}
