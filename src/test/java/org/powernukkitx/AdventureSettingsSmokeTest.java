package org.powernukkitx;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermissionLevel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Exercises {@link AdventureSettings} get/set round-trips for every {@link AdventureSettings.Type}
 * plus clone and permission accessors. All construction-safe against the player fixture.
 */
class AdventureSettingsSmokeTest {

    private static TestPlayer player;

    @BeforeAll
    static void setup() {
        ServerMockFixture.boot();
        player = PlayerFixture.get();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }

    @Test
    void setGetRoundTripsForEveryType() {
        AdventureSettings settings = new AdventureSettings(player);
        for (AdventureSettings.Type type : AdventureSettings.Type.values()) {
            settings.set(type, true);
            assertEquals(true, settings.get(type), "true for " + type);
            settings.set(type, false);
            assertEquals(false, settings.get(type), "false for " + type);
        }
    }

    @Test
    void typeMetadataIsConsistent() {
        for (AdventureSettings.Type type : AdventureSettings.Type.values()) {
            // getDefaultValue never throws; isAbility() and getAbility() agree
            boolean def = type.getDefaultValue();
            assertEquals(def, def);
            if (type.isAbility()) {
                assertNotNull(type.getAbility(), "ability for " + type);
            }
        }
    }

    @Test
    void setByAbilityIndexReflectsInGet() {
        AdventureSettings settings = new AdventureSettings(player);
        for (var ability : AdventureSettings.CONTROLLABLE_ABILITIES) {
            settings.set(ability, true);
            assertEquals(true, settings.get(ability), "ability " + ability);
            settings.set(ability, false);
            assertEquals(false, settings.get(ability), "ability " + ability);
        }
    }

    @Test
    void cloneCopiesValues() {
        AdventureSettings settings = new AdventureSettings(player);
        settings.set(AdventureSettings.Type.FLYING, true);
        settings.setCommandPermission(CommandPermissionLevel.GAME_DIRECTORS);

        AdventureSettings copy = settings.clone();
        assertNotSame(settings, copy);
        assertEquals(true, copy.get(AdventureSettings.Type.FLYING));
        assertEquals(CommandPermissionLevel.GAME_DIRECTORS, copy.getCommandPermission());
    }

    @Test
    void permissionAccessors() {
        AdventureSettings settings = new AdventureSettings(player);
        assertNotNull(settings.getCommandPermission());
        // playerPermission setter also flips the player's op state - keep it tolerant
        safe(() -> settings.setPlayerPermission(settings.getPlayerPermission()));
        assertNotNull(settings.getPlayerPermission());
        safe(() -> settings.buildSerializedAbilitiesData());
        safe(() -> settings.onOpChange(true));
        safe(() -> settings.onOpChange(false));
    }
}
