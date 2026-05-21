package cn.nukkit.network.protocol.types;


public record PropertySyncData(int[] intPropertyIndices, int[] intProperties, int[] floatPropertyIndices, float[] floatProperties) {

    public PropertySyncData(int[] intProperties, float[] floatProperties) {
        this(indexes(intProperties.length), intProperties, indexes(floatProperties.length), floatProperties);
    }

    private static int[] indexes(int length) {
        int[] indices = new int[length];
        for (int i = 0; i < length; i++) {
            indices[i] = i;
        }
        return indices;
    }
}
