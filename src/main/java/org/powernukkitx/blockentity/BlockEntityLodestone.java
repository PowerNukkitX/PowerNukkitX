package org.powernukkitx.blockentity;

import org.powernukkitx.Server;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.positiontracking.PositionTracking;
import org.powernukkitx.positiontracking.PositionTrackingService;
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


    public BlockEntityLodestone(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (nbt.contains("trackingHandler")) {
            this.nbt.put("trackingHandle", this.nbt.removeAndGet("trackingHandler"));
        }
    }

    @NotNull
    public OptionalInt getTrackingHandler() {
        if (nbt.contains("trackingHandle")) {
            return OptionalInt.of(getNbt().getInt("trackingHandle"));
        }
        return OptionalInt.empty();
    }

    public int requestTrackingHandler() throws IOException {
        OptionalInt opt = getTrackingHandler();
        PositionTrackingService positionTrackingService = getLevel().getServer().getPositionTrackingService();
        Position floor = floor();
        if (opt.isPresent()) {
            int handler = opt.getAsInt();
            PositionTracking position = positionTrackingService.getPosition(handler);
            if (position != null && position.matchesNamedPosition(floor)) {
                return handler;
            }
        }

        int handler = positionTrackingService.addOrReusePosition(floor);
        this.nbt.putInt("trackingHandle", handler);
        return handler;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.LODESTONE;
    }

    @Override
    public void onBreak(boolean isSilkTouch) {
        IntList handlers;
        PositionTrackingService positionTrackingService = Server.getInstance().getPositionTrackingService();
        try {
            handlers = positionTrackingService.findTrackingHandlers(this);
            if (handlers.isEmpty()) {
                return;
            }
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
