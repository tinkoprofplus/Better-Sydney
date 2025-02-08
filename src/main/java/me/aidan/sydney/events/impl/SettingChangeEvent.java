package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import me.aidan.sydney.settings.Setting;

@Getter @AllArgsConstructor
public class SettingChangeEvent extends Event {
    private final Setting setting;
}
