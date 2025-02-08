package me.aidan.sydney.modules.impl.core;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import me.aidan.sydney.settings.impl.ColorSetting;
import me.aidan.sydney.settings.impl.NumberSetting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@RegisterModule(name = "ClickGui", description = "Allows you to change and interact with the client's modules and settings through a GUI.", category = Module.Category.CORE, drawn = false, bind = GLFW.GLFW_KEY_RIGHT_SHIFT)
public class ClickGuiModule extends Module {
    public BooleanSetting sounds = new BooleanSetting("Sounds", "Plays Minecraft UI sounds when interacting with the client's GUI.", true);
    public BooleanSetting blur = new BooleanSetting("Blur", "Whether or not to blur the background behind the GUI.", true);
    public NumberSetting scrollSpeed = new NumberSetting("ScrollSpeed", "The speed at which the scrolling of the frames will be at.", 15, 1, 50);
    public ColorSetting color = new ColorSetting("Color", "The color that will be used in the GUI.", new ColorSetting.Color(new Color(130, 202, 255), true, false));

    @Override
    public void onEnable() {
        if (mc.player == null) {
            setToggled(false);
            return;
        }

        mc.setScreen(Sydney.CLICK_GUI);
    }

    @Override
    public void onDisable() {
        mc.setScreen(null);
    }

    public boolean isRainbow() {
        if(color.isSync()) return Sydney.MODULE_MANAGER.getModule(ColorModule.class).color.isRainbow();
        return color.isRainbow();
    }
}
