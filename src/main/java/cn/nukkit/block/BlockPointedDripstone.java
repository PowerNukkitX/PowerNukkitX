package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;

/**
 * @author CoolLoong
 * @since 02.13.2022
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockPointedDripstone extends BlockFallableMeta {
    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    private static final ArrayBlockProperty<String> DRIPSTONE_THICKNESS = new ArrayBlockProperty<>("dripstone_thickness", false,
            new String[]{
                    "base",
                    "frustum",
                    "merge",
                    "middle",
                    "tip"
            }
    );

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    private static final IntBlockProperty HANGING = new IntBlockProperty("hanging", false, 1, 0);

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES = new BlockProperties(DRIPSTONE_THICKNESS, HANGING);

    public BlockPointedDripstone() {
    }

    public BlockPointedDripstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POINTED_DRIPSTONE;
    }

    @Override
    public String getName() {
        return "Pointed Drip Stone";
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }


    @Override
    public int onUpdate(int type) {
        return 1;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        int placeX = this.getFloorX();
        int placeY = this.getFloorY();
        int placeZ = this.getFloorZ();
        int upBlockID = level.getBlockIdAt(placeX, placeY + 1, placeZ);
        int downBlockID = level.getBlockIdAt(placeX, placeY - 1, placeZ);
        if (upBlockID == AIR && downBlockID == AIR) return false;
        /*
              up   down
          1   yes   yes
          2   yes   no
          3   no    no
          4   no    yes
        */
        int state = (upBlockID == POINTED_DRIPSTONE) ? (downBlockID == POINTED_DRIPSTONE ? 1 : 2) : (downBlockID != POINTED_DRIPSTONE ? 3 : 4);
        int hanging = 0;
        switch (state) {
            case 1: {
                setMergeBlock(placeX, placeY, placeZ, 0);
                setBlockThicknessStateAt(placeX, placeY + 1, placeZ, 1, "merge");
                break;
            }
            case 2: {
                if (level.getBlockIdAt(placeX, placeY - 1, placeZ) != AIR) {
                    if (face.equals(BlockFace.UP)) {
                        setBlockThicknessStateAt(placeX, placeY + 1, placeZ, 1, "merge");
                        setMergeBlock(placeX, placeY, placeZ, 0);
                    } else {
                        setTipBlock(placeX, placeY, placeZ, 1);
                        setAddChange(placeX, placeY, placeZ, 1);
                    }
                    return true;
                }
                hanging = 1;
                break;
            }
            case 3: {
                if (downBlockID != AIR) {
                    setTipBlock(placeX, placeY, placeZ, 0);
                } else {
                    setTipBlock(placeX, placeY, placeZ, 1);
                }
                return true;
            }
            case 4: {
                if (level.getBlockIdAt(placeX, placeY + 1, placeZ) != AIR) {
                    if (face.equals(BlockFace.DOWN)) {
                        setMergeBlock(placeX, placeY, placeZ, 1);
                        setBlockThicknessStateAt(placeX, placeY - 1, placeZ, 0, "merge");
                    } else {
                        setTipBlock(placeX, placeY, placeZ, 0);
                        setAddChange(placeX, placeY, placeZ, 0);
                    }
                    return true;
                }
                break;
            }
        }
        setAddChange(placeX, placeY, placeZ, hanging);

        if (state == 1) return true;
        setTipBlock(placeX, placeY, placeZ, hanging);
        return true;
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void setTipBlock(int x, int y, int z, int hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "tip");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void setMergeBlock(int x, int y, int z, int hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "merge");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void setBlockThicknessStateAt(int x, int y, int z, int hanging, String thickness) {
        BlockState blockState;
        this.setPropertyValue(DRIPSTONE_THICKNESS, thickness);
        this.setPropertyValue(HANGING, hanging);
        blockState = this.getCurrentState();
        level.setBlockStateAt(x, y, z, blockState);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public int getPointedDripStoneLength(int hanging) {
        int currentX = this.getFloorX();
        int currentY = this.getFloorY();
        int currentZ = this.getFloorZ();

        if (hanging == 1) {
            for (int y = currentY + 1; y < 320; ++y) {
                int blockId = level.getBlockIdAt(currentX, y, currentZ);
                if (blockId != POINTED_DRIPSTONE) {
                    return y - currentY - 1;
                }
            }
        } else {
            for (int y = currentY - 1; y > -64; --y) {
                int blockId = level.getBlockIdAt(currentX, y, currentZ);
                if (blockId != POINTED_DRIPSTONE) {
                    return currentY - y - 1;
                }
            }
        }
        return 0;
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public void setAddChange(int x, int y, int z, int hanging) {
        int length = getPointedDripStoneLength(hanging);
        int k2 = (hanging == 0) ? -2 : 2;
        int k1 = (hanging == 0) ? -1 : 1;
        if (length == 1) {
            setBlockThicknessStateAt(x, y + k1, z, hanging, "frustum");
        }
        if (length == 2) {
            setBlockThicknessStateAt(x, y + k2, z, hanging, "base");
            setBlockThicknessStateAt(x, y + k1, z, hanging, "frustum");
        }
        if (length >= 3) {
            setBlockThicknessStateAt(x, y + k2, z, hanging, "middle");
            setBlockThicknessStateAt(x, y + k1, z, hanging, "frustum");
        }
    }
}
