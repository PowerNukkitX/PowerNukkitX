package cn.nukkit.level.entity.condition;

import cn.nukkit.block.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Condition {

    protected final String identifier;

    public abstract boolean evaluate(Block block);

}
