package cn.nukkit.network.protocol.types.inventory.creative;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.nukkit.registry.CreativeGroupsRegistry;

/**
 * Stages custom creative groups for runtime registration.
 * Used by CreativeItemRegistry after default group parsing.
 */
@UtilityClass
public class CreativeCustomGroups {

    private final List<CustomGroupDefinition> customGroups = new ArrayList<>();

    /**
     * Define a custom creative group (not yet registered).
     *
     * @param category the creative item category (e.g. CONSTRUCTION)
     * @param name     the group name
     * @param iconId   the item ID for the icon (e.g. "minecraft:stone")
     */
    public void define(CreativeItemCategory category, String name, String iconId) {
        CustomGroupDefinition def = new CustomGroupDefinition(category, name, iconId);
        customGroups.add(def);

        // Trigger actual injection logic if needed
        CreativeGroupsRegistry.load(def);
    }

    /**
     * Returns the staged custom groups.
     */
    public List<CustomGroupDefinition> getDefinedGroups() {
        return Collections.unmodifiableList(customGroups);
    }

    /**
     * Clears all defined custom groups.
     */
    public void clear() {
        customGroups.clear();
    }

    /**
     * Data holder for a single group definition.
     */
    @Getter
    @AllArgsConstructor
    public static class CustomGroupDefinition {
        private final CreativeItemCategory category;
        private final String name;
        private final String iconId;
    }
}
