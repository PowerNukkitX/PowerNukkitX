package cn.nukkit.network.protocol;


import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStartItemCoolDownPacket extends DataPacket {
    private String itemCategory;
    private int coolDownDuration;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.itemCategory = byteBuf.readString();
        this.coolDownDuration = byteBuf.readVarInt();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(itemCategory);
        byteBuf.writeVarInt(coolDownDuration);
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public int getCoolDownDuration() {
        return coolDownDuration;
    }

    public void setCoolDownDuration(int coolDownDuration) {
        this.coolDownDuration = coolDownDuration;
    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_START_ITEM_COOL_DOWN_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
