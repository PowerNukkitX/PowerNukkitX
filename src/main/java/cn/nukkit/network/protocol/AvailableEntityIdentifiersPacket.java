package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.registry.Registries;
import lombok.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteOrder;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AvailableEntityIdentifiersPacket extends DataPacket {
    private static byte[] TAG;

    static {
        reloadTag();
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBytes(TAG);
    }

    public static void reloadTag() {
        try (InputStream inputStream = Nukkit.class.getModule().getResourceAsStream("entity_identifiers.nbt")) {
            if (inputStream == null) {
                throw new AssertionError("Could not find entity_identifiers.nbt");
            }

            BufferedInputStream bis = new BufferedInputStream(inputStream);
            CompoundTag nbt = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
            ListTag<CompoundTag> list = nbt.getList("idlist", CompoundTag.class);
            for (var customEntityDefinition : Registries.ENTITY.getCustomEntityDefinitions()) {
                list.add(customEntityDefinition.toNBT());
            }
            nbt.putList("idlist", list);
            TAG = NBTIO.write(nbt, ByteOrder.BIG_ENDIAN, true);
        } catch (Exception e) {
            throw new AssertionError("Error whilst loading entity_identifiers.dat", e);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}


