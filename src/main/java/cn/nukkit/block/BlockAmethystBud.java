package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;

import static cn.nukkit.blockproperty.CommonBlockProperties.BLOCK_FACE;
import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;


public abstract class BlockAmethystBud extends BlockTransparentMeta implements Faceable {


    public static final BlockProperties PROPERTIES = new BlockProperties(BLOCK_FACE);

    @Override
    public String getName() {
        return getNamePrefix() + " Amethyst Bud";
    }

    protected abstract String getNamePrefix();

    @Override
    public abstract int getId();


    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public abstract int getLightLevel();

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean isSolid() {
        return false;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(BLOCK_FACE);
    }


    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(BLOCK_FACE, face);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (!target.isSolid())
            return false;
        setBlockFace(face);
        this.level.setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (item.isPickaxe()){
            Arrays.stream(this.getDrops(item)).forEach(item1 -> this.level.dropItem(this.add(0.5,0,0.5), item1));
            this.getLevel().setBlock(this, layer, Block.get(BlockID.AIR), true, true);
        }else {
            this.getLevel().setBlock(this, layer, Block.get(BlockID.AIR), true, true);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if ((this.getSide(this.getBlockFace().getOpposite()).getId() == BlockID.AIR))
            this.onBreak(Item.get(ItemID.DIAMOND_PICKAXE));
        return 0;
    }
}
