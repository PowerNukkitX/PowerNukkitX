package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.AGE_3;
import static cn.nukkit.block.property.CommonBlockProperties.DIRECTION;

/**
 * @author CreeperFace
 * @since 27. 10. 2016
 */
public class BlockCocoa extends BlockTransparent implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(COCOA, AGE_3, DIRECTION);

    protected static final AxisAlignedBB[] EAST = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.6875D, 0.4375D, 0.375D, 0.9375D, 0.75D, 0.625D), new SimpleAxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D), new SimpleAxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] WEST = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.0625D, 0.4375D, 0.375D, 0.3125D, 0.75D, 0.625D), new SimpleAxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D), new SimpleAxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] NORTH = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.375D, 0.4375D, 0.0625D, 0.625D, 0.75D, 0.3125D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D)};
    protected static final AxisAlignedBB[] SOUTH = new SimpleAxisAlignedBB[]{new SimpleAxisAlignedBB(0.375D, 0.4375D, 0.6875D, 0.625D, 0.75D, 0.9375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D), new SimpleAxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D)};
    protected static final Object2ObjectArrayMap<BlockFace, AxisAlignedBB[]> ALL = new Object2ObjectArrayMap<>(4);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCocoa() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCocoa(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cocoa";
    }

    @Override
    public double getMinX() {
        return this.x + getRelativeBoundingBox().getMinX();
    }

    @Override
    public double getMaxX() {
        return this.x + getRelativeBoundingBox().getMaxX();
    }

    @Override
    public double getMinY() {
        return this.y + getRelativeBoundingBox().getMinY();
    }

    @Override
    public double getMaxY() {
        return this.y + getRelativeBoundingBox().getMaxY();
    }

    @Override
    public double getMinZ() {
        return this.z + getRelativeBoundingBox().getMinZ();
    }

    @Override
    public double getMaxZ() {
        return this.z + getRelativeBoundingBox().getMaxZ();
    }

    private AxisAlignedBB getRelativeBoundingBox() {
        BlockFace face = getBlockFace();
        AxisAlignedBB[] axisAlignedBBS = ALL.get(face);
        if (axisAlignedBBS != null) {
            return axisAlignedBBS[getAge()];
        }
        AxisAlignedBB[] bbs = switch (face) {
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> NORTH;
        };
        ALL.put(face, bbs);
        return bbs[getAge()];
    }

    static final int[] faces = new int[]{
            0,
            0,
            0,
            2,
            3,
            1,
    };

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if ((target instanceof BlockJungleLog || target instanceof BlockWood wood && wood.getWoodType() == WoodType.JUNGLE)
                && face != BlockFace.DOWN && face != BlockFace.UP) {
            setPropertyValue(DIRECTION, faces[face.getIndex()]);
            this.level.setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    static final int[] faces2 = new int[]{
            3, 4, 2, 5, 3, 4, 2, 5, 3, 4, 2, 5
    };

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block side = this.getSide(BlockFace.fromIndex(faces2[getPropertyValue(DIRECTION)]));
            if (!((side instanceof BlockWood wood && wood.getWoodType() == WoodType.JUNGLE) || side instanceof BlockJungleLog)) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1) {
                if (this.getAge() < 2 && !this.grow()) {
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            if (this.getAge() < 2) {
                if (!this.grow()) {
                    return false;
                }
                this.level.addParticle(new BoneMealParticle(this));

                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }
            }

            return true;
        }

        return false;
    }

    public boolean grow() {
        Block block = this.clone();
        block.setPropertyValue(AGE_3, getAge() + 1);
        BlockGrowEvent ev = new BlockGrowEvent(this, block);
        Server.getInstance().getPluginManager().callEvent(ev);
        return !ev.isCancelled() && this.getLevel().setBlock(this, ev.getNewState(), true, true);
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    @NotNull
    public String getItemId() {
        return ItemID.COCOA_BEANS;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.COCOA_BEANS);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(ItemID.COCOA_BEANS, 0, getPropertyValue(AGE_3) >= 1 ? 3 : 1)
        };
    }

    @Override
    public BlockFace getBlockFace() {
        Integer propertyValue = getPropertyValue(DIRECTION);
        return BlockFace.fromHorizontalIndex(propertyValue);
    }

    @Override
    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        setPropertyValue(DIRECTION, horizontalIndex == -1 ? 0 : horizontalIndex);
    }

    public int getAge() {
        return getPropertyValue(AGE_3);
    }

    public void setAge(int age) {
        setPropertyValue(AGE_3, age);
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
