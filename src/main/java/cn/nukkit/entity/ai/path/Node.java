package cn.nukkit.entity.ai.path;

import cn.nukkit.math.Vector3;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public final class Node implements Comparable<Node>, Cloneable {
    public final int x; // x的值
    public final int y; // y的值
    public final int z; // z的值
    public final EnumNodeOffset offset;
    public final Node destination;
    /**
     * g值是此点到起点消耗的代价
     */
    private long g; // 到起点的代价
    private Node parent; // 父亲
    private boolean isRoot = false;

    public Node(double x, double y, double z, Node destination) {
        this.x = (int) x;
        this.y = (int) y;
        this.z = (int) z;
        this.destination = destination;
        var offsetBin = 0b000;
        if (this.x != x) offsetBin |= 0b001;
        if (this.y != y) offsetBin |= 0b010;
        if (this.z != z) offsetBin |= 0b100;
        this.offset = EnumNodeOffset.fromBinary(offsetBin);
    }

    public Node(int x, int y, int z, EnumNodeOffset offset, Node destination) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.offset = offset;
        this.destination = destination;
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

    public double realX() {
        return x + doubleXOffset() * 0.5;
    }

    public double realY() {
        return y + doubleYOffset() * 0.5;
    }

    public double realZ() {
        return z + doubleZOffset() * 0.5;
    }

    public Vector3 toRealVector() {
        return new Vector3(realX(), realY(), realZ());
    }

    public int doubleRealX() {
        return (x << 1) + doubleXOffset();
    }

    public int doubleRealY() {
        return (y << 1) + doubleYOffset();
    }

    public int doubleRealZ() {
        return (z << 1) + doubleZOffset();
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
        if (isRoot) {
            return;
        }
        this.parent = parent;
    }

    /**
     * @return 双倍的X偏移量（偏移即返回1，否则0）
     */
    public int doubleXOffset() {
        return offset.ordinal() & 0b001;
    }

    /**
     * @return 双倍的Y偏移量（偏移即返回1，否则0）
     */
    public int doubleYOffset() {
        return (offset.ordinal() & 0b010) >>> 1;
    }

    /**
     * @return 双倍的Z偏移量（偏移即返回1，否则0）
     */
    public int doubleZOffset() {
        return (offset.ordinal() & 0b100) >>> 2;
    }

    /**
     * 估测到另一个点的“优化曼哈顿距离”，返回实际估计距离*10（1位定点数）
     *
     * @param target 另一个点
     * @return 优化曼哈顿距离
     */
    public long estimateDistance(Node target) {
        var dx = Math.abs(((target.x << 1) + target.doubleXOffset()) - ((this.x << 1) + this.doubleXOffset()));
        var dz = Math.abs(((target.z << 1) + target.doubleZOffset()) - ((this.z << 1) + this.doubleZOffset()));
        return Math.max(dx, dz) * 5L + Math.min(dx, dz) * 7L + ((((long) target.y << 1) + target.doubleYOffset()) - (((long) this.y << 1) + this.doubleYOffset())) * 5L;
    }

    /**
     * 估算到终点的代价
     *
     * @return 代价
     */
    public long estimateH() {
        return estimateDistance(this.destination);
    }

    /**
     * 复制此点并偏移传入参数的二分之一（为了0.5没有小数）。
     * 请注意，此操作不会更改G值！
     *
     * @param doubleDx 双倍x向移动距离
     * @param doubleDy 双倍y向移动距离
     * @param doubleDz 双倍z向移动距离
     * @return 复制后移动的点
     */
    public Node copyAndOffsetHalf(int doubleDx, int doubleDy, int doubleDz) {
        var resultX = this.x;
        var resultY = this.y;
        var resultZ = this.z;
        var resultOffsetBin = 0b000;
        doubleDx += doubleXOffset();
        doubleDy += doubleYOffset();
        doubleDz += doubleZOffset();
        if (doubleDx != 0) {
            if ((doubleDx & 1) == 1) { //奇数
                resultOffsetBin |= 0b001;
            }
            resultX += doubleDx >> 1;
        }
        if (doubleDy != 0) {
            if ((doubleDy & 1) == 1) {
                resultOffsetBin |= 0b010;
            }
            resultY += doubleDy >> 1;
        }
        if (doubleDz != 0) {
            if ((doubleDz & 1) == 1) {
                resultOffsetBin |= 0b100;
            }
            resultZ += doubleDz >> 1;
        }
        var copy = new Node(resultX, resultY, resultZ, EnumNodeOffset.fromBinary(resultOffsetBin), this.destination);
        copy.setParent(this.parent);
        copy.setG(this.g);
        return copy;
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", offset=" + offset +
                ", g=" + g +
                "} parent -> " + (parent == null ? "null" : parent);
    }

    @Override
    public int compareTo(@NotNull Node o) {
        var a = this.g + estimateH();
        var b = o.g + o.estimateH();
        if (a == b) {
            return Integer.compare(this.hashCode(), o.hashCode());
        }
        return Long.compare(a, b);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node node) {
            return node.x == this.x && node.y == this.y && node.z == this.z && node.offset == this.offset;
        }
        return false;
    }

    @SneakyThrows
    @NotNull
    @Override
    protected Node clone() {
        return (Node) super.clone();
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }
}
