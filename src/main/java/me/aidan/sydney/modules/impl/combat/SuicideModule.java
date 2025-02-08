package me.aidan.sydney.modules.impl.combat;

import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.ClientConnectEvent;
import me.aidan.sydney.events.impl.PacketReceiveEvent;
import me.aidan.sydney.events.impl.PlayerConnectEvent;
import me.aidan.sydney.events.impl.PlayerDeathEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;

@RegisterModule(name = "Suicide", description = "Makes all of the combat modules target you so that you die.", category = Module.Category.COMBAT)
public class SuicideModule extends Module {
    public BooleanSetting offhandOverride = new BooleanSetting("OffhandOverride", "Changes the target offhand item in order to make it easier to die.", true);
    public BooleanSetting deathDisable = new BooleanSetting("DeathDisable", "Disables the module once you have died.", true);
    public BooleanSetting loginDisable = new BooleanSetting("LoginDisable", "Disables the module when you logged in.", true);

    @SubscribeEvent
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!deathDisable.getValue()) return;
        if (event.getPlayer() != mc.player) return;

        setToggled(false);
    }

    @SubscribeEvent
    public void onPlayerLogin(ClientConnectEvent event) {
        if (loginDisable.getValue()) setToggled(false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) setToggled(false);
    }
}
