package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.inventory.ArmorSlotAndDamagePair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PlayerArmorDamagePacket extends DataPacket {
    private final List<ArmorSlotAndDamagePair> armorSlotAndDamagePairs = new ObjectArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        armorSlotAndDamagePairs.addAll(List.of(byteBuf.readArray(ArmorSlotAndDamagePair.class, byteBuf::readArmorDamagePair)));
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(armorSlotAndDamagePairs, byteBuf::writeArmorDamagePair);
    }

    public enum PlayerArmorDamageFlag {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        BODY;
    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
