package cn.nukkit.block;

import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.SandType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockSand extends BlockFallableMeta {


    public static final ArrayBlockProperty<SandType> SAND_TYPE = new ArrayBlockProperty<>("sand_type", true, SandType.class);


    public static final BlockProperties PROPERTIES = new BlockProperties(SAND_TYPE);

    public static final int DEFAULT = 0;
    public static final int RED = 1;

    public BlockSand() {
        // Does nothing
    }

    public BlockSand(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SAND;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        if (this.getDamage() == 0x01) {
            return "Red Sand";
        }

        return "Sand";
    }

}
