package cn.nukkit;

import cn.nukkit.block.Block;
import cn.nukkit.dialog.window.FormWindowDialog;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.PlayerFogPacket;
import cn.nukkit.network.protocol.types.PlayerBlockActionData;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.LoginChainData;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A PlayerHandle is used to access a player's protected data.
 * This class provides methods to interact with and modify the player's state.
 */

@SuppressWarnings("ClassCanBeRecord")
public final class PlayerHandle {
    public final @NotNull Player player;

    /**
     * Constructs a PlayerHandle for the specified player.
     *
     * @param player The player associated with this handle.
     */
    public PlayerHandle(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Forces the player to send empty chunks to the client.
     */
    public void forceSendEmptyChunks() {
        player.forceSendEmptyChunks();
    }

    /**
     * Removes the specified inventory window from the player's view.
     *
     * @param inventory The inventory to be removed.
     */
    public void removeWindow(Inventory inventory) {
        player.removeWindow(inventory);
    }

    /**
     * Handles the player blocking an entity's attack.
     *
     * @param entity  The entity being blocked.
     * @param e       The damage event.
     * @param animate Whether to animate the block action.
     */
    public void onBlock(Entity entity, EntityDamageEvent e, boolean animate) {
        player.onBlock(entity, e, animate);
    }

    /**
     * Retrieves the time the player has been breaking a block.
     *
     * @return The breaking block time in milliseconds.
     */
    public long getBreakingBlockTime() {
        return player.breakingBlockTime;
    }

    /**
     * Sets the time the player has been breaking a block.
     *
     * @param breakingBlockTime The breaking block time in milliseconds.
     */
    public void setBreakingBlockTime(long breakingBlockTime) {
        player.breakingBlockTime = breakingBlockTime;
    }

    /**
     * Retrieves the player's progress in breaking a block.
     *
     * @return The block break progress as a percentage.
     */
    public double getBlockBreakProgress() {
        return player.blockBreakProgress;
    }

    /**
     * Sets the player's progress in breaking a block.
     *
     * @param blockBreakProgress The block break progress as a percentage.
     */
    public void setBlockBreakProgress(double blockBreakProgress) {
        player.blockBreakProgress = blockBreakProgress;
    }

    /**
     * Retrieves the players hidden from this player.
     *
     * @return A map of hidden players.
     */
    public Map<UUID, Player> getHiddenPlayers() {
        return player.hiddenPlayers;
    }

    /**
     * Retrieves the number of chunks the player can load per tick.
     *
     * @return The number of chunks per tick.
     */
    public int getChunksPerTick() {
        return player.chunksPerTick;
    }

    /**
     * Retrieves the player's spawn threshold.
     *
     * @return The spawn threshold.
     */
    public int getSpawnThreshold() {
        return player.spawnThreshold;
    }

    /**
     * Retrieves the player's message counter.
     *
     * @return The message counter.
     */
    public int getMessageCounter() {
        return player.messageLimitCounter;
    }

    /**
     * Sets the player's message counter.
     *
     * @param messageCounter The new message counter value.
     */
    public void setMessageCounter(int messageCounter) {
        player.messageLimitCounter = messageCounter;
    }

    /**
     * Sets the player's connection status.
     *
     * @param connected The new connection status.
     */
    public void setConnected(boolean connected) {
        player.connected.set(connected);
    }

    /**
     * Sets the player's socket address.
     *
     * @param socketAddress The new socket address.
     */
    public void setSocketAddress(InetSocketAddress socketAddress) {
        player.socketAddress = socketAddress;
    }

    /**
     * Checks if the player has the remove format option enabled.
     *
     * @return True if remove format is enabled, false otherwise.
     */
    public boolean isRemoveFormat() {
        return player.removeFormat;
    }

    /**
     * Retrieves the player's username.
     *
     * @return The player's username.
     */
    public String getUsername() {
        return player.getName();
    }

    /**
     * Retrieves the player's display name.
     *
     * @return The player's display name.
     */
    public String getDisplayName() {
        return player.displayName;
    }

    /**
     * Sets the player's display name.
     *
     * @param displayName The new display name.
     */
    public void setDisplayName(String displayName) {
        player.displayName = displayName;
    }

    /**
     * Retrieves the player's sleeping position.
     *
     * @return The player's sleeping position.
     */
    public Vector3 getSleeping() {
        return player.sleeping;
    }

    /**
     * Sets the player's sleeping position.
     *
     * @param sleeping The new sleeping position.
     */
    public void setSleeping(Vector3 sleeping) {
        player.sleeping = sleeping;
    }

    /**
     * Retrieves the number of chunks the player can load per tick.
     *
     * @return The number of chunks the player can load per tick.
     */
    public int getChunkLoadCount() {
        return player.chunkLoadCount;
    }

    /**
     * Sets the number of chunks the player can load per tick.
     *
     * @param chunkLoadCount The new chunk load count.
     */
    public void setChunkLoadCount(int chunkLoadCount) {
        player.chunkLoadCount = chunkLoadCount;
    }

    /**
     * Retrieves the next chunk order run.
     *
     * @return The next chunk order run.
     */
    public int getNextChunkOrderRun() {
        return player.nextChunkOrderRun;
    }

    /**
     * Sets the next chunk order run.
     *
     * @param nextChunkOrderRun The new next chunk order run.
     */
    public void setNextChunkOrderRun(int nextChunkOrderRun) {
        player.nextChunkOrderRun = nextChunkOrderRun;
    }

    /**
     * Retrieves the player's new position.
     *
     * @return The new position.
     */
    public Vector3 getNewPosition() {
        return player.newPosition;
    }

    /**
     * Sets the player's new position.
     *
     * @param newPosition The new position.
     */
    public void setNewPosition(Vector3 newPosition) {
        player.newPosition = newPosition;
    }

    /**
     * Retrieves the player's chunk radius.
     *
     * @return The chunk radius.
     */
    public int getChunkRadius() {
        return player.chunkRadius;
    }

    /**
     * Sets the player's chunk radius.
     *
     * @param chunkRadius The new chunk radius.
     */
    public void setChunkRadius(int chunkRadius) {
        player.chunkRadius = chunkRadius;
    }

    /**
     * Retrieves the player's spawn position.
     *
     * @return The spawn position.
     */
    public Position getSpawnPosition() {
        return player.spawnPoint;
    }

    /**
     * Sets the player's spawn position.
     *
     * @param spawnPosition The new spawn position.
     */
    public void setSpawnPosition(Position spawnPosition) {
        player.spawnPoint = spawnPosition;
    }

    /**
     * Sets the number of ticks the player has been in the air.
     *
     * @param inAirTicks The new in-air ticks count.
     */
    public void setInAirTicks(int inAirTicks) {
        player.inAirTicks = inAirTicks;
    }

    /**
     * Retrieves the player's start air ticks.
     *
     * @return The start air ticks.
     */
    public int getStartAirTicks() {
        return player.startAirTicks;
    }

    /**
     * Sets the player's start air ticks.
     *
     * @param startAirTicks The new start air ticks.
     */
    public void setStartAirTicks(int startAirTicks) {
        player.startAirTicks = startAirTicks;
    }

    /**
     * Checks if the player movement is being checked.
     *
     * @return True if movement is being checked, false otherwise.
     */
    public boolean isCheckMovement() {
        return player.checkMovement;
    }

    /**
     * Sets the player's food data.
     *
     * @param foodData The new food data.
     */
    public void setFoodData(PlayerFood foodData) {
        player.foodData = foodData;
    }

    /**
     * Retrieves the count of form windows the player has.
     *
     * @return The form window count.
     */
    public int getFormWindowCount() {
        return player.formWindowCount;
    }

    /**
     * Sets the count of form windows the player has.
     *
     * @param formWindowCount The new form window count.
     */
    public void setFormWindowCount(int formWindowCount) {
        player.formWindowCount = formWindowCount;
    }

    /**
     * Retrieves the player's form windows.
     *
     * @return A map of form windows.
     */
    public Map<Integer, FormWindow> getFormWindows() {
        return player.formWindows;
    }

    /**
     * Retrieves the player's inventory windows.
     *
     * @return A BiMap of inventory windows.
     */
    public BiMap<Inventory, Integer> getWindows() {
        return player.windows;
    }

    /**
     * Retrieves the player's window index.
     *
     * @return A BiMap of window indices.
     */
    public BiMap<Integer, Inventory> getWindowIndex() {
        return player.windowIndex;
    }

    /**
     * Retrieves the ID of the window the player is closing.
     *
     * @return The closing window ID.
     */
    public int getClosingWindowId() {
        return player.closingWindowId;
    }

    /**
     * Sets the ID of the window the player is closing.
     *
     * @param closingWindowId The new closing window ID.
     */
    public void setClosingWindowId(int closingWindowId) {
        player.closingWindowId = closingWindowId;
    }

    /**
     * Sets the player's form windows.
     *
     * @param formWindows The new form windows.
     */
    public void setFormWindows(Map<Integer, FormWindow> formWindows) {
        player.formWindows = formWindows;
    }

    /**
     * Retrieves the player's server settings.
     *
     * @return A map of server settings.
     */
    public Map<Integer, FormWindow> getServerSettings() {
        return player.serverSettings;
    }

    /**
     * Sets the player's server settings.
     *
     * @param serverSettings The new server settings.
     */
    public void setServerSettings(Map<Integer, FormWindow> serverSettings) {
        player.serverSettings = serverSettings;
    }

    /**
     * Retrieves the player's dialog windows.
     *
     * @return A cache of dialog windows.
     */
    public Cache<String, FormWindowDialog> getDialogWindows() {
        return player.dialogWindows;
    }

    /**
     * Sets the player's dialog windows.
     *
     * @param dialogWindows The new dialog windows.
     */
    public void setDialogWindows(Cache<String, FormWindowDialog> dialogWindows) {
        player.dialogWindows = dialogWindows;
    }

    /**
     * Sets the player's dummy boss bars.
     *
     * @param dummyBossBars The new dummy boss bars.
     */
    public void setDummyBossBars(Map<Long, DummyBossBar> dummyBossBars) {
        player.dummyBossBars = dummyBossBars;
    }

    /**
     * Retrieves the time of the player's last right click.
     *
     * @return The last right click time.
     */
    public double getLastRightClickTime() {
        return player.lastRightClickTime;
    }

    /**
     * Sets the time of the player's last right click.
     *
     * @param lastRightClickTime The new last right click time.
     */
    public void setLastRightClickTime(double lastRightClickTime) {
        player.lastRightClickTime = lastRightClickTime;
    }

    /**
     * Retrieves the position of the player's last right click.
     *
     * @return The last right click position.
     */
    public Vector3 getLastRightClickPos() {
        return player.lastRightClickPos;
    }

    /**
     * Sets the position of the player's last right click.
     *
     * @param lastRightClickPos The new last right click position.
     */
    public void setLastRightClickPos(Vector3 lastRightClickPos) {
        player.lastRightClickPos = lastRightClickPos;
    }

    /**
     * Sets the player's last in-air tick.
     *
     * @param lastInAirTick The new last in-air tick.
     */
    public void setLastInAirTick(int lastInAirTick) {
        player.lastInAirTick = lastInAirTick;
    }

    /**
     * Retrieves the time of the player's last level-up sound.
     *
     * @return The last level-up sound time.
     */
    public int getLastPlayerdLevelUpSoundTime() {
        return player.lastPlayerdLevelUpSoundTime;
    }

    /**
     * Sets the time of the player's last level-up sound.
     *
     * @param lastPlayerdLevelUpSoundTime The new last level-up sound time.
     */
    public void setLastPlayerdLevelUpSoundTime(int lastPlayerdLevelUpSoundTime) {
        player.lastPlayerdLevelUpSoundTime = lastPlayerdLevelUpSoundTime;
    }

    /**
     * Sets the entity the player last attacked.
     *
     * @param lastAttackEntity The new last attacked entity.
     */
    public void setLastAttackEntity(Entity lastAttackEntity) {
        player.lastAttackEntity = lastAttackEntity;
    }

    /**
     * Retrieves the player's fog stack.
     *
     * @return A list of fog stack.
     */
    public List<PlayerFogPacket.Fog> getFogStack() {
        return player.fogStack;
    }

    /**
     * Sets the player's fog stack.
     *
     * @param fogStack The new fog stack.
     */
    public void setFogStack(List<PlayerFogPacket.Fog> fogStack) {
        player.fogStack = fogStack;
    }

    /**
     * Sets the entity that last attacked the player.
     *
     * @param lastBeAttackEntity The new last attacked entity.
     */
    public void setLastBeAttackEntity(Entity lastBeAttackEntity) {
        player.lastBeAttackEntity = lastBeAttackEntity;
    }

    /**
     * Retrieves the player's login chain data.
     *
     * @return The login chain data.
     */
    public LoginChainData getLoginChainData() {
        return player.loginChainData;
    }

    /**
     * Retrieves the player's pre-login event task.
     *
     * @return The pre-login event task.
     */
    public AsyncTask getPreLoginEventTask() {
        return player.preLoginEventTask;
    }

    /**
     * Sets the player's pre-login event task.
     *
     * @param preLoginEventTask The new pre-login event task.
     */
    public void setPreLoginEventTask(AsyncTask preLoginEventTask) {
        player.preLoginEventTask = preLoginEventTask;
    }

    /**
     * Initializes the player locally.
     */
    public void onPlayerLocallyInitialized() {
        player.onPlayerLocallyInitialized();
    }

    /**
     * Checks if the specified block is a valid respawn block for the player.
     *
     * @param block The block to check.
     * @return True if the block is a valid respawn block, false otherwise.
     */
    public boolean isValidRespawnBlock(Block block) {
        return player.isValidRespawnBlock(block);
    }

    /**
     * Respawns the player.
     */
    public void respawn() {
        player.respawn();
    }

    /**
     * Checks the player's chunks.
     */
    public void checkChunks() {
        player.checkChunks();
    }

    /**
     * Processes the player's login.
     */
    public void processLogin() {
        player.processLogin();
    }

    /**
     * Initializes the player's entity.
     */
    public void initEntity() {
        player.initEntity();
    }

    /**
     * Performs the player's first spawn.
     */
    public void doFirstSpawn() {
        player.doFirstSpawn();
    }

    /**
     * Checks the player's ground state based on movement parameters.
     *
     * @param movX The movement in the X direction.
     * @param movY The movement in the Y direction.
     * @param movZ The movement in the Z direction.
     * @param dx   The delta X.
     * @param dy   The delta Y.
     * @param dz   The delta Z.
     */
    public void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        player.checkGroundState(movX, movY, movZ, dx, dy, dz);
    }

    /**
     * Checks the player's block collision.
     */
    public void checkBlockCollision() {
        player.checkBlockCollision();
    }

    /**
     * Checks the entities near the player.
     */
    public void checkNearEntities() {
        player.checkNearEntities();
    }

    /**
     * Handles the player's movement.
     *
     * @param clientPos The client's position.
     */
    public void handleMovement(Location clientPos) {
        player.handleMovement(clientPos);
    }

    /**
     * Offers a movement task for the player.
     *
     * @param newPosition The new position.
     */
    public void offerMovementTask(Location newPosition) {
        player.offerMovementTask(newPosition);
    }

    /**
     * Handles the player's logic during movement.
     *
     * @param invalidMotion Whether the motion is invalid.
     * @param distance      The distance moved.
     */
    public void handleLogicInMove(boolean invalidMotion, double distance) {
        player.handleLogicInMove(invalidMotion, distance);
    }

    /**
     * Resets the player's client movement.
     */
    public void resetClientMovement() {
        player.resetClientMovement();
    }

    /**
     * Reverts the player's client motion to the original position.
     *
     * @param originalPos The original position.
     */
    public void revertClientMotion(Location originalPos) {
        player.revertClientMotion(originalPos);
    }

    /**
     * Retrieves the player's base offset.
     *
     * @return The base offset.
     */
    public float getBaseOffset() {
        return player.getBaseOffset();
    }

    /**
     * Retrieves the last block action performed by the player.
     *
     * @return The last block action data.
     */
    public PlayerBlockActionData getLastBlockAction() {
        return player.lastBlockAction;
    }

    /**
     * Sets the last block action performed by the player.
     *
     * @param actionData The new block action data.
     */
    public void setLastBlockAction(PlayerBlockActionData actionData) {
        player.lastBlockAction = actionData;
    }

    /**
     * Continues the block breaking process at the specified position and face.
     *
     * @param pos  The position of the block.
     * @param face The face of the block.
     */
    public void onBlockBreakContinue(Vector3 pos, BlockFace face) {
        player.onBlockBreakContinue(pos, face);
    }

    /**
     * Starts the block breaking process at the specified position and face.
     *
     * @param pos  The position of the block.
     * @param face The face of the block.
     */
    public void onBlockBreakStart(Vector3 pos, BlockFace face) {
        player.onBlockBreakStart(pos, face);
    }

    /**
     * Aborts the block breaking process at the specified position.
     *
     * @param pos The position of the block.
     */
    public void onBlockBreakAbort(Vector3 pos) {
        player.onBlockBreakAbort(pos);
    }

    /**
     * Completes the block breaking process at the specified position and face.
     *
     * @param blockPos The position of the block.
     * @param face     The face of the block.
     */
    public void onBlockBreakComplete(BlockVector3 blockPos, BlockFace face) {
        player.onBlockBreakComplete(blockPos, face);
    }

    /**
     * Checks if the player is currently showing credits.
     *
     * @return True if the player is showing credits, false otherwise.
     */
    public boolean getShowingCredits() {
        return player.showingCredits;
    }

    /**
     * Retrieves the no shield delay constant.
     *
     * @return The no shield delay.
     */
    public static int getNoShieldDelay() {
        return Player.NO_SHIELD_DELAY;
    }

    /**
     * Checks if the player's inventory is open.
     *
     * @return True if the inventory is open, false otherwise.
     */
    public boolean getInventoryOpen() {
        return player.inventoryOpen;
    }

    /**
     * Sets the player's inventory open state.
     *
     * @param inventoryOpen The new inventory open state.
     */
    public void setInventoryOpen(boolean inventoryOpen) {
        player.inventoryOpen = inventoryOpen;
    }

    /**
     * Adds the default windows to the player's interface.
     */
    public void addDefaultWindows() {
        player.addDefaultWindows();
    }
}