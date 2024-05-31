package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

//todo complete
public class BlockSuspiciousGravel extends BlockFallable {

    public static final BlockProperties $1 = new BlockProperties(SUSPICIOUS_GRAVEL, CommonBlockProperties.HANGING, CommonBlockProperties.BRUSHED_PROGRESS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSuspiciousGravel() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    
    public BlockSuspiciousGravel(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Suspicious Gravel";
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