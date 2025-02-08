package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.aidan.sydney.events.Event;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.Entity;

@AllArgsConstructor @Getter @Setter
public class RenderEntityEvent extends Event {
    private final Entity entity;
    private VertexConsumerProvider vertexConsumers;

    public static class Post extends Event {}
}
