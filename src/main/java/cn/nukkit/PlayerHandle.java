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
 */

@SuppressWarnings("ClassCanBeRecord")
public final class PlayerHandle {
    public final @NotNull Player player;
    /**
     * @deprecated 
     */
    

    public PlayerHandle(@NotNull Player player) {
        this.player = player;
    }
    /**
     * @deprecated 
     */
    

    public void forceSendEmptyChunks() {
        player.forceSendEmptyChunks();
    }
    /**
     * @deprecated 
     */
    

    public void removeWindow(Inventory inventory) {
        player.removeWindow(inventory);
    }
    /**
     * @deprecated 
     */
    

    public void onBlock(Entity entity, EntityDamageEvent e, boolean animate) {
        player.onBlock(entity, e, animate);
    }
    /**
     * @deprecated 
     */
    

    public long getBreakingBlockTime() {
        return player.breakingBlockTime;
    }
    /**
     * @deprecated 
     */
    

    public void setBreakingBlockTime(long breakingBlockTime) {
        player.breakingBlockTime = breakingBlockTime;
    }
    /**
     * @deprecated 
     */
    

    public double getBlockBreakProgress() {
        return player.blockBreakProgress;
    }
    /**
     * @deprecated 
     */
    

    public void setBlockBreakProgress(double blockBreakProgress) {
        player.blockBreakProgress = blockBreakProgress;
    }

    public Map<UUID, Player> getHiddenPlayers() {
        return player.hiddenPlayers;
    }
    /**
     * @deprecated 
     */
    

    public int getChunksPerTick() {
        return player.chunksPerTick;
    }
    /**
     * @deprecated 
     */
    

    public int getSpawnThreshold() {
        return player.spawnThreshold;
    }
    /**
     * @deprecated 
     */
    

    public int getMessageCounter() {
        return player.messageLimitCounter;
    }
    /**
     * @deprecated 
     */
    

    public void setMessageCounter(int messageCounter) {
        player.messageLimitCounter = messageCounter;
    }
    /**
     * @deprecated 
     */
    

    public void setConnected(boolean connected) {
        player.connected.set(connected);
    }
    /**
     * @deprecated 
     */
    

    public void setSocketAddress(InetSocketAddress socketAddress) {
        player.socketAddress = socketAddress;
    }
    /**
     * @deprecated 
     */
    

    public boolean isRemoveFormat() {
        return player.removeFormat;
    }
    /**
     * @deprecated 
     */
    

    public String getUsername() {
        return player.getName();
    }
    /**
     * @deprecated 
     */
    

    public String getDisplayName() {
        return player.displayName;
    }
    /**
     * @deprecated 
     */
    

    public void setDisplayName(String displayName) {
        player.displayName = displayName;
    }

    public Vector3 getSleeping() {
        return player.sleeping;
    }
    /**
     * @deprecated 
     */
    

    public void setSleeping(Vector3 sleeping) {
        player.sleeping = sleeping;
    }
    /**
     * @deprecated 
     */
    

    public int getChunkLoadCount() {
        return player.chunkLoadCount;
    }
    /**
     * @deprecated 
     */
    

    public void setChunkLoadCount(int chunkLoadCount) {
        player.chunkLoadCount = chunkLoadCount;
    }
    /**
     * @deprecated 
     */
    

    public int getNextChunkOrderRun() {
        return player.nextChunkOrderRun;
    }
    /**
     * @deprecated 
     */
    

    public void setNextChunkOrderRun(int nextChunkOrderRun) {
        player.nextChunkOrderRun = nextChunkOrderRun;
    }

    public Vector3 getNewPosition() {
        return player.newPosition;
    }
    /**
     * @deprecated 
     */
    

    public void setNewPosition(Vector3 newPosition) {
        player.newPosition = newPosition;
    }
    /**
     * @deprecated 
     */
    

    public int getChunkRadius() {
        return player.chunkRadius;
    }
    /**
     * @deprecated 
     */
    

    public void setChunkRadius(int chunkRadius) {
        player.chunkRadius = chunkRadius;
    }

    public Position getSpawnPosition() {
        return player.spawnPoint;
    }
    /**
     * @deprecated 
     */
    

    public void setSpawnPosition(Position spawnPosition) {
        player.spawnPoint = spawnPosition;
    }
    /**
     * @deprecated 
     */
    

    public void setInAirTicks(int inAirTicks) {
        player.inAirTicks = inAirTicks;
    }
    /**
     * @deprecated 
     */
    

    public int getStartAirTicks() {
        return player.startAirTicks;
    }
    /**
     * @deprecated 
     */
    

    public void setStartAirTicks(int startAirTicks) {
        player.startAirTicks = startAirTicks;
    }
    /**
     * @deprecated 
     */
    

    public boolean isCheckMovement() {
        return player.checkMovement;
    }
    /**
     * @deprecated 
     */
    

    public void setFoodData(PlayerFood foodData) {
        player.foodData = foodData;
    }
    /**
     * @deprecated 
     */
    

    public int getFormWindowCount() {
        return player.formWindowCount;
    }
    /**
     * @deprecated 
     */
    

    public void setFormWindowCount(int formWindowCount) {
        player.formWindowCount = formWindowCount;
    }

    public Map<Integer, FormWindow> getFormWindows() {
        return player.formWindows;
    }

    public BiMap<Inventory, Integer> getWindows() {
        return player.windows;
    }

    public BiMap<Integer, Inventory> getWindowIndex() {
        return player.windowIndex;
    }
    /**
     * @deprecated 
     */
    

    public int getClosingWindowId() {
        return player.closingWindowId;
    }
    /**
     * @deprecated 
     */
    

    public void setClosingWindowId(int closingWindowId) {
        player.closingWindowId = closingWindowId;
    }
    /**
     * @deprecated 
     */
    

    public void setFormWindows(Map<Integer, FormWindow> formWindows) {
        player.formWindows = formWindows;
    }

    public Map<Integer, FormWindow> getServerSettings() {
        return player.serverSettings;
    }
    /**
     * @deprecated 
     */
    

    public void setServerSettings(Map<Integer, FormWindow> serverSettings) {
        player.serverSettings = serverSettings;
    }

    public Cache<String, FormWindowDialog> getDialogWindows() {
        return player.dialogWindows;
    }
    /**
     * @deprecated 
     */
    

    public void setDialogWindows(Cache<String, FormWindowDialog> dialogWindows) {
        player.dialogWindows = dialogWindows;
    }
    /**
     * @deprecated 
     */
    

    public void setDummyBossBars(Map<Long, DummyBossBar> dummyBossBars) {
        player.dummyBossBars = dummyBossBars;
    }
    /**
     * @deprecated 
     */
    
    public double getLastRightClickTime() {
        return player.lastRightClickTime;
    }
    /**
     * @deprecated 
     */
    

    public void setLastRightClickTime(double lastRightClickTime) {
        player.lastRightClickTime = lastRightClickTime;
    }

    public Vector3 getLastRightClickPos() {
        return player.lastRightClickPos;
    }
    /**
     * @deprecated 
     */
    

    public void setLastRightClickPos(Vector3 lastRightClickPos) {
        player.lastRightClickPos = lastRightClickPos;
    }
    /**
     * @deprecated 
     */
    

    public void setLastInAirTick(int lastInAirTick) {
        player.lastInAirTick = lastInAirTick;
    }
    /**
     * @deprecated 
     */
    

    public int getLastPlayerdLevelUpSoundTime() {
        return player.lastPlayerdLevelUpSoundTime;
    }
    /**
     * @deprecated 
     */
    

    public void setLastPlayerdLevelUpSoundTime(int lastPlayerdLevelUpSoundTime) {
        player.lastPlayerdLevelUpSoundTime = lastPlayerdLevelUpSoundTime;
    }
    /**
     * @deprecated 
     */
    

    public void setLastAttackEntity(Entity lastAttackEntity) {
        player.lastAttackEntity = lastAttackEntity;
    }

    public List<PlayerFogPacket.Fog> getFogStack() {
        return player.fogStack;
    }
    /**
     * @deprecated 
     */
    

    public void setFogStack(List<PlayerFogPacket.Fog> fogStack) {
        player.fogStack = fogStack;
    }
    /**
     * @deprecated 
     */
    

    public void setLastBeAttackEntity(Entity lastBeAttackEntity) {
        player.lastBeAttackEntity = lastBeAttackEntity;
    }

    public LoginChainData getLoginChainData() {
        return player.loginChainData;
    }

    public AsyncTask getPreLoginEventTask() {
        return player.preLoginEventTask;
    }
    /**
     * @deprecated 
     */
    

    public void setPreLoginEventTask(AsyncTask preLoginEventTask) {
        player.preLoginEventTask = preLoginEventTask;
    }
    /**
     * @deprecated 
     */
    

    public void onPlayerLocallyInitialized() {
        player.onPlayerLocallyInitialized();
    }
    /**
     * @deprecated 
     */
    

    public boolean isValidRespawnBlock(Block block) {
        return player.isValidRespawnBlock(block);
    }
    /**
     * @deprecated 
     */
    

    public void respawn() {
        player.respawn();
    }
    /**
     * @deprecated 
     */
    

    public void checkChunks() {
        player.checkChunks();
    }
    /**
     * @deprecated 
     */
    

    public void processLogin() {
        player.processLogin();
    }
    /**
     * @deprecated 
     */
    

    public void initEntity() {
        player.initEntity();
    }
    /**
     * @deprecated 
     */
    

    public void doFirstSpawn() {
        player.doFirstSpawn();
    }
    /**
     * @deprecated 
     */
    

    public void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        player.checkGroundState(movX, movY, movZ, dx, dy, dz);
    }
    /**
     * @deprecated 
     */
    

    public void checkBlockCollision() {
        player.checkBlockCollision();
    }
    /**
     * @deprecated 
     */
    

    public void checkNearEntities() {
        player.checkNearEntities();
    }
    /**
     * @deprecated 
     */
    

    public void handleMovement(Location clientPos) {
        player.handleMovement(clientPos);
    }
    /**
     * @deprecated 
     */
    

    public void offerMovementTask(Location newPosition) {
        player.offerMovementTask(newPosition);
    }
    /**
     * @deprecated 
     */
    

    public void handleLogicInMove(boolean invalidMotion, double distance) {
        player.handleLogicInMove(invalidMotion, distance);
    }
    /**
     * @deprecated 
     */
    

    public void resetClientMovement() {
        player.resetClientMovement();
    }
    /**
     * @deprecated 
     */
    

    public void revertClientMotion(Location originalPos) {
        player.revertClientMotion(originalPos);
    }
    /**
     * @deprecated 
     */
    

    public float getBaseOffset() {
        return player.getBaseOffset();
    }

    public PlayerBlockActionData getLastBlockAction() {
        return player.lastBlockAction;
    }
    /**
     * @deprecated 
     */
    

    public void setLastBlockAction(PlayerBlockActionData actionData) {
        player.lastBlockAction = actionData;
    }
    /**
     * @deprecated 
     */
    

    public void onBlockBreakContinue(Vector3 pos, BlockFace face) {
        player.onBlockBreakContinue(pos, face);
    }
    /**
     * @deprecated 
     */
    

    public void onBlockBreakStart(Vector3 pos, BlockFace face) {
        player.onBlockBreakStart(pos, face);
    }
    /**
     * @deprecated 
     */
    

    public void onBlockBreakAbort(Vector3 pos) {
        player.onBlockBreakAbort(pos);
    }
    /**
     * @deprecated 
     */
    

    public void onBlockBreakComplete(BlockVector3 blockPos, BlockFace face) {
        player.onBlockBreakComplete(blockPos, face);
    }
    /**
     * @deprecated 
     */
    

    public boolean getShowingCredits() {
        return player.showingCredits;
    }
    /**
     * @deprecated 
     */
    

    public static int getNoShieldDelay() {
        return Player.NO_SHIELD_DELAY;
    }
    /**
     * @deprecated 
     */
    

    public boolean getInventoryOpen() {
        return player.inventoryOpen;
    }
    /**
     * @deprecated 
     */
    

    public void setInventoryOpen(boolean inventoryOpen) {
        player.inventoryOpen = inventoryOpen;
    }
    /**
     * @deprecated 
     */
    

    public void addDefaultWindows() {
        player.addDefaultWindows();
    }
}