package me.aidan.sydney.modules.impl.core;

import lombok.Getter;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import net.minecraft.util.Identifier;

@Getter
@RegisterModule(name = "Capes", description = "Applies the Sydney cape to yourself and to other users.", category = Module.Category.CORE, toggled = true, drawn = false)
public class CapesModule extends Module {
    public CapesModule() {
        this.capeTexture = Identifier.of(Sydney.MOD_ID, "textures/cape.png");
    }

    private final Identifier capeTexture;
}
