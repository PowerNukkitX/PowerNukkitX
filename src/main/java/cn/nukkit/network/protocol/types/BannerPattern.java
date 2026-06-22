package cn.nukkit.network.protocol.types;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;
import org.jetbrains.annotations.NotNull;

public record BannerPattern(BannerPatternType type, DyeColor color) {
    @NotNull
    public static BannerPattern fromCompoundTag(@NotNull NbtMap compoundTag) {
        BannerPatternType bannerPatternType = BannerPatternType.fromCode(compoundTag.containsKey("Pattern") ? compoundTag.getString("Pattern") : "bo");
        return new BannerPattern(bannerPatternType, compoundTag.containsKey("Color") ? DyeColor.getByDyeData(compoundTag.getInt("Color")) : DyeColor.BLACK);
    }

    @NotNull
    public static BannerPattern fromCompoundTag(@NotNull CompoundTag compoundTag) {
        BannerPatternType bannerPatternType = BannerPatternType.fromCode(compoundTag.contains("Pattern") ? compoundTag.getString("Pattern") : "bo");
        return new BannerPattern(bannerPatternType, compoundTag.contains("Color") ? DyeColor.getByDyeData(compoundTag.getInt("Color")) : DyeColor.BLACK);
    }
}
