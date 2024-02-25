package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Identifier;
import cn.nukkit.utils.TextFormat;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public abstract class Item implements Cloneable, ItemID {
    public static final Item AIR = new ConstAirItem();
    public static final Item[] EMPTY_ARRAY = new Item[0];

    /**
     * Groups:
     * <ol>
     *     <li>namespace (optional)</li>
     *     <li>item name (choice)</li>
     *     <li>damage (optional, for item name)</li>
     * </ol>
     */
    private static final Pattern ITEM_STRING_PATTERN = Pattern.compile(
            //       1:namespace    2:name           3:damage
            "^(?:(?:([a-z_]\\w*):)?([a-z._]\\w*)(?::(-?\\d+))?)$"
    );

    public static String UNKNOWN_STR = "Unknown";
    protected Block block = null;
    protected final String id;
    protected final Identifier identifier;
    protected int meta;
    protected boolean hasMeta = true;
    private byte[] tags = EmptyArrays.EMPTY_BYTES;
    private CompoundTag cachedNBT;
    public int count;
    protected String name;
    protected Integer netId;
    private static int STACK_NETWORK_ID_COUNTER = 1;

    private String idConvertToName() {
        if (this.name != null) {
            return this.name;
        } else {
            var path = this.id.split(":")[1];
            StringBuilder result = new StringBuilder();
            String[] parts = path.split("_");
            for (String part : parts) {
                if (!part.isEmpty()) {
                    result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
                }
            }
            this.name = result.toString().trim().intern();
            return name;
        }
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
        this.meta = meta & 0xffff;
        this.count = count;
        if (name != null) {
            this.name = name.intern();
        }
        if (autoAssignStackNetworkId) {
            this.autoAssignStackNetworkId();
        }
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
        Item item = Registries.ITEM.get(id, meta, count, tags);
        if (item == null) {
            BlockState itemBlockState = getItemBlockState(id, meta);
            if (itemBlockState == null || itemBlockState == BlockAir.STATE) {
                return Item.AIR;
            }
            item = new ItemBlock(Registries.BLOCK.get(itemBlockState));
            item.setCount(count);
            if (tags != null) {
                item.setCompoundTag(tags);
            }
        }
        if (autoAssignStackNetworkId) {
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

        CompoundTag tag = this.getNamedTag();
        return tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag;

    }

    public Item clearCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return this;
        }
        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag") && tag.get("BlockEntityTag") instanceof CompoundTag) {
            tag.remove("BlockEntityTag");
            this.setNamedTag(tag);
        }

        return this;
    }

    public Item setCustomBlockData(CompoundTag compoundTag) {
        CompoundTag tags = compoundTag.copy();

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        tag.putCompound("BlockEntityTag", tags);
        this.setNamedTag(tag);

        return this;
    }

    public CompoundTag getCustomBlockData() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("BlockEntityTag")) {
            Tag bet = tag.get("BlockEntityTag");
            if (bet instanceof CompoundTag) {
                return (CompoundTag) bet;
            }
        }

        return null;
    }

    /**
     * 该物品是否可以应用附魔效果
     */
    public boolean applyEnchantments() {
        return true;
    }

    public boolean hasEnchantments() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("ench")) {
            Tag enchTag = tag.get("ench");
            return enchTag instanceof ListTag;
        } else if (tag.contains("custom_ench")) {
            Tag enchTag = tag.get("custom_ench");
            return enchTag instanceof ListTag;
        }

        return false;
    }

    /**
     * 通过附魔id来查找对应附魔的等级
     * <p>
     * Find the enchantment level by the enchantment id.
     *
     * @param id The enchantment ID from {@link Enchantment} constants.
     * @return {@code 0} if the item don't have that enchantment or the current level of the given enchantment.
     */
    public int getEnchantmentLevel(int id) {
        if (!this.hasEnchantments()) {
            return 0;
        }

        for (CompoundTag entry : this.getNamedTag().getList("ench", CompoundTag.class).getAll()) {
            if (entry.getShort("id") == id) {
                return entry.getShort("lvl");
            }
        }

        return 0;
    }

    /**
     * 通过附魔id来查找对应附魔的等级
     * <p>
     * Find the enchantment level by the enchantment id.
     *
     * @param id 要查询的附魔标识符
     * @return {@code 0} if the item don't have that enchantment or the current level of the given enchantment.
     */
    public int getCustomEnchantmentLevel(String id) {
        if (!this.hasEnchantments()) {
            return 0;
        }
        for (CompoundTag entry : this.getNamedTag().getList("custom_ench", CompoundTag.class).getAll()) {
            if (entry.getString("id").equals(id)) {
                return entry.getShort("lvl");
            }
        }
        return 0;
    }

    /**
     * @param id 要查询的附魔标识符
     */
    public Enchantment getCustomEnchantment(String id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (CompoundTag entry : this.getNamedTag().getList("custom_ench", CompoundTag.class).getAll()) {
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
     * 检测该物品是否有该附魔
     * <p>
     * Detect if the item has the enchantment
     *
     * @param id 要查询的附魔标识符
     */
    public boolean hasCustomEnchantment(String id) {
        return this.getCustomEnchantmentLevel(id) > 0;
    }

    /**
     * @param id 要查询的附魔标识符
     */
    public int getCustomEnchantmentLevel(@NotNull Identifier id) {
        return getCustomEnchantmentLevel(id.toString());
    }

    /**
     * @param id 要查询的附魔标识符
     */
    public boolean hasCustomEnchantment(@NotNull Identifier id) {
        return hasCustomEnchantment(id.toString());
    }

    /**
     * @param id 要查询的附魔标识符
     */
    public Enchantment getCustomEnchantment(@NotNull Identifier id) {
        return getCustomEnchantment(id.toString());
    }

    /**
     * 从给定的附魔id查找该物品是否存在对应的附魔效果，如果查找不到返回null
     * <p>
     * Get the id of the enchantment
     */
    public Enchantment getEnchantment(int id) {
        return getEnchantment((short) (id & 0xffff));
    }

    public Enchantment getEnchantment(short id) {
        if (!this.hasEnchantments()) {
            return null;
        }

        for (CompoundTag entry : this.getNamedTag().getList("ench", CompoundTag.class).getAll()) {
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

    public void addEnchantment(Enchantment... enchantments) {
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }

        ListTag<CompoundTag> ench;
        if (!tag.contains("ench")) {
            ench = new ListTag<>();
            tag.putList("ench", ench);
        } else {
            ench = tag.getList("ench", CompoundTag.class);
        }
        ListTag<CompoundTag> custom_ench;
        if (!tag.contains("custom_ench")) {
            custom_ench = new ListTag<>();
            tag.putList("custom_ench", custom_ench);
        } else {
            custom_ench = tag.getList("custom_ench", CompoundTag.class);
        }

        for (Enchantment enchantment : enchantments) {
            boolean found = false;
            if (enchantment.getIdentifier() == null) {
                for (int k = 0; k < ench.size(); k++) {
                    CompoundTag entry = ench.get(k);
                    if (entry.getShort("id") == enchantment.getId()) {
                        ench.add(k, new CompoundTag()
                                .putShort("id", enchantment.getId())
                                .putShort("lvl", enchantment.getLevel())
                        );
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ench.add(new CompoundTag()
                            .putShort("id", enchantment.getId())
                            .putShort("lvl", enchantment.getLevel())
                    );
                }
            } else {
                for (int k = 0; k < custom_ench.size(); k++) {
                    CompoundTag entry = custom_ench.get(k);
                    if (entry.getString("id").equals(enchantment.getIdentifier().toString())) {
                        custom_ench.add(k, new CompoundTag()
                                .putString("id", enchantment.getIdentifier().toString())
                                .putShort("lvl", enchantment.getLevel())
                        );
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    custom_ench.add(new CompoundTag()
                            .putString("id", enchantment.getIdentifier().toString())
                            .putShort("lvl", enchantment.getLevel())
                    );
                }
            }
        }
        if (custom_ench.size() != 0) {
            var customName = setCustomEnchantDisplay(custom_ench);
            if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
                tag.getCompound("display").putString("Name", customName);
            } else {
                tag.putCompound("display", new CompoundTag()
                        .putString("Name", customName)
                );
            }
        }
        this.setNamedTag(tag);
    }

    private String setCustomEnchantDisplay(ListTag<CompoundTag> custom_ench) {
        StringJoiner joiner = new StringJoiner("\n", String.valueOf(TextFormat.RESET) + TextFormat.AQUA + idConvertToName() + "\n", "");
        for (var ench : custom_ench.getAll()) {
            var enchantment = Enchantment.getEnchantment(ench.getString("id"));
            joiner.add(enchantment.getLore());
        }
        return joiner.toString();
    }

    /**
     * 获取该物品所带有的全部附魔
     * <p>
     * Get all the enchantments that the item comes with
     *
     * @return 如果没有附魔效果返回Enchantment.EMPTY_ARRAY<br>If there is no enchanting effect return Enchantment.EMPTY_ARRAY
     */
    public Enchantment[] getEnchantments() {
        if (!this.hasEnchantments()) {
            return Enchantment.EMPTY_ARRAY;
        }
        List<Enchantment> enchantments = new ArrayList<>();

        ListTag<CompoundTag> ench = this.getNamedTag().getList("ench", CompoundTag.class);
        for (CompoundTag entry : ench.getAll()) {
            Enchantment e = Enchantment.getEnchantment(entry.getShort("id"));
            if (e != null) {
                e.setLevel(entry.getShort("lvl"), false);
                enchantments.add(e);
            }
        }
        //custom ench
        ListTag<CompoundTag> custom_ench = this.getNamedTag().getList("custom_ench", CompoundTag.class);
        for (CompoundTag entry : custom_ench.getAll()) {
            Enchantment e = Enchantment.getEnchantment(entry.getString("id"));
            if (e != null) {
                e.setLevel(entry.getShort("lvl"), false);
                enchantments.add(e);
            }
        }
        return enchantments.toArray(Enchantment.EMPTY_ARRAY);
    }

    /**
     * 检测该物品是否有该附魔
     * <p>
     * Detect if the item has the enchantment
     *
     * @param id The enchantment ID from {@link Enchantment} constants.
     */
    public boolean hasEnchantment(int id) {
        return this.getEnchantmentLevel(id) > 0;
    }

    public int getRepairCost() {
        if (this.hasCompoundTag()) {
            CompoundTag tag = this.getNamedTag();
            if (tag.contains("RepairCost")) {
                Tag repairCost = tag.get("RepairCost");
                if (repairCost instanceof IntTag) {
                    return ((IntTag) repairCost).data;
                }
            }
        }
        return 0;
    }

    public Item setRepairCost(int cost) {
        if (cost <= 0 && this.hasCompoundTag()) {
            return this.setNamedTag(this.getNamedTag().remove("RepairCost"));
        }

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        return this.setNamedTag(tag.putInt("RepairCost", cost));
    }

    public boolean hasCustomName() {
        if (!this.hasCompoundTag()) {
            return false;
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            return tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag;
        }

        return false;
    }

    public String getCustomName() {
        if (!this.hasCompoundTag()) {
            return "";
        }

        CompoundTag tag = this.getNamedTag();
        if (tag.contains("display")) {
            Tag tag1 = tag.get("display");
            if (tag1 instanceof CompoundTag && ((CompoundTag) tag1).contains("Name") && ((CompoundTag) tag1).get("Name") instanceof StringTag) {
                return ((CompoundTag) tag1).getString("Name");
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

        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
            tag.getCompound("display").putString("Name", name);
        } else {
            tag.putCompound("display", new CompoundTag()
                    .putString("Name", name)
            );
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

        CompoundTag tag = this.getNamedTag();

        if (tag.contains("display") && tag.get("display") instanceof CompoundTag) {
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
        Tag tag = this.getNamedTagEntry("display");
        ArrayList<String> lines = new ArrayList<>();

        if (tag instanceof CompoundTag nbt) {
            ListTag<StringTag> lore = nbt.getList("Lore", StringTag.class);

            if (lore.size() > 0) {
                for (StringTag stringTag : lore.getAll()) {
                    lines.add(stringTag.data);
                }
            }
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
        CompoundTag tag;
        if (!this.hasCompoundTag()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        ListTag<StringTag> lore = new ListTag<>();

        for (String line : lines) {
            lore.add(new StringTag(line));
        }

        if (!tag.contains("display")) {
            tag.putCompound("display", new CompoundTag().putList("Lore", lore));
        } else {
            tag.getCompound("display").putList("Lore", lore);
        }

        this.setNamedTag(tag);
        return this;
    }

    public Tag getNamedTagEntry(String name) {
        CompoundTag tag = this.getNamedTag();
        if (tag != null) {
            return tag.contains(name) ? tag.get(name) : null;
        }

        return null;
    }


    public Item setCompoundTag(CompoundTag tag) {
        this.tags = writeCompoundTag(tag);
        this.cachedNBT = tag;
        return this;
    }

    public Item setCompoundTag(byte[] tags) {
        this.tags = tags;
        this.cachedNBT = parseCompoundTag(tags);
        return this;
    }

    public Item setNamedTag(CompoundTag tag) {
        this.cachedNBT = tag;
        this.tags = writeCompoundTag(tag);
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

    public CompoundTag getNamedTag() {
        if (!this.hasCompoundTag()) {
            return null;
        }

        if (this.cachedNBT == null) {
            this.cachedNBT = parseCompoundTag(this.tags);
        }
        return this.cachedNBT;
    }

    public CompoundTag getOrCreateNamedTag() {
        if (!hasCompoundTag()) {
            setNamedTag(new CompoundTag());
            return cachedNBT;
        }
        return getNamedTag();
    }

    public Item clearNamedTag() {
        this.tags = EmptyArrays.EMPTY_BYTES;
        this.cachedNBT = null;
        return this;
    }

    public static CompoundTag parseCompoundTag(byte[] tag) {
        if (tag == null || tag.length == 0) return null;
        try {
            return NBTIO.read(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] writeCompoundTag(CompoundTag tag) {
        try {
            return NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN);
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
            this.netId = netId;
        } else this.netId = netId;
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

    public boolean is(final String itemTag) {
        boolean contains = ItemTags.getTagSet(this.getId()).contains(itemTag);
        if (contains) return true;
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
    public void setBlockUnsafe(Block block) {
        this.block = block;
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
            log.warn("Can't find runtimeId for item {}, will return unknown itemblock!", getId());
            return getUnknownRuntimeId();//cant find runtimeId
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
        this.meta = damage & 0xffff;
        this.hasMeta = true;
    }

    /**
     * 创建一个通配配方物品,即该物品可以不限制数据值应用到配方中
     * <p>
     * Create a wildcard recipe item,the item can be applied to a recipe without restriction on data(damage/meta) values
     */
    public void disableMeta() {
        this.hasMeta = false;
    }

    /**
     * 定义物品堆叠的最大数量
     * <p>
     * Define the maximum number of items to be stacked
     */
    public int getMaxStackSize() {
        return block == null ? 64 : block.getItemMaxStackSize();
    }

    /**
     * 获取一个可燃烧物品的燃烧时间
     * <p>
     * Get the burn time of a burnable item
     */
    public final Integer getFuelTime() {
        if (!Registries.FUEL.isFuel(this)) {
            return null;
        }
        if (!this.id.equals(BUCKET) || this.meta == 10) {
            return Registries.FUEL.getFuelDuration(this);
        }
        return null;
    }

    public boolean useOn(Entity entity) {
        return false;
    }

    public boolean useOn(Block block) {
        return false;
    }

    /**
     * 定义物品是否为工具
     * <p>
     * Define if this item is a tool
     */
    public boolean isTool() {
        return false;
    }

    /**
     * 定义物品最大耐久值
     * <p>
     * Define the maximum durability value of the item
     */
    public int getMaxDurability() {
        return -1;
    }

    /**
     * 定义物品的挖掘等级
     * <p>
     * Define the item Tier level
     */
    public int getTier() {
        return 0;
    }

    /**
     * 定义物品是否为镐子
     * <p>
     * Define if the item is a Pickaxe
     */
    public boolean isPickaxe() {
        return false;
    }

    /**
     * 定义物品是否为斧子
     * <p>
     * Define if the item is a Axe
     */
    public boolean isAxe() {
        return false;
    }

    /**
     * 定义物品是否为剑
     * <p>
     * Define if the item is a Sword
     */
    public boolean isSword() {
        return false;
    }

    /**
     * 定义物品是否为铲子
     * <p>
     * Define if the item is a Shovel
     */
    public boolean isShovel() {
        return false;
    }

    /**
     * 定义物品是否为锄头
     * <p>
     * Define if the item is a Hoe
     */
    public boolean isHoe() {
        return false;
    }

    /**
     * 定义物品是否为剪刀
     * <p>
     * Define if the item is a Shears
     */
    public boolean isShears() {
        return false;
    }

    /**
     * 定义物品是否为盔甲
     * <p>
     * Define if the item is a Armor
     */
    public boolean isArmor() {
        return false;
    }

    /**
     * 定义物品是否为头盔
     * <p>
     * Define if the item is a Helmet
     */
    public boolean isHelmet() {
        return false;
    }

    /**
     * 定义物品是否为胸甲
     * <p>
     * Define if the item is a Chestplate
     */
    public boolean isChestplate() {
        return false;
    }

    /**
     * 定义物品是否为护腿
     * <p>
     * Define if the item is a Leggings
     */
    public boolean isLeggings() {
        return false;
    }

    /**
     * 定义物品是否为靴子
     * <p>
     * Define if the item is a Boots
     */
    public boolean isBoots() {
        return false;
    }

    /**
     * 定义物品的附魔
     * <p>
     * Define the enchantment of an item
     */
    public int getEnchantAbility() {
        return 0;
    }

    /**
     * 定义物品的攻击伤害
     * <p>
     * Define the attackdamage of an item
     */
    public int getAttackDamage() {
        return 1;
    }

    /**
     * 定义物品的护甲值
     * <p>
     * Define the Armour value of an item
     */
    public int getArmorPoints() {
        return 0;
    }

    /**
     * 定义物品的盔甲韧性
     * <p>
     * Define the Armour Toughness of an item
     */
    public int getToughness() {
        return 0;
    }

    /**
     * 定义物品是否不可损坏
     * <p>
     * Define if the item is Unbreakable
     */
    public boolean isUnbreakable() {
        return false;
    }

    /**
     * 物品是否抵抗熔岩和火，并且可以像在水上一样漂浮在熔岩上。
     * <p>
     * If the item is resistant to lava and fire and can float on lava like if it was on water.
     *
     * @since 1.4.0.0-PN
     */
    public boolean isLavaResistant() {
        return false;
    }

    /**
     * 定义物品是否可以打破盾牌
     * <p>
     * Define if the item can break the shield
     */
    public boolean canBreakShield() {
        return false;
    }

    /**
     * Called before {@link #onUse},The player is right clicking use on an item
     *
     * @param player          player
     * @param directionVector 点击的方向向量<br>The direction vector of the click
     * @return if false is returned, calls {@link #onUse(Player, int)} will be stopped
     */
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return false;
    }

    /**
     * The {@link #onClickAir} is called only after the command is successful
     *
     * @param player    the player
     * @param ticksUsed 物品被使用了多久(右键持续时间)<br>How long the item has been used (right-click duration)
     * @return the boolean
     */
    public boolean onUse(Player player, int ticksUsed) {
        return false;
    }

    /**
     * Called after {@link #onUse(Player, int)},It will only be called when onUse returns true
     */
    public void afterUse(Player player) {
    }

    /**
     * 当玩家在长时间右键物品后释放物品时，该函数被调用。
     * <p>
     * Allows the item to execute code when the player releases the item after long clicking it.
     *
     * @param player    The player who released the click button<br>松开按钮的玩家
     * @param ticksUsed How many ticks the item was held.<br>这个物品被使用多少ticks时间
     * @return If an inventory contents update should be sent to the player<br>是否要向玩家发送库存内容的更新信息
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
                + (this.hasCompoundTag() ? " tags:0x" + Binary.bytesToHexString(this.getCompoundTag()) : "");
    }

    /**
     * 玩家使用一个物品交互时会调用这个方法
     * <p>
     * This method is called when the player interacts with an item
     *
     * @param level  玩家所在地图 <br> Player location level
     * @param player 玩家实例对象 <br> Player instance object
     * @param block  the block
     * @param target 交互的目标方块 <br>Interacting target block
     * @param face   交互的方向 <br>Direction of Interaction
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
     * 如果为true,这个物品可以如骨粉一样减少作物成长时间
     * <p>
     * When true, this item can be used to reduce growing times like a bone meal.
     *
     * @return {@code true} if it can act like a bone meal
     */
    public boolean isFertilizer() {
        return false;
    }


    /**
     * 返回物品堆叠是否与指定的物品堆叠有相同的ID,伤害,NBT和数量
     * <p>
     * Returns whether the specified item stack has the same ID, damage, NBT and count as this item stack.
     *
     * @param other item
     * @return equal
     */
    public final boolean equalsExact(Item other) {
        return this.equals(other, true, true, true) && this.count == other.count;
    }

    @Override
    public final boolean equals(Object item) {
        return item instanceof Item && this.equals((Item) item, true);
    }

    public final boolean equals(Item item, boolean checkDamage) {
        return equals(item, checkDamage, true);
    }

    public final boolean equals(Item item, boolean checkDamage, boolean checkCompound) {
        return equals(item, checkDamage, true, checkCompound);
    }

    /**
     * if two items are equal
     *
     * @param item          the item
     * @param checkDamage   Whether to check the data values
     * @param checkBlock    Whether to check the item blockstate
     * @param checkCompound Whether to check the NBT
     * @return the boolean
     */
    public final boolean equals(Item item, boolean checkDamage, boolean checkBlock, boolean checkCompound) {
        if (!Objects.equals(this.getId(), item.getId())) return false;
        if (checkDamage && this.hasMeta() && item.hasMeta()) {
            if (this.getDamage() != item.getDamage()) return false;
        }
        if (checkBlock && this.isBlock() && item.isBlock()) {
            if (this.getBlockUnsafe().getBlockState() != item.getBlockUnsafe().getBlockState()) return false;
        }
        if (checkCompound && this.hasCompoundTag() && item.hasCompoundTag()) {
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

        CompoundTag thisTags = this.getNamedTag();
        CompoundTag otherTags = item.getNamedTag();
        if (thisTags.equals(otherTags)) {
            return true;
        }

        if (!thisTags.contains("ench") || !otherTags.contains("ench")
                || !(thisTags.get("ench") instanceof ListTag)
                || !(otherTags.get("ench") instanceof ListTag)
                || thisTags.getList("ench").size() != otherTags.getList("ench").size()) {
            return false;
        }

        ListTag<CompoundTag> thisEnchantmentTags = thisTags.getList("ench", CompoundTag.class);
        ListTag<CompoundTag> otherEnchantmentTags = otherTags.getList("ench", CompoundTag.class);

        int size = thisEnchantmentTags.size();
        Int2IntMap enchantments = new Int2IntArrayMap(size);
        enchantments.defaultReturnValue(Integer.MIN_VALUE);

        for (int i = 0; i < size; i++) {
            CompoundTag tag = thisEnchantmentTags.get(i);
            enchantments.put(tag.getShort("id"), tag.getShort("lvl"));
        }

        for (int i = 0; i < size; i++) {
            CompoundTag tag = otherEnchantmentTags.get(i);
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
            item.tags = tags;
            return item;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * 控制此方块（在冒险模式下）可以使用/放置在其上的方块类型。
     * <p>
     * Controls what block types this block may be placed on.
     */
    public void addCanPlaceOn(Block block) {
        CompoundTag tag = getOrCreateNamedTag();
        ListTag<StringTag> canPlaceOn = tag.getList("CanPlaceOn", StringTag.class);
        tag.putList("CanPlaceOn", canPlaceOn.add(new StringTag(block.toItem().getId())));
        this.setCompoundTag(tag);
    }

    public void addCanPlaceOn(Block[] blocks) {
        for (Block block : blocks) {
            addCanPlaceOn(block);
        }
    }

    public void setCanPlaceOn(Block[] blocks) {
        CompoundTag tag = getOrCreateNamedTag();
        ListTag<StringTag> canPlaceOn = new ListTag<>();
        for (Block block : blocks) {
            canPlaceOn.add(new StringTag(block.toItem().getId()));
        }
        tag.putList("CanPlaceOn", canPlaceOn);
        this.setCompoundTag(tag);
    }

    public ListTag<StringTag> getCanPlaceOn() {
        CompoundTag tag = getOrCreateNamedTag();
        return tag.getList("CanPlaceOn", StringTag.class);
    }

    /**
     * 控制此方块（在冒险模式下）可以破坏的方块类型。此效果不会改变原本的破坏速度和破坏后掉落物。
     * <p>
     * Controls what block types can destroy
     */
    public void addCanDestroy(Block block) {
        CompoundTag tag = getOrCreateNamedTag();
        ListTag<StringTag> canDestroy = tag.getList("CanDestroy", StringTag.class);
        tag.putList("CanDestroy", canDestroy.add(new StringTag(block.toItem().getId())));
        this.setCompoundTag(tag);
    }

    public void addCanDestroy(Block[] blocks) {
        for (Block block : blocks) {
            addCanDestroy(block);
        }
    }

    public void setCanDestroy(Block[] blocks) {
        CompoundTag tag = getOrCreateNamedTag();
        ListTag<StringTag> canDestroy = new ListTag<>();
        for (Block block : blocks) {
            canDestroy.add(new StringTag(block.toItem().getId()));
        }
        tag.putList("CanDestroy", canDestroy);
        this.setCompoundTag(tag);
    }

    public ListTag<StringTag> getCanDestroy() {
        CompoundTag tag = getOrCreateNamedTag();
        return tag.getList("CanDestroy", StringTag.class);
    }

    /**
     * 物品锁定在玩家的物品栏
     * LOCK_IN_SLOT 阻止该物品被从玩家物品栏的该槽位移动、移除、丢弃或用于合成
     * LOCK_IN_INVENTORY 阻止该物品被从玩家的物品栏移除、丢弃或用于合成
     * <p>
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
        CompoundTag tag = getOrCreateNamedTag();
        if (mode == ItemLockMode.NONE) {
            tag.remove("minecraft:item_lock");
        } else {
            tag.putByte("minecraft:item_lock", mode.ordinal());
        }
        this.setCompoundTag(tag);
    }

    /**
     * 获取物品锁定在玩家的物品栏的模式
     * <p>
     * Get items locked mode in the player's item inventory
     *
     * @return
     */
    public ItemLockMode getItemLockMode() {
        CompoundTag tag = getOrCreateNamedTag();
        if (tag.contains("minecraft:item_lock")) {
            return ItemLockMode.values()[tag.getByte("minecraft:item_lock")];
        }
        return ItemLockMode.NONE;
    }

    public void setKeepOnDeath(boolean keepOnDeath) {
        CompoundTag tag = getOrCreateNamedTag();
        if (keepOnDeath) {
            tag.putByte("minecraft:keep_on_death", 1);
        } else {
            tag.remove("minecraft:keep_on_death");
        }
        this.setCompoundTag(tag);
    }

    /**
     * 该物品是否死亡不掉落
     * <p>
     * Define if the item does not drop on death
     *
     * @return
     */
    public boolean keepOnDeath() {
        CompoundTag tag = getOrCreateNamedTag();
        return tag.contains("minecraft:keep_on_death");
    }

    protected static BlockState getItemBlockState(final String id, final Integer aux) {
        int i = Registries.BLOCKSTATE_ITEMMETA.get(id, aux);
        if (i == 0) {
            Block block = Registries.BLOCK.get(id);
            if (block == null) return BlockAir.STATE;
            return block.getProperties().getDefaultState();
        }
        return Registries.BLOCKSTATE.get(i);
    }

    public static class ItemJsonComponents {
        private static final Gson gson = new Gson();

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
            return gson.fromJson(json, ItemJsonComponents.class);
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
}
