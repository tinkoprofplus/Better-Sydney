package me.aidan.sydney.modules.impl.core;

import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import me.aidan.sydney.settings.impl.CategorySetting;

@RegisterModule(name = "Menu", description = "Replaces the default title screen with the client's custom main menu screen.", category = Module.Category.CORE, persistent = true, drawn = false)
public class MenuModule extends Module {
    public CategorySetting mainMenuCategory = new CategorySetting("MainMenu", "The category for settings related to the main menu.");
    public BooleanSetting mainMenu = new BooleanSetting("MainMenu", "Enabled", "Replaces Minecraft's default main menu with a customizable one.", new CategorySetting.Visibility(mainMenuCategory), true);
}
