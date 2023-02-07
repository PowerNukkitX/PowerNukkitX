package cn.nukkit.block;


import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemChain;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockChain extends BlockTransparent {
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockChain() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Chain";
    }

    @Override
    public int getId() {
        return CHAIN_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return BlockLog.PILLAR_PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(PILLAR_AXIS);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(PILLAR_AXIS, axis);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setPillarAxis(face.getAxis());
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getMinX() {
        return x + 7/16.0;
    }

    @Override
    public double getMaxX() {
        return x + 9/16.0;
    }

    @Override
    public double getMinZ() {
        return z + 7/16.0;
    }

    @Override
    public double getMaxZ() {
        return z + 9/16.0;
    }

    @Override
    public Item toItem() {
        return new ItemChain();
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
}
