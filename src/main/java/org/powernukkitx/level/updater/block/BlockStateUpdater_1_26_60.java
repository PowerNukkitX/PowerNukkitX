package org.powernukkitx.level.updater.block;

import org.powernukkitx.level.updater.Updater;
import org.powernukkitx.level.updater.util.tagupdater.CompoundTagUpdaterContext;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 1.26.60 gave chorus plants, fire and redstone wire the shape they draw with, as block states.
 * Older states get the defaults here, the real shape is derived once the chunk is loaded.
 *
 * @author xRookieFight
 * @since 15/10/2026
 */
public class BlockStateUpdater_1_26_60 implements Updater {

    public static final Updater INSTANCE = new BlockStateUpdater_1_26_60();

    /**
     * Every property each block gained, with the value a state saved before 1.26.60 starts from
     */
    public static final Map<String, Map<String, Object>> ADDED_PROPERTIES = Map.of(
        "minecraft:chorus_plant", connections("minecraft:connection_down", "minecraft:connection_east",
            "minecraft:connection_north", "minecraft:connection_south", "minecraft:connection_up",
            "minecraft:connection_west"),
        "minecraft:fire", connections("minecraft:connection_east", "minecraft:connection_north",
            "minecraft:connection_south", "minecraft:connection_up", "minecraft:connection_west"),
        "minecraft:redstone_wire", Map.of(
            "redstone_east", "none",
            "redstone_north", "none",
            "redstone_south", "none",
            "redstone_west", "none")
    );

    private static Map<String, Object> connections(String... properties) {
        return Arrays.stream(properties)
            .collect(Collectors.toUnmodifiableMap(property -> property, property -> (Object) (byte) 0));
    }

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ADDED_PROPERTIES.forEach((identifier, properties) -> {
            var builder = ctx.addUpdater(1, 26, 60, false, false)
                .match("name", identifier)
                .visit("states");
            properties.forEach(builder::tryAdd);
        });
    }
}
