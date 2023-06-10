package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.camera.instruction.CameraInstruction;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnegative;
import java.io.IOException;
import java.nio.ByteOrder;

@Since("1.19.70-r1")
@PowerNukkitXOnly
@Getter
@Setter
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
        try {
            this.put(NBTIO.write(this.data, ByteOrder.LITTLE_ENDIAN, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Since("1.20.0-r2")
    public void setInstruction(CameraInstruction instruction) {
        var tag = instruction.serialize();
        data = new CompoundTag().put(tag.getName(), tag);
    }
}
