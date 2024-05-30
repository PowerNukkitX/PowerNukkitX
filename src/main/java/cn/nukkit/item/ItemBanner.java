package cn.nukkit.item;

import cn.nukkit.block.BlockStandingBanner;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.BannerPattern;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;

/**
 * @author PetteriM1
 */
public class ItemBanner extends Item {
    public ItemBanner() {
        this(0);
    }

    public ItemBanner(Integer meta) {
        this(meta, 1);
    }

    public ItemBanner(Integer meta, int count) {
        super(BANNER, meta, count, "Banner");
    }

    @Override
    public void internalAdjust() {
        block = BlockStandingBanner.PROPERTIES.getBlockState(GROUND_SIGN_DIRECTION.createValue(getDamage())).toBlock();
        name = getBaseDyeColor().getName() + " Banner";
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    public int getBaseColor() {
        return this.getDamage() & 0x0f;
    }

    public void setBaseColor(@NotNull DyeColor color) {
        this.setDamage(color.getDyeData() & 0x0f);
    }

    @NotNull
    public DyeColor getBaseDyeColor() {
        return Objects.requireNonNull(DyeColor.getByDyeData(getBaseColor()));
    }

    public int getType() {
        return this.getOrCreateNamedTag().getInt("Type");
    }

    public void setType(int type) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        assert tag != null;
        tag.putInt("Type", type);
        this.setNamedTag(tag);
    }

    public void addPattern(BannerPattern bannerPattern) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        assert tag != null;
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag().
                putInt("Color", bannerPattern.color().getDyeData() & 0x0f).
                putString("Pattern", bannerPattern.type().getCode()));
        tag.putList("Patterns", patterns);
        this.setNamedTag(tag);
    }

    public BannerPattern getPattern(int index) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        assert tag != null;
        return BannerPattern.fromCompoundTag(tag.getList("Patterns").size() > index && index >= 0 ?
                tag.getList("Patterns", CompoundTag.class).get(index) :
                new CompoundTag());
    }

    public void removePattern(int index) {
        CompoundTag tag = this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag();
        assert tag != null;
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
        this.setNamedTag(tag);
    }

    public int getPatternsSize() {
        return (this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).getList("Patterns").size();
    }

    public boolean hasPattern() {
        return (this.hasCompoundTag() ? this.getNamedTag() : new CompoundTag()).contains("Patterns");
    }
}
