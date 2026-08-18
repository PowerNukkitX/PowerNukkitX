package org.powernukkitx.registry;

import org.powernukkitx.block.customblock.data.voxel.VoxelBox;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.VoxelShapes;
import org.cloudburstmc.protocol.bedrock.packet.VoxelShapesPacket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class VoxelShapeRegistry implements IRegistry<String, VoxelShapes.SerializableVoxelShape, VoxelShapes.SerializableVoxelShape> {
    private static final Object2ObjectLinkedOpenHashMap<String, VoxelShapes.SerializableVoxelShape> REGISTRY = new Object2ObjectLinkedOpenHashMap<>();
    @Getter
    private static VoxelShapesPacket PACKET = new VoxelShapesPacket();

    private static VoxelShapes.SerializableVoxelShape EMPTY_SHAPE;
    private static final List<VoxelShapes.SerializableVoxelShape> VANILLA_ANONYMOUS_SHAPES = new ArrayList<>();
    private static int vanillaShapeCount;

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
            unitCubeShapeCells.getStorage().add(1);
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

            loadVanillaShapes();

            vanillaShapeCount = REGISTRY.size();

            rebuildPacket();
        } catch (RegisterException e) {
            e.printStackTrace();
        }
    }

    private void loadVanillaShapes() {
        try (var stream = VoxelShapeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/voxel_shapes.json");
             var reader = new InputStreamReader(stream)) {
            final List<VanillaShape> shapes = new Gson().fromJson(reader, new TypeToken<List<VanillaShape>>() {
            }.getType());
            for (VanillaShape shape : shapes) {
                final List<VoxelBox> boxes = new ArrayList<>();
                for (int[][] box : shape.boxes) {
                    boxes.add(new VoxelBox(box[0], box[1]));
                }

                final VoxelShapes.SerializableVoxelShape voxelShape = convertBoxesToShape(boxes);

                if (shape.identifier == null) {
                    VANILLA_ANONYMOUS_SHAPES.add(voxelShape);
                } else {
                    register(shape.identifier, voxelShape);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (RegisterException e) {
            e.printStackTrace();
        }
    }

    private record VanillaShape(String identifier, int[][][] boxes) {}

    /**
     * Registers a VoxelShape by converting a list of raw boxes.
     */
    public void register(String key, List<VoxelBox> boxes) throws RegisterException {
        register(key, convertBoxesToShape(boxes));
    }

    public void rebuildPacket() {
        VoxelShapesPacket pk = new VoxelShapesPacket();

        int index = 0;

        for (var entry : REGISTRY.object2ObjectEntrySet()) {
            if (index >= vanillaShapeCount) {
                break;
            }

            final VoxelShapes.RegistryHandle handle = new VoxelShapes.RegistryHandle();
            handle.setValue(pk.getShapes().size());

            pk.getShapes().add(entry.getValue());
            pk.getNameMap().put(entry.getKey(), handle);

            index++;
        }

        pk.getShapes().addAll(VANILLA_ANONYMOUS_SHAPES);

        index = 0;

        for (var entry : REGISTRY.object2ObjectEntrySet()) {
            if (index++ < vanillaShapeCount) {
                continue;
            }

            final VoxelShapes.RegistryHandle handle = new VoxelShapes.RegistryHandle();
            handle.setValue(pk.getShapes().size());

            pk.getShapes().add(entry.getValue());
            pk.getNameMap().put(entry.getKey(), handle);
        }

        pk.setCustomShapeCount(Math.max(0, REGISTRY.size() - vanillaShapeCount));

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
                    float midX = (xCoords.get(x) + xCoords.get(x + 1)) / 2.0f;
                    float midY = (yCoords.get(y) + yCoords.get(y + 1)) / 2.0f;
                    float midZ = (zCoords.get(z) + zCoords.get(z + 1)) / 2.0f;

                    if (isInside(midX, midY, midZ, boxes)) {
                        bitSet.set(z + (y * resZ) + (x * resZ * resY));
                    }
                }
            }
        }

        // 3. Convert BitSet to the complete Bedrock storage
        int storageSize = (resX * resY * resZ + 7) / 8;

        List<Integer> bitmask = new ArrayList<>(storageSize);
        byte[] bytes = bitSet.toByteArray();

        for (int i = 0; i < storageSize; i++) {
            bitmask.add(i < bytes.length ? bytes[i] & 0xFF : 0);
        }


        final VoxelShapes.SerializableCells cells = new VoxelShapes.SerializableCells();
        cells.setXSize(resX);
        cells.setYSize(resY);
        cells.setZSize(resZ);
        cells.getStorage().addAll(bitmask);
        final VoxelShapes.SerializableVoxelShape shape = new VoxelShapes.SerializableVoxelShape();
        shape.setCells(cells);
        shape.getXCoordinates().addAll(xCoords);
        shape.getYCoordinates().addAll(yCoords);
        shape.getZCoordinates().addAll(zCoords);

        return shape;
    }

    private List<Float> getAxisBoundaries(List<VoxelBox> boxes, int axis) {
        SortedSet<Float> bounds = new TreeSet<>();
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
        VANILLA_ANONYMOUS_SHAPES.clear();
        vanillaShapeCount = 0;
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
