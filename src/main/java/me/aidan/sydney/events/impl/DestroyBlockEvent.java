package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.util.math.BlockPos;

@AllArgsConstructor @Getter
public class DestroyBlockEvent extends Event {
    private final BlockPos position;
}
