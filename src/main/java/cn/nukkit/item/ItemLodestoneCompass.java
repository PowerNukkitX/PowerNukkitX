package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.positiontracking.NamedPosition;

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
        return hasCompoundTag() ? getNamedTag().getInt("trackingHandle") : 0;
    }

    public void setTrackingHandle(int trackingHandle) {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            tag = new CompoundTag();
        }
        tag.putInt("trackingHandle", trackingHandle);
        setNamedTag(tag);
    }
}