package me.aidan.sydney.modules.impl.core;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.modules.RegisterModule;
import me.aidan.sydney.settings.impl.BooleanSetting;
import me.aidan.sydney.settings.impl.CategorySetting;
import me.aidan.sydney.settings.impl.ModeSetting;
import me.aidan.sydney.settings.impl.StringSetting;
import me.aidan.sydney.utils.text.FormattingUtils;

@RegisterModule(name = "Commands", description = "Manages the client's command and chat message system.", category = Module.Category.CORE, persistent = true, drawn = false)
public class CommandsModule extends Module {
    public CategorySetting watermarkCategory = new CategorySetting("Watermark", "The category that contains all of the settings for the chat watermark.");
    public BooleanSetting watermark = new BooleanSetting("Watermark", "Enabled", "Whether or not to have the client's watermark show up before every chat message.", new CategorySetting.Visibility(watermarkCategory), true);
    public StringSetting watermarkText = new StringSetting("WatermarkText", "Text", "The text that will be shown as the watermark.", new CategorySetting.Visibility(watermarkCategory), Sydney.MOD_NAME);
    public StringSetting opening = new StringSetting("Opening", "The symbol that will be placed before the watermark text.", new CategorySetting.Visibility(watermarkCategory), "[");
    public StringSetting closing = new StringSetting("Closing", "The symbol that will be placed after the watermark text.", new CategorySetting.Visibility(watermarkCategory), "]");
    public ModeSetting primaryWatermarkColor = new ModeSetting("PrimaryWatermarkColor", "Primary", "The primary color for the watermark.", new CategorySetting.Visibility(watermarkCategory), "Client", FormattingUtils.FORMATS);
    public ModeSetting secondaryWatermarkColor = new ModeSetting("SecondaryWatermarkColor", "Secondary", "The secondary color for the watermark.", new CategorySetting.Visibility(watermarkCategory), "Dark Gray", FormattingUtils.FORMATS);

    public CategorySetting messageCategory = new CategorySetting("Message", "The category that contains all of the settings for the chat messages themselves.");
    public ModeSetting primaryMessageColor = new ModeSetting("PrimaryMessageColor", "Primary", "The primary color for the message.", new CategorySetting.Visibility(messageCategory), "Client", FormattingUtils.FORMATS);
    public ModeSetting secondaryMessageColor = new ModeSetting("SecondaryMessageColor", "Secondary", "The secondary color for the message.", new CategorySetting.Visibility(messageCategory), "White", FormattingUtils.FORMATS);
}
