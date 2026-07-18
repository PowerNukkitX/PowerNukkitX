package org.powernukkitx.item;

import org.powernukkitx.Server;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.positiontracking.NamedPosition;

import javax.annotation.Nullable;
import java.io.IOException;

public class ItemLodestoneCompass extends Item {
    public ItemLodestoneCompass() {
        this(0, 1);
    }

    public ItemLodestoneCompass(Integer meta) {
        this(meta, 1);
    }

    public ItemLodestoneCompass(Integer meta, int count) {
        super(LODESTONE_COMPASS, meta, count, "Lodestone Compass");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public void setTrackingPosition(@Nullable NamedPosition position) throws IOException {
        if (position == null) {
            setTrackingHandle(0);
            return;
        }
        setTrackingHandle(Server.getInstance().getPositionTrackingService().addOrReusePosition(position));
    }

    public @Nullable NamedPosition getTrackingPosition() throws IOException {
        int trackingHandle = getTrackingHandle();
        if (trackingHandle == 0) {
            return null;
        }
        return Server.getInstance().getPositionTrackingService().getPosition(trackingHandle);
    }

    public int getTrackingHandle() {
        return hasNbt() ? getNbt().getInt("trackingHandle") : 0;
    }

    public void setTrackingHandle(int trackingHandle) {
        CompoundTag tag = getOrCreateNbt();
        tag.putInt("trackingHandle", trackingHandle);
        setNbt(tag);
    }
}