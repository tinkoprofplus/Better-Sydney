package me.aidan.sydney.mixins;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.impl.core.MenuModule;
import me.aidan.sydney.utils.IMinecraft;
import me.aidan.sydney.utils.color.ColorUtils;
import me.aidan.sydney.utils.graphics.Renderer2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(Screen.class)
public class ScreenMixin implements IMinecraft {
    @Shadow public int width;
    @Shadow public int height;

    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(Sydney.MODULE_MANAGER.getModule(MenuModule.class).isToggled() && Sydney.MODULE_MANAGER.getModule(MenuModule.class).mainMenu.getValue() && mc.world == null) {
            Renderer2D.renderQuad(context.getMatrices(), 0, 0, width, height, new Color(25, 25, 25,255));

            for(int i = 0; i < width; i++) {
                Color color = ColorUtils.getRainbow(2L, 0.7f, 1.0f, 255, i*5L);
                Renderer2D.renderQuad(context.getMatrices(), i, 0, i + 1, 1, color);
            }

            ci.cancel();
        }
    }
}
