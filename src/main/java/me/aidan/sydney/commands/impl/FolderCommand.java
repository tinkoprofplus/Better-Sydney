package me.aidan.sydney.commands.impl;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.commands.Command;
import me.aidan.sydney.commands.RegisterCommand;
import net.minecraft.util.Util;

import java.io.File;

@RegisterCommand(name = "folder", description = "Opens the clients folder.")
public class FolderCommand extends Command {
    @Override
    public void execute(String[] args) {
        File folder = new File(Sydney.MOD_NAME);
        if (folder.exists()) {
            Util.getOperatingSystem().open(folder);
        } else {
            Sydney.CHAT_MANAGER.info("Could not find the client's configuration folder.");
        }
    }
}
