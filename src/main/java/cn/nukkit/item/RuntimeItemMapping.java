package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.item.RuntimeItems.MappingEntry;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.utils.BinaryStream;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

@Log4j2
public class RuntimeItemMapping {
    private final Int2ObjectMap<LegacyEntry> runtime2Legacy = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<RuntimeEntry> legacy2Runtime = new Int2ObjectOpenHashMap<>();//legacyFullID to Runtime
    private final Map<String, LegacyEntry> identifier2Legacy = new HashMap<>();
    @PowerNukkitXOnly
    @Since("1.19.70-r2")
    private final List<RuntimeEntry> itemPaletteEntries = new ArrayList<>();
    @PowerNukkitXOnly
    @Since("1.19.70-r2")
    private final Int2ObjectMap<String> runtimeId2Name = new Int2ObjectOpenHashMap<>();
    @PowerNukkitXOnly
    @Since("1.19.70-r2")
    private final Object2IntMap<String> name2RuntimeId = new Object2IntOpenHashMap<>();
    @PowerNukkitXOnly
    @Since("1.19.70-r2")
    private final Map<String, Supplier<Item>> namespacedIdItem = new HashMap<>();
    private byte[] itemPalette;

    public RuntimeItemMapping(Map<String, MappingEntry> mappings) {
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_states.json")) {
            if (stream == null) {
                throw new AssertionError("Unable to load runtime_item_states.json");
            }
            JsonArray runtimeItems = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();

            for (JsonElement element : runtimeItems) {
                if (!element.isJsonObject()) {
                    throw new IllegalStateException("Invalid entry");
                }
                JsonObject entry = element.getAsJsonObject();
                String identifier = entry.get("name").getAsString();
                int runtimeId = entry.get("id").getAsInt();
                this.runtimeId2Name.put(runtimeId, identifier);
                this.name2RuntimeId.put(identifier, runtimeId);
                boolean hasDamage = false;
                int damage = 0;
                int legacyId;
                if (mappings.containsKey(identifier)) {
                    MappingEntry mapping = mappings.get(identifier);
                    legacyId = RuntimeItems.getLegacyIdFromLegacyString(mapping.legacyName());
                    if (legacyId == -1) {
                        throw new IllegalStateException("Unable to match  " + mapping + " with legacyId");
                    }
                    damage = mapping.damage();
                    hasDamage = true;
                } else {
                    legacyId = RuntimeItems.getLegacyIdFromLegacyString(identifier);
                    if (legacyId == -1) {
                        log.trace("Unable to find legacyId for " + identifier);
                        continue;
                    }
                }

                int fullId = RuntimeItems.getFullId(legacyId, damage);
                LegacyEntry legacyEntry = new LegacyEntry(legacyId, hasDamage, damage);
                RuntimeEntry runtimeEntry = new RuntimeEntry(identifier, runtimeId, hasDamage, false);
                this.runtime2Legacy.put(runtimeId, legacyEntry);
                this.identifier2Legacy.put(identifier, legacyEntry);
                this.legacy2Runtime.put(fullId, runtimeEntry);
                this.itemPaletteEntries.add(runtimeEntry);
            }

            this.generatePalette();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generatePalette() {
        if (this.itemPaletteEntries.isEmpty()) {
            return;
        }
        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(this.itemPaletteEntries.size());
        for (RuntimeEntry entry : this.itemPaletteEntries) {
            paletteBuffer.putString(entry.identifier());
            paletteBuffer.putLShort(entry.runtimeId());
            paletteBuffer.putBoolean(entry.isComponent()); // Component item
        }
        this.itemPalette = paletteBuffer.getBuffer();
    }

    public LegacyEntry fromRuntime(int runtimeId) {
        LegacyEntry legacyEntry = this.runtime2Legacy.get(runtimeId);
        if (legacyEntry == null) {
            throw new IllegalArgumentException("Unknown runtime2Legacy mapping: " + runtimeId);
        }
        return legacyEntry;
    }

    public RuntimeEntry toRuntime(int id, int meta) {
        RuntimeEntry runtimeEntry = this.legacy2Runtime.get(RuntimeItems.getFullId(id, meta));
        if (runtimeEntry == null) {
            runtimeEntry = this.legacy2Runtime.get(RuntimeItems.getFullId(id, 0));
        }

        if (runtimeEntry == null) {
            throw new IllegalArgumentException("Unknown legacy2Runtime mapping: id=" + id + " meta=" + meta);
        }
        return runtimeEntry;
    }

    public LegacyEntry fromIdentifier(String identifier) {
        return this.identifier2Legacy.get(identifier);
    }

    public byte[] getItemPalette() {
        return this.itemPalette;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void registerCustomItem(CustomItem customItem, Supplier<Item> constructor) {
        var runtimeId = CustomItemDefinition.getRuntimeId(customItem.getNamespaceId());
        RuntimeEntry entry = new RuntimeEntry(
                customItem.getNamespaceId(),
                runtimeId,
                false,
                true
        );
        this.itemPaletteEntries.add(entry);
        this.runtimeId2Name.put(runtimeId, customItem.getNamespaceId());
        this.name2RuntimeId.put(customItem.getNamespaceId(), runtimeId);
        this.registerNamespacedIdItem(customItem.getNamespaceId(), constructor);
        this.generatePalette();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void deleteCustomItem(CustomItem customItem) {
        this.runtimeId2Name.remove(customItem.getId());
        this.name2RuntimeId.removeInt(customItem.getNamespaceId());
        this.itemPaletteEntries.removeIf(next -> next.identifier().equals(customItem.getNamespaceId()));
        this.generatePalette();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void registerCustomBlock(List<CustomBlock> blocks) {
        for (var block : blocks) {
            int id = 255 - block.getId();//方块物品id等于 255-方块id(即-750开始递减)
            RuntimeEntry entry = new RuntimeEntry(
                    block.getNamespaceId(),//方块命名空间也是方块物品命名空间
                    id,
                    false,
                    true
            );
            LegacyEntry legacyEntry = new LegacyEntry(id, false, 0);
            this.itemPaletteEntries.add(entry);
            this.namespacedIdItem.put(block.getNamespaceId(), block::toItem);

            this.identifier2Legacy.put(block.getNamespaceId(), legacyEntry);
            this.legacy2Runtime.put(RuntimeItems.getFullId(id, 0), entry);
            this.runtime2Legacy.put(id, legacyEntry);

            this.runtimeId2Name.put(id, block.getNamespaceId());
            this.name2RuntimeId.put(block.getNamespaceId(), id);
        }
        this.generatePalette();
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void deleteCustomBlock(List<CustomBlock> blocks) {
        for (var block : blocks) {
            this.runtimeId2Name.remove(block.getId());
            this.name2RuntimeId.removeInt(block.getNamespaceId());

            this.namespacedIdItem.remove(block.getNamespaceId());
            this.identifier2Legacy.remove(block.getNamespaceId());
            this.legacy2Runtime.remove(RuntimeItems.getFullId(255 - block.getId(), 0));

            this.runtime2Legacy.remove(255 - block.getId());
        }
        var iter = this.itemPaletteEntries.iterator();
        while (iter.hasNext()) {
            RuntimeEntry next = iter.next();
            for (var block : blocks) {
                if (block.getNamespaceId().equals(next.identifier())) {
                    iter.remove();
                    break;
                }
            }
        }
        this.generatePalette();
    }

    /**
     * Returns the <b>network id</b> based on the <b>full id</b> of the given item.
     *
     * @param item Given item
     * @return The <b>network id</b>
     * @throws IllegalArgumentException If the mapping of the <b>full id</b> to the <b>network id</b> is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getNetworkId(Item item) {
        if (item instanceof StringItem) {
            return name2RuntimeId.getInt(item.getNamespaceId());
        }

        int fullId = RuntimeItems.getFullId(item.getId(), item.getDamage());
        if (!item.hasMeta() && item.getDamage() != 0) { // Fuzzy crafting recipe of a remapped item, like charcoal
            fullId = RuntimeItems.getFullId(item.getId(), item.getDamage());
        }
        RuntimeEntry runtimeEntry = legacy2Runtime.get(fullId);
        if (runtimeEntry == null) {
            runtimeEntry = legacy2Runtime.get(RuntimeItems.getFullId(item.getId(), 0));
        }
        if (runtimeEntry == null) {
            throw new IllegalArgumentException("Unknown item mapping " + item);
        }
        return runtimeEntry.runtimeId;
    }

    /**
     * Returns the <b>full id</b> of a given <b>network id</b>.
     *
     * @param networkId The given <b>network id</b>
     * @return The <b>full id</b>
     * @throws IllegalArgumentException If the mapping of the <b>full id</b> to the <b>network id</b> is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getLegacyFullId(int networkId) {
        LegacyEntry legacyEntry = runtime2Legacy.get(networkId);
        if (legacyEntry == null) {
            throw new IllegalArgumentException("Unknown network mapping " + networkId);
        }
        return legacyEntry.fullID();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public byte[] getItemDataPalette() {
        return this.itemPalette;
    }

    /**
     * Returns the <b>namespaced id</b> of a given <b>network id</b>.
     *
     * @param networkId The given <b>network id</b>
     * @return The <b>namespace id</b> or {@code null} if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public String getNamespacedIdByNetworkId(int networkId) {
        return runtimeId2Name.get(networkId);
    }

    /**
     * Returns the <b>network id</b> of a given <b>namespaced id</b>.
     *
     * @param namespaceId The given <b>namespaced id</b>
     * @return A <b>network id</b> wrapped in {@link OptionalInt} or an empty {@link OptionalInt} if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public OptionalInt getNetworkIdByNamespaceId(@NotNull String namespaceId) {
        int id = name2RuntimeId.getOrDefault(namespaceId, -1);
        if (id == -1) return OptionalInt.empty();
        return OptionalInt.of(id);
    }

    /**
     * Creates a new instance of the respective {@link Item} by the <b>namespaced id</b>.
     *
     * @param namespaceId The namespaced id
     * @param amount      How many items will be in the stack.
     * @return The correct {@link Item} instance with the write <b>item id</b> and <b>item damage</b> values.
     * @throws IllegalArgumentException If there are unknown mappings in the process.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public Item getItemByNamespaceId(@NotNull String namespaceId, int amount) {
        Supplier<Item> constructor = this.namespacedIdItem.get(namespaceId.toLowerCase(Locale.ENGLISH));
        if (constructor != null) {
            try {
                Item item = constructor.get();
                item.setCount(amount);
                return item;
            } catch (Exception e) {
                log.warn("Could not create a new instance of {} using the namespaced id {}", constructor, namespaceId, e);
            }
        }

        int legacyFullId;
        try {
            legacyFullId = getLegacyFullId(
                    getNetworkIdByNamespaceId(namespaceId)
                            .orElseThrow(() -> new IllegalArgumentException("The network id of \"" + namespaceId + "\" is unknown"))
            );
        } catch (IllegalArgumentException e) {
            log.debug("Found an unknown item {}", namespaceId, e);
            Item item = new StringItemUnknown(namespaceId);
            item.setCount(amount);
            return item;
        }

        int id = RuntimeItems.getId(legacyFullId);
        int data = 0;
        if (RuntimeItems.hasData(legacyFullId)) {
            data = RuntimeItems.getData(legacyFullId);
        }
        return Item.get(id, data, amount);
    }


    @SneakyThrows
    @PowerNukkitXOnly
    @Since("1.19.70-r2")
    public void registerNamespacedIdItem(@NotNull Class<? extends StringItem> item) {
        Constructor<? extends StringItem> declaredConstructor = item.getDeclaredConstructor();
        var Item = declaredConstructor.newInstance();
        registerNamespacedIdItem(Item.getNamespaceId(), stringItemSupplier(declaredConstructor));
    }

    @PowerNukkitOnly
    public void registerNamespacedIdItem(@NotNull String namespacedId, @NotNull Constructor<? extends Item> constructor) {
        Preconditions.checkNotNull(namespacedId, "namespacedId is null");
        Preconditions.checkNotNull(constructor, "constructor is null");
        this.namespacedIdItem.put(namespacedId.toLowerCase(Locale.ENGLISH), itemSupplier(constructor));
    }

    @SneakyThrows
    @PowerNukkitOnly
    public void registerNamespacedIdItem(@NotNull String namespacedId, @NotNull Supplier<Item> constructor) {
        Preconditions.checkNotNull(namespacedId, "namespacedId is null");
        Preconditions.checkNotNull(constructor, "constructor is null");
        this.namespacedIdItem.put(namespacedId.toLowerCase(Locale.ENGLISH), constructor);
    }

    @NotNull
    private static Supplier<Item> itemSupplier(@NotNull Constructor<? extends Item> constructor) {
        return () -> {
            try {
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new UnsupportedOperationException(e);
            }
        };
    }

    @Since("1.19.60-r1")
    @PowerNukkitXOnly
    @NotNull
    private static Supplier<Item> stringItemSupplier(@NotNull Constructor<? extends StringItem> constructor) {
        return () -> {
            try {
                return (Item) constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new UnsupportedOperationException(e);
            }
        };
    }

    public record LegacyEntry(int legacyId, boolean hasDamage, int damage) {
        public int getDamage() {
            return this.hasDamage ? this.damage : 0;
        }

        public int fullID() {
            return RuntimeItems.getFullId(legacyId, damage);
        }
    }

    public record RuntimeEntry(String identifier, int runtimeId, boolean hasDamage, boolean isComponent) {
    }
}
