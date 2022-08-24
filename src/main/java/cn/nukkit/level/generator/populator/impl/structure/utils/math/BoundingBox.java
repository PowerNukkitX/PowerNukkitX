package cn.nukkit.level.generator.populator.impl.structure.utils.math;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.IntArrayTag;
import com.google.common.base.MoreObjects;

//todo: 使用自带的BoundingBox
@PowerNukkitXOnly
@Since("1.19.21-r2")
public class BoundingBox {

    public int x0;
    public int y0;
    public int z0;
    public int x1;
    public int y1;
    public int z1;

    public BoundingBox() {

    }

    public BoundingBox(int[] array) {
        if (array.length == 6) {
            this.x0 = array[0];
            this.y0 = array[1];
            this.z0 = array[2];
            this.x1 = array[3];
            this.y1 = array[4];
            this.z1 = array[5];
        }
    }

    public BoundingBox(BoundingBox boundingBox) {
        this.x0 = boundingBox.x0;
        this.y0 = boundingBox.y0;
        this.z0 = boundingBox.z0;
        this.x1 = boundingBox.x1;
        this.y1 = boundingBox.y1;
        this.z1 = boundingBox.z1;
    }

    public BoundingBox(int x0, int y0, int z0, int x1, int y1, int z1) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

    public BoundingBox(BlockVector3 vec0, BlockVector3 vec1) {
        this.x0 = Math.min(vec0.getX(), vec1.getX());
        this.y0 = Math.min(vec0.getY(), vec1.getY());
        this.z0 = Math.min(vec0.getZ(), vec1.getZ());
        this.x1 = Math.max(vec0.getX(), vec1.getX());
        this.y1 = Math.max(vec0.getY(), vec1.getY());
        this.z1 = Math.max(vec0.getZ(), vec1.getZ());
    }

    public BoundingBox(int x0, int z0, int x1, int z1) {
        this.x0 = x0;
        this.z0 = z0;
        this.x1 = x1;
        this.z1 = z1;
        this.y0 = 1;
        this.y1 = 512;
    }

    public static BoundingBox getUnknownBox() {
        return new BoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static BoundingBox orientBox(int x, int y, int z, int xOffset, int yOffset, int zOffset, int xLength, int yLength, int zLength, BlockFace orientation) {
        switch (orientation) {
            case NORTH:
                return new BoundingBox(x + xOffset, y + yOffset, z - zLength + 1 + zOffset, x + xLength - 1 + xOffset, y + yLength - 1 + yOffset, z + zOffset);
            case WEST:
                return new BoundingBox(x - zLength + 1 + zOffset, y + yOffset, z + xOffset, x + zOffset, y + yLength - 1 + yOffset, z + xLength - 1 + xOffset);
            case EAST:
                return new BoundingBox(x + zOffset, y + yOffset, z + xOffset, x + zLength - 1 + zOffset, y + yLength - 1 + yOffset, z + xLength - 1 + xOffset);
            case SOUTH:
            default:
                return new BoundingBox(x + xOffset, y + yOffset, z + zOffset, x + xLength - 1 + xOffset, y + yLength - 1 + yOffset, z + zLength - 1 + zOffset);
        }
    }

    public static BoundingBox createProper(int x0, int y0, int z0, int x1, int y1, int z1) {
        return new BoundingBox(Math.min(x0, x1), Math.min(y0, y1), Math.min(z0, z1), Math.max(x0, x1), Math.max(y0, y1), Math.max(z0, z1));
    }

    public boolean intersects(BoundingBox boundingBox) {
        return this.x1 >= boundingBox.x0 && this.x0 <= boundingBox.x1 && this.z1 >= boundingBox.z0 && this.z0 <= boundingBox.z1 && this.y1 >= boundingBox.y0 && this.y0 <= boundingBox.y1;
    }

    public boolean intersects(int x0, int z0, int x1, int z1) {
        return this.x1 >= x0 && this.x0 <= x1 && this.z1 >= z0 && this.z0 <= z1;
    }

    public void expand(BoundingBox boundingBox) {
        this.x0 = Math.min(this.x0, boundingBox.x0);
        this.y0 = Math.min(this.y0, boundingBox.y0);
        this.z0 = Math.min(this.z0, boundingBox.z0);
        this.x1 = Math.max(this.x1, boundingBox.x1);
        this.y1 = Math.max(this.y1, boundingBox.y1);
        this.z1 = Math.max(this.z1, boundingBox.z1);
    }

    public void move(int x, int y, int z) {
        this.x0 += x;
        this.y0 += y;
        this.z0 += z;
        this.x1 += x;
        this.y1 += y;
        this.z1 += z;
    }

    public BoundingBox moved(int x, int y, int z) {
        return new BoundingBox(this.x0 + x, this.y0 + y, this.z0 + z, this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public boolean isInside(BlockVector3 vec) {
        return vec.getX() >= this.x0 && vec.getX() <= this.x1 && vec.getZ() >= this.z0 && vec.getZ() <= this.z1 && vec.getY() >= this.y0 && vec.getY() <= this.y1;
    }

    public BlockVector3 getLength() {
        return new BlockVector3(this.x1 - this.x0, this.y1 - this.y0, this.z1 - this.z0);
    }

    public int getXSpan() {
        return this.x1 - this.x0 + 1;
    }

    public int getYSpan() {
        return this.y1 - this.y0 + 1;
    }

    public int getZSpan() {
        return this.z1 - this.z0 + 1;
    }

    public IntArrayTag createTag() {
        return new IntArrayTag("", new int[]{this.x0, this.y0, this.z0, this.x1, this.y1, this.z1});
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x0", this.x0)
                .add("y0", this.y0)
                .add("z0", this.z0)
                .add("x1", this.x1)
                .add("y1", this.y1)
                .add("z1", this.z1)
                .toString();
    }
}
