package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Getter @AllArgsConstructor
public class AttackEntityEvent extends Event {
    private final PlayerEntity player;
    private final Entity target;
}
