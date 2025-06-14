package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.DebugShape;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
import cn.nukkit.utils.OptionalValue;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ServerScriptDebugDrawerPacket extends DataPacket {
    public List<DebugShape> shapes = new ArrayList<>();

    @Override
    public void decode(HandleByteBuf buf) {
        int shapeCount = buf.readUnsignedVarInt();
        for (int i = 0; i < shapeCount; i++) {
            DebugShape shape = new DebugShape();

            shape.networkId = buf.readUnsignedVarLong();
            shape.shapeType = buf.readOptional(null, buf::readScriptDebugShapeType);
            shape.location = buf.readOptional(null, buf::readVector3f);
            shape.scale = buf.readOptional(null, buf::readFloat);
            shape.rotation = buf.readOptional(null, buf::readVector3f);
            shape.totalTimeLeft = buf.readOptional(null, buf::readFloat);
            shape.color = buf.readOptional(null, buf::readColor);
            shape.text = buf.readOptional(null, buf::readString);
            shape.boxBound = buf.readOptional(null, buf::readVector3f);
            shape.lineEndLocation = buf.readOptional(null, buf::readVector3f);
            shape.arrowHeadLength = buf.readOptional(null, buf::readFloat);
            shape.arrowHeadRadius = buf.readOptional(null, buf::readFloat);
            shape.numSegments = buf.readOptional(null, buf::readInt);

            shapes.add(shape);
        }
    }

    @Override
    public void encode(HandleByteBuf buf) {
        buf.writeUnsignedVarInt(shapes.size());

        for (DebugShape shape : shapes) {
            buf.writeUnsignedVarLong(shape.networkId);
            buf.writeOptional(OptionalValue.of(shape.shapeType), buf::writeScriptDebugShapeType);
            buf.writeOptional(OptionalValue.of(shape.location), buf::writeVector3f);
            buf.writeOptional(OptionalValue.of(shape.scale), buf::writeFloat);
            buf.writeOptional(OptionalValue.of(shape.rotation), buf::writeVector3f);
            buf.writeOptional(OptionalValue.of(shape.totalTimeLeft), buf::writeFloat);
            buf.writeOptional(OptionalValue.of(shape.color), buf::writeColor);
            buf.writeOptional(OptionalValue.of(shape.text), buf::writeString);
            buf.writeOptional(OptionalValue.of(shape.boxBound), buf::writeVector3f);
            buf.writeOptional(OptionalValue.of(shape.lineEndLocation), buf::writeVector3f);
            buf.writeOptional(OptionalValue.of(shape.arrowHeadLength), buf::writeFloat);
            buf.writeOptional(OptionalValue.of(shape.arrowHeadRadius), buf::writeFloat);
            buf.writeOptional(OptionalValue.of(shape.numSegments), buf::writeInt);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVER_SCRIPT_DEBUG_DRAWER_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

