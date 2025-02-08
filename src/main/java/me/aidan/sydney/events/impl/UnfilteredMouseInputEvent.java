package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;

@Getter @AllArgsConstructor
public class UnfilteredMouseInputEvent extends Event {
    private final int button;
    private final int action;
    private final int mods;
}
