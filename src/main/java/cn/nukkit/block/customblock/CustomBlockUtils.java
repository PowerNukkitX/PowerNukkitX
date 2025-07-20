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
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomBlockUtils {

    private CustomBlockUtils() {}

    public static @Nullable AxisAlignedBB getBoundingBox(CustomBlockDefinition def, Block block) {
        CompoundTag components = def.getComponents();
        if (!components.contains("minecraft:collision_box")) {
            return null;
        }

        float[] origin = readVector(components, "origin");
        float[] size = readVector(components, "size");
        if (origin == null || size == null) return null;

        // Bedrock origin/size is in pixels converted to block coordinates (0-1)
        float[] normOrigin = new float[3];
        float[] normSize = new float[3];
        for (int i = 0; i < 3; i++) {
            normOrigin[i] = origin[i] / 16f;
            normSize[i] = size[i] / 16f;
        }

        RotationResult rotation = getRotation(block);
        Vector3f[] corners = buildAndRotateBoxCorners(normOrigin, normSize, rotation.rotX, rotation.rotY, rotation.isVerticalRotated);
        float[] bounds = calculateBounds(corners);

        // Clamp bounds to [0, 1]
        for (int i = 0; i < 3; i++) {
            if (bounds[i] < 0f) {
                float delta = -bounds[i];
                bounds[i] = 0f;
                bounds[i + 3] += delta;
            }
            if (bounds[i + 3] > 1f) {
                float delta = bounds[i + 3] - 1f;
                bounds[i + 3] = 1f;
                bounds[i] -= delta;
            }
        }

        double x = block.x, y = block.y, z = block.z;
        double x1 = x + bounds[0];
        double y1 = y + bounds[1];
        double z1 = z + bounds[2];
        double x2 = x + bounds[3];
        double y2 = y + bounds[4];
        double z2 = z + bounds[5];

        return new SimpleAxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    public static boolean isSolidForBlock(CustomBlockDefinition def, @NotNull Block block) {
        AxisAlignedBB box = getBoundingBox(def, block);
        if (box == null) return true;
        double height = box.getMaxY() - box.getMinY();
        return height >= 0.99;
    }

    private static float[] readVector(CompoundTag components, String key) {
        CompoundTag collision = components.getCompound("minecraft:collision_box");
        ListTag<FloatTag> tagList = collision.getList(key, FloatTag.class);
        if (tagList.size() < 3) return null;
        return new float[]{ tagList.get(0).data, tagList.get(1).data, tagList.get(2).data };
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
