package cn.nukkit.block.customblock.data.voxel;

public class VoxelBox {
    // 0 = x, 1 = y, 2 = z
    public final int[] min;
    public final int[] max;

    public VoxelBox(int[] min, int[] max) {
        this.min = min;
        this.max = max;
    }

    public int getMin(int axis) {
        return min[axis];
    }

    public int getMax(int axis) {
        return max[axis];
    }
}