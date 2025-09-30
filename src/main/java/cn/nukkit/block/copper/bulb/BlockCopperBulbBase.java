package cn.nukkit.block.copper.bulb;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.RedstoneComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BlockCopperBulbBase extends BlockSolid implements RedstoneComponent, Oxidizable, Waxable {
    public BlockCopperBulbBase(BlockState blockState) {
        super(blockState);
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public boolean hasComparatorInputOverride() {
        return true;
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
