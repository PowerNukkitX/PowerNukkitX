package cn.nukkit.network.protocol;

public class CameraShakePacket extends DataPacket {
    public float intensity;
    public float duration;
    public CameraShakeType shakeType;
    public CameraShakeAction shakeAction;

    @Override
    public byte pid() {
        return ProtocolInfo.CAMERA_SHAKE_PACKET;
    }

    @Override
    public void decode() {
        this.intensity = this.getLFloat();
        this.duration = this.getLFloat();
        this.shakeType = CameraShakeType.values()[this.getByte()];
        this.shakeAction = CameraShakeAction.values()[this.getByte()];
    }

    @Override
    public void encode() {
        this.reset();
        this.putLFloat(this.intensity);
        this.putLFloat(this.duration);
        this.putByte((byte) this.shakeType.ordinal());
        this.putByte((byte) this.shakeAction.ordinal());
    }

    public enum CameraShakeAction {
        ADD,
        STOP
    }

    public enum CameraShakeType {
        POSITIONAL,
        ROTATIONAL
    }
}
