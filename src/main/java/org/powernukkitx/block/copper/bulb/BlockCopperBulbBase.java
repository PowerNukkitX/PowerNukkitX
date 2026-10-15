package org.powernukkitx.block.copper.bulb;

import org.powernukkitx.Player;
import org.powernukkitx.block.*;
import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.event.redstone.RedstoneUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BlockCopperBulbBase extends BlockSolid implements RedstoneComponent, Oxidizable, Waxable {
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(3)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .canBeActivated(true)
            .hasComparatorInputOverride(true)
            .build();

    public BlockCopperBulbBase(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockCopperBulbBase(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public int onUpdate(int type) {
        Oxidizable.super.onUpdate(type);

        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            RedstoneUpdateEvent ev = new RedstoneUpdateEvent(this);
            getLevel().getServer().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                return 0;
            }

            if (isGettingPower()) {
                this.setLit(!(getLit()));

                this.setPowered(true);
                this.getLevel().setBlock(this, this, true, true);
                return 1;
            }

            if(getPowered()) {
                this.setPowered(false);
                this.getLevel().setBlock(this, this, true, true);
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getComparatorInputOverride() {
        return getLit() ? 15 : 0;
    }

    public void setLit(boolean lit) {
        this.setPropertyValue(CommonBlockProperties.LIT, lit);
    }

    public void setPowered(boolean powered) {
        this.setPropertyValue(CommonBlockProperties.POWERED_BIT, powered);
    }

    public boolean getLit() {
        return getPropertyValue(CommonBlockProperties.LIT);
    }

    public boolean getPowered() {
        return getPropertyValue(CommonBlockProperties.POWERED_BIT);
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return Registries.BLOCK.getBlockProperties(getCopperId(isWaxed(), oxidizationLevel)).getDefaultState().toBlock();
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(isWaxed(), oxidizationLevel)));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) {
            return true;
        }
        return getValidLevel().setBlock(this, Block.get(getCopperId(waxed, getOxidizationLevel())));
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
            case UNAFFECTED -> waxed ? WAXED_COPPER_BULB : COPPER_BULB;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_BULB : EXPOSED_COPPER_BULB;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_BULB : WEATHERED_COPPER_BULB;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_BULB : OXIDIZED_COPPER_BULB;
        };
    }
}
