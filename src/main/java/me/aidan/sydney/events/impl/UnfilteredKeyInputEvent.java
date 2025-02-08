package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;

@Getter @AllArgsConstructor
public class UnfilteredKeyInputEvent extends Event {
    private final int key;
    private final int scancode;
    private final int action;
    private final int modifiers;
}
