package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class ScriptMessagePacket extends DataPacket {
    private String channel;
    private String message;

    @Override
    public byte pid() {
        return ProtocolInfo.SCRIPT_MESSAGE_PACKET;
    }

    @Override
    public void decode() {
        this.channel = getString();
        this.message = getString();
    }

    @Override
    public void encode() {
        putString(channel);
        putString(message);
    }
}
