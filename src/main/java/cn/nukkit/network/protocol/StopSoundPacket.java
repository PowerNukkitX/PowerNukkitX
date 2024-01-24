package cn.nukkit.network.protocol;

import lombok.ToString;

@ToString
public class StopSoundPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.STOP_SOUND_PACKET;

    public String name;
    public boolean stopAll;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.name);
        this.putBoolean(this.stopAll);
    }
}
