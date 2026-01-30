package cn.nukkit.network.protocol.types.inventory.transaction;


import cn.nukkit.inventory.SpecialWindowId;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InventorySource {
    private static final InventorySource CREATIVE_SOURCE = new InventorySource(Type.CREATIVE, SpecialWindowId.NONE.getId(), Flag.NONE);
    private static final InventorySource GLOBAL_SOURCE = new InventorySource(Type.GLOBAL, SpecialWindowId.NONE.getId(), Flag.NONE);
    private static final InventorySource INVALID_SOURCE = new InventorySource(Type.INVALID, SpecialWindowId.NONE.getId(), Flag.NONE);
    Type type;
    int containerId;
    Flag flag;

    public static InventorySource fromContainerWindowId(int containerId) {
        return new InventorySource(Type.CONTAINER, containerId, Flag.NONE);
    }

    public static InventorySource fromCreativeInventory() {
        return CREATIVE_SOURCE;
    }

    public static InventorySource fromGlobalInventory() {
        return GLOBAL_SOURCE;
    }

    public static InventorySource fromInvalid() {
        return INVALID_SOURCE;
    }

    public static InventorySource fromNonImplementedTodo(int containerId) {
        return new InventorySource(Type.NON_IMPLEMENTED_TODO, containerId, Flag.NONE);
    }

    public static InventorySource fromUntrackedInteractionUI(int containerId) {
        return new InventorySource(Type.UNTRACKED_INTERACTION_UI, containerId, Flag.NONE);
    }

    public static InventorySource fromWorldInteraction(@NotNull Flag flag) {
        return new InventorySource(Type.WORLD_INTERACTION, SpecialWindowId.NONE.getId(), flag);
    }

    public enum Type {
        INVALID(-1),
        CONTAINER(0),
        GLOBAL(1),
        WORLD_INTERACTION(2),
        CREATIVE(3),
        UNTRACKED_INTERACTION_UI(100),
        NON_IMPLEMENTED_TODO(99999);

        private static final Int2ObjectMap<Type> BY_ID = new Int2ObjectOpenHashMap<>(6);

        static {
            for (Type type : values()) {
                BY_ID.put(type.id, type);
            }
        }

        private final int id;

        Type(int id) {
            this.id = id;
        }

        public static Type byId(int id) {
            Type type = BY_ID.get(id);
            return type == null ? INVALID : type;
        }

        public int id() {
            return id;
        }
    }

    public enum Flag {
        DROP_ITEM,
        PICKUP_ITEM,
        NONE
    }
}
