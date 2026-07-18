package org.powernukkitx.level.entity.condition;

import org.powernukkitx.block.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Condition {

    protected final String identifier;

    public abstract boolean evaluate(Block block);

}
