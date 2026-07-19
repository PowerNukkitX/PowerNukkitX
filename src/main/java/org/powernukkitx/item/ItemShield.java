package org.powernukkitx.item;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;
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
        return this.hasNbt() && (this.getNbt().containsInt("Base") ||
                this.getNbt().containsInt("Type") || this.getNbt().containsList("Patterns"));
    }

    public @Nullable ItemBanner getBannerPattern() {
        if (!this.hasBannerPattern()) {
            return null;
        }
        var tag = this.getNbt();
        var item = new ItemBanner();
        for (var e : item.getNbt().getEntrySet()) {
            tag.put(e.getKey(), e.getValue());
        }
        if (this.getNbt().containsInt("Base")) {
            item.setBaseColor(DyeColor.getByDyeData(this.getNbt().getInt("Base")));
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
            tag = this.getNbt();
        }
        for (var e : banner.getNbt().getEntrySet()) {
            tag.put(e.getKey(), e.getValue());
        }
        tag.putInt("Base", banner.getBaseColor());
        this.setNbt(tag);
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