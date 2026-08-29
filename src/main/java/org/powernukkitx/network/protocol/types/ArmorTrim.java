package org.powernukkitx.network.protocol.types;

import org.powernukkitx.item.Item;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.protocol.bedrock.data.TrimMaterial;
import org.cloudburstmc.protocol.bedrock.data.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class ArmorTrim {

    public static final String TAG_TRIM = "Trim";
    public static final String TAG_PATTERN = "Pattern";
    public static final String TAG_MATERIAL = "Material";

    private final String patternId;
    private final String materialId;

    public ArmorTrim(@NotNull String patternId, @NotNull String materialId) {
        this.patternId = Objects.requireNonNull(patternId, "patternId");
        this.materialId = Objects.requireNonNull(materialId, "materialId");
    }

    public static ArmorTrim of(@NotNull TrimPattern pattern, @NotNull TrimMaterial material) {
        return new ArmorTrim(pattern.getPatternId(), material.getMaterialId());
    }

    public String getPatternId() {
        return patternId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public static Optional<ArmorTrim> get(@Nullable Item item) {
        if (item == null || !item.hasNbt()) return Optional.empty();
        CompoundTag nbt = item.getNbt();
        if (nbt == null || !nbt.contains(TAG_TRIM)) return Optional.empty();
        CompoundTag trim = nbt.getCompound(TAG_TRIM);
        String pattern = trim.getString(TAG_PATTERN);
        String material = trim.getString(TAG_MATERIAL);
        if (pattern.isEmpty() || material.isEmpty()) return Optional.empty();
        return Optional.of(new ArmorTrim(pattern, material));
    }

    public static boolean has(@Nullable Item item) {
        return get(item).isPresent();
    }

    public Item applyTo(@NotNull Item item) {
        Item result = item.clone();
        CompoundTag nbt = result.getOrCreateNbt();
        nbt.putCompound(TAG_TRIM, new CompoundTag()
                .putString(TAG_MATERIAL, materialId)
                .putString(TAG_PATTERN, patternId));
        result.setNbt(nbt);
        return result;
    }

    public static Item remove(@NotNull Item item) {
        Item result = item.clone();
        if (result.hasNbt()) {
            CompoundTag nbt = result.getNbt();
            if (nbt != null && nbt.contains(TAG_TRIM)) {
                nbt.remove(TAG_TRIM);
                result.setNbt(nbt);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArmorTrim other)) return false;
        return patternId.equals(other.patternId) && materialId.equals(other.materialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patternId, materialId);
    }

    @Override
    public String toString() {
        return "ArmorTrim{pattern=" + patternId + ", material=" + materialId + '}';
    }
}
