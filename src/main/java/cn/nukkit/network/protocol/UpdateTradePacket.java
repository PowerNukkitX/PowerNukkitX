package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteOrder;

@ToString
public class UpdateTradePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.UPDATE_TRADE_PACKET;

    public byte containerId;
    public byte containerType = 15; //trading id
    public int size = 0; // hardcoded to 0
    public int tradeTier;//交易等级
    public long traderUniqueEntityId;//村民id
    public long playerUniqueEntityId;//村民id
    public String displayName;//硬编码的显示名
    public CompoundTag offers;//交易配方
    public boolean newTradingUi;//是否启用新版交易ui
    public boolean usingEconomyTrade;//未知


    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(containerId);
        byteBuf.writeByte(containerType);
        byteBuf.writeVarInt(size);
        byteBuf.writeVarInt(tradeTier);
        byteBuf.writeEntityUniqueId(traderUniqueEntityId);
        byteBuf.writeEntityUniqueId(playerUniqueEntityId);
        byteBuf.writeString(displayName);
        byteBuf.writeBoolean(newTradingUi);
        byteBuf.writeBoolean(usingEconomyTrade);
        try {
            byteBuf.writeBytes(NBTIO.write(this.offers, ByteOrder.LITTLE_ENDIAN, true));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
