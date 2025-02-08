package me.aidan.sydney.utils.miscellaneous;

import lombok.Getter;
import lombok.Setter;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.impl.core.RendersModule;
import me.aidan.sydney.utils.animations.Easing;
import net.minecraft.util.math.BlockPos;

@Setter
@Getter
public class RenderPosition {
    private BlockPos pos;
    private long startTime;

    public RenderPosition(BlockPos pos) {
        this.pos = pos;
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof RenderPosition) return ((RenderPosition) o).pos.equals(this.pos);
        return false;
    }

    public float get() {
        return Easing.ease(1.0f - Easing.toDelta(startTime, Sydney.MODULE_MANAGER.getModule(RendersModule.class).duration.getValue().intValue()), Easing.Method.EASE_IN_CUBIC);
    }
}
