package me.aidan.sydney.utils.chat;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.impl.core.CommandsModule;
import me.aidan.sydney.utils.text.FormattingUtils;
import net.minecraft.util.StringIdentifiable;

public class ChatUtils {
    public static StringIdentifiable getPrimary() {
        return FormattingUtils.getFormatting(Sydney.MODULE_MANAGER.getModule(CommandsModule.class).primaryMessageColor.getValue());
    }

    public static StringIdentifiable getSecondary() {
        return FormattingUtils.getFormatting(Sydney.MODULE_MANAGER.getModule(CommandsModule.class).secondaryMessageColor.getValue());
    }
}
