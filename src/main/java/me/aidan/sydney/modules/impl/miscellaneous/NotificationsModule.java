package me.aidan.sydney.modules.impl.miscellaneous;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.ClientConnectEvent;
import me.aidan.sydney.events.impl.PlayerDeathEvent;
import me.aidan.sydney.events.impl.PlayerPopEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import me.aidan.sydney.utils.chat.ChatUtils;
import me.aidan.sydney.utils.minecraft.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

import java.util.ArrayList;

@RegisterModule(name = "Notifications", description = "Notifies you in chat whenever something significant happens.", category = Module.Category.MISCELLANEOUS)
public class NotificationsModule extends Module {
    public BooleanSetting totemPops = new BooleanSetting("TotemPops", "Notifies you in chat whenever a player pops a totem.", true);
    public BooleanSetting visualRange = new BooleanSetting("VisualRange", "Notifies you in chat whenever a player enters your render distance.", false);
    public BooleanSetting pearlThrows = new BooleanSetting("PearlThrows", "Notifies you in chat whenever a player throws a pearl.", true);

    private final ArrayList<PlayerEntity> loadedPlayers = new ArrayList<>();
    private final ArrayList<Integer> thrownPearls = new ArrayList<>();

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (getNull()) return;

        if (visualRange.getValue()) {
            for (Entity entity : mc.world.getEntities()) {
                if (!(entity instanceof PlayerEntity player) || entity == mc.player) continue;

                if (!loadedPlayers.contains(player)) {
                    loadedPlayers.add(player);
                    Sydney.CHAT_MANAGER.message(ChatUtils.getPrimary() + player.getName().getString() + ChatUtils.getSecondary() + " has entered your visual range.", "visual-range-" + player.getName().getString());
                }
            }

            if (!loadedPlayers.isEmpty()) {
                for (PlayerEntity player : new ArrayList<>(loadedPlayers)) {
                    if (!mc.world.getPlayers().contains(player)) {
                        loadedPlayers.remove(player);
                        Sydney.CHAT_MANAGER.message(ChatUtils.getPrimary() + player.getName().getString() + ChatUtils.getSecondary() + " has left your visual range.", "visual-range-" + player.getName().getString());
                    }
                }
            }
        }

        if (pearlThrows.getValue()) {
            for(Entity e : mc.world.getEntities()) {
                if(!(e instanceof EnderPearlEntity pearl)) continue;
                if(pearl.getOwner() == null || thrownPearls.contains(pearl.getId())) continue;

                String name = pearl.getOwner().getName().getString();
                Sydney.CHAT_MANAGER.message(ChatUtils.getPrimary() + name + ChatUtils.getSecondary() + " threw a pearl towards " + EntityUtils.getPearlDirection(pearl).toString() + ".", "pearl-throws-" + name);
                thrownPearls.add(pearl.getId());
            }

            thrownPearls.removeIf(id -> !(mc.world.getEntityById(id) instanceof EnderPearlEntity));
        }
    }

    @SubscribeEvent
    public void onClientConnect(ClientConnectEvent event) {
        loadedPlayers.clear();
    }

    @SubscribeEvent
    public void onPlayerPop(PlayerPopEvent event) {
        if (totemPops.getValue()) {
            Sydney.CHAT_MANAGER.message(ChatUtils.getPrimary() + event.getPlayer().getName().getString() + ChatUtils.getSecondary() + " has popped " + ChatUtils.getPrimary() + event.getPops() + ChatUtils.getSecondary() + " totem" + (event.getPops() > 1 ? "s" : "") + ".", "totem-pop-" + event.getPlayer().getName().getString());
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(PlayerDeathEvent event) {
        int pops = Sydney.WORLD_MANAGER.getPoppedTotems().getOrDefault(event.getPlayer().getUuid(), 0);
        if (totemPops.getValue() && pops > 0) {
            Sydney.CHAT_MANAGER.message(ChatUtils.getPrimary() + event.getPlayer().getName().getString() + ChatUtils.getSecondary() + " has died after popping " + ChatUtils.getPrimary() + pops + ChatUtils.getSecondary() + " totem" + (pops > 1 ? "s" : "") + ".", "totem-pop-" + event.getPlayer().getName().getString());
        }
    }
}
