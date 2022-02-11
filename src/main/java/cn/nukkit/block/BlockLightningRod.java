package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

@Since("1.6.0.0-PNX")
@PowerNukkitOnly
public class BlockLightningRod extends BlockTransparentMeta implements Faceable, RedstoneComponent {

    private boolean isPowered;

    @PowerNukkitOnly
    @Since("1.6.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION);

    @Override
    public String getName() {
        return "LightningRod";
    }

    @Override
    public int getId() {
        return LIGHTNING_ROD;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        this.setBlockFace(face);
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.isPowered()) {
                setPowered(false);

                if (this.level.getServer().isRedstoneEnabled()) {
                    this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                    updateAroundRedstone();
                    RedstoneComponent.updateAroundRedstone(getSide(getFacing().getOpposite()), getFacing());
                }
            }
            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }


    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isPowered() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isPowered() ? 0 : (getFacing() == side ? 15 : 0);
    }

    public BlockFace getFacing() {
        return getPropertyValue(FACING_DIRECTION);
    }

    @Override
    public BlockFace getBlockFace() {
        return this.getPropertyValue(FACING_DIRECTION);
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(FACING_DIRECTION, face);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
}
