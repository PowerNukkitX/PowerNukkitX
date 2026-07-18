package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class BlockCopperDoorBase extends BlockDoor implements Oxidizable, Waxable {
    public static final BlockDefinition DEFINITION = BlockDoor.DEFINITION.toBuilder()
            .hardness(3)
            .resistance(3)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .build();

    public BlockCopperDoorBase(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && !isTop()) {
            return Oxidizable.super.onUpdate(type);
        }
        return super.onUpdate(type);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null && player.isSneaking()) {
            return Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                    || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz);
        }

        return super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        String newId = getCopperId(isWaxed(), oxidizationLevel);
        Block newBottom = Registries.BLOCK.getBlockProperties(newId).getDefaultState().toBlock();
        copyDoorProperties(newBottom, false);
        
        // prevents onNormalUpdate from destroying the top half due to block-id mismatch
        if (!isTop()) {
            Block topPos = up();
            if (topPos instanceof BlockCopperDoorBase) {
                Block newTop = Block.get(newId);
                copyDoorProperties(newTop, true);
                getValidLevel().setBlock(topPos, newTop, true, false);
            }
        }
        return newBottom;
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) return true;
        if (isTop()) {
            Block bottom = down();
            if (bottom instanceof BlockCopperDoorBase) {
                return ((BlockCopperDoorBase) bottom).setOxidizationLevel(oxidizationLevel);
            }
            return false;
        }
        return updateBothHalves(getCopperId(isWaxed(), oxidizationLevel));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) return true;
        if (isTop()) {
            Block bottom = down();
            if (bottom instanceof BlockCopperDoorBase) {
                return ((BlockCopperDoorBase) bottom).setWaxed(waxed);
            }
            return false;
        }
        return updateBothHalves(getCopperId(waxed, getOxidizationLevel()));
    }

    @Override
    public boolean isWaxed() {
        return false;
    }

    private void copyDoorProperties(Block target, boolean top) {
        target.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
                getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
        target.setPropertyValue(CommonBlockProperties.OPEN_BIT,
                getPropertyValue(CommonBlockProperties.OPEN_BIT));
        target.setPropertyValue(CommonBlockProperties.DOOR_HINGE_BIT,
                getPropertyValue(CommonBlockProperties.DOOR_HINGE_BIT));
        target.setPropertyValue(CommonBlockProperties.UPPER_BLOCK_BIT, top);
    }

    private boolean updateBothHalves(String newId) {
        Block topPos = up();
        Block newBottom = Block.get(newId);
        copyDoorProperties(newBottom, false);
        Block newTop = Block.get(newId);
        copyDoorProperties(newTop, true);
        getValidLevel().setBlock(this, newBottom, true, false);
        getValidLevel().setBlock(topPos, newTop, true, true);
        return true;
    }

    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_COPPER_DOOR : COPPER_DOOR;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_DOOR : EXPOSED_COPPER_DOOR;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_DOOR : WEATHERED_COPPER_DOOR;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_DOOR : OXIDIZED_COPPER_DOOR;
        };
    }
}