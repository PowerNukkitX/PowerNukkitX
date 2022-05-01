package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.StringItem;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustom extends StringItem {

    private static final ConcurrentHashMap<String, Integer> INTERNAL_ALLOCATION_ID_MAP = new ConcurrentHashMap<>();
    private static int nextRuntiemId = 10000;

    @Getter
    private final int runtimeId;

    @Setter
    @Getter
    private String textureName;

    public ItemCustom(@Nonnull String id, @Nullable String name) {
        super(id.toLowerCase(Locale.ENGLISH), name);
        if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(this.getNamespaceId())) {
            do {
                nextRuntiemId++;
            } while (RuntimeItems.getRuntimeMapping().getNamespacedIdByNetworkId(nextRuntiemId) != null);
            INTERNAL_ALLOCATION_ID_MAP.put(this.getNamespaceId(), nextRuntiemId);
        }
        this.runtimeId = INTERNAL_ALLOCATION_ID_MAP.get(this.getNamespaceId());
    }

    public ItemCustom(@Nonnull String id, @Nullable String name, @Nonnull String textureName) {
        this(id, name);
        this.textureName = textureName;
    }

    public boolean allowOffHand() {
        return false;
    }

    public int getCreativeCategory() {
        return 4;
    }

    public CompoundTag getComponentsData() {
        CompoundTag data = new CompoundTag();
        data.putCompound("components", new CompoundTag()
                .putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", this.allowOffHand())
                        .putBoolean("hand_equipped", this.isTool())
                        .putInt("creative_category", this.getCreativeCategory())
                        .putInt("max_stack_size", this.getMaxStackSize())
                        .putCompound("minecraft:icon", new CompoundTag()
                                .putString("texture", this.getTextureName() != null ? this.getTextureName() : this.name))));
        return data;
    }

    @Override
    public ItemCustom clone() {
        return (ItemCustom) super.clone();
    }
}
