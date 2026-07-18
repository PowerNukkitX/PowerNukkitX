package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.END_PORTAL_EYE_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
public class BlockEndPortalFrame extends BlockTransparent implements Faceable {

    public static final BlockProperties PROPERTIES = new BlockProperties(END_PORTAL_FRAME,
            MINECRAFT_CARDINAL_DIRECTION,
            END_PORTAL_EYE_BIT);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(-1)
            .resistance(3600000)
            .lightEmission(1)
            .canBePushed(false)
            .canBePulled(false)
            .canBeActivated(true)
            .canHarvestWithHand(false)
            .hasComparatorInputOverride(true)
            .waterloggingLevel(1)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockEndPortalFrame() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockEndPortalFrame(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    
    @Override
    public String getName() {
        return "End Portal Frame";
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return player != null && player.isCreative();
    }

    @Override
    public double getMaxY() {
        return this.y + (this.isEndPortalEye() ? 1 : 0.8125);
    }

    
    @Override
    public int getComparatorInputOverride() {
        return this.isEndPortalEye() ? 15 : 0;
    }

    
    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!this.isEndPortalEye() && player != null && item.getId().equals(Item.ENDER_EYE)) {
            this.setEndPortalEye(true);
            this.getLevel().setBlock(this, this, true, true);
            this.getLevel().addSound(this, Sound.BLOCK_END_PORTAL_FRAME_FILL);
            this.createPortal();
            return true;
        }
        return false;
    }

    public void createPortal() {
        Vector3 centerSpot = this.searchCenter();
        if (centerSpot != null) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if ((x == -2 || x == 2) && (z == -2 || z == 2))
                        continue;
                    if (x == -2 || x == 2 || z == -2 || z == 2) {
                        if (!this.checkFrame(this.getLevel().getBlock(centerSpot.add(x, 0, z)))) {
                            return;
                        }
                    }
                }
            }

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Vector3 vector3 = centerSpot.add(x, 0, z);
                    if (!this.getLevel().getBlock(vector3).isAir()) {
                        this.getLevel().useBreakOn(vector3);
                    }
                    this.getLevel().setBlock(vector3, Block.get(Block.END_PORTAL));
                    this.getLevel().addSound(this, Sound.BLOCK_END_PORTAL_SPAWN);
                }
            }
        }
    }

    private Vector3 searchCenter() {
        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        for(int x = -4; x <= 4; x++) {
            for(int z = -4; z <= 4; z++) {
                Block block = level.getBlock(this.add(x, 0, z));
                if(block instanceof BlockEndPortalFrame) {
                    minX = NukkitMath.min(minX, block.getFloorX());
                    minZ = NukkitMath.min(minZ, block.getFloorZ());
                }
            }
        }
        return new Vector3(minX + 2, this.getFloorY(), minZ + 2);
    }

    private boolean checkFrame(Block block) {
        return block.getId().equals(this.getId()) && ((BlockEndPortalFrame) block).isEndPortalEye();
    }

    private boolean checkFrame(Block block, int x, int z) {
        return block.getId().equals(this.getId()) && (block.blockstate.specialValue() - 4) == (x == -2 ? 3 : x == 2 ? 1 : z == -2 ? 0 : z == 2 ? 2 : -1);
    }

    
    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(this.getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    public boolean isEndPortalEye() {
        return getPropertyValue(END_PORTAL_EYE_BIT);
    }

    public void setEndPortalEye(boolean endPortalEye) {
        setPropertyValue(END_PORTAL_EYE_BIT, endPortalEye);
    }
}
