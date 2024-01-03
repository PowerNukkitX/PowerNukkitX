package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import lombok.Getter;

public class PlayerBucketEmptyEvent extends PlayerBucketEvent {
    @Getter
    private static final HandlerList handlers = new HandlerList();

    public PlayerBucketEmptyEvent(Player who, Block blockClicked, BlockFace blockFace, Block liquid, Item bucket, Item itemInHand) {
        super(who, blockClicked, blockFace, liquid, bucket, itemInHand);
    }

}
