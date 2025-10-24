package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CorrectPlayerMovePredictionPacket extends DataPacket {

    public static final int PREDICTION_TYPE_PLAYER = 0;
    public static final int PREDICTION_TYPE_VEHICLE = 1;

    private int predictionType;
    private Vector3f position;
    private Vector3f delta;
    private Vector2f rotation;
    private Float vehicleAngularVelocity;
    private boolean onGround;
    private long tick;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.predictionType = byteBuf.readUnsignedByte();
        this.position = byteBuf.readVector3f();
        this.delta = byteBuf.readVector3f();
        this.rotation = new Vector2f(byteBuf.readFloatLE(), byteBuf.readFloatLE());

        boolean hasVehicleAngularVelocity = byteBuf.readBoolean();
        if (hasVehicleAngularVelocity) {
            this.vehicleAngularVelocity = byteBuf.readFloatLE();
        } else {
            this.vehicleAngularVelocity = null;
        }

        this.onGround = byteBuf.readBoolean();
        this.tick = byteBuf.readVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte((byte) (this.predictionType & 0xFF));
        byteBuf.writeVector3f(this.position);
        byteBuf.writeVector3f(this.delta);

        byteBuf.writeFloatLE(this.rotation.getX());
        byteBuf.writeFloatLE(this.rotation.getY());

        byteBuf.writeBoolean(this.vehicleAngularVelocity != null);
        if (this.vehicleAngularVelocity != null) {
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
