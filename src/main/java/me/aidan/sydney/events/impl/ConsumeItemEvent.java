package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.item.ItemStack;

@Getter @AllArgsConstructor
public class ConsumeItemEvent extends Event {
    private final ItemStack stack;
}
