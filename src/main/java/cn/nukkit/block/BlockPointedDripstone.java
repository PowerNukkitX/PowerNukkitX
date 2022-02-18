package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        int hanging = getPropertyValue(HANGING);
        if (hanging == 0) {
            Block down = down();
            if (down.getId() == AIR) {
                this.getLevel().useBreakOn(this);
            }
        }
        return 0;
    }

    @Override
    public boolean place(@Nullable Item item, @Nonnull Block block, @Nullable Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        int placeX = block.getFloorX();
        int placeY = block.getFloorY();
        int placeZ = block.getFloorZ();
        int upBlockID = level.getBlockIdAt(placeX, placeY + 1, placeZ);
        int downBlockID = level.getBlockIdAt(placeX, placeY - 1, placeZ);
        if (upBlockID == AIR && downBlockID == AIR) return false;
        /*    "up" define is exist drip stone in block above,"down" is Similarly.
              up   down
          1   yes   yes
          2   yes   no
          3   no    no
          4   no    yes
        */
        int state = (upBlockID == POINTED_DRIPSTONE) ? (downBlockID == POINTED_DRIPSTONE ? 1 : 2) : (downBlockID != POINTED_DRIPSTONE ? 3 : 4);
        int hanging = 0;
        switch (state) {
            case 1 -> {
                setMergeBlock(placeX, placeY, placeZ, 0);
                setBlockThicknessStateAt(placeX, placeY + 1, placeZ, 1, "merge");
            }
            case 2 -> {
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
            }
            case 3 -> {
                if (downBlockID != AIR) {
                    setTipBlock(placeX, placeY, placeZ, 0);
                } else {
                    setTipBlock(placeX, placeY, placeZ, 1);
                }
                return true;
            }
            case 4 -> {
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
            }
        }
        setAddChange(placeX, placeY, placeZ, hanging);

        if (state == 1) return true;
        setTipBlock(placeX, placeY, placeZ, hanging);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        int x = this.getFloorX();
        int y = this.getFloorY();
        int z = this.getFloorZ();
        level.setBlock(x, y, z, Block.get(AIR), true, true);
        int hanging = getPropertyValue(HANGING);
        String thickness = getPropertyValue(DRIPSTONE_THICKNESS);
        if (thickness.equals("merge")) {
            if (hanging == 0) {
                setBlockThicknessStateAt(x, y + 1, z, 1, "tip");
            } else setBlockThicknessStateAt(x, y - 1, z, 0, "tip");
        }
        if (hanging == 0) {
            int length = getPointedDripStoneLength(x, y, z, 0);
            if (length > 0) {
                Block downBlock = down();
                for (int i = 0; i <= length - 1; ++i) {
                    level.setBlock(downBlock.down(i), Block.get(AIR), false, false);
                }
                for (int i = length - 1; i >= 0; --i) {
                    place(null, downBlock.down(i), null, BlockFace.DOWN, 0, 0, 0, null);
                }
            }
        }
        if (hanging == 1) {
            int length = getPointedDripStoneLength(x, y, z, 1);
            if (length > 0) {
                Block upBlock = up();
                for (int i = 0; i <= length - 1; ++i) {
                    level.setBlock(upBlock.up(i), Block.get(AIR), false, false);
                }
                for (int i = length - 1; i >= 0; --i) {
                    place(null, upBlock.up(i), null, BlockFace.DOWN, 0, 0, 0, null);
                }
            }/*
            if (down().getId() == POINTED_DRIPSTONE) {
                BlockFallEvent event = new BlockFallEvent(this);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }
                createAllFallingEntity(this, new CompoundTag());
            }*/
        }

        return true;
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setTipBlock(int x, int y, int z, int hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "tip");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setMergeBlock(int x, int y, int z, int hanging) {
        this.setPropertyValue(DRIPSTONE_THICKNESS, "merge");
        this.setPropertyValue(HANGING, hanging);
        this.getLevel().setBlock(x, y, z, this, true, true);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setBlockThicknessStateAt(int x, int y, int z, int hanging, String thickness) {
        BlockState blockState;
        this.setPropertyValue(DRIPSTONE_THICKNESS, thickness);
        this.setPropertyValue(HANGING, hanging);
        blockState = this.getCurrentState();
        level.setBlockStateAt(x, y, z, blockState);
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected int getPointedDripStoneLength(int x, int y, int z, int hanging) {

        if (hanging == 1) {
            for (int j = y + 1; j < 320; ++j) {
                int blockId = level.getBlockIdAt(x, j, z);
                if (blockId != POINTED_DRIPSTONE) {
                    return j - y - 1;
                }
            }
        } else {
            for (int j = y - 1; j > -64; --j) {
                int blockId = level.getBlockIdAt(x, j, z);
                if (blockId != POINTED_DRIPSTONE) {
                    return y - j - 1;
                }
            }
        }
        return 0;
    }

    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    protected void setAddChange(int x, int y, int z, int hanging) {
        int length = getPointedDripStoneLength(x, y, z, hanging);
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

    @PowerNukkitOnly
    protected void createAllFallingEntity(@Nonnull Block block, CompoundTag customNbt) {
        int x = block.getFloorX();
        int y = block.getFloorY();
        int z = block.getFloorZ();
        int length = 0;
        EntityFallingBlock fall = null;
        Block downBlock = block.down();
        while (downBlock.getId() == POINTED_DRIPSTONE) {
            level.setBlock(downBlock, Block.get(AIR));
            downBlock = downBlock.down();
            length++;
        }
        for (int i = 0; i < length; ++i) {
            int hanging = downBlock.down(i).getPropertyValue(HANGING);
            String thickness = downBlock.down(i).getPropertyValue(DRIPSTONE_THICKNESS);
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<DoubleTag>("Pos")
                            .add(new DoubleTag("", x))
                            .add(new DoubleTag("", y - i - 1))
                            .add(new DoubleTag("", y)))
                    .putList(new ListTag<DoubleTag>("Motion")
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0))
                            .add(new DoubleTag("", 0)))
                    .putList(new ListTag<FloatTag>("Rotation")
                            .add(new FloatTag("", 0))
                            .add(new FloatTag("", 0)))
                    .putInt("TileID", downBlock.getId())
                    .putInt("hanging", hanging)
                    .putString("dripstone_thickness", thickness);

            for (Tag customTag : customNbt.getAllTags()) {
                nbt.put(customTag.getName(), customTag.copy());
            }

            fall = (EntityFallingBlock) Entity.createEntity("FallingSand", this.getLevel().getChunk(x >> 4, z >> 4), nbt);

            if (fall != null) {
                fall.spawnToAll();
            }

        }
    }
}
