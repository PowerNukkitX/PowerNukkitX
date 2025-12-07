package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;
import lombok.*;

/**
 * @since 15-10-15
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InteractPacket extends DataPacket {
    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;
    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;
    private Vector3f mousePosition;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.action = byteBuf.readByte();
        this.target = byteBuf.readEntityRuntimeId();
        this.mousePosition = byteBuf.readOptional(null, byteBuf::readVector3f);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) this.action);
        byteBuf.writeEntityRuntimeId(this.target);
        byteBuf.writeOptional(OptionalValue.of(this.mousePosition), byteBuf::writeVector3f);
    }

    @Override
    public int pid() {
        return ProtocolInfo.INTERACT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
