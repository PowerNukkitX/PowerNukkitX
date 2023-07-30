package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;

public class SignColorChangeEvent extends BlockEvent implements Cancellable {

    private final Player player;
    private final BlockColor color;

    public SignColorChangeEvent(Block block, Player player, BlockColor color) {
        super(block);
        this.player = player;
        this.color = color;
    }

    public Player getPlayer() {
        return player;
    }

    public BlockColor getColor() {
        return this.color;
    }
}
