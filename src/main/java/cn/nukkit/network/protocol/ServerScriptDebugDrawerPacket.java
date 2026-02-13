package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.ScriptDebugShapeType;
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
        int count = buf.readUnsignedVarInt();

        for (int i = 0; i < count; i++) {
            DebugShape shape = new DebugShape();

            shape.networkId = buf.readUnsignedVarLong();

            boolean hasType = buf.readBoolean();
            if (hasType) {
                byte ord = buf.readByte();
                shape.shapeType = ScriptDebugShapeType.values()[ord];
            }

            shape.location = buf.readOptional(null, buf::readVector3f);
            shape.scale = buf.readOptional(null, buf::readFloatLE);
            shape.rotation = buf.readOptional(null, buf::readVector3f);
            shape.totalTimeLeft = buf.readOptional(null, buf::readFloatLE);

            boolean hasColor = buf.readBoolean();
            if (hasColor) {
                shape.color = buf.readColor();
            }

            shape.dimension = buf.readOptional(null, buf::readVarInt);

            boolean hasAttach = buf.readBoolean();
            if (hasAttach) {
                shape.attachedToEntityId = buf.readUnsignedVarLong();
            }

            buf.readUnsignedVarInt(); // payloadType

            if (shape.shapeType == null) {
                shapes.add(shape);
                continue;
            }

            switch (shape.shapeType) {
                case ARROW -> {
                    shape.lineEndLocation = buf.readOptional(null, buf::readVector3f);
                    shape.arrowHeadLength = buf.readOptional(null, buf::readFloatLE);
                    shape.arrowHeadRadius = buf.readOptional(null, buf::readFloatLE);

                    boolean hasSeg = buf.readBoolean();
                    if (hasSeg) {
                        shape.numSegments = (int) buf.readByte();
                    }
                }
                case BOX -> shape.boxBound = buf.readVector3f();
                case CIRCLE, SPHERE -> shape.numSegments = (int) buf.readByte();
                case LINE -> shape.lineEndLocation = buf.readVector3f();
                case TEXT -> shape.text = buf.readString();
            }

            shapes.add(shape);
        }
    }

    @Override
    public void encode(HandleByteBuf buf) {
        buf.writeUnsignedVarInt(shapes.size());

        for (DebugShape shape : shapes) {
            buf.writeUnsignedVarLong(shape.networkId);

            if (shape.shapeType != null) {
                buf.writeBoolean(true);
                buf.writeByte((byte) shape.shapeType.ordinal());
            } else {
                buf.writeBoolean(false);
            }

            buf.writeOptional(OptionalValue.ofNullable(shape.location), buf::writeVector3f);
            buf.writeOptional(OptionalValue.ofNullable(shape.scale), buf::writeFloatLE);
            buf.writeOptional(OptionalValue.ofNullable(shape.rotation), buf::writeVector3f);
            buf.writeOptional(OptionalValue.ofNullable(shape.totalTimeLeft), buf::writeFloatLE);

            if (shape.color != null) {
                buf.writeBoolean(true);
                buf.writeColor(shape.color);
            } else {
                buf.writeBoolean(false);
            }

            buf.writeOptional(OptionalValue.ofNullable(shape.dimension), buf::writeVarInt);

            if (shape.attachedToEntityId != null) {
                buf.writeBoolean(true);
                buf.writeUnsignedVarLong(shape.attachedToEntityId);
            } else {
                buf.writeBoolean(false);
            }

            buf.writeUnsignedVarInt(toPayloadType(shape.shapeType));

            if (shape.shapeType == null) {
                continue;
            }

            switch (shape.shapeType) {
                case ARROW -> {
                    buf.writeOptional(OptionalValue.ofNullable(shape.lineEndLocation), buf::writeVector3f);
                    buf.writeOptional(OptionalValue.ofNullable(shape.arrowHeadLength), buf::writeFloatLE);
                    buf.writeOptional(OptionalValue.ofNullable(shape.arrowHeadRadius), buf::writeFloatLE);

                    if (shape.numSegments != null) {
                        buf.writeBoolean(true);
                        buf.writeByte(shape.numSegments.byteValue());
                    } else {
                        buf.writeBoolean(false);
                    }
                }
                case BOX -> buf.writeVector3f(shape.boxBound);
                case CIRCLE, SPHERE -> buf.writeByte(shape.numSegments.byteValue());
                case LINE -> buf.writeVector3f(shape.lineEndLocation);
                case TEXT -> buf.writeString(shape.text);
            }
        }
    }

    private static int toPayloadType(ScriptDebugShapeType type) {
        if (type == null) return 0;

        return switch (type) {
            case ARROW -> 1;
            case TEXT -> 2;
            case BOX -> 3;
            case LINE -> 4;
            case SPHERE, CIRCLE -> 5;
        };
    }

    @Override
    public int pid() {
        return ProtocolInfo.SERVER_SCRIPT_DEBUG_DRAWER_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
