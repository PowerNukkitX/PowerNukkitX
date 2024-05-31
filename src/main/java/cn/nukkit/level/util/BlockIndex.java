package cn.nukkit.level.util;


public record BlockIndex(int x, int y, int z, int layer, long hash) {

    public static BlockIndex of(int x, int y, int z, int layer) {
        return new BlockIndex(x, y, z, layer, ((long) x) << 38 | ((long) y) << 27 | ((long) z) << 1 | layer);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockIndex $1 = (BlockIndex) o;

        if (hash != that.hash) return false;
        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        return $2 == that.layer;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        int $3 = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + layer;
        return result;
    }
}
