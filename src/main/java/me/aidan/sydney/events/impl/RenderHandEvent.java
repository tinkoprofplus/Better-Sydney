package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.aidan.sydney.events.Event;
import net.minecraft.client.render.VertexConsumerProvider;

@Getter @Setter @AllArgsConstructor
public class RenderHandEvent extends Event {
    private VertexConsumerProvider vertexConsumers;

    public static class Post extends Event {}
}
