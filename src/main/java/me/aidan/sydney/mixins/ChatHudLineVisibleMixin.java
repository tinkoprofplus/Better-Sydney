package me.aidan.sydney.mixins;

import me.aidan.sydney.utils.mixins.IChatHudLineVisible;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.Visible.class)
public class ChatHudLineVisibleMixin implements IChatHudLineVisible {
    @Unique private boolean clientMessage = false;
    @Unique private String clientIdentifier = "";

    @Override
    public boolean sydney$isClientMessage() {
        return clientMessage;
    }

    @Override
    public void sydney$setClientMessage(boolean clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public String sydney$getClientIdentifier() {
        return clientIdentifier;
    }

    @Override
    public void sydney$setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }
}