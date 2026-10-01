package org.powernukkitx.block.copper.chest;

import org.powernukkitx.Player;
import org.powernukkitx.block.*;
import org.powernukkitx.block.definition.BlockDefinition;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockCopperChest extends BlockChest implements Waxable, Oxidizable {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    public static final BlockDefinition DEFINITION = BlockChest.DEFINITION.toBuilder()
            .hardness(3)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_STONE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperChest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperChest(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    public BlockCopperChest(BlockState blockstate, BlockDefinition definition) {
        super(blockstate, definition);
    }

    @Override
    public String getName() {
        return "Copper Chest";
    }

    @Override
    public int onUpdate(int type) {
        return Oxidizable.super.onUpdate(type);
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (Waxable.super.onActivate(item, player, blockFace, fx, fy, fz)
                || Oxidizable.super.onActivate(item, player, blockFace, fx, fy, fz)) {
            return true;
        }
        return super.onActivate(item, player, blockFace, fx, fy, fz);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public Block getBlockWithOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        return withDirection(Registries.BLOCK.getBlockProperties(getCopperId(isWaxed(), oxidizationLevel)).getDefaultState().toBlock());
    }

    @Override
    public boolean setOxidizationLevel(@NotNull OxidizationLevel oxidizationLevel) {
        if (getOxidizationLevel().equals(oxidizationLevel)) return true;
        return getValidLevel().setBlock(this, withDirection(Block.get(getCopperId(isWaxed(), oxidizationLevel))));
    }

    @Override
    public boolean setWaxed(boolean waxed) {
        if (isWaxed() == waxed) return true;
        return getValidLevel().setBlock(this, withDirection(Block.get(getCopperId(waxed, getOxidizationLevel()))));
    }

    private Block withDirection(Block newBlock) {
        newBlock.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
                getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
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
            case UNAFFECTED -> waxed ? WAXED_COPPER_CHEST : COPPER_CHEST;
            case EXPOSED -> waxed ? WAXED_EXPOSED_COPPER_CHEST : EXPOSED_COPPER_CHEST;
            case WEATHERED -> waxed ? WAXED_WEATHERED_COPPER_CHEST : WEATHERED_COPPER_CHEST;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_COPPER_CHEST : OXIDIZED_COPPER_CHEST;
        };
    }
}
