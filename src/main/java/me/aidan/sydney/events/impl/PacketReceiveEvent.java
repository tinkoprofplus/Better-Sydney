package me.aidan.sydney.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aidan.sydney.events.Event;
import net.minecraft.network.packet.Packet;

@Getter @AllArgsConstructor
public class PacketReceiveEvent extends Event {
    private final Packet<?> packet;
}
