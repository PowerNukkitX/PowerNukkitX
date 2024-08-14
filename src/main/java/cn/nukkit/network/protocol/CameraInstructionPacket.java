package cn.nukkit.network.protocol;

import cn.nukkit.camera.data.Ease;
import cn.nukkit.camera.data.Time;
import cn.nukkit.camera.instruction.CameraInstruction;
import cn.nukkit.camera.instruction.impl.ClearInstruction;
import cn.nukkit.camera.instruction.impl.FadeInstruction;
import cn.nukkit.camera.instruction.impl.SetInstruction;
import cn.nukkit.camera.instruction.impl.TargetInstruction;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.OptionalValue;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.awt.*;
import java.util.Optional;


@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CameraInstructionPacket extends DataPacket {
    public SetInstruction setInstruction;
    public FadeInstruction fadeInstruction;
    public ClearInstruction clearInstruction;
    private TargetInstruction targetInstruction;
    private OptionalBoolean removeTarget = OptionalBoolean.empty();

    @Override
    public int pid() {
        return ProtocolInfo.CAMERA_INSTRUCTION_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeNotNull(setInstruction, (s) -> {
            byteBuf.writeIntLE(s.getPreset().getId());
            byteBuf.writeNotNull(s.getEase(), e -> this.writeEase(byteBuf, e));
            byteBuf.writeNotNull(s.getPos(), byteBuf::writeVector3f);
            byteBuf.writeNotNull(s.getRot(), byteBuf::writeVector2f);
            byteBuf.writeNotNull(s.getFacing(), byteBuf::writeVector3f);
            byteBuf.writeOptional(s.getDefaultPreset(), byteBuf::writeBoolean);
        });
        if (clearInstruction == null) {
            byteBuf.writeBoolean(false);
        } else {
            byteBuf.writeBoolean(true);//optional.isPresent
            byteBuf.writeBoolean(true);//actual data
        }
        byteBuf.writeNotNull(fadeInstruction, (f) -> {
            byteBuf.writeNotNull(f.getTime(), t -> this.writeTimeData(byteBuf, t));
            byteBuf.writeNotNull(f.getColor(), c -> this.writeColor(byteBuf, c));
        });
        if (this.targetInstruction == null) {
            byteBuf.writeBoolean(false);
        } else {
            byteBuf.writeBoolean(true);
            if (this.targetInstruction.getTargetCenterOffset() != null) {
                byteBuf.writeVector3f(this.targetInstruction.getTargetCenterOffset());
            }
            byteBuf.writeLongLE(this.targetInstruction.getUniqueEntityId());
        }
        byteBuf.writeOptional(OptionalValue.of(this.removeTarget.isPresent()), byteBuf::writeBoolean);
    }

    public void setInstruction(CameraInstruction instruction) {
        if (instruction instanceof SetInstruction se) {
            this.setInstruction = se;
        } else if (instruction instanceof FadeInstruction fa) {
            this.fadeInstruction = fa;
        } else if (instruction instanceof ClearInstruction cl) {
            this.clearInstruction = cl;
        }
    }

    protected void writeEase(HandleByteBuf byteBuf, Ease ease) {
        byteBuf.writeByte((byte) ease.easeType().ordinal());
        byteBuf.writeFloatLE(ease.time());
    }

    protected void writeTimeData(HandleByteBuf byteBuf, Time time) {
        byteBuf.writeFloatLE(time.fadeIn());
        byteBuf.writeFloatLE(time.hold());
        byteBuf.writeFloatLE(time.fadeOut());
    }

    protected void writeColor(HandleByteBuf byteBuf, Color color) {
        byteBuf.writeFloatLE(color.getRed() / 255F);
        byteBuf.writeFloatLE(color.getGreen() / 255F);
        byteBuf.writeFloatLE(color.getBlue() / 255F);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
