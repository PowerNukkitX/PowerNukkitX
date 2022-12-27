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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof final DimensionData other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$dimensionName = this.getDimensionName();
        final Object other$dimensionName = other.getDimensionName();
        if (!Objects.equals(this$dimensionName, other$dimensionName))
            return false;
        if (this.getDimensionId() != other.getDimensionId()) return false;
        if (this.getMinHeight() != other.getMinHeight()) return false;
        if (this.getMaxHeight() != other.getMaxHeight()) return false;
        if (this.getHeight() != other.getHeight()) return false;
        return this.getChunkSectionCount() == other.getChunkSectionCount();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DimensionData;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $dimensionName = this.getDimensionName();
        result = result * PRIME + ($dimensionName == null ? 43 : $dimensionName.hashCode());
        result = result * PRIME + this.getDimensionId();
        result = result * PRIME + this.getMinHeight();
        result = result * PRIME + this.getMaxHeight();
        result = result * PRIME + this.getHeight();
        result = result * PRIME + this.getChunkSectionCount();
        return result;
    }

    public String toString() {
        return "DimensionData(dimensionName=" + this.getDimensionName() + ", dimensionId=" + this.getDimensionId() + ", minHeight=" + this.getMinHeight() + ", maxHeight=" + this.getMaxHeight() + ", height=" + this.getHeight() + ", chunkSectionCount=" + this.getChunkSectionCount() + ")";
    }
}