package org.powernukkitx.registry;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockGrassBlock;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.cloudburstmc.nbt.NbtMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class RegistryAliasTest {
    private static final String COLLIDING_ITEM_ID = "test:diamond_alias";
    private static final String STALE_ITEM_ID = "test:stale_alias_item";
    private static final String STALE_ITEM_ALIAS = "test:stale_alias";

    @BeforeAll
    static void init() {
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
    }

    @Test
    void itemAliasesResolveThroughEveryLookup() {
        String alias = "minecraft:totem";

        assertEquals(ItemID.TOTEM_OF_UNDYING, Registries.ITEM.get(alias).getId());
        assertEquals(ItemID.TOTEM_OF_UNDYING, Registries.ITEM.get(alias, 0).getId());
        assertEquals(ItemID.TOTEM_OF_UNDYING, Registries.ITEM.get(alias, 0, 1).getId());
        assertEquals(ItemID.TOTEM_OF_UNDYING, Registries.ITEM.get(alias, 0, 1, (NbtMap) null).getId());
        assertEquals(ItemID.TOTEM_OF_UNDYING, Registries.ITEM.get(alias, 0, 1, (byte[]) null).getId());
    }

    @Test
    void canonicalItemIdentifierTakesPriorityOverAlias() throws RegisterException {
        Registries.ITEM.register(COLLIDING_ITEM_ID, DiamondAliasItem.class);
        try {
            assertEquals(ItemID.DIAMOND, Registries.ITEM.get(ItemID.DIAMOND).getId());
        } finally {
            Registries.ITEM.reload();
        }
    }

    @Test
    void itemReloadClearsStaleAliases() throws RegisterException {
        Registries.ITEM.register(STALE_ITEM_ID, StaleAliasItem.class);
        try {
            assertEquals(STALE_ITEM_ID, Registries.ITEM.get(STALE_ITEM_ALIAS).getId());

            Registries.ITEM.reload();

            assertNull(Registries.ITEM.get(STALE_ITEM_ALIAS));
        } finally {
            Registries.ITEM.reload();
        }
    }

    @Test
    void blockAliasesResolveThroughEveryLookup() {
        String alias = "minecraft:grass";

        assertInstanceOf(BlockGrassBlock.class, Registries.BLOCK.get(alias));
        assertInstanceOf(BlockGrassBlock.class, Registries.BLOCK.get(alias, 1, 2, 3));
        assertInstanceOf(BlockGrassBlock.class, Registries.BLOCK.get(alias, 1, 2, 3, null));
        Block positioned = Registries.BLOCK.get(alias, 1, 2, 3, 1, null);
        assertInstanceOf(BlockGrassBlock.class, positioned);
        assertEquals(1, positioned.layer);
        assertSame(BlockGrassBlock.PROPERTIES, Registries.BLOCK.getBlockProperties(alias));
    }

    @Test
    void canonicalBlockIdentifierTakesPriorityOverAlias() throws ReflectiveOperationException {
        Map<String, String> aliases = blockAliases();
        String previousAlias = aliases.put(BlockID.STONE, BlockID.GRASS_BLOCK);
        try {
            assertEquals(BlockID.STONE, Registries.BLOCK.get(BlockID.STONE).getId());
        } finally {
            if (previousAlias == null) {
                aliases.remove(BlockID.STONE);
            } else {
                aliases.put(BlockID.STONE, previousAlias);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> blockAliases() throws NoSuchFieldException, IllegalAccessException {
        Field aliases = BlockRegistry.class.getDeclaredField("ALIASES");
        aliases.setAccessible(true);
        return (Map<String, String>) aliases.get(null);
    }

    public static class DiamondAliasItem extends Item {
        public DiamondAliasItem() {
            super(COLLIDING_ITEM_ID);
        }

        @Override
        public String[] getAliases() {
            return new String[]{ItemID.DIAMOND};
        }
    }

    public static class StaleAliasItem extends Item {
        public StaleAliasItem() {
            super(STALE_ITEM_ID);
        }

        @Override
        public String[] getAliases() {
            return new String[]{STALE_ITEM_ALIAS};
        }
    }

}
