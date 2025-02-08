package me.aidan.sydney.events.impl;

import lombok.*;
import me.aidan.sydney.events.Event;

@Getter @Setter @AllArgsConstructor
public class ChatInputEvent extends Event {
    private String message;
}
