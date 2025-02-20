package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.AuthInputAction;
import cn.nukkit.network.protocol.types.AuthInteractionModel;
import cn.nukkit.network.protocol.types.ClientPlayMode;
import cn.nukkit.network.protocol.types.InputMode;
import cn.nukkit.network.protocol.types.PlayerActionType;
import cn.nukkit.network.protocol.types.PlayerBlockActionData;
import cn.nukkit.network.protocol.types.PlayerInputTick;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import lombok.*;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerAuthInputPacket extends DataPacket {
    public float yaw;
    public float pitch;
    public float headYaw;
    public Vector3f position;
    public Vector2 motion;
    public Set<AuthInputAction> inputData = EnumSet.noneOf(AuthInputAction.class);
    public InputMode inputMode;
    public ClientPlayMode playMode;
    public AuthInteractionModel interactionModel;
    public Vector2f interactRotation;
    public PlayerInputTick tick;
    public Vector3f delta;
    /**
     * {@link #inputData} must contain {@link AuthInputAction#PERFORM_ITEM_STACK_REQUEST} in order for this to not be null.
     *
     * @since v428
     */
    public ItemStackRequest itemStackRequest;
    public final Map<PlayerActionType, PlayerBlockActionData> blockActionData = new EnumMap<>(PlayerActionType.class);
    public Vector2f analogMoveVector;
    /**
     * @since 649
     */
    public long predictedVehicle;
    /**
     * @since 662
     */
    public Vector2f vehicleRotation;
    public Vector3f cameraOrientation;

    /**
     * @since 766
     */
    public Vector2f rawMoveVector;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.pitch = byteBuf.readFloatLE();
        this.yaw = byteBuf.readFloatLE();
        this.position = byteBuf.readVector3f();
        this.motion = new Vector2(byteBuf.readFloatLE(), byteBuf.readFloatLE());
        this.headYaw = byteBuf.readFloatLE();

        BigInteger inputData = byteBuf.readUnsignedBigVarInt(AuthInputAction.size());
        for (int i = 0; i < AuthInputAction.size(); i++) {
            if (inputData.testBit(i)) {
                this.inputData.add(AuthInputAction.from(i));
            }
        }

        this.inputMode = InputMode.fromOrdinal(byteBuf.readUnsignedVarInt());
        this.playMode = ClientPlayMode.fromOrdinal(byteBuf.readUnsignedVarInt());
        this.interactionModel = AuthInteractionModel.fromOrdinal(byteBuf.readUnsignedVarInt());

        this.interactRotation = byteBuf.readVector2f();

        this.tick = byteBuf.readPlayerInputTick();
        this.delta = byteBuf.readVector3f();

        if (this.inputData.contains(AuthInputAction.PERFORM_ITEM_STACK_REQUEST)) {
            this.itemStackRequest = byteBuf.readItemStackRequest();
        }

        if (this.inputData.contains(AuthInputAction.PERFORM_BLOCK_ACTIONS)) {
            int arraySize = byteBuf.readVarInt();
            for (int i = 0; i < arraySize; i++) {
                PlayerActionType type = PlayerActionType.from(byteBuf.readVarInt());
                switch (type) {
                    case START_DESTROY_BLOCK:
                    case ABORT_DESTROY_BLOCK:
                    case CRACK_BLOCK:
                    case PREDICT_DESTROY_BLOCK:
                    case CONTINUE_DESTROY_BLOCK:
                        this.blockActionData.put(type, new PlayerBlockActionData(type, byteBuf.readSignedBlockPosition(), byteBuf.readVarInt()));
                        break;
                    default:
                        this.blockActionData.put(type, new PlayerBlockActionData(type, null, -1));
                }
            }
        }

        if (this.inputData.contains(AuthInputAction.IN_CLIENT_PREDICTED_IN_VEHICLE)) {
            this.vehicleRotation = byteBuf.readVector2f();
            this.predictedVehicle = byteBuf.readVarLong();
        }

        // since 1.19.70-r1, v575
        this.analogMoveVector = byteBuf.readVector2f();

        this.cameraOrientation = byteBuf.readVector3f();

        this.rawMoveVector = byteBuf.readVector2f();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_AUTH_INPUT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}