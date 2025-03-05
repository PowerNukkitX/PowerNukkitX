package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.TrimMaterial;
import cn.nukkit.network.protocol.types.TrimPattern;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@Builder
@Getter
@Setter
@ToString(doNotUseGetters = true)
@AllArgsConstructor
public class TrimDataPacket extends DataPacket {
    public final List<TrimPattern> patterns = new ObjectArrayList<>();
    public final List<TrimMaterial> materials = new ObjectArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int length1 = byteBuf.readUnsignedVarInt();
        for (int i = 0; i < length1; i++) {
            patterns.add(new TrimPattern(byteBuf.readString(), byteBuf.readString()));
        }
        int length2 = byteBuf.readUnsignedVarInt();
        for (int i = 0; i < length2; i++) {
            materials.add(new TrimMaterial(byteBuf.readString(), byteBuf.readString(), byteBuf.readString()));
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(patterns.size());
        patterns.forEach(p -> {
            byteBuf.writeString(p.itemName());
            byteBuf.writeString(p.patternId());
        });
        byteBuf.writeUnsignedVarInt(materials.size());
        materials.forEach(m -> {
            byteBuf.writeString(m.materialId());
            byteBuf.writeString(m.color());
            byteBuf.writeString(m.itemName());
        });
    }

    public int pid() {
        return ProtocolInfo.TRIM_DATA;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
