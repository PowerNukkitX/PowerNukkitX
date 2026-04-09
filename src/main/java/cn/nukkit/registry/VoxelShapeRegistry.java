package cn.nukkit.registry;

import cn.nukkit.block.customblock.data.voxel.VoxelBox;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.VoxelShapes;
import org.cloudburstmc.protocol.bedrock.packet.VoxelShapesPacket;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public final class VoxelShapeRegistry implements IRegistry<String, VoxelShapes.SerializableVoxelShape, VoxelShapes.SerializableVoxelShape> {
    private static final Object2ObjectOpenHashMap<String, VoxelShapes.SerializableVoxelShape> REGISTRY = new Object2ObjectOpenHashMap<>();
    @Getter
    private static VoxelShapesPacket PACKET = new VoxelShapesPacket();

    private static VoxelShapes.SerializableVoxelShape EMPTY_SHAPE;

    @Override
    public void init() {
        try {
            final VoxelShapes.SerializableCells emptyCells = new VoxelShapes.SerializableCells();
            emptyCells.setXSize(0);
            emptyCells.setYSize(0);
            emptyCells.setZSize(0);
            EMPTY_SHAPE = new VoxelShapes.SerializableVoxelShape();
            EMPTY_SHAPE.setCells(emptyCells);
            EMPTY_SHAPE.getXCoordinates().add(0f);
            EMPTY_SHAPE.getYCoordinates().add(0f);
            EMPTY_SHAPE.getZCoordinates().add(0f);

            final VoxelShapes.SerializableCells unitCubeShapeCells = new VoxelShapes.SerializableCells();
            unitCubeShapeCells.setXSize(1);
            unitCubeShapeCells.setYSize(1);
            unitCubeShapeCells.setZSize(1);
            final VoxelShapes.SerializableVoxelShape unitCubeShape = new VoxelShapes.SerializableVoxelShape();
            unitCubeShape.setCells(unitCubeShapeCells);
            unitCubeShape.getXCoordinates().add(0f);
            unitCubeShape.getXCoordinates().add(1f);
            unitCubeShape.getYCoordinates().add(0f);
            unitCubeShape.getYCoordinates().add(1f);
            unitCubeShape.getZCoordinates().add(0f);
            unitCubeShape.getZCoordinates().add(1f);

            register("minecraft:empty", EMPTY_SHAPE);
            register("minecraft:unit_cube", unitCubeShape);
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

    public void rebuildPacket() {
        VoxelShapesPacket pk = new VoxelShapesPacket();

        Map<String, VoxelShapes.RegistryHandle> nameMap = new HashMap<>();
        for (String name : REGISTRY.keySet()) {
            final VoxelShapes.RegistryHandle handle = new VoxelShapes.RegistryHandle();
            handle.setValue(nameMap.size());
            nameMap.put(name, handle);
        }

        pk.getShapes().addAll(REGISTRY.values().stream().toList());
        pk.getNameMap().putAll(nameMap);
        pk.setCustomShapeCount(REGISTRY.size() - 2);

        PACKET = pk;
    }

    private VoxelShapes.SerializableVoxelShape convertBoxesToShape(List<VoxelBox> boxes) {
        if (boxes.isEmpty()) {
            return EMPTY_SHAPE;
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


        final VoxelShapes.SerializableCells cells = new VoxelShapes.SerializableCells();
        cells.setXSize(resX);
        cells.setYSize(resY);
        cells.setZSize(resZ);
        cells.getStorage().addAll(bitmask);
        final VoxelShapes.SerializableVoxelShape shape = new VoxelShapes.SerializableVoxelShape();
        shape.getXCoordinates().addAll(xCoords);
        shape.getYCoordinates().addAll(yCoords);
        shape.getZCoordinates().addAll(zCoords);

        return shape;
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
    public VoxelShapes.SerializableVoxelShape get(String key) {
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
    public void register(String key, VoxelShapes.SerializableVoxelShape value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value) != null) {
            throw new RegisterException("The VoxelShape " + key + " has already been registered!");
        }
    }

    public Object2ObjectOpenHashMap<String, VoxelShapes.SerializableVoxelShape> getAll() {
        return new Object2ObjectOpenHashMap<>(REGISTRY);
    }
}