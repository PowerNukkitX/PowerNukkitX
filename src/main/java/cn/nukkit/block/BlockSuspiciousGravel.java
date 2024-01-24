package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

//todo complete
public class BlockSuspiciousGravel extends BlockFallable {

    public static final BlockProperties PROPERTIES = new BlockProperties(SUSPICIOUS_GRAVEL, CommonBlockProperties.HANGING, CommonBlockProperties.BRUSHED_PROGRESS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSuspiciousGravel() {
        this(PROPERTIES.getDefaultState());
    }
    public BlockSuspiciousGravel(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Suspicious Gravel";
    }

    @Override
    public double getHardness() {
        return 0.25;
    }

    @Override
    public double getResistance() {
        return 1.25;
    }

    @Override
    protected EntityFallingBlock createFallingEntity(CompoundTag customNbt) {
        customNbt.putBoolean("BreakOnGround", true);
        return super.createFallingEntity(customNbt);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.AIR};
    }
}