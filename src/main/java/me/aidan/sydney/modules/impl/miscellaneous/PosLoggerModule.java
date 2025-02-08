package me.aidan.sydney.modules.impl.miscellaneous;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import me.aidan.sydney.settings.impl.NumberSetting;
import me.aidan.sydney.utils.minecraft.WorldUtils;
import me.aidan.sydney.utils.system.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

@RegisterModule(name = "PosLogger", category = Module.Category.MISCELLANEOUS)
public class PosLoggerModule extends Module {
    public NumberSetting delay = new NumberSetting("Delay", "", 1, 0, 5);
    public BooleanSetting showIDs = new BooleanSetting("ShowIDs", "", false);
    private final Timer timer = new Timer();

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(getNull()) return;
        PlayerEntity target = getTarget();

        if(target != null && timer.hasTimeElapsed(delay.getValue().intValue()*1000)) {
            Sydney.CHAT_MANAGER.message(target.getName().getString() + ", [" + WorldUtils.getMovementDirection(target.getMovementDirection()) + "], X:" + target.getX() + ", Y:" + target.getY() + ", Z:" + target.getZ() + ", Yaw: " + target.getYaw() + ", Pitch: " + target.getPitch());
            timer.reset();
        }

        if(showIDs.getValue()) {
            for(Entity entity : mc.world.getEntities()) {
                entity.setCustomName(Text.literal(entity.getId() + ""));
                entity.setCustomNameVisible(true);
            }
        }
    }

    private PlayerEntity getTarget() {
        PlayerEntity target = null;
        for(PlayerEntity player : mc.world.getPlayers()) {
            if(player == mc.player || player.isDead() || mc.player.distanceTo(player) > 12) continue;

            if(target == null) {
                target = player;
                continue;
            }

            if(mc.player.distanceTo(player) < mc.player.distanceTo(target)) {
                target = player;
            }
        }
        return target;
    }
}
