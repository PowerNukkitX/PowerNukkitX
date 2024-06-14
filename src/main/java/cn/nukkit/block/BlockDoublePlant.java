package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.DoublePlantType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.DOUBLE_PLANT_TYPE;
import static cn.nukkit.block.property.CommonBlockProperties.UPPER_BLOCK_BIT;

public abstract class BlockDoublePlant extends BlockFlowable {
    public static BlockDoublePlant getFromType(DoublePlantType type) {
        return switch (type) {
            case FERN -> new BlockLargeFern();
            case ROSE -> new BlockRoseBush();
            case GRASS -> new BlockTallGrass();
            case PAEONIA -> new BlockPeony();
            case SYRINGA -> new BlockLilac();
            case SUNFLOWER -> new BlockSunflower();
        };
    }

    public BlockDoublePlant(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull
    public abstract DoublePlantType getDoublePlantType();

    public void setDoublePlantType(@NotNull DoublePlantType type) {
        setPropertyValue(DOUBLE_PLANT_TYPE, type);
    }

    public boolean isTopHalf() {
        return getPropertyValue(UPPER_BLOCK_BIT);
    }

    public void setTopHalf(boolean topHalf) {
        setPropertyValue(UPPER_BLOCK_BIT, topHalf);
    }

    @Override
    public Item toItem() {
        int aux = getDoublePlantType().ordinal();
        return new ItemBlock(this, aux);
    }

    @Override
    public boolean canBeReplaced() {
        return getDoublePlantType() == DoublePlantType.GRASS || getDoublePlantType() == DoublePlantType.FERN;
    }

    @Override
    public String getName() {
        return getDoublePlantType().name();
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isTopHalf()) {
                // Top
                if (!(this.down() instanceof BlockDoublePlant)) {
                    this.getLevel().setBlock(this, Block.get(BlockID.AIR), false, true);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            } else {
                // Bottom
                if (!(this.down() instanceof BlockDoublePlant) || !isSupportValid(down())) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block up = up();

        if (up.isAir() && isSupportValid(down())) {
            setTopHalf(false);
            this.getLevel().setBlock(block, this, true, false); // If we update the bottom half, it will drop the item because there isn't a flower block above

            setTopHalf(true);
            this.getLevel().setBlock(up, this, true, true);
            this.getLevel().updateAround(this);
            return true;
        }

        return false;
    }

    private boolean isSupportValid(Block support) {
        return switch (support.getId()) {
            case GRASS_BLOCK, DIRT, PODZOL, FARMLAND, MYCELIUM, DIRT_WITH_ROOTS, MOSS_BLOCK -> true;
            default -> false;
        };
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = down();

        if (isTopHalf()) { // Top half
            this.getLevel().useBreakOn(down);
        } else {
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true);
        }

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isTopHalf()) {
            return Item.EMPTY_ARRAY;
        }

        if (getDoublePlantType() == DoublePlantType.GRASS || getDoublePlantType() == DoublePlantType.FERN) {
            boolean dropSeeds = ThreadLocalRandom.current().nextInt(10) == 0;
            if (item.isShears()) {
                //todo enchantment
                if (dropSeeds) {
                    return new Item[]{
                            Item.get(ItemID.WHEAT_SEEDS),
                            toItem()
                    };
                } else {
                    return new Item[]{
                            toItem()
                    };
                }
            }
            if (dropSeeds) {
                return new Item[]{
                        Item.get(ItemID.WHEAT_SEEDS)
                };
            } else {
                return Item.EMPTY_ARRAY;
            }
        }

        return new Item[]{toItem()};
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) { //Bone meal
            switch (getDoublePlantType()) {
                case SUNFLOWER:
                case SYRINGA:
                case ROSE:
                case PAEONIA:
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }
                    this.level.addParticle(new BoneMealParticle(this));
                    this.level.dropItem(this, this.toItem());
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }
}
