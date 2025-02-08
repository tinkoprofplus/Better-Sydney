package me.aidan.sydney.utils.mixins;

public interface IChatHudLineVisible {
    boolean sydney$isClientMessage();

    void sydney$setClientMessage(boolean clientMessage);

    String sydney$getClientIdentifier();

    void sydney$setClientIdentifier(String clientIdentifier);
}