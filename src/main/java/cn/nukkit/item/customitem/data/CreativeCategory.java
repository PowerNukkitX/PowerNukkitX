package cn.nukkit.item.customitem.data;

import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;

/**
 * Controls the creative tab category for custom items, such as Construction, Nature, etc.
 * <br>Valid values: 1 CONSTRUCTION, 2 NATURE, 3 EQUIPMENT, 4 ITEMS, 6 NONE
 *
 * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-tabs">Bedrock Wiki</a>
 */
public enum CreativeCategory {
    CONSTRUCTION,
    NATURE,
    EQUIPMENT,
    ITEMS,
    NONE;

    public static CreativeCategory fromID(int num) {
        return switch (num) {
            case 1 -> CONSTRUCTION;
            case 2 -> NATURE;
            case 3 -> EQUIPMENT;
            case 4 -> ITEMS;
            case 6 -> NONE;
            default -> NONE;
        };
    }

    public int getId() {
        return switch (this) {
            case CONSTRUCTION -> 1;
            case NATURE -> 2;
            case EQUIPMENT -> 3;
            case ITEMS -> 4;
            case NONE -> 6;
        };
    }

    /**
     * Converts to the corresponding protocol creative item category.
     */
    public CreativeItemCategory getItemCategory() {
        return switch (this) {
            case CONSTRUCTION -> CreativeItemCategory.CONSTRUCTION;
            case NATURE -> CreativeItemCategory.NATURE;
            case EQUIPMENT -> CreativeItemCategory.EQUIPMENT;
            case ITEMS -> CreativeItemCategory.ITEMS;
            case NONE -> CreativeItemCategory.UNDEFINED;
        };
    }
}
