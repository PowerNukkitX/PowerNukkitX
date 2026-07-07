package org.powernukkitx.block.copper.lightningrod;

import org.powernukkitx.Player;
import org.powernukkitx.block.*;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTransparent;
import org.powernukkitx.block.Oxidizable;
import org.powernukkitx.block.Waxable;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockLightningRod extends BlockTransparent implements Faceable, Waxable, Oxidizable {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHTNING_ROD, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.POWERED_BIT);

    public BlockLightningRod() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockLightningRod(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Lightning Rod";
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        this.setBlockFace(face);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace face, float fx, float fy, float fz) {
        return Waxable.super.onActivate(item, player, face, fx, fy, fz)
                || Oxidizable.super.onActivate(item, player, face, fx, fy, fz);
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return withFacing(Registries.BLOCK.getBlockProperties(getCopperId(isWaxed(), oxidizationLevel)).getDefaultState().toBlock());
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) return true;
        return getValidLevel().setBlock(this, withFacing(Block.get(getCopperId(isWaxed(), oxidizationLevel))));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) return true;
        return getValidLevel().setBlock(this, withFacing(Block.get(getCopperId(waxed, getOxidizationLevel()))));
    }

    private Block withFacing(Block newBlock) {
        newBlock.setPropertyValue(CommonBlockProperties.FACING_DIRECTION,
                getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
        return newBlock;
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_LIGHTNING_ROD : LIGHTNING_ROD;
            case EXPOSED -> waxed ? WAXED_EXPOSED_LIGHTNING_ROD : EXPOSED_LIGHTNING_ROD;
            case WEATHERED -> waxed ? WAXED_WEATHERED_LIGHTNING_ROD : WEATHERED_LIGHTNING_ROD;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_LIGHTNING_ROD : OXIDIZED_LIGHTNING_ROD;
        };
    }
}
