package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.voxel.VoxelCells;
import cn.nukkit.network.protocol.types.voxel.VoxelShape;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since v924
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VoxelShapesPacket extends DataPacket {
    private List<VoxelShape> shapes;
    private Map<String, Integer> nameMap;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int shapeCount = byteBuf.readUnsignedVarInt();
        this.shapes = new ArrayList<>(shapeCount);

        for (int i = 0; i < shapeCount; i++) {
            int cellCount = byteBuf.readUnsignedVarInt();
            List<VoxelCells> cells = new ArrayList<>(cellCount);

            for (int c = 0; c < cellCount; c++) {
                short xSize = byteBuf.readUnsignedByte();
                short ySize = byteBuf.readUnsignedByte();
                short zSize = byteBuf.readUnsignedByte();

                int storageSize = byteBuf.readUnsignedVarInt();
                List<Short> storage = new ArrayList<>(storageSize);

                for (int s = 0; s < storageSize; s++) {
                    storage.add((short) byteBuf.readUnsignedByte());
                }

                cells.add(new VoxelCells(xSize, ySize, zSize, storage));
            }

            int xSize = byteBuf.readUnsignedVarInt();
            List<Float> xCoords = new ArrayList<>(xSize);
            for (int j = 0; j < xSize; j++) xCoords.add(byteBuf.readFloatLE());

            int ySize = byteBuf.readUnsignedVarInt();
            List<Float> yCoords = new ArrayList<>(ySize);
            for (int j = 0; j < ySize; j++) yCoords.add(byteBuf.readFloatLE());

            int zSize = byteBuf.readUnsignedVarInt();
            List<Float> zCoords = new ArrayList<>(zSize);
            for (int j = 0; j < zSize; j++) zCoords.add(byteBuf.readFloatLE());

            shapes.add(new VoxelShape(cells, xCoords, yCoords, zCoords));
        }

        int mapSize = byteBuf.readUnsignedVarInt();
        this.nameMap = new HashMap<>(mapSize);

        for (int i = 0; i < mapSize; i++) {
            String key = byteBuf.readString();
            int val = byteBuf.readUnsignedShortLE();
            nameMap.put(key, val);
        }
    }

    @Override
    public void encode(HandleByteBuf buf) {
        buf.writeUnsignedVarInt(shapes.size());

        for (VoxelShape shape : shapes) {

            buf.writeUnsignedVarInt(shape.getCells().size());
            for (VoxelCells cell : shape.getCells()) {
                buf.writeByte(cell.getXSize());
                buf.writeByte(cell.getYSize());
                buf.writeByte(cell.getZSize());

                buf.writeUnsignedVarInt(cell.getStorage().size());
                for (short v : cell.getStorage()) {
                    buf.writeByte(v);
                }
            }

            buf.writeUnsignedVarInt(shape.getXCoordinates().size());
            for (float f : shape.getXCoordinates()) buf.writeFloatLE(f);

            buf.writeUnsignedVarInt(shape.getYCoordinates().size());
            for (float f : shape.getYCoordinates()) buf.writeFloatLE(f);

            buf.writeUnsignedVarInt(shape.getZCoordinates().size());
            for (float f : shape.getZCoordinates()) buf.writeFloatLE(f);
        }

        buf.writeUnsignedVarInt(nameMap.size());
        nameMap.forEach((k, v) -> {
            buf.writeString(k);
            buf.writeShortLE(v);
        });
    }

    @Override
    public int pid() {
        return ProtocolInfo.VOXEL_SHAPES_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
