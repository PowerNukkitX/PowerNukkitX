package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.StructureVoidType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.block.property.CommonBlockProperties.*;

/**
 * @author good777LUCKY
 */
public class BlockStructureVoid extends BlockSolid {

    public static final BlockProperties $1 = new BlockProperties(STRUCTURE_VOID, STRUCTURE_VOID_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockStructureVoid() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockStructureVoid(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Structure Void";
    }

    @NotNull public StructureVoidType getType() {
        return getPropertyValue(STRUCTURE_VOID_TYPE);
    }
    /**
     * @deprecated 
     */
    

    public void setType(@Nullable StructureVoidType type) {
        setPropertyValue(STRUCTURE_VOID_TYPE, type);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public  boolean canBePulled() {
        return false;
    }
}
