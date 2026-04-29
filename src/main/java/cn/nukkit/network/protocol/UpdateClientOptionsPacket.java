package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.GraphicsMode;
import cn.nukkit.utils.OptionalValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateClientOptionsPacket extends DataPacket {

    public OptionalValue<GraphicsMode> graphicsMode;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        graphicsMode = OptionalValue.of(byteBuf.readOptional(null, () -> GraphicsMode.values()[byteBuf.readByte()]));
        byteBuf.readOptional(null, byteBuf::readBoolean);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeOptional(graphicsMode, graphicsMode -> byteBuf.writeByte(graphicsMode.ordinal()));
        byteBuf.writeBoolean(false);
    }

    @Override
    public int pid() {
        return ProtocolInfo.UPDATE_CLIENT_OPTIONS_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
