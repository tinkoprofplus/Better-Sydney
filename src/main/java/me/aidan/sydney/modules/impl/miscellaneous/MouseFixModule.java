package me.aidan.sydney.modules.impl.miscellaneous;

import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;

@RegisterModule(name = "MouseFix", description = "Fixes multiple mouse issues.", category = me.aidan.sydney.modules.Module.Category.MISCELLANEOUS)
public class MouseFixModule extends Module {
    public BooleanSetting customDebounce = new BooleanSetting("CustomDebounce", "Implements a custom debounce timer on mouse inputs.", true);
}
