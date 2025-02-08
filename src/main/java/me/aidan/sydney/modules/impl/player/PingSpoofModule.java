package me.aidan.sydney.modules.impl.player;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.PacketReceiveEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.modules.impl.miscellaneous.FastLatencyModule;
import me.aidan.sydney.settings.impl.NumberSetting;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;

import java.util.concurrent.ConcurrentLinkedQueue;

@RegisterModule(name = "PingSpoof", description = "Delays packets to spoof your ping.", category = Module.Category.PLAYER)
public class PingSpoofModule extends Module {
    public NumberSetting delay = new NumberSetting("Delay", "The delay of to send the packets at.", 200, 0, 2000);

    private final ConcurrentLinkedQueue<DelayedPacket> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void onEnable() {
        if (Sydney.MODULE_MANAGER.getModule(FastLatencyModule.class).isToggled()) setToggled(false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (getNull() || Sydney.MODULE_MANAGER.getModule(FastLatencyModule.class).isToggled()) return;

        if (event.getPacket() instanceof KeepAliveS2CPacket packet) {
            event.setCancelled(true);
            queue.add(new DelayedPacket(packet, System.currentTimeMillis()));
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (getNull()) return;

        DelayedPacket packet = queue.peek();
        if (packet == null) return;

        if (System.currentTimeMillis() - packet.time() >= delay.getValue().intValue()) {
            mc.getNetworkHandler().sendPacket(new KeepAliveC2SPacket(queue.poll().packet().getId()));
        }
    }

    @Override
    public String getMetaData() {
        return String.valueOf(delay.getValue().intValue());
    }

    private record DelayedPacket(KeepAliveS2CPacket packet, long time) {}
}
