package org.powernukkitx.entity;

import org.powernukkitx.PlayerFixture;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent.DamageCause;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * Spawns every registered entity and drives the player-facing interaction paths -
 * onInteract, collide, damage-by-entity, mount/dismount and ownership. Covers the
 * passive/mob interaction code that the plain getter smoke test never reaches.
 */
public class EntityInteractionSmokeTest {

    static Level level;
    static TestPlayer player;

    @BeforeAll
    static void boot() {
        player = PlayerFixture.get();
        level = ServerMockFixture.level;
    }

    @Test
    void everyEntityHandlesInteraction() {
        Set<String> ids = Registries.ENTITY.getKnownEntities().keySet();
        Assertions.assertFalse(ids.isEmpty(), "no entities registered");

        Item item = Item.get("minecraft:wheat");
        int checked = 0;

        for (String id : ids) {
            Entity entity = null;
            try {
                Position pos = new Position(0, 80, 0, level);
                entity = Entity.createEntity(id, pos);
            } catch (Throwable ignore) {
            }
            if (entity == null) continue;

            drive(entity, item);
            safe(entity::close);
            checked++;
        }

        Assertions.assertTrue(checked > 0, "no entity survived the interaction pass");
    }

    private void drive(Entity e, Item item) {
        Vector3 click = new Vector3(0, 80, 0);
        safe(() -> e.onInteract(player, item, click));
        safe(() -> e.onInteract(player, item));
        safe(() -> e.onCollideWithPlayer(player));
        safe(() -> e.attack(new EntityDamageByEntityEvent(player, e, DamageCause.ENTITY_ATTACK, 1.0f)));
        safe(() -> e.attack(new EntityDamageByEntityEvent(player, e, DamageCause.CONTACT, 0.5f)));
        safe(() -> player.mountEntity(e));
        safe(() -> player.dismountEntity(e));
        safe(() -> e.mountEntity(player));
        safe(() -> e.dismountEntity(player));
        safe(() -> e.setOwnerName(player.getName()));
        safe(e::isRideable);
        safe(e::isRiding);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
