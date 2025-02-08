package me.aidan.sydney.modules.impl.player;

import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.PacketSendEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

@RegisterModule(name = "XCarry", description = "Allows you to carry items in your crafting slots.", category = Module.Category.PLAYER)
public class XCarryModule extends Module {
    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (mc.player == null) return;

        if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;

        mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
    }
}
