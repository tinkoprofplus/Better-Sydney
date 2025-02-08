package me.aidan.sydney.events.impl;

import lombok.*;
import me.aidan.sydney.events.Event;

@EqualsAndHashCode(callSuper = true) @Data
public class KeyInputEvent extends Event {
    private final int key, modifiers;
}
