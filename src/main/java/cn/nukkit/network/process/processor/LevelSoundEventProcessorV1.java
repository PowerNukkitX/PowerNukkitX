package cn.nukkit.network.process.processor;

import cn.nukkit.network.protocol.ProtocolInfo;

public class LevelSoundEventProcessorV1 extends LevelSoundEventProcessor {
    @Override
    public int getPacketId() {
        return ProtocolInfo.LEVEL_SOUND_EVENT_PACKET_V1;
    }
}
