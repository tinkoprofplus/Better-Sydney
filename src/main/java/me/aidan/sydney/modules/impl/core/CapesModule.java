package me.aidan.sydney.modules.impl.core;

import lombok.Getter;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.ModeSetting;
import net.minecraft.util.Identifier;

@Getter
@RegisterModule(name = "Capes", description = "Applies the Sydney cape to yourself and to other users.", category = Module.Category.CORE, toggled = true, drawn = false)
public class CapesModule extends Module {
    private Identifier capeTexture;

    public CapesModule() {
        public ModeSetting capeMode = new ModeSetting("Cape Mode", "Select which cape to display", "Sydney", new String[]{"Sydney", "Klux", "Future"});
        capeMode.onChange(capeMode -> updateCapeTexture());
        updateCapeTexture();
    }

    public void updateCapeTexture() {
        switch (capeMode.getValue().toLowerCase()) {
            case "Sydney":
                this.capeTexture = Identifier.of(Sydney.MOD_ID, "textures/cape.png");
                break;
            case "Klux":
                this.capeTexture = Identifier.of(Sydney.MOD_ID, "textures/klux.png");
                break;
            case "Future":
                this.capeTexture = Identifier.of(Sydney.MOD_ID, "textures/future.png");
                break;
        }
    }
}
