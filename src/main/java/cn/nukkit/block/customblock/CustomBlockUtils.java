package cn.nukkit.block.customblock;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomBlockUtils {

    private CustomBlockUtils() {}

    public static @Nullable AxisAlignedBB getBoundingBox(CustomBlockDefinition def, Block block) {
        CompoundTag components = def.getComponents();
        if (!components.contains("minecraft:collision_box")) return null;
        CompoundTag collision = components.getCompound("minecraft:collision_box");
        if (!collision.contains("boxes")) return null;
        ListTag<CompoundTag> boxes = collision.getList("boxes", CompoundTag.class);
        if (boxes.size() == 0) return null;

        RotationResult rotation = getRotation(block);

        float[] mergedBounds = null;
        for (int i = 0; i < boxes.size(); i++) {
            CompoundTag boxTag = boxes.get(i);

            float minXpx = boxTag.getFloat("minX");
            float minYpx = boxTag.getFloat("minY");
            float minZpx = boxTag.getFloat("minZ");
            float maxXpx = boxTag.getFloat("maxX");
            float maxYpx = boxTag.getFloat("maxY");
            float maxZpx = boxTag.getFloat("maxZ");

            // Normalize to block space
            float minX = minXpx / 16f;
            float minY = minYpx / 16f;
            float minZ = minZpx / 16f;
            float maxX = maxXpx / 16f;
            float maxY = maxYpx / 16f;
            float maxZ = maxZpx / 16f;

            float[] normOrigin = new float[]{ minX, minY, minZ };
            float[] normSize   = new float[]{ maxX - minX, maxY - minY, maxZ - minZ };

            Vector3f[] corners = buildAndRotateBoxCorners(
                    normOrigin,
                    normSize,
                    rotation.rotX,
                    rotation.rotY,
                    rotation.isVerticalRotated
            );
            float[] bounds = calculateBounds(corners);

            if (mergedBounds == null) {
                mergedBounds = bounds;
            } else {
                mergedBounds[0] = Math.min(mergedBounds[0], bounds[0]); // minX
                mergedBounds[1] = Math.min(mergedBounds[1], bounds[1]); // minY
                mergedBounds[2] = Math.min(mergedBounds[2], bounds[2]); // minZ
                mergedBounds[3] = Math.max(mergedBounds[3], bounds[3]); // maxX
                mergedBounds[4] = Math.max(mergedBounds[4], bounds[4]); // maxY
                mergedBounds[5] = Math.max(mergedBounds[5], bounds[5]); // maxZ
            }
        }

        if (mergedBounds == null) return null;

        // Clamp X/Z to [0, 1], Y to [0, 1.5] (24px)
        for (int i = 0; i < 3; i++) {
            float low = 0f;
            float high = (i == 1) ? 1.5f : 1f;

            float min = mergedBounds[i];
            float max = mergedBounds[i + 3];

            if (min < low) {
                float delta = low - min;
                min = low;
                max += delta;
            }
            if (max > high) {
                float delta = max - high;
                max = high;
                min -= delta;
            }

            mergedBounds[i] = min;
            mergedBounds[i + 3] = max;
        }

        double x = block.x, y = block.y, z = block.z;
        double x1 = x + mergedBounds[0];
        double y1 = y + mergedBounds[1];
        double z1 = z + mergedBounds[2];
        double x2 = x + mergedBounds[3];
        double y2 = y + mergedBounds[4];
        double z2 = z + mergedBounds[5];

        return new SimpleAxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public static boolean isSolidForBlock(CustomBlockDefinition def, @NotNull Block block) {
        AxisAlignedBB box = getBoundingBox(def, block);
        if (box == null) return true;
        double height = box.getMaxY() - box.getMinY();
        return height >= 0.99;
    }

    private static RotationResult getRotation(Block block) {
        BlockFace facing = BlockFace.NORTH;
        MinecraftCardinalDirection cardinal = MinecraftCardinalDirection.NORTH;
        BlockProperties props = block.getProperties();

        boolean hasFacing = props.getPropertyTypeSet().contains(CommonBlockProperties.MINECRAFT_FACING_DIRECTION);
        boolean hasCardinal = props.getPropertyTypeSet().contains(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

        if (hasFacing) {
            facing = block.getPropertyValue(CommonBlockProperties.MINECRAFT_FACING_DIRECTION);
        }
        if (hasCardinal) {
            cardinal = block.getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
        }

        boolean isVerticalRotated = hasFacing;

        float rotX = 0f;
        float rotY = 0f;

        if (hasFacing) {
            switch (facing) {
                case NORTH -> rotY = 0f;
                case SOUTH -> rotY = 180f;
                case EAST  -> rotY = -90f;
                case WEST  -> rotY = 90f;
                case UP -> {
                    rotX = 270f;
                    if (hasCardinal) {
                        switch (cardinal) {
                            case NORTH -> rotY = 0f;
                            case SOUTH -> rotY = 180f;
                            case EAST -> rotY = -90f;
                            case WEST -> rotY = 90f;
                        }
                    }
                }
                case DOWN -> {
                    rotX = -270f;
                    if (hasCardinal) {
                        switch (cardinal) {
                            case NORTH -> rotY = 0f;
                            case SOUTH -> rotY = 180f;
                            case EAST -> rotY = -90f;
                            case WEST -> rotY = 90f;
                        }
                    }
                }
            }
        } else if (hasCardinal) {
            // Fallback: use cardinal for horizontal rotation if no facing is present
            switch (cardinal) {
                case NORTH -> rotY = 0f;
                case SOUTH -> rotY = 180f;
                case EAST  -> rotY = -90f;
                case WEST  -> rotY = 90f;
            }
        }
        // else: default rotY = 0 (north)

        return new RotationResult(rotX, rotY, isVerticalRotated);
    }

    private static class RotationResult {
        final float rotX, rotY;
        final boolean isVerticalRotated;
        RotationResult(float rotX, float rotY, boolean isVerticalRotated) {
            this.rotX = rotX;
            this.rotY = rotY;
            this.isVerticalRotated = isVerticalRotated;
        }
    }

    private static Vector3f[] buildAndRotateBoxCorners(float[] normOrigin, float[] normSize, float rotX, float rotY, boolean isVerticalRotated) {
        Vector3f[] corners = new Vector3f[8];
        int idx = 0;
        // Build 8 corners relative to block center
        for (int dx = 0; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) {
                for (int dz = 0; dz <= 1; dz++) {
                    float px = normOrigin[0] + dx * normSize[0];
                    float py = normOrigin[1] + dy * normSize[1];
                    float pz = normOrigin[2] + dz * normSize[2];
                    // Shift to block-space: add 0.5 to rotate around center
                    Vector3f local = new Vector3f(px + 0.5f, py + 0.5f, pz + 0.5f);
                    // Rotate around center (0.5,0.5,0.5)
                    Vector3f centered = new Vector3f(local.x - 0.5f, local.y - 0.5f, local.z - 0.5f);
                    Vector3f rotated = rotateBlock(centered, rotX, rotY, 0f);
                    corners[idx++] = new Vector3f(rotated.x + 0.5f, rotated.y + 0.5f, rotated.z + 0.5f);
                }
            }
        }

        // If NOT vertical rotated, restore original Y min
        if (!isVerticalRotated) {
            for (int i = 0; i < corners.length; i++) {
                corners[i] = new Vector3f(corners[i].x, corners[i].y - 0.5f, corners[i].z);
            }
        }

        return corners;
    }

    private static float[] calculateBounds(Vector3f[] corners) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (Vector3f v : corners) {
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            minZ = Math.min(minZ, v.z);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
            maxZ = Math.max(maxZ, v.z);
        }
        return new float[]{ minX, minY, minZ, maxX, maxY, maxZ };
    }

    // Rotates a point (relative to block center) by angleX, angleY, angleZ (in degrees)
    private static Vector3f rotateBlock(Vector3f point, float angleX, float angleY, float angleZ) {
        float radX = (float) Math.toRadians(angleX);
        float radY = (float) Math.toRadians(angleY);
        float radZ = (float) Math.toRadians(angleZ);

        float x = point.x;
        float y = point.y;
        float z = point.z;

        // X-axis rotation
        float y1 = y * (float) Math.cos(radX) - z * (float) Math.sin(radX);
        float z1 = y * (float) Math.sin(radX) + z * (float) Math.cos(radX);

        // Y-axis rotation
        float x2 = x * (float) Math.cos(radY) + z1 * (float) Math.sin(radY);
        float z2 = -x * (float) Math.sin(radY) + z1 * (float) Math.cos(radY);

        // Z-axis rotation
        float x3 = x2 * (float) Math.cos(radZ) - y1 * (float) Math.sin(radZ);
        float y3 = x2 * (float) Math.sin(radZ) + y1 * (float) Math.cos(radZ);

        return new Vector3f(x3, y3, z2);
    }
}
