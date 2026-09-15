package org.powernukkitx.registry;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.custom.CustomEntityDefinition;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.CustomItem;
import org.powernukkitx.item.customitem.CustomItemDefinition;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers registering custom content from a definition and a factory, the path used by
 * callers that have no dedicated class per identifier.
 * <p>
 * The types here deliberately hide their constructors, so a registration that reached for
 * a public constructor instead of the registered factory would fail to produce anything.
 */
class RegistryFactoryTest {

    private static final String ITEM_ID = "pnxtest:factory_item";
    private static final String ENTITY_ID = "pnxtest:factory_entity";

    private static Level level;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        level = ServerMockFixture.level;
    }

    /** Constructible only through the factory - it exposes no public constructor. */
    public static final class FactoryOnlyEntity extends Entity {

        private FactoryOnlyEntity(IChunk chunk, CompoundTag nbt) {
            super(chunk, nbt);
        }

        static FactoryOnlyEntity of(IChunk chunk, CompoundTag nbt) {
            return new FactoryOnlyEntity(chunk, nbt);
        }

        @Override
        public String getIdentifier() {
            return ENTITY_ID;
        }
    }

    /** Constructible only through the factory - it exposes no public constructor. */
    public static final class FactoryOnlyItem extends Item implements CustomItem {

        private FactoryOnlyItem(String id) {
            super(id);
        }

        static FactoryOnlyItem of(String id) {
            return new FactoryOnlyItem(id);
        }

        @Override
        public CustomItemDefinition getDefinition() {
            return CustomItemDefinition.simpleBuilder(this).build();
        }
    }

    @Test
    void registersItemFromDefinitionAndFactory() throws RegisterException {
        Registries.ITEM.registerCustomItemDefinition(FactoryOnlyItem.of(ITEM_ID), () -> FactoryOnlyItem.of(ITEM_ID));

        assertEquals(0, FactoryOnlyItem.class.getConstructors().length,
                "the fixture must not expose a public constructor, otherwise it proves nothing");

        Item first = Registries.ITEM.get(ITEM_ID);
        assertNotNull(first, "factory-registered item did not resolve");
        assertEquals(ITEM_ID, first.getId());
        assertTrue(Registries.ITEM.getCustomItemDefinition().containsKey(ITEM_ID));

        Item second = Registries.ITEM.get(ITEM_ID);
        assertNotSame(first, second, "each lookup must go through the factory for a fresh item");
    }

    @Test
    void rejectsDuplicateItemIdentifier() throws RegisterException {
        String id = "pnxtest:duplicate_item";
        Registries.ITEM.registerCustomItemDefinition(FactoryOnlyItem.of(id), () -> FactoryOnlyItem.of(id));

        assertThrows(RegisterException.class, () ->
                Registries.ITEM.registerCustomItemDefinition(FactoryOnlyItem.of(id), () -> FactoryOnlyItem.of(id)));
    }

    @Test
    void registersEntityWithoutPublicConstructor() throws RegisterException {
        CustomEntityDefinition definition = CustomEntityDefinition.simpleBuilder(ENTITY_ID)
                .hasSpawnEgg(false)
                .isSummonable(true)
                .build();

        Registries.ENTITY.registerCustomEntityDefinition(definition, FactoryOnlyEntity.class, FactoryOnlyEntity::of);

        assertEquals(0, FactoryOnlyEntity.class.getConstructors().length,
                "the fixture must not expose a public constructor, otherwise it proves nothing");

        Entity entity = Entity.createEntity(ENTITY_ID, new Position(0, 100, 0, level));
        assertNotNull(entity, "factory-registered entity did not resolve");
        assertEquals(ENTITY_ID, entity.getIdentifier());
        assertNotNull(Registries.ENTITY.getEntityDefinition(ENTITY_ID));
        entity.close();
    }

    @Test
    void rejectsDuplicateEntityIdentifier() throws RegisterException {
        String id = "pnxtest:duplicate_entity";
        CustomEntityDefinition definition = CustomEntityDefinition.simpleBuilder(id)
                .hasSpawnEgg(false)
                .build();

        Registries.ENTITY.registerCustomEntityDefinition(definition, FactoryOnlyEntity.class, FactoryOnlyEntity::of);

        assertThrows(RegisterException.class,
                () -> Registries.ENTITY.registerCustomEntityDefinition(definition, FactoryOnlyEntity.class, FactoryOnlyEntity::of));
    }
}
