package cn.nukkit.camera.instruction.impl;

import cn.nukkit.camera.instruction.CameraInstruction;
import cn.nukkit.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetInstruction implements CameraInstruction {
    private Vector3f targetCenterOffset;
    private long uniqueEntityId;
}
