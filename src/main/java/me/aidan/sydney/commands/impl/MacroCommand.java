package me.aidan.sydney.commands.impl;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.commands.Command;
import me.aidan.sydney.commands.RegisterCommand;
import me.aidan.sydney.utils.chat.ChatUtils;
import me.aidan.sydney.utils.input.KeyboardUtils;

import java.util.Arrays;

@RegisterCommand(name = "macro", tag = "Macro", description = "Allows you to manage the client's macro system.", syntax = "add <[key]> <[message]> | remove <[key]> | <clear|list>")
public class MacroCommand extends Command {
    @Override
    public void execute(String[] args) {
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                int key = KeyboardUtils.getKeyNumber(args[1]);
                if (key == 0) {
                    Sydney.CHAT_MANAGER.tagged("The keybind specified is not valid.", getTag());
                    return;
                }

                StringBuilder builder = new StringBuilder();
                String[] array = Arrays.copyOfRange(args, 2, args.length);
                int index = 0;

                for (String str : array) {
                    index++;
                    builder.append(str).append(index == array.length ? "" : " ");
                }

                Sydney.MACRO_MANAGER.add(builder.toString(), key);
                Sydney.CHAT_MANAGER.tagged("Successfully added " + ChatUtils.getPrimary() + builder + ChatUtils.getSecondary() + " as a macro, bound to the " + ChatUtils.getPrimary() + KeyboardUtils.getKeyName(key) + ChatUtils.getSecondary() + " key.", getTag(), "command-" + getName());
            } else {
                messageSyntax();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                int key = KeyboardUtils.getKeyNumber(args[1]);
                if (key == 0) {
                    Sydney.CHAT_MANAGER.tagged("The keybind specified is not valid.", getTag());
                    return;
                }

                if (!Sydney.MACRO_MANAGER.containsValue(key)) {
                    Sydney.CHAT_MANAGER.tagged("There is no macro with the " + ChatUtils.getPrimary() + KeyboardUtils.getKeyName(key) + ChatUtils.getSecondary() + " key.", getTag(), "command-" + getName());
                } else {
                    Sydney.CHAT_MANAGER.tagged("Successfully removed the " + ChatUtils.getPrimary() + Sydney.MACRO_MANAGER.getKey(key) + ChatUtils.getSecondary() + " macro.", getTag(), "command-" + getName());
                    Sydney.MACRO_MANAGER.remove(Sydney.MACRO_MANAGER.getKey(key));
                }
            } else {
                messageSyntax();
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                Sydney.MACRO_MANAGER.clear();
                Sydney.CHAT_MANAGER.tagged("Successfully removed all macros.", getTag(), "command-" + getName());
            } else if (args[0].equalsIgnoreCase("list")) {
                if (Sydney.MACRO_MANAGER.getMacros().isEmpty()) {
                    Sydney.CHAT_MANAGER.tagged("There are currently no macros added.", getTag(), "command-" + getName() + "-list");
                } else {
                    StringBuilder builder = new StringBuilder();
                    int index = 0;

                    for (String message : Sydney.MACRO_MANAGER.getMacros().keySet()) {
                        index++;

                        int key = Sydney.MACRO_MANAGER.getValue(message);
                        builder.append(ChatUtils.getSecondary()).append(message)
                                .append(ChatUtils.getPrimary()).append(" [")
                                .append(ChatUtils.getSecondary()).append(KeyboardUtils.getKeyName(key))
                                .append(ChatUtils.getPrimary()).append("]")
                                .append(ChatUtils.getSecondary())
                                .append(index == Sydney.MACRO_MANAGER.getMacros().size() ? "" : ", ");
                    }

                    Sydney.CHAT_MANAGER.message("Macros " + ChatUtils.getPrimary() + "[" + ChatUtils.getSecondary() + Sydney.MACRO_MANAGER.getMacros().size() + ChatUtils.getPrimary() + "]: " + ChatUtils.getSecondary() + builder, "command-" + getName() + "-list");
                }
            } else {
                messageSyntax();
            }
        } else {
            messageSyntax();
        }
    }
}
