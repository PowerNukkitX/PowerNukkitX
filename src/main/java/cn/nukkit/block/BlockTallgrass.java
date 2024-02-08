package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.DoublePlantType;
import cn.nukkit.block.property.enums.TallGrassType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.TALL_GRASS_TYPE;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockTallgrass extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties(TALLGRASS, TALL_GRASS_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTallgrass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTallgrass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return getPropertyValue(TALL_GRASS_TYPE).name() + "Tallgrass";
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (BlockSweetBerryBush.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockSweetBerryBush.isSupportValid(down(1, 0))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            Block up = this.up();

            if (up.isAir()) {
                DoublePlantType type = switch (getPropertyValue(TALL_GRASS_TYPE)) {
                    case DEFAULT, TALL -> DoublePlantType.GRASS;
                    case FERN, SNOW -> DoublePlantType.FERN;
                };

                if (player != null && !player.isCreative()) {
                    item.count--;
                }

                BlockDoublePlant doublePlant = (BlockDoublePlant) Block.get(BlockID.DOUBLE_PLANT);
                doublePlant.setDoublePlantType(type);
                doublePlant.setTopHalf(false);

                this.level.addParticle(new BoneMealParticle(this));
                this.level.setBlock(this, doublePlant, true, false);

                doublePlant.setTopHalf(true);
                this.level.setBlock(up, doublePlant, true);
                this.level.updateAround(this);
            }

            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        // https://minecraft.wiki/w/Fortune#Grass_and_ferns
        List<Item> drops = new ArrayList<>(2);
        if (item.isShears()) {
            drops.add(toItem());
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(8) == 0) {
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            int fortuneLevel = fortune != null ? fortune.getLevel() : 0;
            int amount = fortuneLevel == 0 ? 1 : 1 + random.nextInt(fortuneLevel * 2);
            drops.add(Item.get(ItemID.WHEAT_SEEDS, 0, amount));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public boolean isPotBlockState() {
        return getPropertyValue(TALL_GRASS_TYPE) == TallGrassType.FERN;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }
}
