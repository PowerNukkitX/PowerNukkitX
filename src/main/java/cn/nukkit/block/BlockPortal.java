package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

/**
 * Alias NetherPortal
 */
public class BlockPortal extends BlockFlowable implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(PORTAL, CommonBlockProperties.PORTAL_AXIS);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPortal() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPortal(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Nether Portal Block";
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public int getLightLevel() {
        return 11;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.AIR));
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean onBreak(Item item) {
        boolean result = super.onBreak(item);
        for (BlockFace face : BlockFace.values()) {
            Block b = this.getSide(face);
            if (b != null) {
                if (b instanceof BlockPortal) {
                    result &= b.onBreak(item);
                }
            }
        }
        return result;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    public static void spawnPortal(Position pos) {
        Level lvl = pos.level; //TODO: This will generate part of the time, seems to be only when the chunk is populated
        int x = pos.getFloorX();
        int y = pos.getFloorY();
        int z = pos.getFloorZ();

        Block air = Block.get(AIR);
        Block obsidian = Block.get(OBSIDIAN);
        Block netherPortal = Block.get(PORTAL);
        for (int xx = -1; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++)  {
                for (int zz = -1; zz < 3; zz++) {
                    lvl.setBlock(x + xx, y + yy, z + zz, air, false, true);
                }
            }
        }

        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);

        z++;
        lvl.setBlock(x, y, z, obsidian, false, true);
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        lvl.setBlock(x + 3, y, z, obsidian, false, true);

        z++;
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        z--;

        for (int i = 0; i < 3; i++) {
            y++;
            lvl.setBlock(x, y, z, obsidian, false, true);
            lvl.setBlock(x + 1, y, z, netherPortal, false, true);
            lvl.setBlock(x + 2, y, z, netherPortal, false, true);
            lvl.setBlock(x + 3, y, z, obsidian, false, true);
        }

        y++;
        lvl.setBlock(x, y, z, obsidian, false, true);
        lvl.setBlock(x + 1, y, z, obsidian, false, true);
        lvl.setBlock(x + 2, y, z, obsidian, false, true);
        lvl.setBlock(x + 3, y, z, obsidian, false, true);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.blockstate.specialValue() & 0x07);
    }
}