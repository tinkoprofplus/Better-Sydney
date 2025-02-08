package me.aidan.sydney.modules.impl.miscellaneous;

import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.StringSetting;

@RegisterModule(name = "NameProtect", description = "Hides your current in game name.", category = Module.Category.MISCELLANEOUS)
public class NameProtectModule extends Module {
    public StringSetting name = new StringSetting("Name", "The name to use as a replacement.", "Sydney");
}
