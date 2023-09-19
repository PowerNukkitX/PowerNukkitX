package cn.nukkit.block;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Since("1.6.0.0-PNX")
@PowerNukkitOnly
public abstract class BlockAmethystBud extends BlockTransparentMeta implements Faceable {
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES = new BlockProperties(CommonBlockProperties.FACING_DIRECTION);

    @Override
    public String getName() {
        return getNamePrefix() + " Amethyst Bud";
    }

    protected abstract String getNamePrefix();

    @Override
    public abstract int getId();

    @PowerNukkitOnly
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

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
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

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }

    @Override
    public boolean place(
            @NotNull Item item,
            @NotNull Block block,
            @NotNull Block target,
            @NotNull BlockFace face,
            double fx,
            double fy,
            double fz,
            @Nullable Player player) {
        if (!target.isSolid()) return false;
        setBlockFace(face);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (item.isPickaxe()) {
            Arrays.stream(this.getDrops(item)).forEach(item1 -> this.getLevel().dropItem(this.add(0.5, 0, 0.5), item1));
            this.getLevel().setBlock(this, layer, Block.get(BlockID.AIR), true, true);
        } else {
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
