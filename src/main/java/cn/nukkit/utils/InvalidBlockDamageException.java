package cn.nukkit.utils;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Deprecated
@DeprecationDetails(since = "1.4.0.0-PN", reason = "Moved to a class with more details and unlimited data bits", replaceWith = "InvalidBlockPropertyMetaException")
public class InvalidBlockDamageException extends InvalidBlockPropertyMetaException {
    private final int blockId;
    private final int damage;
    private final int before;


    public InvalidBlockDamageException(int blockId, int damage, int before) {
        super(BlockUnknown.UNKNOWN,
                before, damage,
                "Invalid block-meta combination. New: " + blockId + ":" + damage + ", Before: " + blockId + ":" + before);
        this.blockId = blockId;
        this.damage = damage;
        this.before = before;
    }


    public int getBlockId() {
        return this.blockId;
    }


    public int getDamage() {
        return this.damage;
    }


    public int getBefore() {
        return this.before;
    }
}
