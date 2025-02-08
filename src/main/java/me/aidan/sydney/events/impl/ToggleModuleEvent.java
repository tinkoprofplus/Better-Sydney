package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import me.aidan.sydney.modules.Module;

@AllArgsConstructor @Getter
public class ToggleModuleEvent extends Event {
    private final Module module;
    private final boolean state;
}
