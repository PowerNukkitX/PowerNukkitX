package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.ComposterEmptyEvent;
import cn.nukkit.event.block.ComposterFillEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemBoneMeal;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

import static cn.nukkit.block.property.CommonBlockProperties.COMPOSTER_FILL_LEVEL;


public class BlockComposter extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(COMPOSTER, COMPOSTER_FILL_LEVEL);
    private static final Object2IntMap<String> compostableItems = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<BlockState> compostableBlocks = new Object2IntOpenHashMap<>();
    public static final Item $2 = new ItemBoneMeal();
    /**
     * @deprecated 
     */
    

    public static void init(){
        registerDefaults();
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockComposter() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockComposter(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Composter";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        return getPropertyValue(COMPOSTER_FILL_LEVEL);
    }
    /**
     * @deprecated 
     */
    

    public boolean incrementLevel() {
        int $3 = getPropertyValue(COMPOSTER_FILL_LEVEL) + 1;
        setPropertyValue(COMPOSTER_FILL_LEVEL, fillLevel);
        this.level.setBlock(this, this, true, true);
        return $4 == 8;
    }
    /**
     * @deprecated 
     */
    

    public boolean isFull() {
        return getPropertyValue(COMPOSTER_FILL_LEVEL) == 8;
    }
    /**
     * @deprecated 
     */
    

    public boolean isEmpty() {
        return getPropertyValue(COMPOSTER_FILL_LEVEL) == 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        if (item.isNull()) {
            return false;
        }

        if (isFull()) {
            ComposterEmptyEvent $5 = new ComposterEmptyEvent(this, player, item, Item.get(ItemID.BONE_MEAL), 0);
            this.level.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                setPropertyValue(COMPOSTER_FILL_LEVEL, event.getNewLevel());
                this.level.setBlock(this, this, true, true);
                this.level.dropItem(add(0.5, 0.85, 0.5), event.getDrop(), event.getMotion(), false, 10);
                this.level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_EMPTY);
            }
            return true;
        }

        int $6 = getChance(item);
        if (chance <= 0) {
            return false;
        }

        boolean $7 = new Random().nextInt(100) < chance;
        ComposterFillEvent $8 = new ComposterFillEvent(this, player, item, chance, success);
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return true;
        }

        if (player != null && !player.isCreative()) {
            item.setCount(item.getCount() - 1);
        }

        if (event.isSuccess()) {
            if (incrementLevel()) {
                level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_READY);
            } else {
                level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_FILL_SUCCESS);
            }
        } else {
            level.addSound(this.add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_FILL);
        }

        return true;
    }

    public Item empty() {
        return empty(null, null);
    }

    public Item empty(@Nullable Item item, @Nullable Player player) {
        ComposterEmptyEvent $9 = new ComposterEmptyEvent(this, player, item, new ItemBoneMeal(), 0);
        this.level.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            setPropertyValue(COMPOSTER_FILL_LEVEL, event.getNewLevel());
            this.level.setBlock(this, this, true, true);
            if (item != null) {
                this.level.dropItem(add(0.5, 0.85, 0.5), event.getDrop(), event.getMotion(), false, 10);
            }
            this.level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_COMPOSTER_EMPTY);
            return event.getDrop();
        }
        return null;
    }

    public Item getOutPutItem() {
        return OUTPUT_ITEM.clone();
    }
    /**
     * @deprecated 
     */
    

    public static void registerItem(int chance, @NotNull String itemId) {
        compostableItems.put(itemId, chance);
    }
    /**
     * @deprecated 
     */
    

    public static void registerItems(int chance, @NotNull String... itemId) {
        for (String minecraftItemID : itemId) {
            registerItem(chance, minecraftItemID);
        }
    }
    /**
     * @deprecated 
     */
    

    public static void registerBlocks(int chance, String... blockIds) {
        for (String blockId : blockIds) {
            registerBlock(chance, blockId, 0);
        }
    }
    /**
     * @deprecated 
     */
    

    public static void registerBlock(int chance, String blockId) {
        BlockState $10 = Registries.BLOCK.get(blockId).getBlockState();
        compostableBlocks.put(blockState, chance);
    }
    /**
     * @deprecated 
     */
    

    public static void registerBlock(int chance, String blockId, int meta) {
        $11nt $1 = Registries.BLOCKSTATE_ITEMMETA.get(blockId, meta);
        BlockState blockState;
        if (i == 0) {
            Block $12 = Registries.BLOCK.get(blockId);
            blockState = block.getProperties().getDefaultState();
        } else {
            blockState = Registries.BLOCKSTATE.get(i);
        }
        compostableBlocks.put(blockState, chance);
    }
    /**
     * @deprecated 
     */
    

    public static int getChance(Item item) {
        if (item instanceof ItemBlock) {
            return compostableBlocks.getInt(item.getBlockUnsafe().getBlockState());
        } else {
            return compostableItems.getInt(item.getId());
        }
    }

    
    /**
     * @deprecated 
     */
    private static void registerDefaults() {
        registerItems(30,
                BlockID.KELP,
                ItemID.BEETROOT_SEEDS,
                ItemID.DRIED_KELP,
                ItemID.MELON_SEEDS,
                ItemID.PUMPKIN_SEEDS,
                ItemID.SWEET_BERRIES,
                ItemID.WHEAT_SEEDS,
                ItemID.GLOW_BERRIES
        );
        registerItems(50, ItemID.MELON_SLICE, ItemID.SUGAR_CANE, BlockID.NETHER_SPROUTS);
        registerItems(65, ItemID.APPLE, BlockID.BEETROOT, ItemID.CARROT, ItemID.COCOA_BEANS, ItemID.POTATO, BlockID.WHEAT);
        registerItems(85, ItemID.BAKED_POTATO, ItemID.BREAD, ItemID.COOKIE, ItemID.MUSHROOM_STEW);
        registerItems(100, BlockID.CAKE, ItemID.PUMPKIN_PIE);

        registerBlocks(30, PINK_PETALS, ItemID.LEAVES, ItemID.LEAVES2, ItemID.SAPLING, SEAGRASS, SWEET_BERRY_BUSH, MOSS_CARPET, HANGING_ROOTS, SMALL_DRIPLEAF_BLOCK);
        registerBlocks(50, GLOW_LICHEN, GRASS_BLOCK, CACTUS, DRIED_KELP_BLOCK, VINE, NETHER_SPROUTS,
                TWISTING_VINES, WEEPING_VINES, GLOW_LICHEN);
        registerBlock(50, TALLGRASS, 0);
        registerBlock(50, TALLGRASS, 1);
        registerBlock(65, TALLGRASS, 2);
        registerBlock(65, TALLGRASS, 3);
        registerBlocks(65, YELLOW_FLOWER, ItemID.RED_FLOWER, DOUBLE_PLANT, WITHER_ROSE, WATERLILY, MELON_BLOCK,
                PUMPKIN, CARVED_PUMPKIN, SEA_PICKLE, BROWN_MUSHROOM, RED_MUSHROOM,
                WARPED_ROOTS, CRIMSON_ROOTS, SHROOMLIGHT, AZALEA, BIG_DRIPLEAF, MOSS_BLOCK,
                SPORE_BLOSSOM);
        registerBlocks(85, HAY_BLOCK, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK);
        registerBlocks(100, CAKE);
    }
}