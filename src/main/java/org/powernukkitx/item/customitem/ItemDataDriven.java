package org.powernukkitx.item.customitem;

import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Locale;
import java.util.Set;

/**
 * An item that exists only as data, with no class of its own.
 * <p>
 * Items loaded from a behavior pack have no dedicated Java class: everything about them
 * lives in the {@link CustomItemDefinition} built from the pack's JSON, which the
 * {@link Item} core already reads at runtime for food, durability, wearability, cooldowns
 * and the rest. One instance of this class therefore backs one pack item, and many
 * instances with different definitions back a whole pack.
 */
public class ItemDataDriven extends Item implements CustomItem {

    /**
     * @param id         the item identifier, which must match the definition's
     * @param definition the definition describing the item; null only for the short-lived
     *                   prototype used while the definition is still being built, since
     *                   the builder needs an instance to read the identifier from
     */
    public ItemDataDriven(@NotNull String id, @Nullable CustomItemDefinition definition) {
        this(id, definition, ServerComponents.NONE);
    }

    /**
     * @param serverComponents the behaviour only the server acts on, which is not sent to clients
     */
    public ItemDataDriven(@NotNull String id, @Nullable CustomItemDefinition definition, @NotNull ServerComponents serverComponents) {
        super(id, definition);
        this.serverComponents = serverComponents;
    }

    /**
     * The music an item plays in a jukebox, from {@code minecraft:record}.
     *
     * @param soundEvent       the sound played, such as {@code record.cat}
     * @param duration         how long the record plays, in seconds
     * @param comparatorSignal the signal a comparator reads from a jukebox holding the record, 1 to 13
     */
    public record Record(@NotNull String soundEvent, float duration, int comparatorSignal) {
    }

    /**
     * Behaviour pack components the server acts on and clients do not need.
     *
     * @param record           the record the item is, or null when it is not one
     * @param absorbableCauses the damage causes the item absorbs into its durability while worn, from
     *                         {@code minecraft:damage_absorption}; {@code all} absorbs every cause
     */
    public record ServerComponents(@Nullable Record record, @NotNull Set<String> absorbableCauses) {

        public static final ServerComponents NONE = new ServerComponents(null, Set.of());

        public ServerComponents {
            absorbableCauses = Set.copyOf(absorbableCauses);
        }
    }

    private final ServerComponents serverComponents;

    /**
     * @return the record the item is, or null when it does not play in a jukebox
     */
    public @Nullable Record getRecord() {
        return serverComponents.record();
    }

    /**
     * @return whether the item, while worn, takes damage of this cause into its durability instead of the wearer
     */
    public boolean absorbs(@NotNull EntityDamageEvent.DamageCause cause) {
        Set<String> causes = serverComponents.absorbableCauses();
        return !causes.isEmpty() && (causes.contains("all") || causes.contains(cause.name().toLowerCase(Locale.ENGLISH)));
    }

    /**
     * @return the definition this item was built with, or null for the prototype that
     * exists only while that definition is being built
     */
    @Override
    public @Nullable CustomItemDefinition getDefinition() {
        return definition() instanceof CustomItemDefinition custom ? custom : null;
    }

    @Override
    public ItemDataDriven clone() {
        return (ItemDataDriven) super.clone();
    }
}
