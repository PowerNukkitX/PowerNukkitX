package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.UPDATE_BIT;

public class BlockMangroveLeaves extends BlockLeaves {

    public static final BlockProperties $1 = new BlockProperties(MANGROVE_LEAVES, PERSISTENT_BIT, UPDATE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveLeaves() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMangroveLeaves(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public WoodType getType() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Mangrove Leaves";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        }

        List<Item> drops = new ArrayList<>(1);
        Enchantment $2 = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);

        int $3 = fortuneEnchantment != null ? fortuneEnchantment.getLevel() : 0;
        int stickOdds;
        switch (fortune) {
            case 0 -> stickOdds = 50;
            case 1 -> stickOdds = 45;
            case 2 -> stickOdds = 40;
            default -> stickOdds = 30;
        }
        ThreadLocalRandom $4 = ThreadLocalRandom.current();
        if (random.nextInt(stickOdds) == 0) {
            drops.add(Item.get(ItemID.STICK));
        }
        return drops.toArray(Item.EMPTY_ARRAY);
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
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        //todo: 实现红树树叶催化
        return false;
    }
}
