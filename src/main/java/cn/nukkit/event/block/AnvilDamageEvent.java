package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.block.BlockState;
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
    private final BlockState oldState;

    @NotNull
    private BlockState newState;


    public AnvilDamageEvent(@NotNull Block block, int oldDamage, int newDamage, @NotNull DamageCause cause, @Nullable Player player) {
        this(adjustBlock(block, oldDamage), block.getBlockState().withData(newDamage), player, null, cause);
    }

    public AnvilDamageEvent(@NotNull Block block, @NotNull Block newState, @Nullable Player player, @Nullable CraftingTransaction transaction, @NotNull DamageCause cause) {
        this(block, newState.getBlockState(), player, transaction, cause);
    }

    public AnvilDamageEvent(@NotNull Block block, @NotNull BlockState newState, @Nullable Player player, @Nullable CraftingTransaction transaction, @NotNull DamageCause cause) {
        super(Preconditions.checkNotNull(block, "block").clone());
        this.oldState = block.getBlockState();
        this.player = player;
        this.transaction = transaction;
        this.cause = Preconditions.checkNotNull(cause, "cause");
        this.newState = Preconditions.checkNotNull(newState, "newState");
    }

    public @Nullable CraftingTransaction getTransaction() {
        return transaction;
    }

    @NotNull
    public DamageCause getDamageCause() {
        return cause;
    }

    @Deprecated
    @DeprecationDetails(since = "FUTURE", by = "PowerNukkit", reason = "Unstable use of raw block state data", replaceWith = "getOldAnvilDamage or getOldBlockState")

    public int getOldDamage() {
        if (!block.getProperties().contains(BlockAnvil.DAMAGE)) {
            return 0;
        }
        return block.getIntValue(BlockAnvil.DAMAGE);
    }

    public @Nullable AnvilDamage getOldAnvilDamage() {
        if (oldState.getProperties().contains(BlockAnvil.DAMAGE)) {
            return oldState.getPropertyValue(BlockAnvil.DAMAGE);
        }
        return null;
    }

    @NotNull
    public BlockState getOldBlockState() {
        return oldState;
    }

    @NotNull
    public BlockState getNewBlockState() {
        return newState;
    }

    @NotNull
    public Block getNewState() {
        return newState.getBlockRepairing(block);
    }

    @Deprecated
    @DeprecationDetails(since = "FUTURE", by = "PowerNukkit", reason = "Unstable use of raw block state data", replaceWith = "getNewAnvilDamage or getNewBlockState")

    public int getNewDamage() {
        BlockState newBlockState = getNewBlockState();
        return newBlockState.getProperties().contains(BlockAnvil.DAMAGE)? newBlockState.getIntValue(BlockAnvil.DAMAGE) : 0;
    }

    public void setNewBlockState(@NotNull BlockState state) {
        this.newState = Preconditions.checkNotNull(state);
    }

    @Deprecated
    @DeprecationDetails(since = "FUTURE", by = "PowerNukkit", reason = "Unstable use of raw block state data",
            replaceWith = "setNewBlockState example: setNewBlockState(BlockState.of(BlockID.ANVIL).withProperty(BlockAnvil.DAMAGE, AnvilDamage.VERY_DAMAGED))")

    public void setNewDamage(int newDamage) {
        BlockState newBlockState = getNewBlockState();
        if (newBlockState.getProperties().contains(BlockAnvil.DAMAGE)) {
            this.setNewBlockState(newBlockState.withProperty(BlockAnvil.DAMAGE, BlockAnvil.DAMAGE.getValueForMeta(newDamage)));
        }
    }

    public void setNewState(@NotNull Block block) {
        this.newState = block.getBlockState();
    }

    @NotNull
    public DamageCause getCause() {
        return this.cause;
    }

    public @Nullable Player getPlayer() {
        return this.player;
    }

    public enum DamageCause {


    }

    private static Block adjustBlock(Block block, int damage) {
        Block adjusted = Preconditions.checkNotNull(block, "block").clone();
        adjusted.setDataStorage(damage);
        return adjusted;
    }
}
