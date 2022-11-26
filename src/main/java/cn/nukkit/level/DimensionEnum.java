package cn.nukkit.level;

public enum DimensionEnum {
    OVERWORLD(new DimensionData(Level.DIMENSION_OVERWORLD, -64, 319, 24)),
    NETHER(new DimensionData(Level.DIMENSION_NETHER, 0, 127, 16)),
    END(new DimensionData(Level.DIMENSION_THE_END, 0, 255, 16));

    private final DimensionData dimensionData;

    DimensionEnum(DimensionData dimensionData) {
        this.dimensionData = dimensionData;
    }

    public static DimensionData getDataFromId(int dimension) {
        for (DimensionEnum value : values()) {
            if (value.getDimensionData().getDimensionId() == dimension) {
                return value.getDimensionData();
            }
        }
        return null;
    }

    public DimensionData getDimensionData() {
        return this.dimensionData;
    }
}
