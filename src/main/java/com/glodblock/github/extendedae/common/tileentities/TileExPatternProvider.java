package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.stacks.AEItemKey;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.container.ContainerExPatternProvider;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileExPatternProvider extends PatternProviderBlockEntity {

    public TileExPatternProvider(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileExPatternProvider.class, TileExPatternProvider::new, EPPItemAndBlock.EX_PATTERN_PROVIDER), pos, blockState);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, 36);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(ContainerExPatternProvider.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExPatternProvider.TYPE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EPPItemAndBlock.EX_PATTERN_PROVIDER);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(EPPItemAndBlock.EX_PATTERN_PROVIDER);
    }

}
