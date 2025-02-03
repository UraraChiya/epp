package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.RedstoneMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.Tooltips;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import com.glodblock.github.extendedae.api.ThresholdMode;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.client.gui.subgui.SetAmount;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.container.ContainerThresholdExportBus;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class GuiThresholdExportBus extends UpgradeableScreen<ContainerThresholdExportBus> implements IActionHolder {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<SchedulingMode> schedulingMode;
    private final CycleEPPButton thresholdMode;
    private final Map<String, Consumer<Paras>> actions = createHolder();

    public GuiThresholdExportBus(ContainerThresholdExportBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);

        if (menu.getHost().getConfigManager().hasSetting(Settings.SCHEDULING_MODE)) {
            this.schedulingMode = new ServerSettingToggleButton<>(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
            addToLeftToolbar(this.schedulingMode);
        } else {
            this.schedulingMode = null;
        }

        this.thresholdMode = new CycleEPPButton();
        this.thresholdMode.addActionPair(EPPIcon.OVER_STACK, Component.translatable("gui.expatternprovider.threshold_export_bus.greater"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", ThresholdMode.LOWER.ordinal())));
        this.thresholdMode.addActionPair(EPPIcon.BELOW_STACK, Component.translatable("gui.expatternprovider.threshold_export_bus.lower"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", ThresholdMode.GREATER.ordinal())));
        this.actions.put("init", o -> this.thresholdMode.setState(o.get(0)));
        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("update"));
        addToLeftToolbar(this.thresholdMode);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        if (this.schedulingMode != null) {
            this.schedulingMode.set(menu.getSchedulingMode());
        }
        this.thresholdMode.setState(menu.getMode().ordinal());
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        assert this.minecraft != null;
        if (this.minecraft.options.keyPickItem.matchesMouse(btn)) {
            var slot = Ae2ReflectClient.getSlot(this, xCoord, yCoord);
            if (isValidSlot(slot)) {
                var currentStack = GenericStack.fromItemStack(slot.getItem());
                if (currentStack != null) {
                    var screen = new SetAmount<>(
                            this,
                            new ItemStack(EPPItemAndBlock.THRESHOLD_EXPORT_BUS),
                            currentStack,
                            newStack -> NetworkHandler.instance().sendToServer(new InventoryActionPacket(
                                    InventoryAction.SET_FILTER, slot.index,
                                    GenericStack.wrapInItemStack(newStack))),
                            false);
                    switchToScreen(screen);
                    return true;
                }
            }
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.isValidSlot(this.hoveredSlot)) {
            var itemTooltip = new ArrayList<>(getTooltipFromContainerItem(this.hoveredSlot.getItem()));
            var unwrapped = GenericStack.fromItemStack(this.hoveredSlot.getItem());
            if (unwrapped != null) {
                itemTooltip.add(Tooltips.getAmountTooltip(ButtonToolTips.Amount, unwrapped));
            }
            itemTooltip.add(Tooltips.getSetAmountTooltip());
            drawTooltip(guiGraphics, x, y, itemTooltip);
        } else {
            super.renderTooltip(guiGraphics, x, y);
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(10, 17, 0);
        poseStack.scale(0.6f, 0.6f, 1);
        var color = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        guiGraphics.drawString(font, Component.translatable("gui.expatternprovider.precise_export_bus.set_amount"), 0, 0, color.toARGB(), false);
        poseStack.popPose();
    }

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isConfigSlot(slot);
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }
}
