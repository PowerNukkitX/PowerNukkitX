package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public abstract class BlockTerracottaGlazed extends BlockSolidMeta implements Faceable {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES;

    public BlockTerracottaGlazed() {
        this(0);
    }

    public BlockTerracottaGlazed(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getResistance() {
        return 7;
    }

    @Override
    public double getHardness() {
        return 1.4;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        return this.getLevel().setBlock(block, this, true, true);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    //带釉陶瓦可以被推动但不能被收回
    //see: https://minecraft.fandom.com/zh/wiki/%E5%B8%A6%E9%87%89%E9%99%B6%E7%93%A6

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }
}
