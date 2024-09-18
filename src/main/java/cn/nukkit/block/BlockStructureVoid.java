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

    public static final BlockProperties PROPERTIES = new BlockProperties(STRUCTURE_VOID);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStructureVoid() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStructureVoid(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Structure Void";
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
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
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
