package cn.nukkit.level;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DimensionData {
    private final String dimensionName;
    private final int dimensionId;
    private final int minHeight;
    private final int maxHeight;
    private final int height;
    private final int chunkSectionCount;

    public DimensionData(int dimensionId, int minHeight, int maxHeight) {
        this(dimensionId, minHeight, maxHeight, null);
    }

    public DimensionData(int dimensionId, int minHeight, int maxHeight, @Nullable Integer chunkSectionCount) {
        this(switch (dimensionId) {
            case 1 -> "minecraft:nether";
            case 2 -> "minecraft:the_end";
            default -> "minecraft:overworld";
        }, dimensionId, minHeight, maxHeight, chunkSectionCount);
    }

    public DimensionData(String dimensionName, int dimensionId, int minHeight, int maxHeight) {
        this(dimensionName, dimensionId, minHeight, maxHeight, null);
    }

    public DimensionData(String dimensionName, int dimensionId, int minHeight, int maxHeight, @Nullable Integer chunkSectionCount) {
        this.dimensionName = dimensionName;
        this.dimensionId = dimensionId;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        int height = maxHeight - minHeight;
        if (minHeight <= 0 && maxHeight > 0) {
            height += 1; // 0 y coordinate counts too
        }
        this.height = height;

        this.chunkSectionCount = Objects.requireNonNullElseGet(chunkSectionCount, () -> this.height >> 4 + ((this.height & 15) == 0 ? 0 : 1));
    }

    public String getDimensionName() {
        return this.dimensionName;
    }

    public int getDimensionId() {
        return this.dimensionId;
    }

    public int getMinHeight() {
        return this.minHeight;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public int getHeight() {
        return this.height;
    }

    public int getChunkSectionCount() {
        return this.chunkSectionCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DimensionData that)) return false;
        return dimensionId == that.dimensionId && minHeight == that.minHeight && maxHeight == that.maxHeight && height == that.height && chunkSectionCount == that.chunkSectionCount && dimensionName.equals(that.dimensionName);
    }

    public String toString() {
        return "DimensionData(dimensionName=" + this.getDimensionName() + ", dimensionId=" + this.getDimensionId() + ", minHeight=" + this.getMinHeight() + ", maxHeight=" + this.getMaxHeight() + ", height=" + this.getHeight() + ", chunkSectionCount=" + this.getChunkSectionCount() + ")";
    }
}