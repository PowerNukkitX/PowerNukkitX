package cn.nukkit.level.util;

public record BlockIndex(int x, int y, int z, int layer, long hash) {

    public static BlockIndex of(int x, int y, int z, int layer) {
        return new BlockIndex(x, y, z, layer, ((long) x) << 38 | ((long) y) << 27 | ((long) z) << 1 | layer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockIndex that = (BlockIndex) o;

        if (hash != that.hash) return false;
        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        return layer == that.layer;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + layer;
        return result;
    }
}
