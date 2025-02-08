package me.aidan.sydney.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.impl.RenderOverlayEvent;
import me.aidan.sydney.modules.impl.core.HUDModule;
import me.aidan.sydney.modules.impl.visuals.CrosshairModule;
import me.aidan.sydney.modules.impl.visuals.NoRenderModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo info) {
        if (client.options.hudHidden) return;
        Sydney.EVENT_HANDLER.post(new RenderOverlayEvent(context, tickCounter.getTickDelta(true)));
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo info) {
        if (Sydney.MODULE_MANAGER != null && Sydney.MODULE_MANAGER.getModule(HUDModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(HUDModule.class).vanillaPotions.getValue().equalsIgnoreCase("Hide")) {
            info.cancel();
        }
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo info) {
        if (Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).portalOverlay.getValue()) {
            info.cancel();
        }
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void renderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo info) {
        if (Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).vignette.getValue()) {
            info.cancel();
        }
    }

    @WrapWithCondition(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 0))
    private boolean renderPumpkinOverlay(InGameHud instance, DrawContext context, Identifier texture, float opacity) {
        return !(Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).pumpkinOverlay.getValue());
    }

    @WrapWithCondition(method = "renderMiscOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V", ordinal = 1))
    private boolean renderSnowOverlay(InGameHud instance, DrawContext context, Identifier texture, float opacity) {
        return !(Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(NoRenderModule.class).snowOverlay.getValue());
    }

    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo info) {
        if(Sydney.MODULE_MANAGER.getModule(CrosshairModule.class).isToggled()) info.cancel();
    }
}
