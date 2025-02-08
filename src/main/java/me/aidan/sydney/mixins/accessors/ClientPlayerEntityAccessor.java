package me.aidan.sydney.mixins.accessors;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {
    @Invoker("isWalking")
    boolean invokeIsWalking();

    @Invoker("canSprint")
    boolean invokeCanSprint();

    @Accessor("lastOnGround")
    void setLastOnGround(boolean lastOnGround);
}
