package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PredictionType;
import cn.nukkit.utils.OptionalValue;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CorrectPlayerMovePredictionPacket extends DataPacket {

    private PredictionType predictionType;
    private Vector3f position;
    private Vector3f delta;
    private Vector2f rotation;
    private Float vehicleAngularVelocity;
    private boolean onGround;
    private long tick;

    @Override
    public void decode(HandleByteBuf buf) {
        this.predictionType = PredictionType.values()[buf.readUnsignedByte()];
        this.position = buf.readVector3f();
        this.delta = buf.readVector3f();
        this.rotation = buf.readVector2f();
        this.vehicleAngularVelocity = buf.readOptional(null, buf::readFloatLE);
        this.onGround = buf.readBoolean();
        this.tick = buf.readVarLong();
    }

    @Override
    public void encode(HandleByteBuf buf) {
        buf.writeByte(this.predictionType.ordinal());
        buf.writeVector3f(this.position);
        buf.writeVector3f(this.delta);
        buf.writeVector2f(this.rotation);
        buf.writeOptional(OptionalValue.ofNullable(this.vehicleAngularVelocity), buf::writeFloatLE);
        buf.writeBoolean(this.onGround);
        buf.writeVarLong(this.tick);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CORRECT_PLAYER_MOVE_PREDICTION_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
