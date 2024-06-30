package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
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

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockTallGrass extends BlockDoublePlant {
    public static final BlockProperties PROPERTIES = new BlockProperties(TALL_GRASS, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTallGrass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTallGrass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.GRASS;
    }

    @Override
    public String getName() {
        return "Tallgrass";
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
}
