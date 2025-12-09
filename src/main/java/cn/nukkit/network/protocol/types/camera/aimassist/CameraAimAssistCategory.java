package cn.nukkit.network.protocol.types.camera.aimassist;

import lombok.Data;

@Data
public class CameraAimAssistCategory {
    public String name;
    public CameraAimAssistCategoryPriorities priorities;
    public Integer entityDefaultPriorities;
    public Integer blockDefaultPriorities;
}
