package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
public class ContainerSetDataPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.CONTAINER_SET_DATA_PACKET;

    public static final int PROPERTY_FURNACE_TICK_COUNT = 0;
    public static final int PROPERTY_FURNACE_LIT_TIME = 1;
    public static final int PROPERTY_FURNACE_LIT_DURATION = 2;
    //TODO: check property 3
    public static final int PROPERTY_FURNACE_FUEL_AUX = 4;

    public static final int PROPERTY_BREWING_STAND_BREW_TIME = 0;
    public static final int PROPERTY_BREWING_STAND_FUEL_AMOUNT = 1;
    public static final int PROPERTY_BREWING_STAND_FUEL_TOTAL = 2;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public int windowId;
    public int property;
    public int value;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeByte((byte) this.windowId);
        byteBuf.writeVarInt(this.property);
        byteBuf.writeVarInt(this.value);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
