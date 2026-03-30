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
    private int customShapeCount;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        List<VoxelShape> shapes = new ArrayList<>();

        byteBuf.readArray(shapes, (buf) -> {
            short xSize = buf.readUnsignedByte();
            short ySize = buf.readUnsignedByte();
            short zSize = buf.readUnsignedByte();

            List<Short> storage = new ArrayList<>();
            buf.readArray(storage, HandleByteBuf::readUnsignedByte);

            VoxelCells cells = new VoxelCells(xSize, ySize, zSize, storage);

            List<Float> xCoordinates = new ArrayList<>();
            buf.readArray(xCoordinates, HandleByteBuf::readFloatLE);

            List<Float> yCoordinates = new ArrayList<>();
            buf.readArray(yCoordinates, HandleByteBuf::readFloatLE);

            List<Float> zCoordinates = new ArrayList<>();
            buf.readArray(zCoordinates, HandleByteBuf::readFloatLE);

            return new VoxelShape(cells, xCoordinates, yCoordinates, zCoordinates);
        });

        setShapes(shapes);

        Map<String, Integer> nameMap = new HashMap<>();

        int size = byteBuf.readUnsignedVarInt();
        for (int i = 0; i < size; i++) {
            nameMap.put(byteBuf.readString(), byteBuf.readUnsignedShortLE());
        }

        setNameMap(nameMap);

        setCustomShapeCount(byteBuf.readUnsignedShortLE());
    }

    @Override
    public void encode(HandleByteBuf buf) {
        buf.writeArray(shapes, (shape) -> {
            buf.writeByte(shape.getCells().getXSize());
            buf.writeByte(shape.getCells().getYSize());
            buf.writeByte(shape.getCells().getZSize());

            buf.writeArray(shape.getCells().getStorage(), (buf2, value) -> buf2.writeByte(value));

            buf.writeArray(shape.getXCoordinates(), HandleByteBuf::writeFloatLE);
            buf.writeArray(shape.getYCoordinates(), HandleByteBuf::writeFloatLE);
            buf.writeArray(shape.getZCoordinates(), HandleByteBuf::writeFloatLE);
        });

        buf.writeUnsignedVarInt(nameMap.size());
        nameMap.forEach((k, v) -> {
            buf.writeString(k);
            buf.writeShortLE(v);
        });

        buf.writeShortLE(customShapeCount);
    }

    @Override
    public int pid() {
        return ProtocolInfo.VOXEL_SHAPES_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
