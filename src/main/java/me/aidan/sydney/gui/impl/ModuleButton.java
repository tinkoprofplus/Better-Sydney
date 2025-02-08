package me.aidan.sydney.gui.impl;

import lombok.Getter;
import lombok.Setter;
import me.aidan.sydney.Sydney;
import me.aidan.sydney.gui.ClickGuiScreen;
import me.aidan.sydney.modules.Module;
import me.aidan.sydney.gui.api.Button;
import me.aidan.sydney.gui.api.Frame;
import me.aidan.sydney.settings.Setting;
import me.aidan.sydney.settings.impl.*;
import me.aidan.sydney.utils.color.ColorUtils;
import me.aidan.sydney.utils.graphics.Renderer2D;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;

@Getter @Setter
public class ModuleButton extends Button {
    private final Module module;
    private boolean open = false;
    private final ArrayList<Button> buttons = new ArrayList<>();

    public ModuleButton(Module module, Frame parent, int height) {
        super(parent, height, module.getDescription());
        this.module = module;

        for(Setting setting : module.getSettings()) {
            if(setting instanceof BooleanSetting s) {
                buttons.add(new BooleanButton(s, parent, height));
            } else if(setting instanceof NumberSetting s) {
                buttons.add(new NumberButton(s, parent, height));
            } else if(setting instanceof CategorySetting s) {
                buttons.add(new CategoryButton(s, parent, height));
            } else if(setting instanceof BindSetting s) {
                buttons.add(new BindButton(s, parent, height));
            } else if(setting instanceof ModeSetting s) {
                buttons.add(new ModeButton(s, parent, height));
            } else if(setting instanceof WhitelistSetting s) {
                buttons.add(new WhitelistButton(s, parent, height));
            } else if(setting instanceof StringSetting s) {
                buttons.add(new StringButton(s, parent, height));
            } else if(setting instanceof ColorSetting s) {
                buttons.add(new ColorButton(s, parent, height));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(this.isHovering(mouseX, mouseY) && Sydney.CLICK_GUI.getDescriptionFrame().getDescription().isEmpty()) Sydney.CLICK_GUI.getDescriptionFrame().setDescription(this.getDescription());

        Renderer2D.renderQuad(context.getMatrices(), getX() + getPadding(), getY(), getX() + getWidth() - getPadding(), getY() + getHeight() - 1, ClickGuiScreen.getButtonColor(getY(), module.isToggled() ? 80 : 30));
        Sydney.FONT_MANAGER.drawTextWithShadow(context, (module.isToggled() ? "" : Formatting.GRAY ) + module.getName(), getX() + getTextPadding(), getY() + 2, Color.WHITE);

        if(open) {
            for(Button button : buttons) {
                if(!button.isVisible()) continue;
                button.render(context, mouseX, mouseY, delta);
                if(button.isHovering(mouseX, mouseY) && Sydney.CLICK_GUI.getDescriptionFrame().getDescription().isEmpty()) Sydney.CLICK_GUI.getDescriptionFrame().setDescription(button.getDescription());
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if(isHovering(mouseX, mouseY)) {
            if(button == 0) {
                module.setToggled(!module.isToggled());
                playClickSound();
            } else if(button == 1) {
                open = !open;
                playClickSound();
            }
        }

        if(open) {
            for(Button b : buttons) {
                if(!b.isVisible()) continue;
                b.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        for(Button b : buttons) {
            b.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Button b : buttons) {
            b.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if(open) {
            for(Button b : buttons) {
                if(!b.isVisible()) continue;
                b.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        if(open) {
            for(Button b : buttons) {
                if(!b.isVisible()) continue;
                b.charTyped(chr, modifiers);
            }
        }
    }
}
