package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.jetbrains.annotations.Nullable;


public class ItemShield extends ItemTool {
    public ItemShield() {
        this(0, 1);
    }

    public ItemShield(Integer meta) {
        this(meta, 1);
    }

    public ItemShield(Integer meta, int count) {
        super(SHIELD, meta, count, "Shield");
    }

    /**
     * 为自定义盾牌提供的构造函数
     * <p>
     * Constructor for custom shield
     *
     * @param id    the id
     * @param meta  the meta
     * @param count the count
     * @param name  the name
     */
    protected ItemShield(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasBannerPattern() {
        return this.hasCompoundTag() && (this.getNamedTag().containsKey("Base") ||
                this.getNamedTag().containsKey("Type") || this.getNamedTag().containsKey("Patterns"));
    }

    public @Nullable ItemBanner getBannerPattern() {
        if (!this.hasBannerPattern()) {
            return null;
        }
        var tag = this.getNamedTag().toBuilder();
        var item = new ItemBanner();
        tag.putAll(item.getNamedTag());
        this.setNamedTag(tag.build());
        if (this.getNamedTag().containsKey("Base")) {
            item.setBaseColor(DyeColor.getByDyeData(this.getNamedTag().getInt("Base")));
        }
        return item;
    }

    public void setBannerPattern(@Nullable ItemBanner banner) {
        if (banner == null) {
            this.clearNamedTag();
            return;
        }
        NbtMap tag;
        if (!hasBannerPattern()) {
            tag = NbtMap.EMPTY;
        } else {
            tag = this.getNamedTag();
        }
        final NbtMapBuilder builder = tag.toBuilder();
        builder.putAll(banner.getNamedTag());
        builder.putInt("Base", banner.getBaseColor());
        this.setNamedTag(builder.build());
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_SHIELD;
    }

    @Override
    public boolean isUsingNetId() {
        return false;
    }
}
