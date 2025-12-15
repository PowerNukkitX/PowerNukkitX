package cn.nukkit.camera.instruction.impl;

import cn.nukkit.camera.instruction.CameraInstruction;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */


public class ClearInstruction implements CameraInstruction {
    private static final ClearInstruction INSTANCE = new ClearInstruction();

    private ClearInstruction() {}

    public static ClearInstruction get() {
        return INSTANCE;
    }
}
