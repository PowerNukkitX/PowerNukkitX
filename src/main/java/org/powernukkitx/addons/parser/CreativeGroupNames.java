package org.powernukkitx.addons.parser;

/**
 * Behaviour packs name vanilla creative groups with a namespace, {@code minecraft:itemGroup.name.sapling},
 * while the server's group list, read from vanilla data, names them without one. Custom groups keep
 * their own namespace.
 */
final class CreativeGroupNames {

    private static final String VANILLA_NAMESPACE = "minecraft:";

    private CreativeGroupNames() {
    }

    /**
     * @return the group name in the form the creative registry uses
     */
    static String normalize(String group) {
        return group.startsWith(VANILLA_NAMESPACE) ? group.substring(VANILLA_NAMESPACE.length()) : group;
    }
}
