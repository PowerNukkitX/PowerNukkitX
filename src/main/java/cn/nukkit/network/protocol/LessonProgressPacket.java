package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.LessonAction;
import lombok.ToString;

@Since("1.19.30-r1")
@PowerNukkitXOnly
@ToString
public class LessonProgressPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.LESSON_PROGRESS_PACKET;

    public LessonAction action;
    public int score;
    public String activityId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.action = LessonAction.values()[this.getVarInt()];
        this.score = this.getVarInt();
        this.activityId = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(action.ordinal());
        this.putVarInt(score);
        this.putString(activityId);
    }
}
