package cn.nukkit.entity.components;

import java.util.Locale;


/**
 * Bedrock component: {@code minecraft:inventory}.
 *
 * Defines inventory/container capabilities for an entity.
 * This includes the container type, base inventory size, optional
 * strength-based slot expansion, and access restrictions.
 *
 * <p>This component is used by entities such as horses and chest boats to
 * expose a UI container to players. It also controls whether automation
 * (ex: hoppers) can interact with the inventory and whether access is
 * private or restricted to the owner.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code container_type} — Container type identifier.</li>
 *   <li>{@code inventory_size} — Base inventory size (slots).</li>
 *   <li>{@code additional_slots_per_strength} — Extra slots per strength level (if applicable).</li>
 *   <li>{@code can_be_siphoned_from} — If true, automation may extract items (ex: hoppers).</li>
 *   <li>{@code private} — If true, inventory is treated as private.</li>
 *   <li>{@code restrict_to_owner} — If true, only the owner may access the inventory.</li>
 * </ul>
 *
 * <p><b>Defaults</b></p>
 * <ul>
 *   <li>{@code container_type}: {@link Type#NONE}</li>
 *   <li>{@code inventory_size}: 5</li>
 *   <li>{@code additional_slots_per_strength}: 0</li>
 *   <li>{@code can_be_siphoned_from}: false</li>
 *   <li>{@code private}: false</li>
 *   <li>{@code restrict_to_owner}: false</li>
 * </ul>
 */
// TODO: Siphoned
public record InventoryComponent(
        Integer additionalSlotsPerStrength,
        Boolean canBeSiphonedFrom,
        Type containerType,
        Integer inventorySize,
        Boolean isPrivate,
        Boolean restrictToOwner
    ) {
    public InventoryComponent {
        if (additionalSlotsPerStrength != null) {
            if (additionalSlotsPerStrength < 0) additionalSlotsPerStrength = 0;
        }

        if (inventorySize != null) {
            if (inventorySize < 0) inventorySize = 0;
        }
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return additionalSlotsPerStrength == null
                && canBeSiphonedFrom == null
                && containerType == null
                && inventorySize == null
                && isPrivate == null
                && restrictToOwner == null;
    }

    public int strengthModifier() {
        return additionalSlotsPerStrength != null ? additionalSlotsPerStrength : 0;
    }

    public boolean resolvedCanBeSiphonedFrom() {
        return canBeSiphonedFrom != null ? canBeSiphonedFrom : false;
    }

    public Type resolvedType() {
        return containerType != null ? containerType : Type.NONE;
    }

    public int size() {
        return inventorySize != null ? inventorySize : 5;
    }

    public boolean isPrivateInventory() {
        return isPrivate != null ? isPrivate : false;
    }

    public boolean isRestrictedToOwner() {
        return restrictToOwner != null ? restrictToOwner : false;
    }

    public static InventoryComponent defaults() {
        return new InventoryComponent(null, null, null, null, null, null);
    }

    public enum Type {
        NONE(0),
        HORSE(12),
        MINECART_CHEST(10),
        CHEST_BOAT(10),
        MINECART_HOPPER(11),
        INVENTORY(0),
        CONTAINER(0),
        HOPPER(11);

        private final int bedrockId;

        Type(int bedrockId) {
            this.bedrockId = bedrockId;
        }

        /** Bedrock numeric container type ID */
        public int getBedrockId() {
            return bedrockId;
        }

        public static Type fromString(String s) {
            if (s == null) return null;
            String v = s.trim().toLowerCase(Locale.ROOT);
            if (v.isEmpty()) return null;
            return switch (v) {
                case "none" -> NONE;
                case "horse" -> HORSE;
                case "minecart_chest" -> MINECART_CHEST;
                case "chest_boat" -> CHEST_BOAT;
                case "minecart_hopper" -> MINECART_HOPPER;
                case "inventory" -> INVENTORY;
                case "container" -> CONTAINER;
                case "hopper" -> HOPPER;
                default -> null;
            };
        }

        public String toBedrockString() {
            return switch (this) {
                case HORSE -> "horse";
                case MINECART_CHEST -> "minecart_chest";
                case CHEST_BOAT -> "chest_boat";
                case MINECART_HOPPER -> "minecart_hopper";
                case INVENTORY -> "inventory";
                case CONTAINER -> "container";
                case HOPPER -> "hopper";
                case NONE -> "none";
            };
        }
    }

    public int typeId() {
        return resolvedType().getBedrockId();
    }
}
