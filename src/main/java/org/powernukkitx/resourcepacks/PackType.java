package org.powernukkitx.resourcepacks;

/**
 * The side of the pack stack a pack belongs to, as the server sees it.
 * <p>
 * Bedrock keeps resource packs and behaviour packs apart, and the server needs the
 * distinction to know which loaded packs hold data-driven content it has to read. How a
 * pack is announced to clients is decided separately, in the pack handshake.
 */
public enum PackType {
    /**
     * Resource pack: textures, models, sounds and client entity definitions.
     */
    RESOURCES,
    /**
     * Behavior pack: data-driven content (items, blocks, entities, recipes, loot tables,
     * spawn rules) and scripts.
     */
    BEHAVIOR
}
