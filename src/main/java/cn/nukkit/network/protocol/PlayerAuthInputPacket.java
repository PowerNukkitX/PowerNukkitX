package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.*;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import lombok.Getter;
import lombok.ToString;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@ToString
@Getter
public class PlayerAuthInputPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.PLAYER_AUTH_INPUT_PACKET;

    private float yaw;
    private float pitch;
    private float headYaw;
    private Vector3f position;
    private Vector2 motion;
    private Set<AuthInputAction> inputData = EnumSet.noneOf(AuthInputAction.class);
    private InputMode inputMode;
    private ClientPlayMode playMode;
    private AuthInteractionModel interactionModel;
    private Vector3f vrGazeDirection;
    private long tick;
    private Vector3f delta;
    /**
     * {@link #inputData} must contain {@link AuthInputAction#PERFORM_ITEM_STACK_REQUEST} in order for this to not be null.
     *
     * @since v428
     */
    private ItemStackRequest itemStackRequest;
    private final Map<PlayerActionType, PlayerBlockActionData> blockActionData = new EnumMap<>(PlayerActionType.class);
    private Vector2f analogMoveVector;
    /**
     * @since 649
     */
    private long predictedVehicle;


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.pitch = byteBuf.readFloatLE();
        this.yaw = byteBuf.readFloatLE();
        this.position = byteBuf.readVector3f();
        this.motion = new Vector2(byteBuf.readFloatLE(), byteBuf.readFloatLE());
        this.headYaw = byteBuf.readFloatLE();

        long inputData = byteBuf.readUnsignedVarLong();
        for (int i = 0; i < AuthInputAction.size(); i++) {
            if ((inputData & (1L << i)) != 0) {
                this.inputData.add(AuthInputAction.from(i));
            }
        }

        this.inputMode = InputMode.fromOrdinal((int) byteBuf.readUnsignedVarInt());
        this.playMode = ClientPlayMode.fromOrdinal((int) byteBuf.readUnsignedVarInt());
        this.interactionModel = AuthInteractionModel.fromOrdinal((int) byteBuf.readUnsignedVarInt());

        if (this.playMode == ClientPlayMode.REALITY) {
            this.vrGazeDirection = byteBuf.readVector3f();
        }

        this.tick = byteBuf.readUnsignedVarLong();
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
            this.predictedVehicle = byteBuf.readVarLong();
        }

        // since 1.19.70-r1, v575
        this.analogMoveVector = byteBuf.readVector2f();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        // Noop
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}