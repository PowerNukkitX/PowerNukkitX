package cn.nukkit.camera.instruction.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CameraFovInstruction {
    private float fov;
    private float easeTime;
    private CameraEase easeType;
    private boolean clear;
}
