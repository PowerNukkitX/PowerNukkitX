package cn.nukkit.camera.instruction.impl;

import cn.nukkit.camera.data.Time;
import cn.nukkit.camera.instruction.CameraInstruction;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * @author daoge_cmd (PowerNukkitX Project)
 * @since 2023/6/11
 */


@Builder
@Getter
public class FadeInstruction implements CameraInstruction {

    @Nullable
    private final Color color;
    @Nullable
    private final Time time;
}
