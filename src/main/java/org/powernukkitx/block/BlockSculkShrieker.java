package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySculkShrieker;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.ACTIVE;


public class BlockSculkShrieker extends BlockFlowable implements BlockEntityHolder<BlockEntitySculkShrieker> {

    public static final BlockProperties PROPERTIES = new BlockProperties(SCULK_SHRIEKER, CommonBlockProperties.ACTIVE, CommonBlockProperties.CAN_SUMMON);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .hardness(3.0)
            .resistance(3)
            .toolType(ItemTool.TYPE_HOE)
            .canPassThrough(false)
            .canBePushed(false)
            .canBePulled(false)
            .breaksWhenMoved(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSculkShrieker() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSculkShrieker(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Sculk Shrieker";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{Block.get(SCULK_SHRIEKER).toItem()};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public int getDropExp() {
        return 5;
    }

    @Override
    @NotNull public Class<? extends BlockEntitySculkShrieker> getBlockEntityClass() {
        return BlockEntitySculkShrieker.class;
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.SCULK_SHRIEKER;
    }

    
    @Override
    public void onEntityStepOn(Entity entity) {
        if (entity instanceof Player player) {
            getOrCreateBlockEntity().tryShriek(player);
        }
    }

    public boolean isShrieking() {
        return getPropertyValue(ACTIVE);
    }

    public void setShrieking(boolean shrieking) {
        this.setPropertyValue(ACTIVE, shrieking);
        this.level.setBlock(this, this, true, false);
        if (shrieking) {
            this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.SHRIEK_SCULK_SHRIEKER);
        }
    }

    
    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}
