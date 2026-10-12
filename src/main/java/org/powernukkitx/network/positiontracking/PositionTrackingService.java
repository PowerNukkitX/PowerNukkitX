package org.powernukkitx.network.positiontracking;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.packet.PositionTrackingDBServerBroadcastPacket;
import org.iq80.leveldb.DB;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemLodestoneCompass;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;

/**
 * Provides server-global position tracking backed by the LevelDB records stored in {@code server_data}.
 *
 * @author joserobjr
 * @author Curse
 */
@ParametersAreNonnullByDefault
@Slf4j
public class PositionTrackingService implements Closeable {
    private final PositionTrackingStorage storage;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final Map<Player, IntSet> tracking = new MapMaker().weakKeys().makeMap();

    /**
     * Creates a position-tracking service backed by the server-global LevelDB.
     *
     * @param database server-global LevelDB
     */
    public PositionTrackingService(DB database) {
        this.storage = new PositionTrackingStorage(database);
    }

    private boolean hasTrackingDevice(Player player, @Nullable Inventory inventory, int trackingHandler) throws IOException {
        if (inventory == null) {
            return false;
        }
        int size = inventory.getSize();
        for (int i = 0; i < size; i++) {
            if (isTrackingDevice(player, inventory.getItem(i), trackingHandler)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTrackingDevice(Player player, @Nullable Item item, int trackingHandler) throws IOException {
        if (!(item != null && item.getId().equals(ItemID.LODESTONE_COMPASS) && item instanceof ItemLodestoneCompass compassLodestone)) {
            return false;
        }
        if (compassLodestone.getTrackingHandle() != trackingHandler) {
            return false;
        }

        PositionTracking position = getPosition(trackingHandler);
        return position != null && position.getLevelName().equals(player.getLevelName());
    }

    public boolean hasTrackingDevice(Player player, int trackingHandler) throws IOException {
        for (Inventory inventory : inventories(player)) {
            if (hasTrackingDevice(player, inventory, trackingHandler)) {
                return true;
            }
        }
        return false;
    }

    private void sendTrackingUpdate(Player player, int trackingHandler, PositionTracking pos) {
        if (player.getLevelName().equals(pos.getLevelName())) {
            PositionTrackingDBServerBroadcastPacket packet = new PositionTrackingDBServerBroadcastPacket();
            packet.setAction(PositionTrackingDBServerBroadcastPacket.Action.UPDATE);
            packet.setTrackingId(trackingHandler);
            packet.setPositionTrackingData(
                    NbtMap.builder()
                            .putByte("version", (byte) 1)
                            .putString("id", String.format("0x%08x", trackingHandler))
                            .putList("pos", NbtType.INT, Arrays.asList(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()))
                            .putByte("status", (byte) 0)
                            .putInt("dim", player.getLevel().getDimension())
                            .build()
            );
            player.sendPacket(packet);
        } else {
            sendTrackingDestroy(player, trackingHandler);
        }
    }

    private void sendTrackingDestroy(Player player, int trackingHandler) {
        player.sendPacket(destroyPacket(trackingHandler));
    }

    /**
     * Starts tracking a stored position for a player.
     *
     * @param player player starting tracking
     * @param trackingHandler tracking handle
     * @param validate whether a matching tracking device must be present
     * @return tracked position, or {@code null} when tracking cannot be started
     * @throws IOException if the tracking data cannot be read
     */
    public @Nullable synchronized PositionTracking startTracking(Player player, int trackingHandler, boolean validate) throws IOException {
        Preconditions.checkArgument(trackingHandler >= 0, "Tracking handler must be positive");
        if (trackingHandler == 0) {
            return null;
        }
        if (isTracking(player, trackingHandler, validate)) {
            PositionTracking position = getPosition(trackingHandler);
            if (position != null) {
                sendTrackingUpdate(player, trackingHandler, position);
                return position;
            }
            stopTracking(player, trackingHandler);
            return null;
        }

        if (validate && !hasTrackingDevice(player, trackingHandler)) {
            return null;
        }

        PositionTracking position = getPosition(trackingHandler);
        if (position == null) {
            return null;
        }

        tracking.computeIfAbsent(player, p -> new IntOpenHashSet(3)).add(trackingHandler);
        return position;
    }

    private PositionTrackingDBServerBroadcastPacket destroyPacket(int trackingHandler) {
        PositionTrackingDBServerBroadcastPacket packet = new PositionTrackingDBServerBroadcastPacket();
        packet.setAction(PositionTrackingDBServerBroadcastPacket.Action.DESTROY);
        packet.setTrackingId(trackingHandler);
        packet.setPositionTrackingData(
                NbtMap.builder()
                        .putByte("version", (byte) 1)
                        .putString("id", String.format("0x%08x", trackingHandler))
                        .putList("pos", NbtType.INT, Arrays.asList(0, 0, 0))
                        .putByte("status", (byte) 2)
                        .putInt("dim", 0)
                        .build()
        );
        return packet;
    }

    public synchronized boolean stopTracking(Player player) {
        IntSet toRemove = tracking.remove(player);
        if (toRemove != null && player.isOnline()) {
            for (int trackingHandler : toRemove) {
                player.sendPacket(destroyPacket(trackingHandler));
            }
        }
        return toRemove != null;
    }

    public synchronized boolean stopTracking(Player player, int trackingHandler) {
        IntSet handlers = tracking.get(player);
        if (handlers == null || !handlers.remove(trackingHandler)) {
            return false;
        }
        if (handlers.size() == 0) {
            tracking.remove(player);
        }
        player.sendPacket(destroyPacket(trackingHandler));
        return true;
    }

    public synchronized boolean isTracking(Player player, int trackingHandler, boolean validate) throws IOException {
        IntSet handlers = tracking.get(player);
        if (handlers == null || !handlers.contains(trackingHandler)) {
            return false;
        }
        if (validate && !hasTrackingDevice(player, trackingHandler)) {
            stopTracking(player, trackingHandler);
            return false;
        }
        return true;
    }

    public synchronized void forceRecheckAllPlayers() {
        tracking.keySet().removeIf(player -> !player.isOnline());
        Map<Player, IntList> toRemove = new HashMap<>(2);

        for (Map.Entry<Player, IntSet> entry : tracking.entrySet()) {
            Player player = entry.getKey();
            entry.getValue().forEach((IntConsumer) trackingHandler -> {
                try {
                    if (!hasTrackingDevice(player, trackingHandler)) {
                        toRemove.computeIfAbsent(player, p -> new IntArrayList(2)).add(trackingHandler);
                    }
                } catch (IOException e) {
                    log.error("Failed to update the tracking handler {} for player {}", trackingHandler, player.getName(), e);
                }
            });
        }

        toRemove.forEach((player, list) -> list.forEach((IntConsumer) handler -> stopTracking(player, handler)));
        Server.getInstance().getOnlinePlayers().values().forEach(this::detectNeededUpdates);
    }

    private Iterable<Inventory> inventories(Player player) {
        return () -> new Iterator<>() {
            int next = 0;

            @Override
            public boolean hasNext() {
                return next <= 4;
            }

            @Override
            public Inventory next() {
                return switch (next++) {
                    case 0 -> player.getInventory();
                    case 1 -> player.getCursorInventory();
                    case 2 -> player.getOffhandInventory();
                    case 3 -> player.getCraftingGrid();
                    case 4 -> player.getTopWindow().orElse(null);
                    default -> throw new NoSuchElementException();
                };
            }
        };
    }

    private void detectNeededUpdates(Player player) {
        for (Inventory inventory : inventories(player)) {
            if (inventory == null) {
                continue;
            }
            int size = inventory.getSize();
            for (int slot = 0; slot < size; slot++) {
                Item item = inventory.getItem(slot);
                if (item.getId().equals(ItemID.LODESTONE_COMPASS) && item instanceof ItemLodestoneCompass compass) {
                    int trackingHandle = compass.getTrackingHandle();
                    if (trackingHandle == 0) {
                        continue;
                    }
                    try {
                        PositionTracking pos = getPosition(trackingHandle);
                        if (pos != null && pos.getLevelName().equals(player.getLevelName())) {
                            startTracking(player, trackingHandle, false);
                        }
                    } catch (IOException e) {
                        log.error("Failed to get the position of the tracking handler {}", trackingHandle, e);
                    }
                }
            }
        }
    }

    public void forceRecheck(Player player) {
        IntSet handlers = tracking.get(player);
        if (handlers != null) {
            IntList toRemove = new IntArrayList(2);
            handlers.forEach((IntConsumer) trackingHandler -> {
                try {
                    if (!hasTrackingDevice(player, trackingHandler)) {
                        toRemove.add(trackingHandler);
                    }
                } catch (IOException e) {
                    log.error("Failed to update the tracking handler {} for player {}", trackingHandler, player.getName(), e);
                }
            });
            toRemove.forEach((IntConsumer) handler -> stopTracking(player, handler));
        }
        detectNeededUpdates(player);
    }

    /**
     * Reuses an enabled tracking handle for a position or creates a new one.
     */
    public synchronized int addOrReusePosition(NamedPosition position) throws IOException {
        checkClosed();
        return storage.addOrReusePosition(position).orElseThrow(InternalError::new);
    }

    /**
     * Creates a new enabled tracking handle for a position.
     */
    public synchronized int addNewPosition(NamedPosition position) throws IOException {
        return addNewPosition(position, true);
    }

    /**
     * Creates a new tracking handle for a position.
     */
    public synchronized int addNewPosition(NamedPosition position, boolean enabled) throws IOException {
        checkClosed();
        return storage.addNewPosition(position, enabled).orElseThrow(InternalError::new);
    }

    /**
     * Finds one enabled tracking handle for a position.
     */
    public @NotNull OptionalInt findTrackingHandler(NamedPosition position) throws IOException {
        checkClosed();
        return storage.findTrackingHandler(position);
    }

    public synchronized boolean invalidateHandler(int trackingHandler) throws IOException {
        checkClosed();
        if (trackingHandler == 0 || !storage.hasPosition(trackingHandler, false)) {
            return false;
        }
        storage.invalidateHandler(trackingHandler);
        handlerDisabled(trackingHandler);
        return true;
    }

    private void handlerDisabled(int trackingHandler) {
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Player, IntSet> entry : tracking.entrySet()) {
            if (entry.getValue().contains(trackingHandler)) {
                players.add(entry.getKey());
            }
        }
        if (players.size() != 0) {
            Server.broadcastPacket(players, destroyPacket(trackingHandler));
        }
    }

    private void handlerEnabled(int trackingHandler) throws IOException {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            if (hasTrackingDevice(player, trackingHandler) && !isTracking(player, trackingHandler, false)) {
                startTracking(player, trackingHandler, false);
            }
        }
    }

    public @Nullable PositionTracking getPosition(int trackingHandle) throws IOException {
        return getPosition(trackingHandle, true);
    }

    public @Nullable PositionTracking getPosition(int trackingHandle, boolean onlyEnabled) throws IOException {
        checkClosed();
        return trackingHandle == 0 ? null : storage.getPosition(trackingHandle, onlyEnabled);
    }

    public synchronized boolean isEnabled(int trackingHandler) throws IOException {
        checkClosed();
        return trackingHandler != 0 && storage.isEnabled(trackingHandler);
    }

    public synchronized boolean setEnabled(int trackingHandler, boolean enabled) throws IOException {
        checkClosed();
        if (trackingHandler == 0 || !storage.setEnabled(trackingHandler, enabled)) {
            return false;
        }
        if (enabled) {
            handlerEnabled(trackingHandler);
        } else {
            handlerDisabled(trackingHandler);
        }
        return true;
    }

    public synchronized boolean hasPosition(int trackingHandler) throws IOException {
        return hasPosition(trackingHandler, true);
    }

    public synchronized boolean hasPosition(int trackingHandler, boolean onlyEnabled) throws IOException {
        checkClosed();
        return trackingHandler != 0 && storage.hasPosition(trackingHandler, onlyEnabled);
    }

    public @NotNull IntList findTrackingHandlers(NamedPosition pos) throws IOException {
        return findTrackingHandlers(pos, true);
    }

    public @NotNull IntList findTrackingHandlers(NamedPosition pos, boolean onlyEnabled) throws IOException {
        return findTrackingHandlers(pos, onlyEnabled, Integer.MAX_VALUE);
    }

    public synchronized @NotNull IntList findTrackingHandlers(NamedPosition pos, boolean onlyEnabled, int limit) throws IOException {
        checkClosed();
        return storage.findTrackingHandlers(pos, onlyEnabled, limit);
    }

    @Override
    public synchronized void close() {
        closed.set(true);
        tracking.clear();
    }

    private void checkClosed() throws IOException {
        if (closed.get()) {
            throw new IOException("The service is closed");
        }
    }
}
