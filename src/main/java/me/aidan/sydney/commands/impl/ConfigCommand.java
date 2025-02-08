package me.aidan.sydney.commands.impl;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.commands.Command;
import me.aidan.sydney.commands.RegisterCommand;
import me.aidan.sydney.utils.chat.ChatUtils;
import me.aidan.sydney.utils.system.FileUtils;

import java.io.IOException;

@RegisterCommand(name = "config", tag = "Config", description = "Allows you to manage the client's configuration system.", syntax = "<load|save> <[name]> | <reload|save|current>")
public class ConfigCommand extends Command {
    @Override
    public void execute(String[] args) {
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "load" -> {
                    if (!FileUtils.fileExists(Sydney.MOD_NAME + "/Configs/" + args[1] + ".json")) {
                        Sydney.CHAT_MANAGER.tagged("The specified configuration does not exist.", getTag(), getName());
                        return;
                    }

                    try {
                        Sydney.CONFIG_MANAGER.loadModules(args[1]);
                        Sydney.CHAT_MANAGER.tagged("Successfully loaded the " + ChatUtils.getPrimary() + args[1] + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                    } catch (IOException exception) {
                        Sydney.CHAT_MANAGER.tagged("Failed to load the " + ChatUtils.getPrimary() + args[1] + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                    }
                }
                case "save" -> {
                    try {
                        Sydney.CONFIG_MANAGER.saveModules(args[1]);
                        Sydney.CHAT_MANAGER.tagged("Successfully saved the configuration to " + ChatUtils.getPrimary() + args[1] + ".json" + ChatUtils.getSecondary() + ".", getTag(), getName());
                    } catch (IOException exception) {
                        Sydney.CHAT_MANAGER.tagged("Failed to save the " + ChatUtils.getPrimary() + args[1] + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                    }
                }
                default -> messageSyntax();
            }
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    Sydney.CONFIG_MANAGER.loadConfig();
                    Sydney.CHAT_MANAGER.tagged("Successfully reloaded the current configuration.", getTag(), getName());
                }
                case "save" -> {
                    Sydney.CONFIG_MANAGER.saveConfig();
                    Sydney.CHAT_MANAGER.tagged("Successfully saved the current configuration.", getTag(), getName());
                }
                case "current" -> Sydney.CHAT_MANAGER.tagged("The client is currently using the " + ChatUtils.getPrimary() + Sydney.CONFIG_MANAGER.getCurrentConfig() + ChatUtils.getSecondary() + " configuration.", getTag(), getName());
                default -> messageSyntax();
            }
        } else {
            messageSyntax();
        }
    }
}
