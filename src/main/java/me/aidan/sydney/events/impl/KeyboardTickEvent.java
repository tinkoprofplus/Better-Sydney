package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.aidan.sydney.events.Event;

@AllArgsConstructor @Getter @Setter
public class KeyboardTickEvent extends Event {
    private float movementForward;
    private float movementSideways;
}
