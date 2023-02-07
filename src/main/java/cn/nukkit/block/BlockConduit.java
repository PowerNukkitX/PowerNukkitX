package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityConduit;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@PowerNukkitOnly
public class BlockConduit extends BlockTransparent implements BlockEntityHolder<BlockEntityConduit> {
    @PowerNukkitOnly
    public BlockConduit() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CONDUIT;
    }

    @Override
    public String getName() {
        return "Conduit";
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public Class<? extends BlockEntityConduit> getBlockEntityClass() {
        return BlockEntityConduit.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.CONDUIT;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (item.getBlock() != null && item.getBlockId() == CONDUIT && target.getId() == CONDUIT) {
            return false;
        }

        BlockEntityConduit conduit = BlockEntityHolder.setBlockAndCreateEntity(this, true, true,
                new CompoundTag().putBoolean("IsMovable", true));
        if (conduit != null) {
            conduit.scheduleUpdate();
            return true;
        }

        return false;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    public double getMinX() {
        return x + (5.0/16);
    }

    @Override
    public double getMinY() {
        return y + (5.0/16);
    }

    @Override
    public double getMinZ() {
        return z + (5.0/16);
    }

    @Override
    public double getMaxX() {
        return x + (11.0/16);
    }

    @Override
    public double getMaxY() {
        return y + (11.0/16);
    }

    @Override
    public double getMaxZ() {
        return z + (11.0/16);
    }
}
