package cn.nukkit.level;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Data
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

    public DimensionData(int dimensionId, int minHeight, int maxHeight,@Nullable Integer chunkSectionCount) {
        this(switch (dimensionId) {
            case 1 -> "minecraft:nether";
            case 2 -> "minecraft:the_end";
            default -> "minecraft:overworld";
        }, dimensionId, minHeight, maxHeight, chunkSectionCount);
    }

    public DimensionData(String dimensionName, int dimensionId, int minHeight, int maxHeight) {
        this(dimensionName, dimensionId, minHeight, maxHeight, null);
    }

    public DimensionData(String dimensionName, int dimensionId, int minHeight, int maxHeight,@Nullable Integer chunkSectionCount) {
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
}