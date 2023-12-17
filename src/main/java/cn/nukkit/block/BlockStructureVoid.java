package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StructureVoidType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * @author good777LUCKY
 */


public class BlockStructureVoid extends BlockSolid {


    public static final ArrayBlockProperty<StructureVoidType> STRUCTURE_VOID_TYPE = new ArrayBlockProperty<>("structure_void_type", false, StructureVoidType.class);


    public static final BlockProperties PROPERTIES = new BlockProperties(STRUCTURE_VOID_TYPE);


    public BlockStructureVoid() {
        // Does Nothing
    }
    
    @Override
    public int getId() {
        return STRUCTURE_VOID;
    }
    
    @Override
    public String getName() {
        return "Structure Void";
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }


    @NotNull
    public StructureVoidType getType() {
        return getPropertyValue(STRUCTURE_VOID_TYPE);
    }


    public void setType(@Nullable StructureVoidType type) {
        setPropertyValue(STRUCTURE_VOID_TYPE, type);
    }

    @Override
    public double getHardness() {
        return 0;
    }
    
    @Override
    public double getResistance() {
        return 0;
    }
    
    @Override
    public boolean canPassThrough() {
        return true;
    }


    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override

    public  boolean canBePulled() {
        return false;
    }

}
