package cn.nukkit.item;

import cn.nukkit.block.BlockStandingBanner;
import cn.nukkit.network.protocol.types.BannerPattern;
import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
        NbtMapBuilder tag = this.hasCompoundTag() ? this.getNamedTag().toBuilder() : NbtMap.builder();
        assert tag != null;
        tag.putInt("Type", type);
        this.setNamedTag(tag.build());
    }

    public void addPattern(BannerPattern bannerPattern) {
        NbtMap tag = this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY;
        assert tag != null;
        List<NbtMap> patterns = tag.getList("Patterns", NbtType.COMPOUND);
        patterns.add(NbtMap.builder()
                .putInt("Color", bannerPattern.color().getDyeData() & 0x0f)
                .putString("Pattern", bannerPattern.type().getCode())
                .build()
        );
        tag = tag.toBuilder().putList("Patterns", NbtType.COMPOUND, patterns).build();
        this.setNamedTag(tag);
    }

    public BannerPattern getPattern(int index) {
        final NbtMap tag = this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY;
        assert tag != null;
        return BannerPattern.fromCompoundTag(tag.getList("Patterns", NbtType.COMPOUND).size() > index && index >= 0 ?
                tag.getList("Patterns", NbtType.COMPOUND).get(index) :
                NbtMap.EMPTY);
    }

    public void removePattern(int index) {
        NbtMap tag = this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY;
        assert tag != null;
        List<NbtMap> patterns = tag.getList("Patterns", NbtType.COMPOUND);
        if (patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
        this.setNamedTag(tag);
    }

    public int getPatternsSize() {
        return (this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY).getList("Patterns", NbtType.COMPOUND).size();
    }

    public boolean hasPattern() {
        return (this.hasCompoundTag() ? this.getNamedTag() : NbtMap.EMPTY).containsKey("Patterns");
    }
}
