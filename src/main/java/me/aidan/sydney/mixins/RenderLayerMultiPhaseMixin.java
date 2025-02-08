package me.aidan.sydney.mixins;

import me.aidan.sydney.utils.mixins.IMultiPhase;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderLayer.MultiPhase.class)
public class RenderLayerMultiPhaseMixin implements IMultiPhase {
    @Shadow
    @Final
    private RenderLayer.MultiPhaseParameters phases;

    @Override
    public RenderLayer.MultiPhaseParameters sydney$getParameters() {
        return this.phases;
    }
}
