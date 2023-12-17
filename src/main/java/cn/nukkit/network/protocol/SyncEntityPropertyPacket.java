package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import java.nio.ByteOrder;


public class SyncEntityPropertyPacket extends DataPacket {


    public static final byte NETWORK_ID = ProtocolInfo.SYNC_ENTITY_PROPERTY_PACKET;

    private CompoundTag data;


    public SyncEntityPropertyPacket() {
        // Does nothing
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        try {
            this.data = NBTIO.read(this.get(), ByteOrder.BIG_ENDIAN, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void encode(){
        this.reset();
        try {
            this.put(NBTIO.write(data, ByteOrder.BIG_ENDIAN, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public CompoundTag getData() {
        return data;
    }


    public void setData(CompoundTag data) {
        this.data = data;
    }
}
