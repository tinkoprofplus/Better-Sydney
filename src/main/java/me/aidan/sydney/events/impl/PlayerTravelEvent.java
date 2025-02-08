package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.util.math.Vec3d;

@Getter @AllArgsConstructor
public class PlayerTravelEvent extends Event {
    private final Vec3d movementInput;
}
