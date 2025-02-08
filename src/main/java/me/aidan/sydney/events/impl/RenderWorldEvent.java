package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.client.util.math.MatrixStack;

@Getter @AllArgsConstructor
public class RenderWorldEvent extends Event {
    private final MatrixStack matrices;
    private final float tickDelta;

    @Getter @AllArgsConstructor
    public static class Post extends Event {
        private final MatrixStack matrices;
        private final float tickDelta;
    }
}
