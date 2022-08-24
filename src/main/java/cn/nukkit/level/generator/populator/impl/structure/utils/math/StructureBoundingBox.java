package cn.nukkit.level.generator.populator.impl.structure.utils.math;

import cn.nukkit.math.BlockVector3;

public class StructureBoundingBox {

    private BlockVector3 min;
    private BlockVector3 max;

    public StructureBoundingBox(BlockVector3 min, BlockVector3 max) {
        this.min = min;
        this.max = max;
    }

    public BlockVector3 getMin() {
        return this.min;
    }

    public BlockVector3 getMax() {
        return this.max;
    }

    public int getMinChunkX() {
        return this.min.x >> 4;
    }

    public int getMinChunkZ() {
        return this.min.z >> 4;
    }

    public int getMaxChunkX() {
        return this.max.x >> 4;
    }

    public int getMaxChunkZ() {
        return this.max.z >> 4;
    }

    /**
     * Checks whether the given point is inside a block that intersects this
     * box.
     *
     * @param vec the point to check
     * @return true if this box intersects the block containing {@code vec}
     */
    public boolean isVectorInside(BlockVector3 vec) {
        return vec.x >= this.min.x && vec.x <= this.max.x && vec.y >= this.min.y && vec.y <= this.max.y && vec.z >= this.min.z && vec.z <= this.max.z;
    }

    /**
     * Whether this box intersects the given box.
     *
     * @param boundingBox the box to check intersection with
     * @return true if the given box intersects this box; false otherwise
     */
    public boolean intersectsWith(StructureBoundingBox boundingBox) {
        return boundingBox.getMin().x <= this.max.x && boundingBox.getMax().x >= this.min.x && boundingBox.getMin().y <= this.max.y && boundingBox.getMax().y >= this.min.y && boundingBox.getMin().z <= this.max.z && boundingBox.getMax().z >= this.min.z;
    }

    /**
     * Whether this box intersects the given vertically-infinite box.
     *
     * @param minX the minimum X coordinate
     * @param minZ the minimum Z coordinate
     * @param maxX the maximum X coordinate
     * @param maxZ the maximum Z coordinate
     * @return true if the given box intersects this box; false otherwise
     */
    public boolean intersectsWith(int minX, int minZ, int maxX, int maxZ) {
        return minX <= this.max.x && maxX >= this.min.x && minZ <= this.max.z && maxZ >= this.min.z;
    }

    /**
     * Changes this bounding box to the bounding box of the union of itself and
     * another bounding box.
     *
     * @param boundingBox the other bounding box to contain
     */
    public void expandTo(StructureBoundingBox boundingBox) {
        this.min = new BlockVector3(Math.min(this.min.x, boundingBox.getMin().x), Math.min(this.min.y, boundingBox.getMin().y), Math.min(this.min.z, boundingBox.getMin().z));
        this.max = new BlockVector3(Math.max(this.max.x, boundingBox.getMax().x), Math.max(this.max.y, boundingBox.getMax().y), Math.max(this.max.z, boundingBox.getMax().z));
    }

    public void offset(BlockVector3 offset) {
        this.min = this.min.add(offset);
        this.max = this.max.add(offset);
    }
}
