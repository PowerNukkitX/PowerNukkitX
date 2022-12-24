package cn.nukkit.block.customblock.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Builder;
import lombok.Getter;

@Since("1.19.50-r3")
@PowerNukkitXOnly
@Builder
@Getter
public class BoneCondition {
    String conditionName;
    /**
     * The molang condition.
     */
    String conditionExpr;
    String boneName;
    @Builder.Default
    int molangVersion = 6;
}
