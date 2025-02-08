package me.aidan.sydney.mixins;

import me.aidan.sydney.utils.mixins.IMultiPhaseParameters;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public abstract class RenderLayerMultiPhaseParametersMixin implements IMultiPhaseParameters {
    @Shadow @Final private RenderPhase.Target target;

    @Override
    public RenderPhase.Target sydney$getTarget() {
        return this.target;
    }
}
