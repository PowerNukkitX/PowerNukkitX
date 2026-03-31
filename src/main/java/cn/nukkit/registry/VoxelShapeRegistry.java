package cn.nukkit.registry;

import cn.nukkit.block.customblock.data.voxel.VoxelBox;
import cn.nukkit.network.protocol.types.voxel.VoxelCells;
import cn.nukkit.network.protocol.types.voxel.VoxelShape;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;

public final class VoxelShapeRegistry implements IRegistry<String, VoxelShape, VoxelShape> {
    private static final Object2ObjectOpenHashMap<String, VoxelShape> REGISTRY = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {
        try {
            register("minecraft:empty", new VoxelShape(new VoxelCells(0, 0, 0, List.of()), List.of(0f), List.of(0f), List.of(0f)));
            register("minecraft:unit_cube", new VoxelShape(new VoxelCells(1, 1, 1, List.of(1)), List.of(0f, 1f), List.of(0f, 1f), List.of(0f, 1f)));
        } catch (RegisterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers a VoxelShape by converting a list of raw boxes.
     */
    public void register(String key, List<VoxelBox> boxes) throws RegisterException {
        register(key, convertBoxesToShape(boxes));
    }

    private VoxelShape convertBoxesToShape(List<VoxelBox> boxes) {
        if (boxes.isEmpty()) {
            return new VoxelShape(new VoxelCells(0, 0, 0, List.of()), List.of(0f), List.of(0f), List.of(0f));
        }

        // 1. Generate unique normalized axis boundaries
        List<Float> xCoords = getAxisBoundaries(boxes, 0);
        List<Float> yCoords = getAxisBoundaries(boxes, 1);
        List<Float> zCoords = getAxisBoundaries(boxes, 2);

        int resX = xCoords.size() - 1;
        int resY = yCoords.size() - 1;
        int resZ = zCoords.size() - 1;

        // 2. Fill the bitmask based on box occupancy
        BitSet bitSet = new BitSet(resX * resY * resZ);
        for (int z = 0; z < resZ; z++) {
            for (int y = 0; y < resY; y++) {
                for (int x = 0; x < resX; x++) {
                    float midX = (xCoords.get(x) + xCoords.get(x + 1)) / 32.0f; // Scale to 0-1 range for check
                    float midY = (yCoords.get(y) + yCoords.get(y + 1)) / 32.0f;
                    float midZ = (zCoords.get(z) + zCoords.get(z + 1)) / 32.0f;

                    if (isInside(midX, midY, midZ, boxes)) {
                        bitSet.set(x + (y * resX) + (z * resX * resY));
                    }
                }
            }
        }

        // 3. Convert BitSet to a list of integers (Bedrock style)
        List<Integer> bitmask = new ArrayList<>();
        for (byte b : bitSet.toByteArray()) {
            bitmask.add(b & 0xFF);
        }

        return new VoxelShape(new VoxelCells(resX, resY, resZ, bitmask), xCoords, yCoords, zCoords);
    }

    private List<Float> getAxisBoundaries(List<VoxelBox> boxes, int axis) {
        SortedSet<Float> bounds = new TreeSet<>();
        bounds.add(0.0f);
        bounds.add(1.0f);
        for (VoxelBox box : boxes) {
            bounds.add(box.min[axis] / 16.0f);
            bounds.add(box.max[axis] / 16.0f);
        }
        return new ArrayList<>(bounds);
    }

    private boolean isInside(float x, float y, float z, List<VoxelBox> boxes) {
        for (VoxelBox box : boxes) {
            if (x >= box.min[0] / 16f && x <= box.max[0] / 16f &&
                    y >= box.min[1] / 16f && y <= box.max[1] / 16f &&
                    z >= box.min[2] / 16f && z <= box.max[2] / 16f) return true;
        }
        return false;
    }

    @Override
    public VoxelShape get(String key) {
        return REGISTRY.get(key);
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void reload() {
        REGISTRY.clear();
    }

    @Override
    public void register(String key, VoxelShape value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value) != null) {
            throw new RegisterException("The VoxelShape " + key + " has already been registered!");
        }
    }

    public Object2ObjectOpenHashMap<String, VoxelShape> getAll() {
        return new Object2ObjectOpenHashMap<>(REGISTRY);
    }
}