package me.aidan.sydney.modules.impl.combat;

import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import me.aidan.sydney.settings.impl.NumberSetting;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;

@RegisterModule(name = "AutoLog", description = "Logs out automatically to avoid dying.", category = Module.Category.COMBAT)
public class AutoLogModule extends Module{
    public BooleanSetting healthCheck = new BooleanSetting("HealthCheck", "Checks if you are at a specific health to log out.", false);
    public NumberSetting health = new NumberSetting("Health", "The health the player must be at to log out.", new BooleanSetting.Visibility(healthCheck, true), 10, 0, 20);
    public BooleanSetting totemCheck = new BooleanSetting("TotemCheck", "Checks if you ran out of totems to be able to log out.", true);
    public NumberSetting totemCount = new NumberSetting("Totems", "The amount of totems to have in your inventory to log out.", new BooleanSetting.Visibility(totemCheck, true), 2, 0, 9);
    public BooleanSetting selfDisable = new BooleanSetting("SelfDisable", "Toggles off the module after logging out.", true);


    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(getNull()) return;

        int totems = mc.player.getInventory().count(Items.TOTEM_OF_UNDYING);

        if(healthCheck.getValue() && mc.player.getHealth() <= health.getValue().intValue()) {
            mc.getNetworkHandler().onDisconnect(new DisconnectS2CPacket(Text.literal("Health was lower than or equal to " + health.getValue().intValue() + ".")));
            if(selfDisable.getValue()) setToggled(false);
        }

        if(totemCheck.getValue() && totems <= totemCount.getValue().intValue()) {
            mc.getNetworkHandler().onDisconnect(new DisconnectS2CPacket(Text.literal("Couldn't find totems in your inventory.")));
            if(selfDisable.getValue()) setToggled(false);
        }
    }
}
