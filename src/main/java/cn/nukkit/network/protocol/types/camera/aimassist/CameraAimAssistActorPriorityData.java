package cn.nukkit.network.protocol.types.camera.aimassist;

import lombok.Value;

@Value
public class CameraAimAssistActorPriorityData {

    int presetIndex;
    int categoryIndex;
    int actorIndex;
    int priorityValue;

}
