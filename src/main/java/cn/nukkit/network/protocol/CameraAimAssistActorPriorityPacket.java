package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.camera.aimassist.CameraAimAssistActorPriorityData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.*;

import java.util.List;

/**
 * @since v924
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CameraAimAssistActorPriorityPacket extends DataPacket {

    private List<CameraAimAssistActorPriorityData> priorityData;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        int size = byteBuf.readUnsignedVarInt();
        ObjectList<CameraAimAssistActorPriorityData> list = new ObjectArrayList<>(size);

        for (int i = 0; i < size; i++) {
            list.add(new CameraAimAssistActorPriorityData(
                    byteBuf.readIntLE(),
                    byteBuf.readIntLE(),
                    byteBuf.readIntLE(),
                    byteBuf.readIntLE()
            ));
        }

        this.priorityData = list;
    }


    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeArray(this.getPriorityData(), (priority) -> {
            byteBuf.writeIntLE(priority.getPresetIndex());
            byteBuf.writeIntLE(priority.getCategoryIndex());
            byteBuf.writeIntLE(priority.getActorIndex());
            byteBuf.writeIntLE(priority.getPriorityValue());
        });
    }

    @Override
    public int pid() {
        return ProtocolInfo.CAMERA_AIM_ASSIST_ACTOR_PRIORITY_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
