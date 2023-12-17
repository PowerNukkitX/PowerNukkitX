package cn.nukkit.network.protocol;

import cn.nukkit.camera.data.Ease;
import cn.nukkit.camera.data.Time;
import cn.nukkit.camera.instruction.CameraInstruction;
import cn.nukkit.camera.instruction.impl.ClearInstruction;
import cn.nukkit.camera.instruction.impl.FadeInstruction;
import cn.nukkit.camera.instruction.impl.SetInstruction;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnegative;
import java.awt.*;


@Getter
@Setter
public class CameraInstructionPacket extends DataPacket {
    private SetInstruction setInstruction;
    private FadeInstruction fadeInstruction;
    private ClearInstruction clearInstruction;

    @Override
    @Deprecated
    public byte pid() {
        return (byte) packetId();
    }

    @Nonnegative

    @Override
    public int packetId() {
        return ProtocolInfo.CAMERA_INSTRUCTION_PACKET;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        putNotNull(setInstruction, (s) -> {
            putLInt(s.getPreset().getId());
            putNotNull(s.getEase(), this::writeEase);
            putNotNull(s.getPos(), this::putVector3f);
            putNotNull(s.getRot(), this::putVector2f);
            putNotNull(s.getFacing(), this::putVector3f);
            putOptional(s.getDefaultPreset(), this::putBoolean);
        });
        if (clearInstruction == null) {
            putBoolean(false);
        } else {
            putBoolean(true);//optional.isPresent
            putBoolean(true);//actual data
        }
        putNotNull(fadeInstruction, (f) -> {
            putNotNull(f.getTime(), this::writeTimeData);
            putNotNull(f.getColor(), this::writeColor);
        });
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

    protected void writeEase(Ease ease) {
        this.putByte((byte) ease.easeType().ordinal());
        this.putLFloat(ease.time());
    }

    protected void writeTimeData(Time time) {
        this.putLFloat(time.fadeIn());
        this.putLFloat(time.hold());
        this.putLFloat(time.fadeOut());
    }

    protected void writeColor(Color color) {
        this.putLFloat(color.getRed() / 255F);
        this.putLFloat(color.getGreen() / 255F);
        this.putLFloat(color.getBlue() / 255F);
    }
}
