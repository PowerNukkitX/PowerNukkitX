package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;

import javax.annotation.Nonnegative;

@Since("1.19.70-r1")
@PowerNukkitXOnly
@Getter
public class CameraInstructionPacket extends DataPacket {
    private CompoundTag data;

    @Override
    @Deprecated
    public byte pid() {
        return (byte) packetId();
    }

    @Nonnegative
    @Since("1.19.70-r1")
    @Override
    public int packetId() {
        return ProtocolInfo.CAMERA_INSTRUCTION_PACKET;
    }

    @Override
    public void decode() {
        this.data = this.getTag();
    }

    @Override
    public void encode() {
        this.reset();
        this.putTag(this.data);
    }
}
