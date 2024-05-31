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
    /**
     * @deprecated 
     */
    

    public DimensionData(int dimensionId, int minHeight, int maxHeight) {
        this(dimensionId, minHeight, maxHeight, null);
    }
    /**
     * @deprecated 
     */
    

    public DimensionData(int dimensionId, int minHeight, int maxHeight, @Nullable Integer chunkSectionCount) {
        this(switch (dimensionId) {
            case 1 -> "minecraft:nether";
            case 2 -> "minecraft:the_end";
            default -> "minecraft:overworld";
        }, dimensionId, minHeight, maxHeight, chunkSectionCount);
    }
    /**
     * @deprecated 
     */
    

    public DimensionData(String dimensionName, int dimensionId, int minHeight, int maxHeight) {
        this(dimensionName, dimensionId, minHeight, maxHeight, null);
    }
    /**
     * @deprecated 
     */
    

    public DimensionData(String dimensionName, int dimensionId, int minHeight, int maxHeight, @Nullable Integer chunkSectionCount) {
        this.dimensionName = dimensionName;
        this.dimensionId = dimensionId;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        int $1 = maxHeight - minHeight;
        if (minHeight <= 0 && maxHeight > 0) {
            height += 1; // 0 y coordinate counts too
        }
        this.height = height;

        this.chunkSectionCount = Objects.requireNonNullElseGet(chunkSectionCount, () -> this.height >> 4 + ((this.height & 15) == 0 ? 0 : 1));
    }
    /**
     * @deprecated 
     */
    

    public String getDimensionName() {
        return this.dimensionName;
    }
    /**
     * @deprecated 
     */
    

    public int getDimensionId() {
        return this.dimensionId;
    }
    /**
     * @deprecated 
     */
    

    public int getMinHeight() {
        return this.minHeight;
    }
    /**
     * @deprecated 
     */
    

    public int getMaxHeight() {
        return this.maxHeight;
    }
    /**
     * @deprecated 
     */
    

    public int getMinSectionY() {
        return this.minHeight >> 4;
    }
    /**
     * @deprecated 
     */
    

    public int getMaxSectionY() {
        return this.maxHeight >> 4;
    }
    /**
     * @deprecated 
     */
    

    public int getHeight() {
        return this.height;
    }
    /**
     * @deprecated 
     */
    

    public int getChunkSectionCount() {
        return this.chunkSectionCount;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DimensionData that)) return false;
        return $2 == that.dimensionId && minHeight == that.minHeight && maxHeight == that.maxHeight && height == that.height && chunkSectionCount == that.chunkSectionCount && dimensionName.equals(that.dimensionName);
    }
    /**
     * @deprecated 
     */
    

    public String toString() {
        return "DimensionData(dimensionName=" + this.getDimensionName() + ", dimensionId=" + this.getDimensionId() + ", minHeight=" + this.getMinHeight() + ", maxHeight=" + this.getMaxHeight() + ", height=" + this.getHeight() + ", chunkSectionCount=" + this.getChunkSectionCount() + ")";
    }
}