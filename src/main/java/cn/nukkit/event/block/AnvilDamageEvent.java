package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.Damage;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AnvilDamageEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    private final Player player;

    @Nullable
    private final CraftingTransaction transaction;

    @NotNull
    private final DamageCause cause;
    @NotNull
    private Block newBlock;

    public AnvilDamageEvent(Block block, BlockAnvil newBlock, DamageCause cause, Player source, CraftingTransaction transaction) {
        super(block);
        this.newBlock = newBlock;
        this.cause = cause;
        this.player = source;
        this.transaction = transaction;
    }

    @NotNull
    public DamageCause getDamageCause() {
        return cause;
    }

    public BlockAnvil getOldBlock() {
        return (BlockAnvil) super.getBlock();
    }

    public BlockAnvil getNewBlock() {
        return (BlockAnvil) newBlock;
    }

    public void setNewBlock(@NotNull Block block) {
        this.newBlock = block;
    }

    @NotNull
    public DamageCause getCause() {
        return this.cause;
    }

    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    @Nullable
    public CraftingTransaction getTransaction() {
        return transaction;
    }

    public enum DamageCause {
        USE,
        FALL
    }
}
