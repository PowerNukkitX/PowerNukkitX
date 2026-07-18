package org.powernukkitx.item;

import org.powernukkitx.block.BlockStandingBanner;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.network.protocol.types.BannerPattern;
import org.powernukkitx.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.powernukkitx.block.property.CommonBlockProperties.GROUND_SIGN_DIRECTION;

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
        return this.getOrCreateNbt().getInt("Type");
    }

    public void setType(int type) {
        CompoundTag tag = this.hasNbt() ? this.getNbt() : new CompoundTag();
        tag.putInt("Type", type);
        this.setNbt(tag);
    }

    public void addPattern(BannerPattern bannerPattern) {
        CompoundTag tag = this.hasNbt() ? this.getNbt() : new CompoundTag();
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag()
                .putInt("Color", bannerPattern.color().getDyeData() & 0x0f)
                .putString("Pattern", bannerPattern.type().getCode()));
        tag.putList("Patterns", patterns);
        this.setNbt(tag);
    }

    public BannerPattern getPattern(int index) {
        final CompoundTag tag = this.hasNbt() ? this.getNbt() : new CompoundTag();
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        return BannerPattern.fromCompoundTag(patterns.size() > index && index >= 0 ?
                patterns.get(index) :
                new CompoundTag());
    }

    public void removePattern(int index) {
        CompoundTag tag = this.hasNbt() ? this.getNbt() : new CompoundTag();
        ListTag<CompoundTag> patterns = tag.getList("Patterns", CompoundTag.class);
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
        tag.putList("Patterns", patterns);
        this.setNbt(tag);
    }

    public int getPatternsSize() {
        return (this.hasNbt() ? this.getNbt() : new CompoundTag()).getList("Patterns", CompoundTag.class).size();
    }

    public boolean hasPattern() {
        return (this.hasNbt() ? this.getNbt() : new CompoundTag()).contains("Patterns");
    }
}
