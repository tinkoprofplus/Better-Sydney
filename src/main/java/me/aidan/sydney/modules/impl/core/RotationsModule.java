package me.aidan.sydney.modules.impl.core;

import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;

@RegisterModule(name = "Rotations", description = "Manages the client's rotation system.", category = Module.Category.CORE, persistent = true, drawn = false)
public class RotationsModule extends Module {
    public BooleanSetting movementFix = new BooleanSetting("MovementFix", "Makes your movement in accordance with your yaw.", false);
    public BooleanSetting snapBack = new BooleanSetting("SnapBack", "Reverts rotations to previous values after rotating.", false);
}
