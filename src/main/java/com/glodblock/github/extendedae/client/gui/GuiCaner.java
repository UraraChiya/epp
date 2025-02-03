package com.glodblock.github.extendedae.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.extendedae.api.CanerMode;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.container.ContainerCaner;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class GuiCaner extends AEBaseScreen<ContainerCaner> implements IActionHolder {

    private final Map<String, Consumer<Paras>> actions = createHolder();
    private final CycleEPPButton modeBtn;

    public GuiCaner(ContainerCaner menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.modeBtn = new CycleEPPButton();
        this.modeBtn.addActionPair(EPPIcon.FILLED, Component.translatable("gui.expatternprovider.caner.fill"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", CanerMode.EMPTY.ordinal())));
        this.modeBtn.addActionPair(EPPIcon.BUCKET, Component.translatable("gui.expatternprovider.caner.empty"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", CanerMode.FILL.ordinal())));
        this.actions.put("init", o -> this.modeBtn.setState(o.get(0)));
        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("update"));
        addToLeftToolbar(this.modeBtn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.modeBtn.setState(menu.getMode().ordinal());
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }
}
