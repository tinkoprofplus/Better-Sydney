package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.entity.projectile.FireworkRocketEntity;

@Getter @AllArgsConstructor
public class RemoveFireworkEvent extends Event {
    private final FireworkRocketEntity entity;
}
