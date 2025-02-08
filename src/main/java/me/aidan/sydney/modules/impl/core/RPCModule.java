package me.aidan.sydney.modules.impl.core;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.ModeSetting;
import me.aidan.sydney.settings.impl.StringSetting;
import me.aidan.sydney.utils.system.MathUtils;
import me.aidan.sydney.utils.system.Timer;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;

@RegisterModule(name = "RPC", description = "Enabled the discord presence for the client.", category = Module.Category.CORE)
public class RPCModule extends Module {
    public ModeSetting imageMode = new ModeSetting("Image", "The mode for the discord presence image.", "Random", new String[]{"MushyGrape", "Wizard", "EvilMushroom", "Nett", "Yns", "Weed", "Miku", "Miku2", "Rukia", "Rei", "Nigga", "Bladee", "VapeV4", "Skeletrix", "Maxon", "ChillGuy", "Random"});
    public ModeSetting detailsMode = new ModeSetting("Details", "The mode for the discord presence details.", "Random", new String[]{"Custom", "Activity", "Random"});
    public StringSetting customDetails = new StringSetting("CustomDetails", "The custom text to use in the discord presence details.", new ModeSetting.Visibility(detailsMode, "Custom"), "margielaware.cc");

    private final String[] DETAILS = {"violently edging", "those who know", "getting money", "currently drinking and driving", "悉尼客户万岁", "jonking my shi", "FxckYouBxtchNxgga", "Playing 2b2t.pe", "batemanhook uid 3", "Abusing Fentanyl", "margielaware.cc", "richest person alive", "my richness powered by Sydney", "#hitmaker #trendsetter", "#beamer", "merry switchmas", "surf gang @ da beach +_+"};

    private final RichPresence rpc = new RichPresence();
    private final Timer imageTimer = new Timer();
    private final Timer detailsTimer = new Timer();
    private int ticks = 0;

    @Override
    public void onEnable() {
        DiscordIPC.start(1306415893160661003L, null);
        rpc.setStart(Sydney.UPTIME/1000);
        setImage();
        if(detailsMode.getValue().equals("Random")) rpc.setDetails(getDetails());
    }

    @Override
    public void onDisable() {
        DiscordIPC.stop();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if(ticks > 0) {
            ticks--;
            return;
        }

        if (imageMode.getValue().equals("Random")) {
            if(imageTimer.hasTimeElapsed(600000)) {
                setImage();
                imageTimer.reset();
            }
        } else {
            setImage();
        }

        if(detailsMode.getValue().equals("Random")) {
            if (detailsTimer.hasTimeElapsed(300000)) {
                rpc.setDetails(getDetails());
                detailsTimer.reset();
            }
        } else if(detailsMode.getValue().equals("Activity")) {
            if(mc.isInSingleplayer()) {
                rpc.setDetails("Playing Singleplayer");
            } else if(mc.getCurrentServerEntry() != null) {
                rpc.setDetails("Playing " + mc.getCurrentServerEntry().address);
            } else {
                rpc.setDetails("Idling");
            }
        } else {
            rpc.setDetails(customDetails.getValue());
        }

        DiscordIPC.setActivity(rpc);
        ticks = 200;
    }

    private void setImage() {
        rpc.setLargeImage(getImageIndex() + "", Sydney.MOD_NAME + " " + Sydney.MOD_VERSION);
    }

    private int getImageIndex() {
        return switch (imageMode.getValue()) {
            case "Wizard" -> 1;
            case "EvilMushroom" -> 2;
            case "Nett" -> 3;
            case "Yns" -> 4;
            case "Weed" -> 5;
            case "Miku" -> 6;
            case "Rukia" -> 7;
            case "Rei" -> 8;
            case "Nigga" -> 9;
            case "Bladee" -> 10;
            case "VapeV4" -> 11;
            case "Skeletrix" -> 12;
            case "Maxon" -> 13;
            case "ChillGuy" -> 14;
            case "Miku2" -> 15;
            case "Random" -> (int) MathUtils.random(15, 0);
            default -> 0;
        };
    }

    private String getDetails() {
        return DETAILS[(int) MathUtils.random(DETAILS.length, 0)];
    }
}
