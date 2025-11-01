package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.PredictionType;
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
    public void decode(HandleByteBuf byteBuf) {
        this.predictionType = PredictionType.values()[byteBuf.readUnsignedByte()];
        this.position = byteBuf.readVector3f();
        this.delta = byteBuf.readVector3f();
        this.rotation = byteBuf.readVector2f();

        if (this.predictionType == PredictionType.VEHICLE) {
            this.vehicleAngularVelocity = byteBuf.readFloatLE();
        } else {
            this.vehicleAngularVelocity = null;
        }

        this.onGround = byteBuf.readBoolean();
        this.tick = byteBuf.readVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(this.predictionType.ordinal());
        byteBuf.writeVector3f(this.position);
        byteBuf.writeVector3f(this.delta);
        byteBuf.writeVector2f(this.rotation);

        if (this.predictionType == PredictionType.VEHICLE && this.vehicleAngularVelocity != null) {
            byteBuf.writeFloatLE(this.vehicleAngularVelocity);
        }

        byteBuf.writeBoolean(this.onGround);
        byteBuf.writeVarLong(this.tick);
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
