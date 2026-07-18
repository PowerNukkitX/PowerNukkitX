package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockCopperTrapdoor extends BlockTrapdoor implements Oxidizable, Waxable {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);
    public static final BlockDefinition DEFINITION = BlockTrapdoor.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperTrapdoor(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockCopperTrapdoor(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Copper Trapdoor";
    }

    @Override
    public int onUpdate(int type) {
        int result = Oxidizable.super.onUpdate(type);
        if (result != 0) return result;
        return super.onUpdate(type);
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (player != null && player.isSneaking()) {
            if (Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                    || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz)) {
                return true;
            }
        }
        return super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return withCurrentProperties(Registries.BLOCK.getBlockProperties(getCopperId(isWaxed(), oxidizationLevel)).getDefaultState().toBlock());
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) return true;
        return getValidLevel().setBlock(this, withCurrentProperties(Block.get(getCopperId(isWaxed(), oxidizationLevel))));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) return true;
        return getValidLevel().setBlock(this, withCurrentProperties(Block.get(getCopperId(waxed, getOxidizationLevel()))));
    }

    private Block withCurrentProperties(Block newBlock) {
        newBlock.setPropertyValue(CommonBlockProperties.DIRECTION, getPropertyValue(CommonBlockProperties.DIRECTION));
        newBlock.setPropertyValue(CommonBlockProperties.OPEN_BIT, getPropertyValue(CommonBlockProperties.OPEN_BIT));
        newBlock.setPropertyValue(CommonBlockProperties.UPSIDE_DOWN_BIT, getPropertyValue(CommonBlockProperties.UPSIDE_DOWN_BIT));
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
            case UNAFFECTED -> waxed ? WAXED_COPPER_TRAPDOOR : COPPER_TRAPDOOR;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_TRAPDOOR : EXPOSED_COPPER_TRAPDOOR;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_TRAPDOOR : WEATHERED_COPPER_TRAPDOOR;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_TRAPDOOR : OXIDIZED_COPPER_TRAPDOOR;
        };
    }
}