package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PlayerConnectEvent extends Event {
    private final UUID id;
}
