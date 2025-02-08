package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;

@Getter @AllArgsConstructor
public class ChangePitchEvent extends Event {
    private final float pitch;
}
