/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.entity;

import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.entity.mob.EntityZombie;
import cn.nukkit.entity.mob.EntityZombiePigman;
import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

/**
 * @author joserobjr
 * @since 2021-06-26
 */
@ExtendWith(PowerNukkitExtension.class)
class EntityTest {
    @MockLevel
    Level level;
    
    @Mock
    FullChunk chunk;
    
    Entity entity;

    @BeforeEach
    void setUp() {
        LevelProvider provider = level.getProvider();
        lenient().when(chunk.getProvider()).thenReturn(provider);
    }

    @Test
    void flameAttack() {
        entity = createEntity(EntityPig.NETWORK_ID);
        Entity attacker = createEntity(EntityZombiePigman.NETWORK_ID);
        Map<EntityDamageEvent.DamageModifier, Float> modifiers = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
        modifiers.put(EntityDamageEvent.DamageModifier.BASE, 10_000f);
        Enchantment enchantment = Enchantment.getEnchantment(Enchantment.ID_FIRE_ASPECT).setLevel(2);
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(attacker, entity, EntityDamageEvent.DamageCause.CONTACT, modifiers, 0.3f,
                new Enchantment[]{enchantment});
        entity.attack(event);
        assertFalse(entity.isAlive());
        assertTrue(entity.isOnFire());
        Optional<EntityItem> drop = Arrays.stream(level.getEntities()).filter(EntityItem.class::isInstance).map(EntityItem.class::cast).findFirst();
        assertTrue(drop.isPresent());
        assertEquals(ItemID.COOKED_PORKCHOP, drop.get().getItem().getId());
    }

    @Test
    void checkObstruction() {
        level.setBlockStateAt(1, 1, 3, BlockState.of(BlockID.GRASS));
        level.setBlockStateAt(1, 2, 3, BlockState.of(BlockID.DOOR_BLOCK));
        EntityObstructionTest entity = new EntityObstructionTest(level.getChunk(0, 0), Entity.getDefaultNBT(new Vector3(1, 2, 3)));
        assertFalse(entity.checkObstruction(1, 2, 3));
        level.setBlockStateAt(1, 2, 3, BlockState.of(BlockID.STONE));
        assertTrue(entity.checkObstruction(1, 2, 3));
    }

    @Test
    void teleport() {
        entity = createEntity(EntityPig.NETWORK_ID);
        entity.yaw = 1.0;
        entity.pitch = 2.0;
        entity.headYaw = 3.0;
        assertTrue(entity.teleport(new Position(5, 6, 7), PlayerTeleportEvent.TeleportCause.PLUGIN));
        assertEquals(1, entity.yaw);
        assertEquals(2, entity.pitch);
        assertEquals(3, entity.headYaw);
        assertEquals(5, entity.x);
        assertEquals(6, entity.y);
        assertEquals(7, entity.z);
    }

    @Test
    void setRotation() {
        entity = createEntity(EntityChicken.NETWORK_ID);
        entity.setRotation(1, 2, 3);
        assertEquals(1, entity.yaw);
        assertEquals(2, entity.pitch);
        assertEquals(3, entity.headYaw);
    }

    @Test
    void setPositionAndRotation() {
        entity = createEntity(EntityBlaze.NETWORK_ID);
        entity.setPositionAndRotation(new Vector3(1, 2, 3), 4, 5, 6);
        assertEquals(1, entity.x);
        assertEquals(2, entity.y);
        assertEquals(3, entity.z);
        assertEquals(4, entity.yaw);
        assertEquals(5, entity.pitch);
        assertEquals(6, entity.headYaw);
    }

    @Test
    void fallSlowFalling() {
        entity = createEntity(EntityZombie.NETWORK_ID);
        level.setBlockStateAt(1, 2, 3, BlockState.of(BlockID.STONE));
        entity.setPosition(new Vector3(1, 3, 3));
        entity.addEffect(Effect.getEffect(Effect.SLOW_FALLING));
        float health = entity.getHealth();
        entity.fall(200);
        assertEquals(health, entity.getHealth());

        entity.removeAllEffects();
        entity.setMaxHealth(1000);
        entity.setHealth(1000);
        entity.fall(200);
        assertEquals(803, entity.getHealth());

        entity.removeAllEffects();
        entity.setHealth(1000);
        entity.noDamageTicks = 0;
        entity.entityBaseTick(10);
        level.setBlockStateAt(1, 2, 3, BlockState.of(BlockID.HAY_BALE));
        entity.fall(200);
        assertEquals(960.6f, entity.getHealth());

        entity.setHealth(1000);
        entity.noDamageTicks = 0;
        entity.entityBaseTick(10);
        level.setBlockStateAt(1, 2, 3, BlockState.of(BlockID.HONEY_BLOCK));
        entity.fall(200);
        assertEquals(960.6f, entity.getHealth());
    }

    @ParameterizedTest
    @MethodSource("getEntityIdStream")
    void testNames(String id) {
        if (id.equals("Human") && Entity.getSaveId(id).orElse(-1) == -1) {
            return;
        }
        entity = createEntity(id);
        assertNotNull(entity, ()-> "Entity " + Entity.getSaveId(id));
        assertNotNull(entity.getOriginalName(), "Static Name");
        String staticName = entity.getOriginalName();
        assertEquals(staticName, entity.getName());
        assertFalse(entity.hasCustomName(), "Should not have custom");
        assertEquals(entity.getName(), entity.getVisibleName());
        
        if (entity instanceof EntityNameable) {
            EntityNameable nameable = (EntityNameable) entity;
            nameable.setNameTag("Customized");
            assertTrue(entity.hasCustomName(), "Should have custom");
            assertNotNull(entity.getOriginalName(), "Static name should not be null");
            assertEquals(staticName, entity.getOriginalName(), "Static name should not change");
            assertEquals("Customized", entity.getName());
            assertNotEquals(entity.getName(), entity.getOriginalName());
            
            nameable.setNameTag(" ");
            assertTrue(entity.hasCustomName());
            assertNotNull(entity.getOriginalName());
            assertEquals(" ", entity.getName());
            assertNotEquals(entity.getName(), entity.getOriginalName());
            assertEquals(entity.getOriginalName(), entity.getVisibleName());
        }
    }

    Entity createEntity(int id) {
        return createEntity(Integer.toString(id));
    }

    Entity createEntity(String id) {
        return Entity.createEntity(id, chunk, Entity.getDefaultNBT(new Vector3(0, 64, 0)));
    }

    static Stream<String> getEntityIdStream() {
        return Arrays.stream(Entity.getKnownEntityIds().toIntArray())
                .mapToObj(Entity::getSaveId)
                .map(Objects::requireNonNull);
    }

    @AfterEach
    void tearDown() {
        try {
            if (entity != null) {
                entity.close();
            }
        } finally {
            entity = null;
        }
    }

    static class EntityObstructionTest extends Entity {
        @Override
        public int getNetworkId() {
            return EntityItem.NETWORK_ID;
        }

        public EntityObstructionTest(FullChunk chunk, CompoundTag nbt) {
            super(chunk, nbt);
        }

        @Override
        public boolean checkObstruction(double x, double y, double z) {
            return super.checkObstruction(x, y, z);
        }

        @Override
        public float getWidth() {
            return 1;
        }

        @Override
        public float getHeight() {
            return 1;
        }
    }
}
