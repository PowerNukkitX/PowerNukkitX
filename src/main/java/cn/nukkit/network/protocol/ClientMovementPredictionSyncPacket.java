package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.*;

import java.math.BigInteger;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientMovementPredictionSyncPacket extends DataPacket {
    private final Set<EntityFlag> flags = new ObjectOpenHashSet<>();
    private Vector3f actorBoundingBox;
    private MovementAttributesComponent movementAttributesComponent;
    private long actorRuntimeId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        BigInteger flagsInt = byteBuf.readUnsignedBigVarInt(EntityFlag.values().length);
        for (EntityFlag flag : EntityFlag.values()) {
            if (flagsInt.testBit(flag.ordinal())) {
                flags.add(flag);
            }
        }
        actorBoundingBox = byteBuf.readVector3f();
        readMovementAttributesComponent(byteBuf);
        actorRuntimeId = byteBuf.readEntityRuntimeId();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        BigInteger flagsInt = BigInteger.ZERO;
        for (EntityFlag flag : flags) {
            flagsInt = flagsInt.setBit(flag.ordinal());
        }
        byteBuf.writeUnsignedBigVarInt(flagsInt);
        byteBuf.writeVector3f(actorBoundingBox);
        writeMovementAttributesComponent(byteBuf);
        byteBuf.writeEntityRuntimeId(actorRuntimeId);
    }

    public void writeMovementAttributesComponent(HandleByteBuf byteBuf) {
        byteBuf.writeFloatLE(getMovementAttributesComponent().movementSpeed);
        byteBuf.writeFloatLE(getMovementAttributesComponent().underwaterMovementSpeed);
        byteBuf.writeFloatLE(getMovementAttributesComponent().lavaMovementSpeed);
        byteBuf.writeFloatLE(getMovementAttributesComponent().jumpStrength);
        byteBuf.writeFloatLE(getMovementAttributesComponent().health);
        byteBuf.writeFloatLE(getMovementAttributesComponent().hunger);
    }

    public void readMovementAttributesComponent(HandleByteBuf byteBuf) {
        movementAttributesComponent = new MovementAttributesComponent(byteBuf.readFloatLE(), byteBuf.readFloatLE(), byteBuf.readFloatLE(), byteBuf.readFloatLE(), byteBuf.readFloatLE(), byteBuf.readFloatLE());
    }

    public record MovementAttributesComponent(float movementSpeed, float underwaterMovementSpeed, float lavaMovementSpeed, float jumpStrength, float health, float hunger) {
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENT_MOVEMENT_PREDICTION_SYNC_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
