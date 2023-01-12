package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMuddyMangroveRoots extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(PILLAR_AXIS);

    public BlockMuddyMangroveRoots() {
    }

    @Override
    public String getName() {
        return "Muddy Mangrove Roots";
    }

    @Override
    public int getId() {
        return MUDDY_MANGROVE_ROOTS;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 0.7;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public int getToolType() {
        return ItemTool.AIR;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    public BlockFace.Axis getPillarAxis() {
        return getPropertyValue(PILLAR_AXIS);
    }

    public void setPillarAxis(BlockFace.Axis axis) {
        setPropertyValue(PILLAR_AXIS, axis);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setPillarAxis(face.getAxis());
        getLevel().setBlock(block, this, true, true);
        return true;
    }
}
