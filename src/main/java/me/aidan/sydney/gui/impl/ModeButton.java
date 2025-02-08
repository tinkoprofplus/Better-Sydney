package me.aidan.sydney.gui.impl;

import me.aidan.sydney.Sydney;
import me.aidan.sydney.gui.ClickGuiScreen;
import me.aidan.sydney.gui.api.Button;
import me.aidan.sydney.gui.api.Frame;
import me.aidan.sydney.settings.impl.ModeSetting;
import me.aidan.sydney.utils.color.ColorUtils;
import me.aidan.sydney.utils.graphics.Renderer2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class ModeButton extends Button {
    private final ModeSetting setting;
    private boolean open = false;

    public ModeButton(ModeSetting setting, Frame parent, int height) {
        super(setting, parent, height, setting.getDescription());
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Renderer2D.renderQuad(context.getMatrices(), getX() + getPadding() + 1, getY(), getX() + getWidth() - getPadding() - 1, getY() + getParent().getHeight() - 1, ClickGuiScreen.getButtonColor(getY(), 100));
        Renderer2D.renderQuad(context.getMatrices(), getX() + getPadding() + 1, getY(), getX() + getPadding() + 2, getY() + getHeight() - 1, ClickGuiScreen.getButtonColor(getY(), 255));

        Sydney.FONT_MANAGER.drawTextWithShadow(context, setting.getTag(), getX() + getTextPadding() + 1, getY() + 2, Color.WHITE);
        Sydney.FONT_MANAGER.drawTextWithShadow(context, Formatting.GRAY + setting.getValue(), getX() + getWidth() - getTextPadding() - 1 - Sydney.FONT_MANAGER.getWidth(setting.getValue()), getY() + 2, Color.WHITE);

        if(open) {
            int i = 0;
            for(String s : setting.getModes()) {
                Sydney.FONT_MANAGER.drawTextWithShadow(context, (setting.getValue().equals(s) ? "" : Formatting.GRAY) + s, getX() + getTextPadding() + 2, getY() + getParent().getHeight() + i + 2, Color.WHITE);
                i += getParent().getHeight();
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if(isHovering(mouseX, mouseY)) {
            if(button == 0) {
                int choice = setting.getModes().indexOf(setting.getValue());
                choice ++;
                if(choice > setting.getModes().size() - 1) choice = 0;

                setting.setValue(setting.getModes().get(choice));
                playClickSound();
            } else if(button == 1) {
                open = !open;
            }
        }

        if(open && isHoveringModes(mouseX, mouseY)) {
            int choice = MathHelper.clamp((int)(mouseY - getY() - getParent().getHeight())/getParent().getHeight(), 0, setting.getModes().size() - 1);
            setting.setValue(setting.getModes().get(choice));
        }
    }

    @Override
    public int getHeight() {
        return getParent().getHeight() + (open ? getParent().getHeight() * setting.getModes().size() : 0);
    }

    @Override
    public boolean isHovering(double mouseX, double mouseY) {
        return getX() + getPadding() <= mouseX && getY() <= mouseY && getX() + getWidth() - getPadding() > mouseX && getY() + getParent().getHeight() > mouseY;
    }

    public boolean isHoveringModes(double mouseX, double mouseY) {
        int modesHeight = getParent().getHeight() * setting.getModes().size();
        return getX() + getPadding() <= mouseX && getY() + getParent().getHeight() <= mouseY && getX() + getWidth() - getPadding() > mouseX && getY() + getParent().getHeight() + modesHeight > mouseY;
    }
}
