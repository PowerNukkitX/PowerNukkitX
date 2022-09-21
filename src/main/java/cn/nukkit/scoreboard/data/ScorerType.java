package cn.nukkit.scoreboard.data;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 计分对象类型 <br>
 * 除了INVALID，其他枚举都有对应{@link cn.nukkit.scoreboard.scorer.IScorer}的实现类 <br>
 * 对于插件来说，使用FAKE类型即可
 */
@PowerNukkitXOnly
@Since("1.19.30-r1")
public enum ScorerType {
    INVALID,
    PLAYER,
    ENTITY,
    FAKE
}
