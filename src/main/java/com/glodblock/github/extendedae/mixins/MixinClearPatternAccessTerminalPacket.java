package com.glodblock.github.extendedae.mixins;

import appeng.core.sync.packets.ClearPatternAccessTerminalPacket;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClearPatternAccessTerminalPacket.class)
public abstract class MixinClearPatternAccessTerminalPacket {

    @Inject(
            method = "clientPacketData",
            at = @At("HEAD"),
            remap = false,
            cancellable = true
    )
    private void handleExGui(Player player, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof GuiExPatternTerminal patternAccessTerminal) {
            patternAccessTerminal.clear();
            ci.cancel();
        }
    }

}
