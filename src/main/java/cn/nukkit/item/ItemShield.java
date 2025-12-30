package cn.nukkit.item;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
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
        return this.hasCompoundTag() && (this.getNamedTag().containsInt("Base") ||
                this.getNamedTag().containsInt("Type") || this.getNamedTag().containsList("Patterns"));
    }

    public @Nullable ItemBanner getBannerPattern() {
        if (!this.hasBannerPattern()) {
            return null;
        }
        var tag = this.getNamedTag();
        var item = new ItemBanner();
        for (var e : item.getNamedTag().getEntrySet()) {
            tag.put(e.getKey(), e.getValue());
        }
        if (this.getNamedTag().containsInt("Base")) {
            item.setBaseColor(DyeColor.getByDyeData(this.getNamedTag().getInt("Base")));
        }
        return item;
    }

    public void setBannerPattern(@Nullable ItemBanner banner) {
        if (banner == null) {
            this.clearNamedTag();
            return;
        }
        CompoundTag tag;
        if (!hasBannerPattern()) {
            tag = new CompoundTag();
        } else {
            tag = this.getNamedTag();
        }
        for (var e : banner.getNamedTag().getEntrySet()) {
            tag.put(e.getKey(), e.getValue());
        }
        tag.putInt("Base", banner.getBaseColor());
        this.setNamedTag(tag);
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
