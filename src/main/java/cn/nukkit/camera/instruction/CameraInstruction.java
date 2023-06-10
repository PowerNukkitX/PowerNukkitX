package cn.nukkit.camera.instruction;

import cn.nukkit.api.DoNotModify;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.tag.Tag;

/**
 * Author: daoge_cmd <br>
 * Date: 2023/6/11 <br>
 * PowerNukkitX Project <br>
 */
@PowerNukkitXOnly
@Since("1.20.0-r2")
public interface CameraInstruction {
    @DoNotModify
    Tag serialize();
}
