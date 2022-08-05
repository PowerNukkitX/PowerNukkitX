package cn.nukkit.item.customitem;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.item.StringItem;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ItemCustom extends StringItem {

    private static final ConcurrentHashMap<String, Integer> INTERNAL_ALLOCATION_ID_MAP = new ConcurrentHashMap<>();
    private static int nextRuntimeId = 10000;

    @Getter
    private final int runtimeId;

    @Setter
    @Getter
    private String textureName;

    @Setter
    @Getter
    private int textureSize = 16;

    public ItemCustom(@Nonnull String id, @Nullable String name) {
        super(id.toLowerCase(Locale.ENGLISH), name);
        if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(this.getNamespaceId())) {
            do {
                nextRuntimeId++;
            } while (RuntimeItems.getRuntimeMapping().getNamespacedIdByNetworkId(nextRuntimeId) != null);
            INTERNAL_ALLOCATION_ID_MAP.put(this.getNamespaceId(), nextRuntimeId);
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

    public int getEnchantableValue() {
        return 10;
    }

    public CompoundTag getComponentsData() {
        CompoundTag data = new CompoundTag();
        data.putCompound("components", new CompoundTag()
                .putCompound("minecraft:display_name", new CompoundTag()
                        .putString("value", this.getName())
                ).putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", this.allowOffHand())
                        .putBoolean("hand_equipped", this.isTool())
                        .putInt("creative_category", this.getCreativeCategory())
                        .putInt("max_stack_size", this.getMaxStackSize())
                        .putCompound("minecraft:icon", new CompoundTag()
                                .putString("texture", this.getTextureName() != null ? this.getTextureName() : this.name)
                        )
                )
        );
        if (this.getTextureSize() != 16) {
            float scale1 = (float) (0.075 / (this.getTextureSize() / 16f));
            float scale2 = (float) (0.125 / (this.getTextureSize() / 16f));
            float scale3 = (float) (0.075 / (this.getTextureSize() / 16f * 2.4f));

            data.getCompound("components")
                    .putCompound("minecraft:render_offsets", new CompoundTag()
                            .putCompound("main_hand", new CompoundTag()
                                    .putCompound("first_person", xyzToCompoundTag(scale3, scale3, scale3))
                                    .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                            ).putCompound("off_hand", new CompoundTag()
                                    .putCompound("first_person", xyzToCompoundTag(scale1, scale2, scale1))
                                    .putCompound("third_person", xyzToCompoundTag(scale1, scale2, scale1))
                            )
                    );
        }
        return data;
    }

    private static CompoundTag xyzToCompoundTag(float x, float y, float z) {
        var listTag = new ListTag<FloatTag>("scale");
        listTag.add(new FloatTag("", x));
        listTag.add(new FloatTag("", y));
        listTag.add(new FloatTag("", z));
        return new CompoundTag().putList(listTag);
    }

    @Override
    public ItemCustom clone() {
        return (ItemCustom) super.clone();
    }
}
