package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.utils.random.NukkitRandom;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockGravel extends BlockFallable implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAVEL);
    public static final BlockDefinition DEFINITION = FALLABLE.toBuilder()
            .hardness(0.6)
            .resistance(3)
            .toolType(ItemTool.TYPE_SHOVEL)
            .canSilkTouch(true)
            .build();

    public BlockGravel() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockGravel(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Gravel";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public Item[] getDrops(Item item) {
        Enchantment enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortune = 0;
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }

        NukkitRandom nukkitRandom = new NukkitRandom();
        switch (fortune) {
            case 0 -> {
                if (nukkitRandom.nextInt(0, 9) == 0) {
                    return new Item[]{Item.get(ItemID.FLINT, 0, 1)};

                }
            }
            case 1 -> {
                if (nukkitRandom.nextInt(0, 6) == 0) {
                    return new Item[]{Item.get(ItemID.FLINT, 0, 1)};
                }
            }
            case 2 -> {
                if (nukkitRandom.nextInt(0, 3) == 0) {
                    return new Item[]{Item.get(ItemID.FLINT, 0, 1)};
                }
            }
            default -> {
                return new Item[]{Item.get(ItemID.FLINT, 0, 1)};
            }
        }
        return new Item[]{ toItem() };
    }
    
    }
