package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;


public class BlockLight extends BlockTransparentMeta {


    public static final IntBlockProperty LIGHT_LEVEL = new IntBlockProperty("block_light_level", true, 15);


    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_LEVEL);


    public BlockLight() {
        this(0);
    }


    public BlockLight(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Light Block";
    }

    @Override
    public int getId() {
        return LIGHT_BLOCK;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getLightLevel() {
        return getDamage() & 0xF;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }


    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
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
    public Item toItem() {
        return Item.get(Item.AIR);
    }
}
