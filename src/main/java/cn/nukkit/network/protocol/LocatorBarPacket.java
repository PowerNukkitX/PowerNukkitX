package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.waypoint.*;
import cn.nukkit.utils.OptionalBoolean;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @since v944
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class LocatorBarPacket extends DataPacket {
    private final List<LocatorBarWaypointPayload> waypoints = new ObjectArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        byteBuf.readArray(getWaypoints(), this::readLocatorBarWaypointPayload);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(waypoints, this::writeLocatorBarWaypointPayload);
    }

    @Override
    public int pid() {
        return ProtocolInfo.LOCATOR_BAR_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    protected void writeLocatorBarWaypointPayload(HandleByteBuf byteBuf, LocatorBarWaypointPayload payload) {
        byteBuf.writeUUID(payload.getGroupHandle());
        this.writeServerWaypointPayload(byteBuf, payload.getServerWaypointPayload());
        byteBuf.writeByte(payload.getActionFlag().ordinal());
    }

    protected LocatorBarWaypointPayload readLocatorBarWaypointPayload(HandleByteBuf byteBuf) {
        final LocatorBarWaypointPayload payload = new LocatorBarWaypointPayload();
        payload.setGroupHandle(byteBuf.readUUID());
        payload.setServerWaypointPayload(this.readServerWaypointPayload(byteBuf));
        payload.setActionFlag(ServerWaypointPayload.Action.from(byteBuf.readUnsignedVarInt()));
        return payload;
    }

    protected void writeServerWaypointPayload(HandleByteBuf byteBuf, ServerWaypointPayload payload) {
        byteBuf.writeIntLE(payload.getUpdateFlag());

        byteBuf.writeBoolean(payload.getIsVisible().isPresent());
        if (payload.getIsVisible().isPresent()) {
            byteBuf.writeBoolean(payload.getIsVisible().getAsBoolean());
        }

        byteBuf.writeNotNull(payload.getWorldPosition(), pos -> writeWorldPosition(byteBuf, pos));
        byteBuf.writeNotNull(payload.getTextureId(), byteBuf::writeIntLE);
        byteBuf.writeNotNull(payload.getColor(), byteBuf::writeIntLE);

        byteBuf.writeBoolean(payload.getClientPositionAuthority().isPresent());
        if (payload.getClientPositionAuthority().isPresent()) {
            byteBuf.writeBoolean(payload.getClientPositionAuthority().getAsBoolean());
        }

        byteBuf.writeNotNull(payload.getActorUniqueID(), byteBuf::writeVarLong);
    }

    protected ServerWaypointPayload readServerWaypointPayload(HandleByteBuf byteBuf) {
        final ServerWaypointPayload payload = new ServerWaypointPayload();
        payload.setUpdateFlag(byteBuf.readIntLE());
        payload.setIsVisible(byteBuf.readOptional(OptionalBoolean.empty(), () -> OptionalBoolean.of(byteBuf.readBoolean())));
        payload.setWorldPosition(byteBuf.readOptional(null, () -> readWorldPosition(byteBuf)));
        payload.setTextureId(byteBuf.readOptional(null, byteBuf::readIntLE));
        payload.setColor(byteBuf.readOptional(null, byteBuf::readIntLE));
        payload.setClientPositionAuthority(byteBuf.readOptional(OptionalBoolean.empty(), () -> OptionalBoolean.of(byteBuf.readBoolean())));
        payload.setActorUniqueID(byteBuf.readOptional(null, byteBuf::readVarLong));
        return payload;
    }

    protected void writeWorldPosition(HandleByteBuf byteBuf, WorldPosition position) {
        byteBuf.writeVector3f(position.getPosition());
        byteBuf.writeVarInt(position.getDimensionId());
    }

    protected WorldPosition readWorldPosition(HandleByteBuf byteBuf) {
        final Vector3f position = byteBuf.readVector3f();
        final int dimensionType = byteBuf.readVarInt();
        return new WorldPosition(position, dimensionType);
    }
}
