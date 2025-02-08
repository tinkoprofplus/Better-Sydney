package me.aidan.sydney.managers;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.events.SubscribeEvent;
import me.aidan.sydney.events.impl.TickEvent;
import me.aidan.sydney.utils.IMinecraft;

import java.util.ArrayList;

public class TaskManager implements IMinecraft {
    private final ArrayList<Runnable> tasks = new ArrayList<>();

    public TaskManager() {
        Sydney.EVENT_HANDLER.subscribe(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!tasks.isEmpty()) {
            tasks.getFirst().run();
            tasks.removeFirst();
        }
    }

    public void submit(Runnable runnable) {
        tasks.add(runnable);
    }
}