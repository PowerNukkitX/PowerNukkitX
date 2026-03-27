package cn.nukkit.entity.components;

import java.util.*;

import org.jetbrains.annotations.Nullable;

/**
 * Bedrock component: {@code minecraft:equippable}.
 *
 * Defines equipment slots available on an entity and which items are accepted
 * in each slot (e.g. saddle, horse armor, carpets).
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code slot} — UI slot index (0+).</li>
 *   <li>{@code type} — Logical slot type used by gameplay rules (defaults to {@link Type#GENERIC}).</li>
 *   <li>{@code acceptedItems} — Accepted item identifiers. If empty, the slot accepts any item.</li>
 *   <li>{@code interactText} — Optional interaction text key shown to the player (defaults to "").</li>
 * </ul>
 * 
 * @author Curse
 */
public record EquippableComponent(List<EquippableComponent.Slot> slots) {
    public EquippableComponent {
        if (slots == null || slots.isEmpty()) {
            slots = Collections.emptyList();
        } else {
            ArrayList<Slot> out = new ArrayList<>(slots.size());
            for (Slot s : slots) {
                if (s != null) out.add(s);
            }
            slots = out.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(out);
        }
    }

    public static EquippableComponent defaults() {
        return new EquippableComponent(Collections.emptyList());
    }

    public enum Type {
        SADDLE("saddle"),
        HORSE_ARMOR("horsearmoriron"),
        NAUTILUS_ARMOR("nautilusarmor"),
        CARPET("carpet"),
        GENERIC("generic");

        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public static Type from(String value) {
            if (value == null) return GENERIC;
            String v = value.trim().toLowerCase();
            for (Type t : values()) {
                if (t.id.equals(v)) return t;
            }
            return GENERIC;
        }
    }

    public record Slot(int slot, Type type, Set<String> acceptedItems, String interactText) {
        public Slot {
            slot = Math.max(0, slot);

            type = (type == null) ? Type.GENERIC : type;

            interactText = (interactText == null) ? "" : interactText;

            if (acceptedItems == null || acceptedItems.isEmpty()) {
                acceptedItems = Collections.emptySet();
            } else {
                LinkedHashSet<String> out = new LinkedHashSet<>();
                for (String v : acceptedItems) {
                    if (v == null) continue;
                    String s = v.trim();
                    if (s.isEmpty()) continue;
                    out.add(s.toLowerCase());
                }
                acceptedItems = out.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(out);
            }
        }

        public static Slot defaults() {
            return new Slot(
                    0,
                    Type.GENERIC,
                    Collections.emptySet(),
                    ""
            );
        }

        public boolean accepts(String identifier) {
            if (identifier == null) return false;
            String id = identifier.trim().toLowerCase();
            if (id.isEmpty()) return false;
            if (acceptedItems.isEmpty()) return true;
            return acceptedItems.contains(id);
        }

        public boolean hasEquippedItem(String itemId) {
            return itemId != null && !itemId.isEmpty();
        }
    }

    public int getEquipCount() {
        if (slots == null || slots.isEmpty()) return 0;

        int count = 0;
        for (Slot s : slots) {
            if (s != null) count++;
        }
        return count;
    }

    public int getPackedIndexForType(Type type) {
        if (slots == null || slots.isEmpty()) return -1;

        int idx = 0;
        for (Slot s : slots.stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(Slot::slot)).toList()) {
            if (s.type() == type) return idx;
            idx++;
        }
        return -1;
    }

    public int getPackedIndexForSlotNumber(int slotNumber) {
        if (slots == null || slots.isEmpty()) return -1;

        int idx = 0;
        for (Slot s : slots.stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(Slot::slot)).toList()) {
            if (s.slot() == slotNumber) return idx;
            idx++;
        }
        return -1;
    }

    public @Nullable Type getTypeByUiSlot(int uiSlotNumber) {
        if (slots == null) return null;
        for (var s : slots) {
            if (s != null && s.slot() == uiSlotNumber) return s.type();
        }
        return null;
    }

    public List<Slot> getSortedSlots() {
        if (slots == null || slots.isEmpty()) return List.of();
        ArrayList<Slot> defs = new ArrayList<>(slots.size());
        for (var s : slots) if (s != null) defs.add(s);
        defs.sort(Comparator.comparingInt(Slot::slot));
        return defs;
    }

}
