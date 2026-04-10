package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.item.ItemWearEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.utils.ItemArmorType;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.JSONUtils;
import cn.nukkit.utils.RuntimeBlockDefinition;
import cn.nukkit.utils.TextFormat;
import com.google.gson.annotations.SerializedName;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.definitions.SimpleItemDefinition;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemUseMethod;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.StringJoiner;

import static cn.nukkit.utils.Utils.dynamic;


/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class Item implements Cloneable, ItemID {
    public static final Item AIR = new ConstAirItem();
    public static final Item[] EMPTY_ARRAY = new Item[0];

    public static String UNKNOWN_STR = "Unknown";
    protected String id;
    protected Identifier identifier;
    protected String name;
    public int meta;
    public int count;
    protected Integer netId;
    protected Block block = null;
    protected boolean hasMeta = true;
    private byte[] tags = EmptyArrays.EMPTY_BYTES;
    private NbtMap cachedNBT;
    private static int STACK_NETWORK_ID_COUNTER = 1;

    private static String DP_DEFAULT_GROUP_UUID() {
        return Server.getDefaultDynamicPropertiesGroupUUID();
    }

    private static final int DP_MAX_STRING_BYTES = Server.getDynamicPropertiesMaxStringBytes();
    private static final double DP_NUMBER_ABS_MAX = Server.getDynamicPropertiesNumberAbsMax();
    private static final String DP_ROOT = Server.getDynamicPropertyRoot();

    public static final int WEARABLE_TIER_LEATHER = 1;
    public static final int WEARABLE_TIER_IRON = 2;
    public static final int WEARABLE_TIER_CHAIN = 3;
    public static final int WEARABLE_TIER_COPPER = 4;
    public static final int WEARABLE_TIER_GOLD = 5;
    public static final int WEARABLE_TIER_DIAMOND = 6;
    public static final int WEARABLE_TIER_NETHERITE = 7;
    public static final int WEARABLE_TIER_OTHER = dynamic(1000);

    private String idConvertToName() {
        if (this.name == null) {
            var path = this.id.split(":")[1];
            StringBuilder result = new StringBuilder();
            String[] parts = path.split("_");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
                }
            }
            this.name = result.toString().trim().intern();
        }
        return this.name;
    }

    public Item(@NotNull String id) {
        this(id, 0);
    }

    public Item(@NotNull String id, int meta) {
        this(id, meta, 1);
    }

    public Item(@NotNull String id, int meta, int count) {
        this(id, meta, count, null);
    }

    public Item(@NotNull String id, int meta, int count, @Nullable String name) {
        this(id, meta, count, name, true);
    }

    public Item(@NotNull String id, int meta, int count, @Nullable String name, boolean autoAssignStackNetworkId) {
        this.id = id.intern();
        this.identifier = new Identifier(id);
        this.count = count;
        if (name != null) {
            this.name = name.intern();
        }
        this.setDamage(meta);
        if (autoAssignStackNetworkId) {
            this.autoAssignStackNetworkId();
        }
    }

    protected Item(@NotNull Block block, int meta, int count, @Nullable String name, boolean autoAssignStackNetworkId) {
        this.id = block.getItemId().intern();
        this.identifier = new Identifier(id);
        this.count = count;
        if (name != null) {
            this.name = name.intern();
        }
        this.block = block;
        this.setDamage(meta);
        if (autoAssignStackNetworkId) {
            this.autoAssignStackNetworkId();
        }
    }

    @ApiStatus.Internal
    public void internalAdjust() {
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public boolean hasMeta() {
        return hasMeta;
    }

    public boolean canBeActivated() {
        return false;
    }

    public static Item get(String id) {
        return get(id, 0);
    }

    public static Item get(String id, int meta) {
        return get(id, meta, 1);
    }

    public static Item get(String id, int meta, int count) {
        return get(id, meta, count, null);
    }

    @NotNull
    public static Item get(String id, int meta, int count, byte[] tags) {
        return get(id, meta, count, tags, true);
    }

    @NotNull
    public static Item get(String id, int meta, int count, byte[] tags, boolean autoAssignStackNetworkId) {
        id = id.contains(":") ? id : "minecraft:" + id;
        Item item = Registries.ITEM.get(id, meta, count, tags);

        if (item instanceof ItemCustomEntitySpawnEgg egg) {
            egg.resolveSpawnEgg(id);
        }

        if (item == null) {
            BlockState itemBlockState = getItemBlockState(id, meta);
            if (itemBlockState == null || itemBlockState == BlockAir.STATE) {
                return Item.AIR;
            }
            item = itemBlockState.toItem();
            item.setCount(count);
            if (tags != null) {
                item.setCompoundTag(tags);
            }
        } else if (autoAssignStackNetworkId) {
            item.autoAssignStackNetworkId();
        }
        return item;
    }

    public void readItemJsonComponents(ItemJsonComponents components) {
        if (components.canPlaceOn != null)
            this.setCanPlaceOn(Arrays.stream(components.canPlaceOn.blocks).map(str -> Block.get(str.startsWith("minecraft:") ? str : "minecraft:" + str)).toArray(Block[]::new));
        if (components.canDestroy != null)
            this.setCanDestroy(Arrays.stream(components.canDestroy.blocks).map(str -> Block.get(str.startsWith("minecraft:") ? str : "minecraft:" + str)).toArray(Block[]::new));
        if (components.itemLock != null)
            this.setItemLockMode(switch (components.itemLock.mode) {
                case ItemJsonComponents.ItemLock.LOCK_IN_SLOT -> ItemLockMode.LOCK_IN_SLOT;
                case ItemJsonComponents.ItemLock.LOCK_IN_INVENTORY -> ItemLockMode.LOCK_IN_INVENTORY;
                default -> ItemLockMode.NONE;
            });
        if (components.keepOnDeath != null)
            this.setKeepOnDeath(true);
    }

    public boolean hasCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        NbtMap tag = this.getNamedTag();
        return tag.containsKey("BlockEntityTag") && tag.get("BlockEntityTag") instanceof NbtMap;

    }

    public Item clearCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return this;
        }
        NbtMap tag = this.getNamedTag();

        if (tag.containsKey("BlockEntityTag") && tag.get("BlockEntityTag") instanceof NbtMap) {
            tag.remove("BlockEntityTag");
            this.setNamedTag(tag);
        }

        return this;
    }

    public Item setCustomBlockData(NbtMap compoundTag) {
        NbtMapBuilder tags = compoundTag.toBuilder();

        NbtMap tag;
        if (!this.hasCompoundTag()) {
            tag = NbtMap.EMPTY;
        } else {
            tag = this.getNamedTag();
        }

        this.setNamedTag(tag.toBuilder().putCompound("BlockEntityTag", tags.build()).build());

        return this;
    }

    public NbtMap getCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        NbtMap tag = this.getNamedTag();

        if (tag.containsKey("BlockEntityTag")) {
            return tag.getCompound("BlockEntityTag");
        }

        return null;
    }

    public boolean isFilledBucketItem() {
        if (this.isNull()) return false;
        String id = this.getId();
        if (id.equals(Item.BUCKET)) return false;
        return id.endsWith("_bucket");
    }

    /**
     * Whether the item can be enchanted
     */
    public boolean applyEnchantments() {
        return true;
    }

    public boolean hasEnchantments() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        NbtMap tag = this.getNamedTag();

        if (tag.containsKey("ench")) {
            Object enchTag = tag.get("ench");
            return enchTag instanceof List<?>;
        } else if (tag.containsKey("custom_ench")) {
            Object enchTag = tag.get("custom_ench");
            return enchTag instanceof List<?>;
        }

        return false;
    }

    /**
     * Find the enchantment level by the enchantment id.
     *
     * @param id The enchantment ID from {@link Enchantment} constants.
     * @return {@code 0} if the item don't have that enchantment or the current level of the given enchantment.
     */
    public int getEnchantmentLevel(int id) {
        if (!this.hasEnchantments()) {
            return 0;
        }

        for (NbtMap entry : this.getNamedTag().getList("ench", NbtType.COMPOUND)) {
            if (entry.getShort("id") == id) {
                return entry.getShort("lvl");
            }
        }

        return 0;
    }

    /**
     * Find the enchantment level by the enchantment id.
     *
     * @param id The enchantment identifier to query
     * @return {@code 0} if the item don't have that enchantment or the current level of the given enchantment.
     */
    public int getCustomEnchantmentLevel(String id) {
        if (!this.hasEnchantments()) {
            return 0;
        }
        for (NbtMap entry : this.getNamedTag().getList("custom_ench", NbtType.COMPOUND)) {
            if (entry.getString("id").equals(id)) {
                return entry.getShort("lvl");
            }
        }
        return 0;
    }

    /**
     * @param id The enchantment identifier to query
     */
    public Enchantment getCustomEnchantment(String id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (NbtMap entry : this.getNamedTag().getList("custom_ench", NbtType.COMPOUND)) {
            if (entry.getString("id").equals(id)) {
                Enchantment e = Enchantment.getEnchantment(entry.getString("id"));
                if (e != null) {
                    e.setLevel(entry.getShort("lvl"), false);
                    return e;
                }
            }
        }

        return null;
    }

    /**
     * Detect if the item has the enchantment
     *
     * @param id The enchantment identifier to query
     */
    public boolean hasCustomEnchantment(String id) {
        return this.getCustomEnchantmentLevel(id) > 0;
    }

    /**
     * @param id The enchantment identifier to query
     */
    public int getCustomEnchantmentLevel(@NotNull Identifier id) {
        return getCustomEnchantmentLevel(id.toString());
    }

    /**
     * @param id The enchantment identifier to query
     */
    public boolean hasCustomEnchantment(@NotNull Identifier id) {
        return hasCustomEnchantment(id.toString());
    }

    /**
     * @param id The enchantment identifier to query
     */
    public Enchantment getCustomEnchantment(@NotNull Identifier id) {
        return getCustomEnchantment(id.toString());
    }

    /**
     * Get the id of the enchantment
     */
    public Enchantment getEnchantment(int id) {
        return getEnchantment((short) (id & 0xffff));
    }

    public Enchantment getEnchantment(short id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (NbtMap entry : this.getNamedTag().getList("ench", NbtType.COMPOUND)) {
            if (entry.getShort("id") == id) {
                Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
                if (e != null) {
                    e.setLevel(entry.getShort("lvl"), false);
                    return e;
                }
            }
        }

        return null;
    }

    public boolean canAddEnchantment(Enchantment enchantment) {
        if (enchantment == null) return false;
        if (!this.applyEnchantments()) return false;
        if (!enchantment.canEnchant(this)) return false;

        for (Enchantment existing : this.getEnchantments()) {
            if (!enchantment.isCompatibleWith(existing)) {
                return false;
            }
        }

        if (enchantment.getIdentifier() == null) {
            int current = this.getEnchantmentLevel(enchantment.getId());
            return current < enchantment.getLevel();
        } else {
            int current = this.getCustomEnchantmentLevel(enchantment.getIdentifier().toString());
            return current < enchantment.getLevel();
        }
    }

    public void addEnchantment(Enchantment... enchantments) {
        NbtMap tag;
        if (!this.hasCompoundTag()) {
            tag = NbtMap.EMPTY;
        } else {
            tag = this.getNamedTag();
        }

        List<NbtMap> ench;
        if (!tag.containsKey("ench")) {
            ench = new ObjectArrayList<>();
            tag = tag.toBuilder().putList("ench", NbtType.COMPOUND, ench).build();
        } else {
            ench = tag.getList("ench", NbtType.COMPOUND);
        }
        List<NbtMap> custom_ench;
        if (!tag.containsKey("custom_ench")) {
            custom_ench = new ObjectArrayList<>();
            tag = tag.toBuilder().putList("custom_ench", NbtType.COMPOUND, custom_ench).build();
        } else {
            custom_ench = tag.getList("custom_ench", NbtType.COMPOUND);
        }

        for (Enchantment enchantment : enchantments) {
            boolean found = false;
            if (enchantment.getIdentifier() == null) {
                for (int k = 0; k < ench.size(); k++) {
                    NbtMap entry = ench.get(k);
                    if (entry.getShort("id") == enchantment.getId()) {
                        ench.add(k, NbtMap.builder()
                                .putShort("id", (short) enchantment.getId())
                                .putShort("lvl", (short) enchantment.getLevel())
                                .build()
                        );
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ench.add(NbtMap.builder()
                            .putShort("id", (short) enchantment.getId())
                            .putShort("lvl", (short) enchantment.getLevel())
                            .build()
                    );
                }
            } else {
                for (int k = 0; k < custom_ench.size(); k++) {
                    NbtMap entry = custom_ench.get(k);
                    if (entry.getString("id").equals(enchantment.getIdentifier().toString())) {
                        custom_ench.add(k, NbtMap.builder()
                                .putString("id", enchantment.getIdentifier().toString())
                                .putShort("lvl", (short) enchantment.getLevel())
                                .build()
                        );
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    custom_ench.add(NbtMap.builder()
                            .putString("id", enchantment.getIdentifier().toString())
                            .putShort("lvl", (short) enchantment.getLevel())
                            .build()
                    );
                }
            }
        }
        if (custom_ench.size() != 0) {
            var customName = setCustomEnchantDisplay(custom_ench);
            if (tag.containsKey("display") && tag.get("display") instanceof NbtMap displayTag) {
                tag = tag.toBuilder().putCompound("display",
                        displayTag.toBuilder().putString("Name", customName).build()
                ).build();
            } else {
                tag = tag.toBuilder().putCompound("display", NbtMap.builder()
                        .putString("Name", customName)
                        .build()
                ).build();
            }
        }
        this.setNamedTag(tag);
    }

    private String setCustomEnchantDisplay(List<NbtMap> custom_ench) {
        StringJoiner joiner = new StringJoiner("\n", String.valueOf(TextFormat.RESET) + TextFormat.AQUA + idConvertToName() + "\n", "");
        for (var ench : custom_ench) {
            var enchantment = Enchantment.getEnchantment(ench.getString("id")).setLevel(ench.getShort("lvl"));
            joiner.add(enchantment.getLore());
        }
        return joiner.toString();
    }

    /**
     * Get all the enchantments that the item comes with
     *
     * @return If there is no enchanting effect return Enchantment.EMPTY_ARRAY
     */
    public Enchantment[] getEnchantments() {
        if (!this.hasEnchantments()) {
            return Enchantment.EMPTY_ARRAY;
        }
        List<Enchantment> enchantments = new ArrayList<>();

        List<NbtMap> ench = this.getNamedTag().getList("ench", NbtType.COMPOUND);
        for (NbtMap entry : ench) {
            Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
            if (e != null) {
                e.setLevel(entry.getShort("lvl"), false);
                enchantments.add(e);
            }
        }
        //custom ench
        List<NbtMap> custom_ench = this.getNamedTag().getList("custom_ench", NbtType.COMPOUND);
        for (NbtMap entry : custom_ench) {
            Enchantment e = Enchantment.getEnchantment(entry.getString("id"));
            if (e != null) {
                e.setLevel(entry.getShort("lvl"), false);
                enchantments.add(e);
            }
        }
        return enchantments.toArray(Enchantment.EMPTY_ARRAY);
    }

    /**
     * Detect if the item has the enchantment
     *
     * @param id The enchantment ID from {@link Enchantment} constants.
     */
    public boolean hasEnchantment(int id) {
        return this.getEnchantmentLevel(id) > 0;
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        if (enchantment == null) return false;
        if (enchantment.getIdentifier() == null) {
            return this.hasEnchantment(enchantment.getId());
        } else {
            return this.hasCustomEnchantment(enchantment.getIdentifier().toString());
        }
    }

    public void removeEnchantment(int id) {
        if (!this.hasCompoundTag()) return;

        NbtMap tag = this.getNamedTag();
        if (!tag.containsKey("ench")) {
            this.setNamedTag(tag);
            return;
        }

        List<NbtMap> ench = tag.getList("ench", NbtType.COMPOUND);
        for (int i = ench.size() - 1; i >= 0; i--) {
            NbtMap entry = ench.get(i);
            if (entry.getShort("id") == (short) id) {
                ench.remove(i);
            }
        }

        if (ench.size() == 0) {
            tag.remove("ench");
        }

        this.setNamedTag(tag);
    }

    public void removeEnchantment(@NotNull Identifier id) {
        this.removeEnchantment(id.toString());
    }

    public void removeEnchantment(Enchantment enchantment) {
        if (enchantment == null) return;
        if (enchantment.getIdentifier() == null) {
            this.removeEnchantment(enchantment.getId());
        } else {
            this.removeEnchantment(enchantment.getIdentifier());
        }
    }

    public void removeEnchantment(@NotNull String id) {
        if (!this.hasCompoundTag()) return;

        NbtMap tag = this.getNamedTag();
        if (!tag.containsKey("custom_ench")) {
            this.setNamedTag(tag);
            return;
        }

        List<NbtMap> custom = tag.getList("custom_ench", NbtType.COMPOUND);
        boolean removed = false;

        for (int i = custom.size() - 1; i >= 0; i--) {
            NbtMap entry = custom.get(i);
            if (id.equals(entry.getString("id"))) {
                custom.remove(i);
                removed = true;
            }
        }

        if (removed) {
            if (custom.size() == 0) {
                tag.remove("custom_ench");
            } else {
                String customName = setCustomEnchantDisplay(custom);
                if (tag.containsKey("display") && tag.get("display") instanceof NbtMap displayTag) {
                    tag = tag.toBuilder().putCompound("display",
                            displayTag.toBuilder().putString("Name", customName).build()
                    ).build();
                } else {
                    tag = tag.toBuilder()
                            .putCompound("display",
                                    NbtMap.builder()
                                            .putString("Name", customName)
                                            .build()
                            ).build();
                }
            }
        }

        this.setNamedTag(tag);
    }

    public void removeAllEnchantments() {
        if (!this.hasCompoundTag()) return;

        NbtMap tag = this.getNamedTag();
        tag.remove("ench");
        tag.remove("custom_ench");
        this.setNamedTag(tag);
    }

    public int getRepairCost() {
        if (this.hasCompoundTag()) {
            NbtMap tag = this.getNamedTag();
            if (tag.containsKey("RepairCost")) {
                Object repairCost = tag.get("RepairCost");
                if (repairCost instanceof Integer repairCostAsInt) {
                    return repairCostAsInt;
                }
            }
        }
        return 0;
    }

    public Item setRepairCost(int cost) {
        if (cost <= 0 && this.hasCompoundTag()) {
            final NbtMap nbtMap = this.getNamedTag();
            nbtMap.remove("RepairCost");
            return this.setNamedTag(nbtMap);
        }

        NbtMap tag;
        if (!this.hasCompoundTag()) {
            tag = NbtMap.EMPTY;
        } else {
            tag = this.getNamedTag();
        }
        return this.setNamedTag(tag.toBuilder().putInt("RepairCost", cost).build());
    }

    public boolean hasCustomName() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        NbtMap tag = this.getNamedTag();
        if (tag.containsKey("display")) {
            Object tag1 = tag.get("display");
            return tag1 instanceof NbtMap && ((NbtMap) tag1).containsKey("Name") && ((NbtMap) tag1).get("Name") instanceof String;
        }

        return false;
    }

    public String getCustomName() {
        if (!this.hasCompoundTag()) {
            return "";
        }

        NbtMap tag = this.getNamedTag();
        if (tag.containsKey("display")) {
            Object tag1 = tag.get("display");
            if (tag1 instanceof NbtMap && ((NbtMap) tag1).containsKey("Name") && ((NbtMap) tag1).get("Name") instanceof String) {
                return ((NbtMap) tag1).getString("Name");
            }
        }

        return "";
    }

    /**
     * 设置物品的自定义名字
     * <p>
     * Set custom names for items
     *
     * @param name
     * @return
     */
    public Item setCustomName(String name) {
        if (name == null || name.equals("")) {
            this.clearCustomName();
        }

        NbtMap tag;
        if (!this.hasCompoundTag()) {
            tag = NbtMap.EMPTY;
        } else {
            tag = this.getNamedTag();
        }
        if (tag.containsKey("display") && tag.get("display") instanceof NbtMap displayTag) {
            tag = tag.toBuilder().putCompound("display",
                    displayTag.toBuilder().putString("Name", name).build()
            ).build();
        } else {
            tag = tag.toBuilder().putCompound("display",
                    NbtMap.builder().putString("Name", name).build()
            ).build();
        }
        this.setNamedTag(tag);
        return this;
    }

    /**
     * 清除物品的自定义名称
     * <p>
     * Clear custom name for item
     *
     * @return
     */
    public Item clearCustomName() {
        if (!this.hasCompoundTag()) {
            return this;
        }

        NbtMap tag = this.getNamedTag();

        if (tag.containsKey("display") && tag.get("display") instanceof NbtMap) {
            tag.getCompound("display").remove("Name");
            if (tag.getCompound("display").isEmpty()) {
                tag.remove("display");
            }

            this.setNamedTag(tag);
        }

        return this;
    }

    /**
     * 定义物品的Lore信息
     * <p>
     * Get the Lore information of the item
     *
     * @return
     */
    public String[] getLore() {
        Object tag = this.getNamedTagEntry("display");
        ArrayList<String> lines = new ArrayList<>();

        if (tag instanceof NbtMap nbt) {
            lines.addAll(nbt.getList("Lore", NbtType.STRING));
        }

        return lines.toArray(EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * 设置物品的Lore信息
     * <p>
     * Set the Lore information of the item
     *
     * @param lines the lines
     * @return the lore
     */
    public Item setLore(String... lines) {
        NbtMap tag;
        if (!this.hasCompoundTag()) {
            tag = NbtMap.EMPTY;
        } else {
            tag = this.getNamedTag();
        }
        List<String> lore = new ObjectArrayList<>();
        lore.addAll(Arrays.asList(lines));

        tag = tag.toBuilder().putCompound("display", NbtMap.builder().putList("Lore", NbtType.STRING, lore).build()).build();

        this.setNamedTag(tag);
        return this;
    }

    /**
     * Remove a DynamicProperty by key id.
     *
     * @param key the key id of the DynamicProperty
     */
    public Item removeDynamicProperty(String key) {
        if (!this.hasCompoundTag()) return this;
        NbtMap root = this.getNamedTag();
        NbtMap dyn = root.getCompound(DP_ROOT);
        if (dyn == null) return this;
        NbtMap group = dyn.getCompound(DP_DEFAULT_GROUP_UUID());
        if (group == null || !group.containsKey(key)) return this;

        group.remove(key);
        this.setNamedTag(root);
        return this;
    }


    /**
     * Remove all DynamicProperties on the item.
     */
    public Item clearDynamicProperties() {
        if (!this.hasCompoundTag()) return this;
        NbtMap root = this.getNamedTag();
        NbtMap dyn = root.getCompound(DP_ROOT);
        if (dyn == null) return this;

        dyn = dyn.toBuilder().putCompound(DP_DEFAULT_GROUP_UUID(), NbtMap.EMPTY).build();
        this.setNamedTag(root);
        return this;
    }

    /**
     * Set a double int DynamicProperty.
     *
     * @param key   the key id of the DynamicProperty
     * @param value the double int value of the DynamicProperty
     */
    public Item setDynamicProperty(String key, Double value) {
        if (value == null) return removeDynamicProperty(key);
        if (!isFiniteAndInRange(value)) {
            log.warn("DynamicProperty '{}' rejected: out of numeric bounds or non-finite (value={})", key, value);
            return this;
        }
        NbtMap g = ensureDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID()).toBuilder().putDouble(key, value).build();
        saveDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID(), g);
        return this;
    }

    /**
     * Set a int DynamicProperty.
     *
     * @param key   the key id of the DynamicProperty
     * @param value the int value of the DynamicProperty
     */
    public Item setDynamicProperty(String key, Integer value) {
        return setDynamicProperty(key, value == null ? null : value.doubleValue());
    }

    /**
     * Set a int DynamicProperty.
     *
     * @param key   the key id of the DynamicProperty
     * @param value the int value of the DynamicProperty
     */
    public Item setDynamicProperty(String key, Float value) {
        return setDynamicProperty(key, value == null ? null : value.doubleValue());
    }

    /**
     * Set a boolean DynamicProperty.
     *
     * @param key  the key id of the DynamicProperty
     * @param bool the bool value of the DynamicProperty
     */
    public Item setDynamicProperty(String key, Boolean bool) {
        if (bool == null) return removeDynamicProperty(key);
        NbtMap g = ensureDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID()).toBuilder().putBoolean(key, bool).build();
        saveDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID(), g);
        return this;
    }

    /**
     * Set a string DynamicProperty.
     *
     * @param key    the key id of the DynamicProperty
     * @param string the string value of the DynamicProperty
     */
    public Item setDynamicProperty(String key, String string) {
        if (string == null) return removeDynamicProperty(key);
        if (!fitsUtf8Limit(string)) {
            log.warn("DynamicProperty '{}' rejected: string exceeds {} UTF-8 bytes", key, DP_MAX_STRING_BYTES);
            return this;
        }
        NbtMap g = ensureDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID()).toBuilder().putString(key, string).build();
        saveDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID(), g);
        return this;
    }

    /**
     * Set a Vec3 DynamicProperty.
     *
     * @param key  the key id of the DynamicProperty
     * @param vec3 the vec3 value of the DynamicProperty
     */
    public Item setVec3DynamicProperty(String key, Vector3 vec3) {
        if (vec3 == null) return removeDynamicProperty(key);
        if (!isFiniteAndInRange(vec3.x) || !isFiniteAndInRange(vec3.y) || !isFiniteAndInRange(vec3.z)) {
            log.warn("DynamicProperty '{}' rejected: vec3 has component(s) out of bounds or non-finite (x={}, y={}, z={})", key, vec3.x, vec3.y, vec3.z);
            return this;
        }
        final List<Float> list = Arrays.asList((float) vec3.x, (float) vec3.y, (float) vec3.z);
        NbtMap g = ensureDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID()).toBuilder().putList(key, NbtType.FLOAT, list).build();
        saveDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID(), g);
        return this;
    }

    /**
     * Set a Vec3 DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @param xyz a map with keys "x","y","z" and numeric values, e.g. {x: 400, y: 60, z: 300}
     */
    public Item setVec3DynamicProperty(String key, Map<String, Number> xyz) {
        if (xyz == null) return removeDynamicProperty(key);
        Number nx = xyz.get("x"), ny = xyz.get("y"), nz = xyz.get("z");
        if (nx == null || ny == null || nz == null) {
            log.warn("DynamicProperty '{}' rejected: vec3 map must contain numeric keys 'x','y','z'", key);
            return this;
        }
        double x = nx.doubleValue(), y = ny.doubleValue(), z = nz.doubleValue();
        if (!isFiniteAndInRange(x) || !isFiniteAndInRange(y) || !isFiniteAndInRange(z)) {
            log.warn("DynamicProperty '{}' rejected: vec3 has component(s) out of bounds or non-finite (x={}, y={}, z={})", key, x, y, z);
            return this;
        }
        final List<Float> list = Arrays.asList((float) x, (float) y, (float) z);
        NbtMap g = ensureDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID()).toBuilder().putList(key, NbtType.FLOAT, list).build();
        saveDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID(), g);
        return this;
    }

    /**
     * Get a double int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the double int value or null if not available.
     */
    public Double getDoubleDynamicProperty(String key) {
        Object t = findDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;

        return switch (t) {
            case Double d -> d;
            case Float f -> (double) f;
            case Integer i -> (double) i;
            case Long l -> (double) l;
            case Short s -> (double) s;
            case Byte b -> (double) b;
            case String s -> {
                try {
                    yield Double.parseDouble(s.trim());
                } catch (NumberFormatException ignored) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    /**
     * Get a double int DynamicProperty.
     *
     * @param key          the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the double int value or defaultValue if not available.
     */
    public Double getDoubleDynamicProperty(String key, double defaultValue) {
        Double d = getDoubleDynamicProperty(key);
        return (d != null) ? d : defaultValue;
    }

    /**
     * Get a int DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the int value or defaultValue if not available.
     */
    public Integer getIntDynamicProperty(String key) {
        Double d = getDoubleDynamicProperty(key);
        if (d == null) return null;
        return (int) Math.floor(d);
    }

    /**
     * Get a int DynamicProperty.
     *
     * @param key          the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the int value or defaultValue if not available.
     */
    public int getIntDynamicProperty(String key, int defaultValue) {
        Integer i = getIntDynamicProperty(key);
        return (i != null) ? i : defaultValue;
    }

    /**
     * Get a float DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the float value or null if not available.
     */
    public Float getFloatDynamicProperty(String key) {
        Double d = getDoubleDynamicProperty(key);
        if (d == null) return null;
        return d.floatValue();
    }

    /**
     * Get a float DynamicProperty.
     *
     * @param key          the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the float value or defaultValue if not available.
     */
    public float getFloatDynamicProperty(String key, float defaultValue) {
        Float f = getFloatDynamicProperty(key);
        return (f != null) ? f : defaultValue;
    }

    /**
     * Get a boolean DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the bool value or false if not available.
     */
    public Boolean getBoolDynamicProperty(String key) {
        Object t = findDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;
        if (t instanceof Byte) return ((Byte) t) != 0;
        Double d = getDoubleDynamicProperty(key);
        if (d != null) return d != 0.0;
        if (t instanceof String) {
            String s = ((String) t).trim().toLowerCase();
            if ("true".equals(s) || "1".equals(s)) return true;
            if ("false".equals(s) || "0".equals(s)) return false;
        }
        return null;
    }

    /**
     * Get a boolean DynamicProperty.
     *
     * @param key          the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the bool value or defaultValue if not available.
     */
    public boolean getBoolDynamicProperty(String key, boolean defaultValue) {
        Boolean b = getBoolDynamicProperty(key);
        return (b != null) ? b : defaultValue;
    }

    /**
     * Get a string DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the bool value or null if not available.
     */
    public String getStringDynamicProperty(String key) {
        Object t = findDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;
        return String.valueOf(t);
    }

    /**
     * Get a string DynamicProperty.
     *
     * @param key          the key id of the DynamicProperty
     * @param defaultValue the default value to be returned if null.
     * @return the string value or defaultValue if not available.
     */
    public String getStringDynamicProperty(String key, String defaultValue) {
        String s = getStringDynamicProperty(key);
        return (s != null) ? s : defaultValue;
    }

    /**
     * Get a vec3 DynamicProperty.
     *
     * @param key the key id of the DynamicProperty
     * @return the bool value or null if not available.
     */
    public Vector3 getVec3DynamicProperty(String key) {
        Object t = findDynamicPropertyTagInConfiguredGroup(key);
        if (t == null) return null;

        if (t instanceof List<?> list &&
                list.size() == 3 &&
                list.get(0) instanceof Float fx &&
                list.get(1) instanceof Float fy &&
                list.get(2) instanceof Float fz) {
            float x = fx;
            float y = fy;
            float z = fz;
            return new Vector3(x, y, z);
        }
        return null;
    }

    // Dynamic Properties Helpers start
    private static boolean isFiniteAndInRange(double v) {
        return !Double.isNaN(v) && !Double.isInfinite(v) && Math.abs(v) <= DP_NUMBER_ABS_MAX;
    }

    private static boolean fitsUtf8Limit(String s) {
        if (s == null) return false;
        int byteCount = s.getBytes(StandardCharsets.UTF_8).length;
        return byteCount <= DP_MAX_STRING_BYTES;
    }

    private NbtMap ensureDynamicPropertiesGroup(String groupId) {
        NbtMap root = this.getOrCreateNamedTag();
        NbtMap dyn = root.getCompound(DP_ROOT);
        NbtMap group = (dyn != null) ? dyn.getCompound(groupId) : null;
        if (group == null) group = NbtMap.EMPTY;
        return group;
    }

    private NbtMap getDynamicPropertiesGroup(String groupId) {
        NbtMap root = this.getNamedTag();
        if (root == null || !root.containsKey(DP_ROOT)) return null;
        NbtMap dyn = root.getCompound(DP_ROOT);
        if (dyn == null) return null;
        return dyn.getCompound(groupId);
    }

    private void saveDynamicPropertiesGroup(String groupId, NbtMap group) {
        NbtMap root = this.getOrCreateNamedTag();
        NbtMap dyn = root.getCompound(DP_ROOT);
        if (!root.containsKey(DP_ROOT) || dyn == null) {
            dyn = NbtMap.EMPTY;
        }
        NbtMapBuilder dynBuilder = dyn.toBuilder().putCompound(groupId, group);
        root = root.toBuilder().putCompound(DP_ROOT, dynBuilder.build()).build();
        this.setNamedTag(root);
    }

    private Object findDynamicPropertyTagInConfiguredGroup(String key) {
        NbtMap group = getDynamicPropertiesGroup(DP_DEFAULT_GROUP_UUID());
        if (group == null || !group.containsKey(key)) return null;
        return group.get(key);
    }
    // Dynamic Properties Helpers end

    public Object getNamedTagEntry(String name) {
        NbtMap tag = this.getNamedTag();
        if (tag != null) {
            return tag.containsKey(name) ? tag.get(name) : null;
        }

        return null;
    }

    public Item setNamedTag(@Nullable NbtMap tag) {
        this.cachedNBT = tag;
        this.tags = writeCompoundTag(tag);
        return this;
    }

    @Nullable
    public NbtMap getNamedTag() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        if (this.cachedNBT == null) {
            this.cachedNBT = parseCompoundTag(this.tags);
        }
        return this.cachedNBT;
    }

    public Item setCompoundTag(@Nullable NbtMap tag) {
        return setNamedTag(tag);
    }

    public Item setCompoundTag(byte[] tags) {
        this.tags = tags;
        this.cachedNBT = parseCompoundTag(tags);
        return this;
    }

    public byte[] getCompoundTag() {
        return tags;
    }

    public boolean hasCompoundTag() {
        if (tags.length > 0) {
            if (cachedNBT == null) cachedNBT = parseCompoundTag(tags);
            return !cachedNBT.isEmpty();
        } else return false;
    }

    public NbtMap getOrCreateNamedTag() {
        if (!hasCompoundTag()) {
            setNamedTag(NbtMap.EMPTY);
            cachedNBT = NbtMap.EMPTY;
            return cachedNBT;
        }
        return getNamedTag();
    }

    public Item clearNamedTag() {
        this.tags = EmptyArrays.EMPTY_BYTES;
        this.cachedNBT = null;
        return this;
    }

    public static NbtMap parseCompoundTag(byte[] tag) {
        if (tag == null || tag.length == 0) return null;
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(tag);
             final NBTInputStream nbtInputStream = NbtUtils.createReaderLE(inputStream)) {
            return (NbtMap) nbtInputStream.readTag();
        } catch (IOException e) {
            try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(tag);
                 final NBTInputStream nbtInputStream = NbtUtils.createReader(inputStream)) {
                return (NbtMap) nbtInputStream.readTag();
            } catch (IOException ee) {
                throw new UncheckedIOException(ee);
            }
        }
    }

    public byte[] writeCompoundTag(NbtMap tag) {
        if (tag == null) {
            return EmptyArrays.EMPTY_BYTES;
        }
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final NBTOutputStream nbtOutputStream = NbtUtils.createWriterLE(outputStream)) {
            nbtOutputStream.writeTag(tag);
            nbtOutputStream.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Gets whether this item is using a net id.
     *
     * @return whether this item is using a net id
     */
    @ApiStatus.Internal
    public boolean isUsingNetId() {
        return netId != null;
    }

    @ApiStatus.Internal
    public Integer getNetId() {
        return netId;
    }

    @ApiStatus.Internal
    public void setNetId(Integer netId) {
        if (netId != null) {
            if (netId < 0)
                throw new IllegalArgumentException("stack network id cannot be negative");
        }
        this.netId = netId;
    }

    @ApiStatus.Internal
    public Item autoAssignStackNetworkId() {
        this.netId = STACK_NETWORK_ID_COUNTER++;
        return this;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isNull() {
        return Objects.equals(this.id, Block.AIR) || this.count <= 0;
    }

    /**
     * @deprecated Use {@link #hasTag(String)} instead.
     */
    @Deprecated
    public boolean is(final String itemTag) {
        return hasTag(itemTag);
    }

    /**
     * @return true if item has a string tag
     */
    public boolean hasTag(final String itemTag) {
        NbtMap customTags = getCustomItemComponent("minecraft:tags");
        if (customTags != null) {
            List<String> list = customTags.getList("tags", NbtType.STRING);
            if (list != null && list.stream().anyMatch(s -> itemTag.equals(s))) {
                return true;
            }
        }
        if (ItemTags.getTagSet(this.getId()).contains(itemTag)) return true;
        return ItemTags.getTagSet(this.getBlockId()).contains(itemTag);
    }

    public @NotNull
    final String getName() {
        return this.hasCustomName() ? this.getCustomName() : idConvertToName();
    }

    public @NotNull
    final String getDisplayName() {
        return this.hasCustomName() ? this.getCustomName() : idConvertToName();
    }

    public final boolean canBePlaced() {
        return ((this.block != null) && this.block.canBePlaced());
    }

    @NotNull
    public Block getBlock() {
        if (this.block != null) {
            return this.block.clone();
        } else {
            return Block.get(BlockID.AIR);
        }
    }

    @ApiStatus.Internal
    public Block getBlockUnsafe() {
        return this.block;
    }

    @ApiStatus.Internal
    public void setBlockUnsafe(@Nullable Block block) {
        this.block = block;
        if (block != null) {
            this.id = block.getItemId().intern();
            this.identifier = new Identifier(id);
        }
    }

    public final String getId() {
        return id;
    }

    public final Identifier getIdentifier() {
        return identifier;
    }

    private int getAirRuntimeId() {
        return Registries.ITEM_RUNTIMEID.getInt(BlockID.AIR);
    }

    private int getUnknownRuntimeId() {
        return Registries.ITEM_RUNTIMEID.getInt(BlockID.UNKNOWN);
    }

    public final int getRuntimeId() {
        if (this.isNull()) return getAirRuntimeId();

        int i = Registries.ITEM_RUNTIMEID.getInt(this.getId());
        if (i == Integer.MAX_VALUE) {
            i = Registries.ITEM_RUNTIMEID.getInt(this.getBlockId());
        }
        if (i == Integer.MAX_VALUE) {
            log.warn("Can't find runtimeId for item {}, will return unknown itemblock!", this.getId());
            return getUnknownRuntimeId();// Can't find runtimeId
        }
        return i;
    }

    public final int getFullId() {
        return (((short) getRuntimeId()) << 16) | ((meta & 0x7fff) << 1);
    }

    public boolean isBlock() {
        return this.block != null;
    }

    public String getBlockId() {
        if (block != null) {
            return block.getId();
        } else {
            return UNKNOWN_STR;
        }
    }

    public int getDamage() {
        return meta;
    }

    public void setDamage(int damage) {
        if (this.isTool()) {
            toolSetDamage(damage);
            return;
        } else if (this.isWearable()) {
            wearableSetDamage(damage);
            return;
        }
        setDamageRaw(damage);
    }

    protected final void setDamageRaw(int damage) {
        this.meta = damage & 0xffff;
        this.hasMeta = true;
        internalAdjust();
    }

    /**
     * Create a wildcard recipe item,the item can be applied to a recipe without restriction on data(damage/meta) values
     */
    public void disableMeta() {
        this.hasMeta = false;
    }

    public boolean useOn(Entity entity) {
        if (this.isTool()) return toolUseOnEntity(entity);
        return false;
    }

    public boolean useOn(Block block) {
        if (this.isTool()) return toolUseOnBlock(block);
        return false;
    }

    /**
     * Called before {@link #onUse},The player is right clicking use on an item
     *
     * @param player          player
     * @param directionVector The direction vector of the click
     * @return if false is returned, calls {@link #onUse(Player, int)} will be stopped
     */
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (isEdible()) return foodOnClickAir(player, directionVector);
        if (isWearable()) return wearableOnClickAir(player, directionVector);

        return false;
    }

    /**
     * The {@link #onClickAir} is called only after the command is successful
     *
     * @param player    the player
     * @param ticksUsed How long the item has been used (right-click duration)
     * @return the boolean
     */
    public boolean onUse(Player player, int ticksUsed) {
        if (isEdible()) return foodOnUse(player, ticksUsed);
        return false;
    }

    /**
     * Called after {@link #onUse(Player, int)},It will only be called when onUse returns true
     */
    public void afterUse(Player player) {
        NbtMap c = getCustomItemComponent("minecraft:cooldown");
        if (c != null) {
            String categoryId = c.getString("category");
            int duration = Math.max(0, Math.round(c.getFloat("duration") * 20f));
            player.setItemCoolDown(duration, categoryId);
        }
    }

    /**
     * Allows the item to execute code when the player releases the item after long clicking it.
     *
     * @param player    The player who released the click button
     * @param ticksUsed How many ticks the item was held.
     * @return If an inventory contents update should be sent to the player
     */
    public boolean onRelease(Player player, int ticksUsed) {
        return false;
    }

    @Override
    final public String toString() {
        return "Item " + idConvertToName() +
                " (" + this.id
                + ":" + (!this.hasMeta ? "?" : this.meta)
                + ")x" + this.count
                + (this.hasCompoundTag() ? " tags:0x" + ByteBufUtil.hexDump(this.getCompoundTag()) : "");
    }

    /**
     * This method is called when the player interacts with an item
     *
     * @param level  Player location level
     * @param player Player instance object
     * @param block  the block
     * @param target Interacting target block
     * @param face   Direction of Interaction
     * @param fx     the fx
     * @param fy     the fy
     * @param fz     the fz
     * @return boolean
     */
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        return false;
    }

    public final Item decrement(int amount) {
        return increment(-amount);
    }

    public final Item increment(int amount) {
        if (count + amount <= 0) {
            return get(BlockID.AIR);
        }
        Item cloned = clone();
        cloned.count += amount;
        return cloned;
    }

    /**
     * When true, this item can be used to reduce growing times like a bone meal.
     *
     * @return {@code true} if it can act like a bone meal
     */
    public boolean isFertilizer() {
        return false;
    }


    /**
     * Returns whether the specified item stack has the same ID, damage, NBT and count as this item stack.
     *
     * @param other item
     * @return equal
     */
    public final boolean equalsExact(Item other) {
        return this.equals(other, true, true) && this.count == other.count;
    }

    @Override
    public final boolean equals(Object item) {
        return item instanceof Item it && this.equals(it, true);
    }

    public final boolean equals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    public boolean equalItemBlock(Item item) {
        if (this.isBlock() && item.isBlock()) {
            return this.getBlockUnsafe().getBlockState() == item.getBlockUnsafe().getBlockState();
        }
        return true;
    }

    public final boolean equals(Item item, boolean checkDamage, boolean checkCompound) {
        if (!Objects.equals(this.getId(), item.getId())) return false;

        if (checkDamage) {
            if (!equalItemBlock(item)) return false;
            int d1 = this.hasMeta() ? this.getDamage() : 0;
            int d2 = item.hasMeta() ? item.getDamage() : 0;
            if (d1 != d2) return false;
        }

        if (checkCompound && (this.hasCompoundTag() || item.hasCompoundTag())) {
            return Objects.equals(this.getNamedTag(), item.getNamedTag());
        }
        return true;
    }


    /**
     * Same as {@link #equals(Item, boolean)} but the enchantment order of the items does not affect the result.
     */
    public final boolean equalsIgnoringEnchantmentOrder(Item item, boolean checkDamage) {
        if (!this.equals(item, checkDamage, false)) {
            return false;
        }
        if (Arrays.equals(this.getCompoundTag(), item.getCompoundTag())) {
            return true;
        }

        if (!this.hasCompoundTag() || !item.hasCompoundTag()) {
            return false;
        }

        NbtMap thisTags = this.getNamedTag();
        NbtMap otherTags = item.getNamedTag();
        if (thisTags.equals(otherTags)) {
            return true;
        }

        if (!thisTags.containsKey("ench") || !otherTags.containsKey("ench")
                || !(thisTags.get("ench") instanceof List<?>)
                || !(otherTags.get("ench") instanceof List<?>)
                || thisTags.getList("ench", NbtType.COMPOUND).size() != otherTags.getList("ench", NbtType.COMPOUND).size()) {
            return false;
        }

        List<NbtMap> thisEnchantmentTags = thisTags.getList("ench", NbtType.COMPOUND);
        List<NbtMap> otherEnchantmentTags = otherTags.getList("ench", NbtType.COMPOUND);

        int size = thisEnchantmentTags.size();
        Int2IntMap enchantments = new Int2IntArrayMap(size);
        enchantments.defaultReturnValue(Integer.MIN_VALUE);

        for (int i = 0; i < size; i++) {
            NbtMap tag = thisEnchantmentTags.get(i);
            enchantments.put(tag.getShort("id"), tag.getShort("lvl"));
        }

        for (int i = 0; i < size; i++) {
            NbtMap tag = otherEnchantmentTags.get(i);
            if (enchantments.get(tag.getShort("id")) != tag.getShort("lvl")) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Item clone() {
        try {
            byte[] tags = EmptyArrays.EMPTY_BYTES;
            if (this.hasCompoundTag()) {
                tags = this.tags.clone();
            }
            Item item = (Item) super.clone();
            item.setCompoundTag(tags);

            return item;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Controls what block types this block may be placed on.
     */
    public void addCanPlaceOn(Block block) {
        NbtMap tag = getOrCreateNamedTag();
        List<String> canPlaceOn = tag.getList("CanPlaceOn", NbtType.STRING);
        canPlaceOn.add(block.toItem().getId());
        tag = tag.toBuilder().putList("CanPlaceOn", NbtType.STRING, canPlaceOn).build();
        this.setCompoundTag(tag);
    }

    public void addCanPlaceOn(Block[] blocks) {
        for (Block block : blocks) {
            addCanPlaceOn(block);
        }
    }

    public void setCanPlaceOn(Block[] blocks) {
        NbtMap tag = getOrCreateNamedTag();
        List<String> canPlaceOn = new ObjectArrayList<>();
        for (Block block : blocks) {
            canPlaceOn.add(block.toItem().getId());
        }
        tag = tag.toBuilder().putList("CanPlaceOn", NbtType.STRING, canPlaceOn).build();
        this.setCompoundTag(tag);
    }

    public void setCanPlaceOn(List<String> canPlaceOn) {
        NbtMap tag = getOrCreateNamedTag();
        tag = tag.toBuilder().putList("CanPlaceOn", NbtType.STRING, canPlaceOn).build();
        this.setCompoundTag(tag);
    }

    public List<String> getCanPlaceOn() {
        NbtMap tag = getOrCreateNamedTag();
        return tag.getList("CanPlaceOn", NbtType.STRING);
    }

    /**
     * Controls the types of blocks this block can break (in Adventure Mode). This effect does not change its normal breaking speed or loot.
     */
    public void addCanDestroy(Block block) {
        NbtMap tag = getOrCreateNamedTag();
        List<String> canDestroy = tag.getList("CanDestroy", NbtType.STRING);
        canDestroy.add(block.toItem().getId());
        tag = tag.toBuilder().putList("CanDestroy", NbtType.STRING, canDestroy).build();
        this.setCompoundTag(tag);
    }

    public void addCanDestroy(Block[] blocks) {
        for (Block block : blocks) {
            addCanDestroy(block);
        }
    }

    public void setCanDestroy(Block[] blocks) {
        NbtMap tag = getOrCreateNamedTag();
        List<String> canDestroy = new ObjectArrayList<>();
        for (Block block : blocks) {
            canDestroy.add(block.toItem().getId());
        }
        tag = tag.toBuilder().putList("CanDestroy", NbtType.STRING, canDestroy).build();
        this.setCompoundTag(tag);
    }

    public void setCanDestroyOn(List<String> canDestroy) {
        NbtMap tag = getOrCreateNamedTag();
        tag = tag.toBuilder().putList("CanDestroy", NbtType.STRING, canDestroy).build();
        this.setCompoundTag(tag);
    }

    public List<String> getCanDestroy() {
        NbtMap tag = getOrCreateNamedTag();
        return tag.getList("CanDestroy", NbtType.STRING);
    }

    /**
     * Locks the item in the player's inventory
     * LOCK_IN_SLOT Prevents the item from being removed from the player's inventory, dropped, or crafted with.
     * LOCK_IN_INVENTORY Prevents the item from being moved or removed from its slot in the player's inventory, dropped, or crafted with
     */
    public enum ItemLockMode {
        NONE,//only used in server
        LOCK_IN_SLOT,
        LOCK_IN_INVENTORY
    }

    public void setItemLockMode(ItemLockMode mode) {
        NbtMap tag = getOrCreateNamedTag();
        if (mode == ItemLockMode.NONE) {
            tag.remove("minecraft:item_lock");
        } else {
            tag = tag.toBuilder().putByte("minecraft:item_lock", (byte) mode.ordinal()).build();
        }
        this.setCompoundTag(tag);
    }

    /**
     * Get items locked mode in the player's item inventory
     *
     * @return ItemLockMode
     */
    public ItemLockMode getItemLockMode() {
        NbtMap tag = getOrCreateNamedTag();
        if (tag.containsKey("minecraft:item_lock")) {
            return ItemLockMode.values()[tag.getByte("minecraft:item_lock")];
        }
        return ItemLockMode.NONE;
    }

    public void setKeepOnDeath(boolean keepOnDeath) {
        NbtMap tag = getOrCreateNamedTag();
        if (keepOnDeath) {
            tag = tag.toBuilder().putByte("minecraft:keep_on_death", (byte) 1).build();
        } else {
            tag.remove("minecraft:keep_on_death");
        }
        this.setCompoundTag(tag);
    }

    /**
     * Define if the item does not drop on death
     *
     * @return if item does not drop on death
     */
    public boolean keepOnDeath() {
        NbtMap tag = getOrCreateNamedTag();
        return tag.containsKey("minecraft:keep_on_death");
    }

    protected static BlockState getItemBlockState(final String id, final Integer aux) {
        Block block = Registries.BLOCK.get(id);
        if (block == null) return BlockAir.STATE;
        return block.getProperties().getDefaultState();
    }

    public static class ItemJsonComponents {
        public static class CanPlaceOn {
            public String[] blocks;
        }

        public static class CanDestory {
            public String[] blocks;
        }

        public static class ItemLock {
            public static final String LOCK_IN_INVENTORY = "lock_in_inventory";
            public static final String LOCK_IN_SLOT = "lock_in_slot";
            String mode;
        }

        public static ItemJsonComponents fromJson(String json) {
            return JSONUtils.from(json, ItemJsonComponents.class);
        }

        public static class KeepOnDeath {
        }

        private ItemJsonComponents() {
        }

        @SerializedName(value = "minecraft:can_place_on", alternate = {"can_place_on"})
        public CanPlaceOn canPlaceOn;
        @SerializedName(value = "minecraft:can_destroy", alternate = {"can_destroy"})
        public CanDestory canDestroy;
        @SerializedName(value = "minecraft:item_lock", alternate = {"item_lock"})
        public ItemLock itemLock;
        @SerializedName(value = "minecraft:keep_on_death", alternate = {"keep_on_death"})
        public KeepOnDeath keepOnDeath;
    }


    /////////////////////////////
    // Generic Item Components
    /////////////////////////////

    /**
     * Define if item can take damage
     */
    public boolean canTakeDamage() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.canTakeDamage();
        return false;
    }

    /**
     * Define the maximum number of items to be stacked
     */
    public int getMaxStackSize() {
        NbtMap c = getCustomItemComponent("minecraft:max_stack_size");
        if (c != null) {
            return c.getByte("value") & 0xFF;
        }
        return block == null ? 64 : block.getItemMaxStackSize();
    }


    /**
     * Get the burn time of a burnable item
     */
    public final Integer getFuelTime() {
        NbtMap c = getCustomItemComponent("minecraft:fuel");
        if (c != null) {
            float seconds = c.getFloat("duration");
            return Math.round(seconds * 20);
        }

        if (!Registries.FUEL.isFuel(this)) {
            return null;
        }
        if (!this.id.equals(BUCKET) || this.meta == 10) {
            return Registries.FUEL.getFuelDuration(this);
        }
        return null;
    }

    /**
     * Define the maximum durability value of the item
     */
    public int getMaxDurability() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.maxDurability();
        return -1;
    }

    /**
     * Specifies the percentage chance of this item losing durability. Default is set to 100. Defined as an int range with min and max value.
     * <p>
     * getDamageChanceMin() and getDamageChanceMax()
     */
    public int getDamageChanceMin() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.damageChanceMin();
        return 100;
    }

    /**
     * Specifies the percentage chance of this item losing durability. Default is set to 100. Defined as an int range with min and max value.
     * <p>
     * getDamageChanceMin() and getDamageChanceMax()
     */
    public int getDamageChanceMax() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.damageChanceMax();
        return 100;
    }

    public float getUseDuration() {
        NbtMap c = getCustomItemComponent("minecraft:use_modifiers");
        if (c != null) {
            return c.getFloat("use_duration");
        }
        return 0f;
    }


    public int getUsingTicks() {
        return Math.max(0, (int) Math.ceil(getUseDuration() * 20f));
    }

    public float getMovimentModifier() {
        NbtMap c = getCustomItemComponent("minecraft:use_modifiers");
        if (c != null) {
            return c.getFloat("movement_modifier");
        }
        return 1f;
    }

    /**
     * Define if the item is Unbreakable
     */
    public boolean isUnbreakable() {
        return false;
    }

    /**
     * Define the item Tier level
     */
    public int getTier() {
        return 0;
    }

    /**
     * Define the enchantment of an item
     */
    public int getEnchantAbility() {
        return 0;
    }

    /**
     * If the item is resistant to lava and fire and can float on lava like if it was on water.
     *
     * @since 1.4.0.0-PN
     */
    public boolean isLavaResistant() {
        return false;
    }

    /**
     * Returns the block that this item’s block_placer would place, or null if none.
     */
    public @Nullable Block getBlockPlacerTargetBlock() {
        CustomItemDefinition def = getCustomDefinition();
        if (def == null) return null;

        CustomItemDefinition.BlockPlacerData data = def.getBlockPlacerData();
        if (data == null) return null;

        Block b = Block.get(data.blockId());
        return (b == null || b.isAir()) ? null : b;
    }

    /**
     * Convenience: whether item has minecraft:block_placer.
     */
    public boolean hasBlockPlacer() {
        CustomItemDefinition def = getCustomDefinition();
        return def != null && def.getBlockPlacerData() != null;
    }

    /**
     * No damage to item when it's used to attack entities
     *
     * @return whether the item should take damage when used to attack entities
     */
    public boolean noDamageOnAttack() {
        return false;
    }

    /**
     * No damage to item when it's used to break blocks
     *
     * @return whether the item should take damage when used to break blocks
     */
    public boolean noDamageOnBreak() {
        return false;
    }

    /**
     * Define if item never despawns
     */
    public boolean shouldDespawn() {
        NbtMap p = getCustomItemProperties();
        if (p != null && p.containsKey("should_despawn")) {
            return p.getBoolean("should_despawn");
        }
        return true;
    }


    /////////////////////////////
    // Item Food/Edible Methods

    /// //////////////////////////
    public boolean isEdible() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) {
            return def.isEdible();
        }
        return false;
    }

    public int getNutrition() {
        NbtMap c = getCustomItemComponent("minecraft:food");
        if (c != null) {
            return c.getInt("nutrition");
        }
        return getFoodRestore();
    }

    /**
     * @deprecated Use {@link #getNutrition()} instead.
     */
    @Deprecated
    public int getFoodRestore() {
        return 0;
    }

    public float getSaturation() {
        NbtMap c = getCustomItemComponent("minecraft:food");
        if (c != null) {
            int itemNutrition = getNutrition();
            float itemSaturationModifier = c.getFloat("saturation_modifier");
            return (itemNutrition * itemSaturationModifier * 2f);
        }
        return getSaturationRestore();
    }

    /**
     * @deprecated Use {@link #getSaturation()} instead.
     */
    @Deprecated
    public float getSaturationRestore() {
        return 0;
    }

    public float getSaturationModifier() {
        NbtMap c = getCustomItemComponent("minecraft:food");
        if (c != null) {
            return c.getFloat("saturation_modifier");
        }
        return 0;
    }

    public boolean canAlwaysEat() {
        NbtMap c = getCustomItemComponent("minecraft:food");
        if (c != null) {
            return c.getBoolean("can_always_eat");
        }
        return !isRequiresHunger();
    }

    /**
     * @deprecated Use {@link #canAlwaysEat()} instead.
     */
    @Deprecated
    public boolean isRequiresHunger() {
        return true;
    }


    public int getEatingTicks() {
        NbtMap c = getCustomItemComponent("minecraft:use_modifiers");
        if (c != null) {
            float seconds = c.getFloat("use_duration");
            return Math.max(0, Math.round(seconds * 20f));
        }
        return 0;
    }

    /*
     * Used for additional behaviour in Food like: Chorus, Suspicious Stew and etc.
     */
    public boolean onEaten(Player player) {
        return true;
    }

    public boolean foodOnClickAir(Player player, Vector3 directionVector) {
        if (player.getFoodData().isHungry() || this.canAlwaysEat() || player.isCreative()) {
            return true;
        }
        player.getFoodData().sendFood();
        return false;
    }

    public boolean foodOnUse(Player player, int ticksUsed) {
        if (ticksUsed < getUsingTicks()) {
            return false;
        }

        PlayerItemConsumeEvent event = new PlayerItemConsumeEvent(player, this);
        Server.getInstance().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.getInventory().sendContents(player);
            return false;
        }

        if (this.onEaten(player)) {
            player.getFoodData().addFood(this);
            player.completeUsingItem(this.getRuntimeId(), ItemUseMethod.EAT);

            if (player.isAdventure() || player.isSurvival()) {
                --this.count;
                player.getInventory().setItemInHand(this);
                handleUsingConvertsTo(player);
                player.getLevel().addSound(player, Sound.RANDOM_BURP);
            }
        }

        player.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(player, player.add(0, player.getEyeHeight()), VibrationType.EAT));

        return true;
    }

    private void handleUsingConvertsTo(Player player) {
        NbtMap c = getCustomItemComponent("minecraft:food");
        if (c == null) return;

        Object tag = c.get("using_converts_to");
        if (!(tag instanceof String)) return;

        String id = c.getString("using_converts_to");
        if (id == null || id.isBlank()) return;

        Item container = Item.get(id);
        if (container.isNull()) return;
        container.setCount(1);

        if (this.count <= 0) {
            player.getInventory().setItemInHand(container);
            return;
        }
        if (player.getInventory().canAddItem(container)) {
            player.getInventory().addItem(container);
        } else {
            player.getLevel().dropItem(player.getPosition(), container);
        }
    }


    /////////////////////////////
    // Item Armor Methods
    /////////////////////////////

    /**
     * Define if the item is a Armor
     */
    public boolean isWearable() {
        return getWearableType() != ItemArmorType.NONE;
    }

    public boolean isArmor() {
        return isWearable();
    }

    public @NotNull ItemArmorType getWearableType() {
        NbtMap c = getCustomItemComponent("minecraft:wearable");
        if (c != null) {
            ItemArmorType t = ItemArmorType.get(c.getString("slot"));
            return t != null ? t : ItemArmorType.NONE;
        }
        if (this.isHelmet()) return ItemArmorType.HEAD;
        if (this.isChestplate()) return ItemArmorType.CHEST;
        if (this.isLeggings()) return ItemArmorType.LEGS;
        if (this.isBoots()) return ItemArmorType.FEET;
        return ItemArmorType.NONE;
    }

    /**
     * Define if the item is a Helmet
     */
    public boolean isHelmet() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isHelmet();
        return false;
    }

    /**
     * Define if the item is a Chestplate
     */
    public boolean isChestplate() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isChestplate();
        return false;
    }

    /**
     * Define if the item is a Leggings
     */
    public boolean isLeggings() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isLeggings();
        return false;
    }

    /**
     * Define if the item is a Boots
     */
    public boolean isBoots() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isBoots();
        return false;
    }

    /**
     * Define the Armor value of an item
     */
    public int getArmorPoints() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.wearableProtection();
        return 0;
    }

    /**
     * Triggers when the player uses an item.
     *
     * @param player The player using item
     */
    public void whileUsing(Player player) {
    }

    /**
     * Define the Armor Toughness of an item
     */
    public int getToughness() {
        return 0;
    }

    public boolean wearableOnClickAir(Player player, Vector3 directionVector) {
        Level level = player.getLevel();
        HumanInventory inv = player.getInventory();

        Item old = getEquipped(inv);
        if (!old.isNull() && old.hasEnchantment(Enchantment.ID_BINDING_CURSE) && !player.isCreative()) {
            return false;
        }

        if (setEquipped(inv, this)) {
            inv.setItem(inv.getHeldItemIndex(), old);
            Sound s = switch (getTier()) {
                case WEARABLE_TIER_CHAIN -> Sound.ARMOR_EQUIP_CHAIN;
                case WEARABLE_TIER_DIAMOND -> Sound.ARMOR_EQUIP_DIAMOND;
                case WEARABLE_TIER_GOLD -> Sound.ARMOR_EQUIP_GOLD;
                case WEARABLE_TIER_IRON -> Sound.ARMOR_EQUIP_IRON;
                case WEARABLE_TIER_LEATHER -> Sound.ARMOR_EQUIP_LEATHER;
                case WEARABLE_TIER_NETHERITE -> Sound.ARMOR_EQUIP_NETHERITE;
                default -> Sound.ARMOR_EQUIP_GENERIC;
            };
            level.addSound(player, s);
        }

        return this.getCount() == 0;
    }

    private Item getEquipped(HumanInventory inv) {
        return switch (getWearableType()) {
            case HEAD -> inv.getHelmet();
            case CHEST -> inv.getChestplate();
            case LEGS -> inv.getLeggings();
            case FEET -> inv.getBoots();
            case NONE -> Item.AIR;
        };
    }

    private boolean setEquipped(HumanInventory inv, Item item) {
        return switch (getWearableType()) {
            case HEAD -> inv.setHelmet(item);
            case CHEST -> inv.setChestplate(item);
            case LEGS -> inv.setLeggings(item);
            case FEET -> inv.setBoots(item);
            case NONE -> false;
        };
    }

    public void wearableSetDamage(int damage) {
        ItemWearEvent event = new ItemWearEvent(this, damage);
        Server server = Server.getInstance();
        if (server == null) return; // unit tests sometimes have invalid server instance
        PluginManager pluginManager = server.getPluginManager();
        if (pluginManager != null)
            pluginManager.callEvent(event); //Method gets called on server start before plugin manager is initiated
        if (!event.isCancelled()) {
            setDamageRaw(event.getNewDurability());
            this.setNamedTag(this.getOrCreateNamedTag().toBuilder().putInt("Damage", event.getNewDurability()).build());
        }
    }

    /**
     * Retrieves armor knockback resistance of an item
     *
     * @return armor knockback resistance
     */
    public float getKnockbackResistance() {
        return 0.0f;
    }


    /////////////////////////////
    // Item Tools/Weapons Methods
    /////////////////////////////

    /**
     * Define if this item is a tool
     */
    public boolean isTool() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) {
            return isPickaxe() || isAxe() || isShovel() || isHoe() || isSword() || isShears();
        }
        return false;
    }

    /**
     * Define the attackdamage of an item
     */
    public int getAttackDamage() {
        NbtMap c = getCustomItemComponent("minecraft:damage");
        if (c != null && c.containsKey("value")) {
            return c.getByte("value") & 0xFF;
        }
        NbtMap p = getCustomItemProperties();
        if (p != null && p.containsKey("damage")) {
            return p.getInt("damage");
        }
        return 1;
    }

    public int getAttackDamage(Entity entity) {
        return getAttackDamage();
    }

    /**
     * Define if the item is a Sword
     */
    public boolean isSword() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isSword();
        return false;
    }

    /**
     * Define if the item is a Spear
     */
    public boolean isSpear() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isSpear();
        return false;
    }

    /**
     * Define if the item is a Axe
     */
    public boolean isAxe() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isAxe();
        return false;
    }

    /**
     * Define if the item is a Pickaxe
     */
    public boolean isPickaxe() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isPickaxe();
        return false;
    }

    /**
     * Define if the item is a Shovel
     */
    public boolean isShovel() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isShovel();
        return false;
    }

    /**
     * Define if the item is a Hoe
     */
    public boolean isHoe() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isHoe();
        return false;
    }

    /**
     * Define if the item is a Shield
     */
    public boolean isShield() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isShield();
        if (this instanceof ItemShield) return true;
        return false;
    }

    /**
     * Define if the item is a Bow
     */
    public boolean isBow() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isBow();
        if (this instanceof ItemBow) return true;
        return false;
    }

    /**
     * Define if the item is a Crossbow
     */
    public boolean isCrossbow() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isCrossbow();
        if (this instanceof ItemCrossbow) return true;
        return false;
    }

    /**
     * Define if the item is a Trident
     */
    public boolean isTrident() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isTrident();
        if (this instanceof ItemTrident) return true;
        return false;
    }

    /**
     * Define if the item is a Mace
     */
    public boolean isMace() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isMace();
        if (this instanceof ItemMace) return true;
        return false;
    }

    /**
     * Define if the item is a Shears
     */
    public boolean isShears() {
        CustomItemDefinition def = getCustomDefinition();
        if (def != null) return def.isShears();
        return false;
    }

    /**
     * Define if the item can break the shield
     */
    public boolean canBreakShield() {
        return false;
    }

    /**
     * Returns the digger speed based on the block Id or tags it contains, also adds efficience bonus if enabled
     */
    @Nullable
    public Integer getDiggerSpeed(@Nullable Block block) {
        if (block == null) return null;

        NbtMap digger = getCustomItemComponent("minecraft:digger");
        if (digger == null) return null;

        List<NbtMap> rules = digger.getList("destroy_speeds", NbtType.COMPOUND);
        if (rules == null || rules.size() == 0) return null;

        final String blockId = block.getId();

        for (NbtMap rule : rules) {
            NbtMap blk = rule.getCompound("block");

            String name = blk.containsKey("name") ? blk.getString("name") : "";
            if (!name.isEmpty() && name.equals(blockId)) return rule.getInt("speed");
        }

        for (NbtMap rule : rules) {
            NbtMap blk = rule.getCompound("block");

            String tagsExpr = blk.containsKey("tags") ? blk.getString("tags") : "";
            if (tagsExpr.isEmpty()) continue;

            boolean matched = anyTagMatches(block, tagsExpr);
            if (matched) return rule.getInt("speed");
        }

        return null;
    }

    /**
     * Supports: query.any_tag('wood') or query.any_tag('wood','logs')
     */
    private boolean anyTagMatches(Block block, String expr) {
        int lp = expr.indexOf('('), rp = expr.lastIndexOf(')');
        if (lp < 0 || rp <= lp + 1) return false;

        String inner = expr.substring(lp + 1, rp);
        String[] parts = inner.split(",");
        for (String raw : parts) {
            String s = raw.trim();
            if (s.length() >= 2) {
                char q0 = s.charAt(0), q1 = s.charAt(s.length() - 1);
                if ((q0 == '\'' && q1 == '\'') || (q0 == '"' && q1 == '"')) {
                    String tag = s.substring(1, s.length() - 1).trim();
                    if (!tag.isEmpty()) {
                        if (block.hasTag(tag)) return true;
                        if (block.getTags() != null && java.util.Arrays.asList(block.getTags()).contains(tag))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean toolUseOnBlock(Block block) {
        if (this.isUnbreakable() || this.isDurable() || this.noDamageOnBreak()) {
            return true;
        }

        if (block.getToolType() == ItemTool.TYPE_PICKAXE && this.isPickaxe() ||
                block.getToolType() == ItemTool.TYPE_SHOVEL && this.isShovel() ||
                block.getToolType() == ItemTool.TYPE_AXE && this.isAxe() ||
                block.getToolType() == ItemTool.TYPE_HOE && this.isHoe() ||
                block.getToolType() == ItemTool.TYPE_SWORD && this.isSword() ||
                block.getToolType() == ItemTool.TYPE_SHEARS && this.isShears()
        ) {
            this.incDamage(1);
        } else if (!this.isShears() && block.calculateBreakTime(this) > 0) {
            this.incDamage(2);
        } else if (this.isHoe()) {
            if (block.getId().equals(Block.GRASS_BLOCK) || block.getId().equals(Block.DIRT)) {
                this.incDamage(1);
            }
        } else {
            this.incDamage(1);
        }
        return true;
    }

    public void toolSetDamage(int damage) {
        ItemWearEvent event = new ItemWearEvent(this, damage);
        Server server = Server.getInstance();
        if (server == null) return; // unit tests sometimes have invalid server instance
        PluginManager pluginManager = server.getPluginManager();
        if (pluginManager != null)
            pluginManager.callEvent(event); //Method gets called on server start before plugin manager is initiated
        if (!event.isCancelled()) {
            setDamageRaw(event.getNewDurability());
            this.setNamedTag(this.getOrCreateNamedTag().toBuilder().putInt("Damage", event.getNewDurability()).build());
        }
    }

    public boolean toolUseOnEntity(Entity entity) {
        if (this.isUnbreakable() || this.isDurable() || this.noDamageOnAttack()) {
            return true;
        }

        if ((entity != null) && !this.isSword()) {
            incDamage(2);
        } else {
            incDamage(1);
        }

        return true;
    }

    private boolean isDurable() {
        if (!this.hasEnchantments()) {
            return false;
        }

        Enchantment durability = this.getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= new Random().nextInt(100);
    }

    public void incDamage(int v) {
        setDamage(this.meta += v);
    }

    public boolean isCustomItem() {
        return this instanceof CustomItem;
    }

    private NbtMap customComponents() {
        CustomItemDefinition def = getCustomDefinition();
        return def == null ? null : def.getComponents();
    }

    private NbtMap getCustomItemProperties() {
        NbtMap comps = customComponents();
        if (comps == null) return null;
        return comps.containsKey("item_properties") ? comps.getCompound("item_properties") : null;
    }

    public NbtMap getCustomItemComponent(String key) {
        NbtMap comps = customComponents();
        if (comps == null) return null;
        if (comps.containsKey(key)) {
            return comps.getCompound(key);
        }
        return null;
    }

    @Nullable
    public CustomItemDefinition getCustomDefinition() {
        if (this instanceof CustomItem customItem) {
            return ItemRegistry.getCustomItemDefinitionByIdStatic(((Item) customItem).getId());
        }
        return null;
    }

    public ItemData toNetwork() {
        final boolean hasNbt = this.getNamedTag() != null;
        if (this.getNetId() == 0 && !this.getIdentifier().equals(Item.AIR.getIdentifier())) {
            this.autoAssignStackNetworkId();
        }

        return ItemData.builder()
                .definition(this.getItemDefinition())
                .damage(this.getDamage())
                .count(this.getCount())
                .tag(this.getNamedTag())
                .canPlace(!hasNbt ? new String[0] : this.getCanPlaceOn().toArray(String[]::new))
                .canBreak(!hasNbt ? new String[0] : this.getCanDestroy().toArray(String[]::new))
                .blockDefinition(new RuntimeBlockDefinition(this.block == null ? Block.get(Block.AIR).getRuntimeId() : this.getBlock().getRuntimeId()))
                .usingNetId(true)
                .netId(this.getNetId())
                .build();
    }

    public static Item fromNetwork(ItemData itemData) {
        final ItemDefinition definition = itemData.getDefinition();
        final Item item = Item.get(definition.getIdentifier(), itemData.getDamage(), itemData.getCount());
        item.setNamedTag(itemData.getTag());
        item.setCanPlaceOn(Arrays.stream(itemData.getCanPlace()).toList());
        item.setCanDestroyOn(Arrays.stream(itemData.getCanBreak()).toList());
        if (itemData.getBlockDefinition() != null) {
            item.setBlockUnsafe(Block.get(Registries.BLOCKSTATE.get(itemData.getBlockDefinition().getRuntimeId())));
        }
        item.setNetId(itemData.getNetId());
        return item;
    }

    public ItemDefinition getItemDefinition() {
        return new SimpleItemDefinition(
                this.identifier.toString(),
                this.getRuntimeId(),
                this.customComponents() != null ? ItemVersion.DATA_DRIVEN : ItemVersion.NONE,
                this.customComponents() != null,
                this.customComponents()
        );
    }
}