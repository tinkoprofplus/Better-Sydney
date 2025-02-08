package me.aidan.sydney.modules.impl.player;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.NumberSetting;

@RegisterModule(name = "Timer", description = "Makes your game run at a faster tick speed.", category = Module.Category.PLAYER)
public class TimerModule extends Module {
    public NumberSetting multiplier = new NumberSetting("Multiplier", "The multiplier that will be added to the game's speed.", 1.0f, 0.0f, 20.0f);

    @SubscribeEvent(priority = Integer.MIN_VALUE)
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;
        Sydney.WORLD_MANAGER.setTimerMultiplier(multiplier.getValue().floatValue());
    }

    @Override
    public void onEnable() {
        Sydney.WORLD_MANAGER.setTimerMultiplier(multiplier.getValue().floatValue());
    }

    @Override
    public void onDisable() {
        Sydney.WORLD_MANAGER.setTimerMultiplier(1.0f);
    }

    @Override
    public String getMetaData() {
        return String.valueOf(multiplier.getValue().floatValue());
    }
}
