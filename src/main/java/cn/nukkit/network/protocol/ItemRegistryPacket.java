package cn.nukkit.network.protocol;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.MainLogger;
import lombok.*;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author GoodLucky777
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemRegistryPacket extends DataPacket {
    private Entry[] entries = Entry.EMPTY_ARRAY;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUnsignedVarInt(this.entries.length);
        try {
            for (Entry entry : this.entries) {
                byteBuf.writeString(entry.getName());
                byteBuf.writeShortLE(entry.runtimeId);
                byteBuf.writeBoolean(entry.componentBased);
                byteBuf.writeVarInt(entry.version);
                byteBuf.writeBytes(NBTIO.write(entry.getData(), ByteOrder.LITTLE_ENDIAN, true));
            }
        } catch (IOException e) {
            MainLogger.getLogger().error("Error while encoding NBT data of ItemRegistryPacket", e);
        }
    }

    public void setEntries(Entry[] entries) {
        this.entries = entries == null? null : entries.length == 0? Entry.EMPTY_ARRAY : entries.clone();
    }

    public Entry[] getEntries() {
        return entries == null? null : entries.length == 0? Entry.EMPTY_ARRAY : entries.clone();
    }

    @ToString
    public static class Entry {

        public static final Entry[] EMPTY_ARRAY = new Entry[0];
        private final String name;

        private final Integer runtimeId;
        private final Integer version;
        private final boolean componentBased;
        private final CompoundTag data;
        public Entry(String name, Integer runtimeId, Integer version, boolean componentBased, CompoundTag data) {
            this.name = name;
            this.runtimeId = runtimeId;
            this.version = version;
            this.componentBased = componentBased;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public Integer getRuntimeId() {
            return runtimeId;
        }

        public CompoundTag getData() {
            return data;
        }

    }

    @Override
    public int pid() {
        return ProtocolInfo.ITEM_REGISTRY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
