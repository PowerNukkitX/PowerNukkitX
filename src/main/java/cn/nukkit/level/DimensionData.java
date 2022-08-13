package cn.nukkit.level;

import lombok.Data;

@Data
public class DimensionData {
    private final String dimensionName;
    private final int dimensionId;
    private final int minHeight;
    private final int maxHeight;
    private final int height;

    public DimensionData(int dimensionId, int minHeight, int maxHeight) {
        this(switch (dimensionId) {
            case 1 -> "minecraft:nether";
            case 2 -> "minecraft:the_end";
            default -> "minecraft:overworld";
        }, dimensionId, minHeight, maxHeight);
    }

    public DimensionData(String dimensionName, int dimensionId, int minHeight, int maxHeight) {
        this.dimensionName = dimensionName;
        this.dimensionId = dimensionId;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        int height = maxHeight - minHeight;
        if (minHeight <= 0 && maxHeight > 0) {
            height += 1; // 0 y coordinate counts too
        }
        this.height = height;
    }
}