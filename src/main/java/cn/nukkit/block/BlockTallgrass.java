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
    public static final BlockProperties $1 = new BlockProperties(TALLGRASS, TALL_GRASS_TYPE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTallgrass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTallgrass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return getPropertyValue(TALL_GRASS_TYPE).name() + "Tallgrass";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 60;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 100;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (BlockSweetBerryBush.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isFertilizer()) {
            Block $2 = this.up();

            if (up.isAir()) {
                DoublePlantType $3 = switch (getPropertyValue(TALL_GRASS_TYPE)) {
                    case DEFAULT, TALL -> DoublePlantType.GRASS;
                    case FERN, SNOW -> DoublePlantType.FERN;
                };

                if (player != null && !player.isCreative()) {
                    item.count--;
                }

                BlockDoublePlant $4 = (BlockDoublePlant) Block.get(BlockID.DOUBLE_PLANT);
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

        ThreadLocalRandom $5 = ThreadLocalRandom.current();
        if (random.nextInt(8) == 0) {
            Enchantment $6 = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            int $7 = fortune != null ? fortune.getLevel() : 0;
            int $8 = fortuneLevel == 0 ? 1 : 1 + random.nextInt(fortuneLevel * 2);
            drops.add(Item.get(ItemID.WHEAT_SEEDS, 0, amount));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPotBlockState() {
        return getPropertyValue(TALL_GRASS_TYPE) == TallGrassType.FERN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isFertilizable() {
        return true;
    }
}
