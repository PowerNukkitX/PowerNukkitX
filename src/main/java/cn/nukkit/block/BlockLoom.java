package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;

/**
 * @implNote Faceable since FUTURE
 */

public class BlockLoom extends BlockSolidMeta implements Faceable {


    public static final BlockProperties PROPERTIES = new BlockProperties(DIRECTION);


    public BlockLoom() {
        this(0);
    }


    public BlockLoom(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LOOM;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Loom";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockLoom());
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        //TODO Loom's inventory
        return false;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            setBlockFace(player.getDirection().getOpposite());
        }
        this.level.setBlock(this, this, true, true);
        return true;
    }


    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(DIRECTION);
    }


    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION, face);
    }
}
