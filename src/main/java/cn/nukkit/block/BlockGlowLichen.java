package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Gabriel8579
 * @since 2021-06-13
 */
@Since("FUTURE")
public class BlockGlowLichen extends BlockTransparent {

    public static final IntBlockProperty MULTI_FACE_DIRECTION_BITS = new IntBlockProperty("multi_face_direction_bits", false, 63, 0, 6);

    @PowerNukkitOnly
    @Since("FUTURE")
    public static final BlockProperties PROPERTIES = new BlockProperties(MULTI_FACE_DIRECTION_BITS);

    public BlockGlowLichen() {
        super();
    }

    @Override
    public String getName() {
        return "Glow Lichen";
    }

    @Override
    public int getId() {
        return GLOW_LICHEN;
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, @Nullable Player player) {

        Block current = getLevel().getBlock(this);
        int currentMeta = 0;
        if (current.getId() == GLOW_LICHEN) {
            currentMeta = current.getPropertyValue(MULTI_FACE_DIRECTION_BITS);
        }

        setPropertyValue(MULTI_FACE_DIRECTION_BITS, currentMeta | (0b0001 << face.getOpposite().getIndex()));
        getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        return 7;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean canHarvest(Item item) {
        return item.isAxe() || item.isShears();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LICHEN_GREEN;
    }

    @PowerNukkitDifference(info = "Prevents players from getting invalid items by limiting the return to the maximum damage defined in getMaxItemDamage()", since = "1.4.0.0-PN")
    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }
}
