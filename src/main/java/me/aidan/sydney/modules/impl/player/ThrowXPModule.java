package me.aidan.sydney.modules.impl.player;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.PlayerUpdateEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import me.aidan.sydney.settings.impl.ModeSetting;
import me.aidan.sydney.settings.impl.NumberSetting;
import me.aidan.sydney.utils.minecraft.InventoryUtils;
import me.aidan.sydney.utils.minecraft.NetworkUtils;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

@RegisterModule(name = "ThrowXP", description = "Automatically switches to experience bottles and throws them.", category = Module.Category.PLAYER)
public class ThrowXPModule extends Module {
    public ModeSetting autoSwitch = new ModeSetting("Switch", "The mode that will be used for automatically switching to necessary items.", "Silent", InventoryUtils.SWITCH_MODES);
    public NumberSetting delay = new NumberSetting("Delay", "The delay in ticks between throwing experience bottles.", 1, 0, 20);
    public NumberSetting repeat = new NumberSetting("Repeat", "Allows you to throw a lot more XP bottles at once.", 1, 1, 15);
    public ModeSetting antiWaste = new ModeSetting("AntiWaste", "How wasting of experience bottles should be prevented.", "Avoid", new String[]{"None", "Avoid", "Disable"});

    public BooleanSetting itemDisable = new BooleanSetting("ItemDisable", "Automatically disables the module when you run out of XP.", true);

    private int ticks = 0;

    @SubscribeEvent
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (autoSwitch.getValue().equalsIgnoreCase("None") && !(mc.player.getMainHandStack().getItem() instanceof BlockItem)) {
            Sydney.CHAT_MANAGER.tagged("You are currently not holding any experience bottles.", getName());
            setToggled(false);
            return;
        }

        if (ticks < delay.getValue().intValue()) {
            ticks++;
            return;
        }

        if (!needsExperience() && !antiWaste.getValue().equals("None")) {
            if (antiWaste.getValue().equals("Disable")) setToggled(false);
            return;
        }

        int slot = InventoryUtils.find(Items.EXPERIENCE_BOTTLE, 0, autoSwitch.getValue().equalsIgnoreCase("AltSwap") || autoSwitch.getValue().equalsIgnoreCase("AltPickup") ? 35 : 8);
        int previousSlot = mc.player.getInventory().selectedSlot;

        if (slot == -1) {
            Sydney.CHAT_MANAGER.tagged("No experience bottles could be found in your hotbar.", getName());
            setToggled(false);
            return;
        }

        InventoryUtils.switchSlot(autoSwitch.getValue(), slot, previousSlot);

        for (int i = 0; i < repeat.getValue().intValue(); i++) NetworkUtils.sendSequencedPacket(sequence -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch()));
        mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

        InventoryUtils.switchBack(autoSwitch.getValue(), slot, previousSlot);

        ticks = 0;
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) setToggled(false);
    }

    private boolean needsExperience() {
        for (ItemStack stack : mc.player.getArmorItems()) {
            if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem && (Math.round(((stack.getMaxDamage() - stack.getDamage()) * 100.0f) / stack.getMaxDamage()) < 100.0f)) {
                return true;
            }
        }

        return false;
    }
}
