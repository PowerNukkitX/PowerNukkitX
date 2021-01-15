package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
public class BlockEndPortalFrame extends BlockTransparentMeta implements Faceable {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected static final BooleanBlockProperty END_PORTAL_EYE_BIT = new BooleanBlockProperty("end_portal_eye_bit", false);
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(
        DIRECTION,
        END_PORTAL_EYE_BIT
    );
    
    public BlockEndPortalFrame() {
        this(0);
    }

    public BlockEndPortalFrame(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return END_PORTAL_FRAME;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    
    @Override
    public double getResistance() {
        return 3600000;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "End Portal Frame";
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + (this.isEndPortalEye() ? 1 : 0.8125);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride() {
        return this.isEndPortalEye() ? 15 : 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (!this.isEndPortalEye() && player != null && item.getId() == Item.ENDER_EYE) {
            this.setEndPortalEye(true);
            this.getLevel().setBlock(this, this, true, true);
            this.getLevel().addSound(this, Sound.BLOCK_END_PORTAL_FRAME_FILL);
            this.createPortal();
            return true;
        }
        return false;
    }

    @Since("1.3.0.0-PN")
    public void createPortal() {
        Vector3 centerSpot = this.searchCenter(new ArrayList<>());
        if (centerSpot != null) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if ((x == -2 || x == 2) && (z == -2 || z == 2))
                        continue;
                    if (x == -2 || x == 2 || z == -2 || z == 2) {
                        if (!this.checkFrame(this.getLevel().getBlock(centerSpot.add(x, 0, z)), x, z)) {
                            return;
                        }
                    }
                }
            }

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Vector3 vector3 = centerSpot.add(x, 0, z);
                    if (this.getLevel().getBlock(vector3).getId() != Block.AIR) {
                        this.getLevel().useBreakOn(vector3);
                    }
                    this.getLevel().setBlock(vector3, Block.get(Block.END_PORTAL));
                }
            }
        }
    }

    private Vector3 searchCenter(List<Block> visited) {
        for (int x = -2; x <= 2; x++) {
            if (x == 0)
                continue;
            Block block = this.getLevel().getBlock(this.add(x, 0, 0));
            Block iBlock = this.getLevel().getBlock(this.add(x * 2, 0, 0));
            if (this.checkFrame(block) && !visited.contains(block)) {
                visited.add(block);
                if ((x == -1 || x == 1) && this.checkFrame(iBlock))
                    return ((BlockEndPortalFrame) block).searchCenter(visited);
                for (int z = -4; z <= 4; z++) {
                    if (z == 0)
                        continue;
                    block = this.getLevel().getBlock(this.add(x, 0, z));
                    if (this.checkFrame(block)) {
                        return this.add(x / 2, 0, z / 2);
                    }
                }
            }
        }
        for (int z = -2; z <= 2; z++) {
            if (z == 0)
                continue;
            Block block = this.getLevel().getBlock(this.add(0, 0, z));
            Block iBlock = this.getLevel().getBlock(this.add(0, 0, z * 2));
            if (this.checkFrame(block) && !visited.contains(block)) {
                visited.add(block);
                if ((z == -1 || z == 1) && this.checkFrame(iBlock))
                    return ((BlockEndPortalFrame) block).searchCenter(visited);
                for (int x = -4; x <= 4; x++) {
                    if (x == 0)
                        continue;
                    block = this.getLevel().getBlock(this.add(x, 0, z));
                    if (this.checkFrame(block)) {
                        return this.add(x / 2, 0, z / 2);
                    }
                }
            }
        }
        return null;
    }

    private boolean checkFrame(Block block) {
        return block.getId() == this.getId() && ((BlockEndPortalFrame) block).isEndPortalEye();
    }

    private boolean checkFrame(Block block, int x, int z) {
        return block.getId() == this.getId() && (block.getDamage() - 4) == (x == -2 ? 3 : x == 2 ? 1 : z == -2 ? 0 : z == 2 ? 2 : -1);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(DIRECTION);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION, face);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isEndPortalEye() {
        return getPropertyValue(END_PORTAL_EYE_BIT);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setEndPortalEye(boolean endPortalEye) {
        setPropertyValue(END_PORTAL_EYE_BIT, endPortalEye);
    }
}
