package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.Nullable;

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends ItemTool instead of Item only in PowerNukkit")
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
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public ItemShield(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Since("1.20.0-r2")
    @PowerNukkitXOnly
    public boolean hasBannerPattern() {
        return this.hasCompoundTag() && (this.getNamedTag().containsInt("Base") ||
                this.getNamedTag().containsInt("Type") || this.getNamedTag().containsList("Patterns"));
    }

    @Since("1.20.0-r2")
    @PowerNukkitXOnly
    public @Nullable ItemBanner getBannerPattern() {
        if (!this.hasBannerPattern()) {
            return null;
        }
        var tag = this.getNamedTag();
        var item = new ItemBanner();
        for (var each : item.getNamedTag().getTags().values()) {
            tag.put(each.getName(), each);
        }
        if (this.getNamedTag().containsInt("Base")) {
            item.setBaseColor(DyeColor.getByDyeData(this.getNamedTag().getInt("Base")));
        }
        return item;
    }

    @Since("1.20.0-r2")
    @PowerNukkitXOnly
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
        for (var each : banner.getNamedTag().getTags().values()) {
            tag.put(each.getName(), each);
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
}
