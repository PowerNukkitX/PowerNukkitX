package cn.nukkit.block;

import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;

public class BlockSuspiciousSand extends BlockFallable {
    public static final BlockProperties $1 = new BlockProperties(SUSPICIOUS_SAND, HANGING, BRUSHED_PROGRESS);
    /**
     * @deprecated 
     */
    

    public BlockSuspiciousSand() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSuspiciousSand(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Suspicious Sand";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.25;
    }

    @Override
    /**
     * @deprecated 
     */
    
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