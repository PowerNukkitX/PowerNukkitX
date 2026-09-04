package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockSeaLantern extends BlockTransparent {

    public static final BlockProperties PROPERTIES = new BlockProperties(SEA_LANTERN);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.3)
            .resistance(1.5)
            .lightEmission(15)
            .canSilkTouch(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSeaLantern() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSeaLantern(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Sea Lantern";
    }

    @Override
    public Item[] getDrops(Item item) {
        Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortuneLevel = fortune != null ? fortune.getLevel() : 0;
        // it drops 2–3 prismarine crystals
        // Each level of Fortune increases the maximum number of prismarine crystals dropped. 
        // The amount is capped at 5, so Fortune III simply increases the chance of getting 5 crystals.
        int count = Math.min(5, 2 + ThreadLocalRandom.current().nextInt(1 + fortuneLevel));

        return new Item[]{Item.get(ItemID.PRISMARINE_CRYSTALS, 0, count)};
    }

    }
