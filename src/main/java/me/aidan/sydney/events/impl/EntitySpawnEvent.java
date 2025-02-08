package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.entity.Entity;

@AllArgsConstructor @Getter
public class EntitySpawnEvent extends Event {
    private final Entity entity;
}
