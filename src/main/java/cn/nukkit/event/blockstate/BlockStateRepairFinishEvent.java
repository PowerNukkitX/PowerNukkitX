package cn.nukkit.event.blockstate;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockStateRepair;
import cn.nukkit.event.HandlerList;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author joserobjr
 */


public class BlockStateRepairFinishEvent extends BlockStateRepairEvent {
    private @NotNull final List<BlockStateRepair> allRepairs;

    private @NotNull Block result;

    public BlockStateRepairFinishEvent(@NotNull List<BlockStateRepair> allRepairs, @NotNull Block result) {
        super(allRepairs.get(allRepairs.size() - 1));
        this.allRepairs = Collections.unmodifiableList(new ArrayList<>(allRepairs));
        this.result = result;
    }

    public @NotNull List<BlockStateRepair> getAllRepairs() {
        return allRepairs;
    }

    public @NotNull Block getResult() {
        return result;
    }

    public void setResult(@NotNull Block result) {
        this.result = Preconditions.checkNotNull(result);
    }

    private @NotNull static final HandlerList handlers = new HandlerList();

    public @NotNull static HandlerList getHandlers() {
        return handlers;
    }
}
