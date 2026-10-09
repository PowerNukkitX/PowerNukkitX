package org.powernukkitx.blockentity;

import org.powernukkitx.Server;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.network.positiontracking.PositionTracking;
import org.powernukkitx.network.positiontracking.PositionTrackingService;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.OptionalInt;

/**
 * @author joserobjr
 */
@Slf4j
public class BlockEntityLodestone extends BlockEntitySpawnable {
    private static final String PNX_EXTRA_TRACKING_HANDLES = "lodestoneTrackingHandles";

    private static String getTrackingKey(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    private static int getPnxTrackingHandle(CompoundTag extraData, int x, int y, int z) {
        if (!extraData.containsCompound(PNX_EXTRA_TRACKING_HANDLES)) return 0;

        CompoundTag handles = extraData.getCompound(PNX_EXTRA_TRACKING_HANDLES);
        String key = getTrackingKey(x, y, z);
        return handles.containsNumber(key) ? handles.getInt(key) : 0;
    }

    /**
     * Updates the PNX lodestone tracking-handle mapping for a block position.
     *
     * @param extraData PNX extra data
     * @param x block X
     * @param y block Y
     * @param z block Z
     * @param trackingHandle tracking handle, or a non-positive value to remove it
     * @return whether the mapping changed
     */
    public static boolean setPnxTrackingHandle(CompoundTag extraData, int x, int y, int z, int trackingHandle) {
        String key = getTrackingKey(x, y, z);

        if (trackingHandle <= 0) {
            if (!extraData.containsCompound(PNX_EXTRA_TRACKING_HANDLES)) return false;

            CompoundTag handles = extraData.getCompound(PNX_EXTRA_TRACKING_HANDLES);
            if (!handles.contains(key)) return false;

            handles.remove(key);
            if (handles.isEmpty()) {
                extraData.remove(PNX_EXTRA_TRACKING_HANDLES);
            } else {
                extraData.putCompound(PNX_EXTRA_TRACKING_HANDLES, handles);
            }
            return true;
        }

        CompoundTag handles = extraData.containsCompound(PNX_EXTRA_TRACKING_HANDLES)
                ? extraData.getCompound(PNX_EXTRA_TRACKING_HANDLES)
                : new CompoundTag();

        if (handles.containsNumber(key) && handles.getInt(key) == trackingHandle) return false;

        handles.putInt(key, trackingHandle);
        extraData.putCompound(PNX_EXTRA_TRACKING_HANDLES, handles);
        return true;
    }

    public BlockEntityLodestone(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @NotNull
    public OptionalInt getTrackingHandler() {
        int handler = getPnxTrackingHandle(this.chunk.getExtraData(), this.getFloorX(), this.getFloorY(), this.getFloorZ());
        return handler > 0 ? OptionalInt.of(handler) : OptionalInt.empty();
    }

    private void setTrackingHandler(int trackingHandle) {
        if (setPnxTrackingHandle(this.chunk.getExtraData(), this.getFloorX(), this.getFloorY(), this.getFloorZ(), trackingHandle)) {
            this.chunk.setChanged();
        }
    }

    public int requestTrackingHandler() throws IOException {
        OptionalInt opt = getTrackingHandler();
        PositionTrackingService positionTrackingService = getLevel().getServer().getPositionTrackingService();
        Position floor = floor();
        if (opt.isPresent()) {
            int handler = opt.getAsInt();
            PositionTracking position = positionTrackingService.getPosition(handler);
            if (position != null && position.matchesNamedPosition(floor)) return handler;
        }

        int handler = positionTrackingService.addOrReusePosition(floor);
        this.setTrackingHandler(handler);
        return handler;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.LODESTONE;
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        this.setTrackingHandler(0);

        IntList handlers;
        PositionTrackingService positionTrackingService = Server.getInstance().getPositionTrackingService();
        try {
            handlers = positionTrackingService.findTrackingHandlers(this);
            if (handlers.isEmpty()) return;
        } catch (IOException e) {
            log.error("Failed to remove the tracking position handler for {}", getLocation());
            return;
        }

        int size = handlers.size();
        for (int i = 0; i < size; i++) {
            int handler = handlers.getInt(i);
            try {
                positionTrackingService.invalidateHandler(handler);
            } catch (IOException e) {
                log.error("Failed to remove the tracking handler {} for position {}", handler, getLocation(), e);
            }
        }
    }
}
