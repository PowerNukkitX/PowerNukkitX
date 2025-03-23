package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockTallDryGrass extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(TALL_DRY_GRASS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTallDryGrass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTallDryGrass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.DRY;
    }

    @Override
    public String getName() {
        return "Tall dry grass";
    }
}
