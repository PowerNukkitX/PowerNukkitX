package cn.nukkit.command.selector.args.impl;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.selector.ParseUtils;
import cn.nukkit.command.selector.SelectorType;
import cn.nukkit.command.selector.args.CachedSimpleSelectorArgument;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.level.Location;
import cn.nukkit.registry.Registries;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Selector argument implementation for the 'type' parameter in Minecraft selectors (PowerNukkitX).
 * <p>
 * The 'type' argument is used to filter entities by their type identifier. It allows specifying required types (entities must match all specified types)
 * and excluded types (entities must not match any of the specified types). Negation is supported by prefixing a type with '!' (e.g., @e[type=!zombie]).
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Supports the 'type' selector argument for entity type-based filtering.</li>
 *   <li>Allows multiple types, with negation for exclusion.</li>
 *   <li>Parses arguments into required and excluded type lists, with automatic namespace completion (e.g., 'zombie' becomes 'minecraft:zombie').</li>
 *   <li>Handles vanilla, custom, and player entity types.</li>
 *   <li>Returns a predicate that checks if an entity matches all required types and none of the excluded types.</li>
 *   <li>Provides a default value of 'minecraft:player' for random player selectors.</li>
 *   <li>Integrates with the PowerNukkitX selector argument system via {@link CachedSimpleSelectorArgument}.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used internally by the entity selector system for @e[type=...] and similar selectors.</li>
 *   <li>Not intended for direct instantiation; registered automatically.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // @e[type=zombie] selects entities of type 'minecraft:zombie'
 * // @e[type=!player] selects entities not of type 'minecraft:player'
 * // @e[type=zombie,skeleton] selects entities of type 'minecraft:zombie' and 'minecraft:skeleton'
 * // @e[type=zombie,!skeleton] selects entities of type 'minecraft:zombie' but not 'minecraft:skeleton'
 * </pre>
 *
 * <b>Argument Rules:</b>
 * <ul>
 *   <li>Multiple arguments are allowed (comma-separated).</li>
 *   <li>Negation is supported by prefixing with '!'.</li>
 *   <li>Empty or null arguments are ignored.</li>
 *   <li>Namespace completion is performed for vanilla types (e.g., 'zombie' becomes 'minecraft:zombie').</li>
 *   <li>Custom entity types are supported without namespace completion.</li>
 * </ul>
 *
 * <b>Type Resolution:</b>
 * <ul>
 *   <li>Uses {@link Registries.ENTITY} to resolve type names and network IDs.</li>
 *   <li>Handles player entities and custom entities with special logic.</li>
 * </ul>
 *
 * @author PowerNukkitX Project Team
 * @see CachedSimpleSelectorArgument
 * @see cn.nukkit.command.selector.ParseUtils
 * @see cn.nukkit.command.selector.SelectorType
 * @see cn.nukkit.command.CommandSender
 * @see cn.nukkit.level.Location
 * @see cn.nukkit.entity.Entity
 * @see cn.nukkit.entity.custom.CustomEntity
 * @see cn.nukkit.registry.Registries
 * @since PowerNukkitX 2.0.0
 */
public class Type extends CachedSimpleSelectorArgument {

    public static final Map<Integer, String> ENTITY_ID2TYPE = Registries.ENTITY.getEntityId2NetworkIdMap();
    public static final Map<String, Integer> ENTITY_TYPE2ID;

    static {
        ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
        ENTITY_ID2TYPE.forEach((id, name) -> builder.put(name, id));
        ENTITY_TYPE2ID = builder.build();
    }

    @Override
    protected Predicate<Entity> cache(SelectorType selectorType, CommandSender sender, Location basePos, String... arguments) {
        final var have = new ArrayList<String>();
        final var dontHave = new ArrayList<String>();
        for (var type : arguments) {
            boolean reversed = ParseUtils.checkReversed(type);
            if (reversed) {
                type = completionPrefix(type.substring(1));
                dontHave.add(type);
            } else have.add(completionPrefix(type));
        }
        return entity -> have.stream().allMatch(type -> isType(entity, type)) && dontHave.stream().noneMatch(type -> isType(entity, type));
    }

    @Override
    public @Nullable String getDefaultValue(Map<String, List<String>> values, SelectorType selectorType, CommandSender sender) {
        return selectorType == SelectorType.RANDOM_PLAYER ? "minecraft:player" : null;
    }

    @Override
    public String getKeyName() {
        return "type";
    }

    @Override
    public int getPriority() {
        return 4;
    }

    protected String completionPrefix(String type) {
        var completed = type.contains(":") ? type : "minecraft:" + type;
        if (!ENTITY_TYPE2ID.containsKey(type) && !ENTITY_TYPE2ID.containsKey(completed)) {
            // It is a custom creature and does not need to be completed
            return type;
        }
        return completed;
    }

    protected boolean isType(Entity entity, String type) {
        if (entity instanceof Player)
            // The player needs a special judgment because EntityHuman's getNetworkId() returns -1
            return type.equals("minecraft:player");
        else if (entity instanceof CustomEntity)
            return entity.getIdentifier().equals(type);
        else
            return ENTITY_TYPE2ID.containsKey(type) && entity.getNetworkId() == ENTITY_TYPE2ID.get(type);

    }
}
