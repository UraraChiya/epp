package com.glodblock.github.extendedae.common.inventory;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.glodblock.github.extendedae.common.items.InfinityCell;
import com.glodblock.github.extendedae.config.EPPConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InfinityCellInventory implements StorageCell {

    private final ItemStack stack;
    private final AEKey record;
    public static final ICellHandler HANDLER = new Handler();

    public InfinityCellInventory(ItemStack stack) {
        if (!(stack.getItem() instanceof InfinityCell cell)) {
            throw new IllegalArgumentException("Cell isn't an infinity cell!");
        }
        this.stack = stack;
        this.record = cell.getRecord(stack);
    }

    @Override
    public CellState getStatus() {
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return EPPConfig.infCellCost;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (this.record.equals(what)) {
            return amount;
        }
        return 0;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (this.record.equals(what)) {
            return amount;
        }
        return 0;
    }

    @Override
    public void persist() {
        // NO-OP
    }

    @Override
    public Component getDescription() {
        return this.stack.getHoverName();
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        out.add(this.record, InfinityCell.getAsIntMax(this.record));
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return this.record.equals(what);
    }

    private static class Handler implements ICellHandler {

        @Override
        public boolean isCell(ItemStack is) {
            return is != null && is.getItem() instanceof InfinityCell;
        }

        @Override
        public @Nullable StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
            return isCell(is) ? new InfinityCellInventory(is) : null;
        }
    }

}
