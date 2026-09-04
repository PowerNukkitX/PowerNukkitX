package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemGlowstoneDust;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author xtypr
 * @since 2015/12/6
 */
public class BlockGlowstone extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(GLOWSTONE);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.3)
            .resistance(1.5)
            .burnChance(0)
            .burnAbility(0)
            .lightEmission(15)
            .canSilkTouch(true)
            .build();
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGlowstone() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockGlowstone(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Glowstone";
    }

    @Override
    public Item[] getDrops(Item item) {
        Random random = new Random();
        int count = 2 + random.nextInt(3);

        Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                new ItemGlowstoneDust(0, MathHelper.clamp(count, 1, 4))
        };
    }

    }
