package me.aidan.sydney.modules.impl.movement;

import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.NumberSetting;

@RegisterModule(name = "KeepSprint", description = "Makes it so that you're sprinting even after attacking.", category = Module.Category.MOVEMENT)
public class KeepSprintModule extends Module {
    public NumberSetting motion = new NumberSetting("Motion", "The velocity that will be applied to your movement when attacking.", 1.0f, 0.0f, 1.0f);

    @Override
    public String getMetaData() {
        return String.valueOf(motion.getValue().doubleValue());
    }
}
