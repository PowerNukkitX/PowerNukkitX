package cn.nukkit.entity.path;

public final class Node {
    public final int x; // x的值
    public final int y; // y的值
    public final int z; // z的值
    public final EnumNodeOffset offset;
    public long g; // 到起点的代价
    public Node parent; // 父亲

    public Node(double x, double y, double z) {
        this.x = (int) x;
        this.y = (int) y;
        this.z = (int) z;
        var offsetBin = 0b000;
        if (this.x != x) offsetBin |= 0b001;
        if (this.y != y) offsetBin |= 0b010;
        if (this.z != z) offsetBin |= 0b100;
        this.offset = EnumNodeOffset.fromBinary(offsetBin);
    }

    public Node(int x, int y, int z, EnumNodeOffset offset) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.offset = offset;
    }

    /**
     * 为xz处于[-16777216, 16777215]循环及区块y值范围[-64, 384)范围内的整数坐标及整数方块中心坐标（x+0.5, y, z+0.5）生成哈希。
     * 这在大概率上确保了生物寻路的时候不会哈希重合，因为生物寻路一次不可能有长达一千六百万格的路径。
     * 点位偏移  y+64  x符号  z符号  x绝对值  z绝对值
     * 3bit     9bit  1bit  1bit  25bit   25bit
     *
     * @param x      x [-16777216, 16777215]
     * @param y      y [-64, 383]
     * @param z      z [-16777216, 16777215]
     * @param offset 点位偏移
     * @return 哈希值
     */
    public static long nodeHash(int x, int y, int z, EnumNodeOffset offset) {
        var hash = 0L;
        hash |= ((long) offset.ordinal() << 61);
        hash |= (((long) (y + 64) << 55) >>> 3);
        hash |= ((long) (x >>> 31) << 51);
        hash |= ((long) (z >>> 31) << 50);
        hash |= (((long) x << 39 >>> 14));
        hash |= (((long) z << 39 >>> 39));
        return hash;
    }

    public long nodeHashCode() {
        return nodeHash(x, y, z, offset);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public EnumNodeOffset getOffset() {
        return offset;
    }

    public long getG() {
        return g;
    }

    public void setG(long g) {
        this.g = g;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int doubleXOffset() {
        return (offset.ordinal() ^ 0b001) == 0 ? 1 : 0;
    }

    public long doubleManhattanDistance(Node other) {
        var result = Math.abs(other.x - this.x) + Math.abs(other.y - this.y) + Math.abs(other.z - this.z);
        return result;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(nodeHashCode());
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", offset=" + offset +
                ", g=" + g +
                "} parent -> " + parent;
    }
}
