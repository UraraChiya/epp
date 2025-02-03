package com.glodblock.github.extendedae.container;

import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.common.parts.PartModExportBus;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerModExportBus extends UpgradeableMenu<PartModExportBus> implements IActionHolder {
    private final Map<String, Consumer<Paras>> actions = createHolder();

    public static final MenuType<ContainerModExportBus> TYPE = MenuTypeBuilder
            .create(ContainerModExportBus::new, PartModExportBus.class)
            .build("mod_export_bus");

    @GuiSync(9)
    public String exp = "";

    public ContainerModExportBus(int id, Inventory ip, PartModExportBus host) {
        super(TYPE, id, ip, host);
        this.actions.put("set", o -> this.setExp(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EPPNetworkHandler.INSTANCE.sendTo(new SGenericPacket("init", this.exp), sp);
            }
        });
    }

    @Override
    protected void setupConfig() {
        // NO-OP
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.exp.equals(getHost().getModNameFilter())) {
            this.exp = getHost().getModNameFilter();
        }
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    public void setExp(String exp) {
        getHost().setModNameFilter(exp);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }


}
