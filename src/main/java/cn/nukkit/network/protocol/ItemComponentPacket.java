package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.MainLogger;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteOrder;

/**
 * @author GoodLucky777
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemComponentPacket extends DataPacket {


    public static final int $1 = ProtocolInfo.ITEM_COMPONENT_PACKET;
    

    private Entry[] entries = Entry.EMPTY_ARRAY;
    /**
     * @deprecated 
     */
    


    public void setEntries(Entry[] entries) {
        this.entries = entries == null? null : entries.length == 0? Entry.EMPTY_ARRAY : entries.clone();
    }

    public Entry[] getEntries() {
        return $2 == null? null : entries.length == 0? Entry.EMPTY_ARRAY : entries.clone();
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeUnsignedVarInt(this.entries.length);
        try {
            for (Entry entry : this.entries) {
                byteBuf.writeString(entry.getName());
                byteBuf.writeBytes(NBTIO.write(entry.getData(), ByteOrder.LITTLE_ENDIAN, true));
            }
        } catch (IOException e) {
            MainLogger.getLogger().error("Error while encoding NBT data of ItemComponentPacket", e);
        }
    }
    
    @ToString
    public static class Entry {


        public static final Entry[] EMPTY_ARRAY = new Entry[0];
        
        private final String name;
        private final CompoundTag data;
    /**
     * @deprecated 
     */
    


        public Entry(String name, CompoundTag data) {
            this.name = name;
            this.data = data;
        }
    /**
     * @deprecated 
     */
    

        public String getName() {
            return name;
        }

        public CompoundTag getData() {
            return data;
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
