package io.github.itzispyder.clickcrystals.mixins;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.itzispyder.clickcrystals.ClickCrystals;
import io.github.itzispyder.clickcrystals.commands.Command;
import io.github.itzispyder.clickcrystals.events.events.ChatCommandEvent;
import io.github.itzispyder.clickcrystals.events.events.ChatSendEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.itzispyder.clickcrystals.ClickCrystals.system;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String content, CallbackInfo ci) {
        String prefix = ClickCrystals.commandPrefix.getKeyName();
        prefix = prefix.equals("NONE") ? "'" : prefix;

        if (content.startsWith(prefix)) {
            if (content.startsWith(prefix + prefix)) {
                return;
            }

            try {
                Command.dispatch(content.substring(prefix.length()));
            }
            catch (CommandSyntaxException ex) {
                Command.error(ex.getMessage());
            }
            ci.cancel();
            return;
        }

        ChatSendEvent event = new ChatSendEvent(content);
        system.eventBus.pass(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    public void sendCommand(String command, CallbackInfo ci) {
        ChatCommandEvent event = new ChatCommandEvent(command);
        system.eventBus.pass(event);
        if (event.isCancelled()) ci.cancel();
    }
}