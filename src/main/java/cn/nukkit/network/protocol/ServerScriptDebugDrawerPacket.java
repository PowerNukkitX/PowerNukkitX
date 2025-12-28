package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.debugshape.DebugShape;
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
            buf.writeOptional(OptionalValue.ofNullable(shape.shapeType), buf::writeScriptDebugShapeType);
            buf.writeOptional(OptionalValue.ofNullable(shape.location), buf::writeVector3f);
            buf.writeOptional(OptionalValue.ofNullable(shape.scale), buf::writeFloat);
            buf.writeOptional(OptionalValue.ofNullable(shape.rotation), buf::writeVector3f);
            buf.writeOptional(OptionalValue.ofNullable(shape.totalTimeLeft), buf::writeFloat);
            buf.writeOptional(OptionalValue.ofNullable(shape.color), buf::writeColor);
            buf.writeVarInt(shape.dimension);
            buf.writeUnsignedVarInt(shape.getShapeType().payloadType);
            switch (shape.getShapeType()) {
                case ARROW -> {
                    buf.writeOptional(OptionalValue.ofNullable(shape.lineEndLocation), buf::writeVector3f);
                    buf.writeOptional(OptionalValue.ofNullable(shape.arrowHeadLength), buf::writeFloatLE);
                    buf.writeOptional(OptionalValue.ofNullable(shape.arrowHeadRadius), buf::writeFloatLE);
                    buf.writeOptional(OptionalValue.ofNullable(shape.numSegments), buf::writeByte);
                }
                case BOX -> {
                    buf.writeVector3f(shape.boxBound);
                }
                case CIRCLE,
                     SPHERE-> {
                    buf.writeByte(shape.numSegments);
                }
                case LINE -> {
                    buf.writeVector3f(shape.lineEndLocation);
                }
                case TEXT -> {
                    buf.writeString(shape.text);
                }
            }
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

