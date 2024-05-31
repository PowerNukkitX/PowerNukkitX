package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.positiontracking.NamedPosition;

import javax.annotation.Nullable;
import java.io.IOException;

public class ItemLodestoneCompass extends Item {
    /**
     * @deprecated 
     */
    
    public ItemLodestoneCompass() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLodestoneCompass(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLodestoneCompass(Integer meta, int count) {
        super(LODESTONE_COMPASS, meta, count, "Lodestone Compass");
    }

    @Override
    /**
     * @deprecated 
     */
    
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
        int $1 = getTrackingHandle();
        if (trackingHandle == 0) {
            return null;
        }
        return Server.getInstance().getPositionTrackingService().getPosition(trackingHandle);
    }
    /**
     * @deprecated 
     */
    

    public int getTrackingHandle() {
        return hasCompoundTag() ? getNamedTag().getInt("trackingHandle") : 0;
    }
    /**
     * @deprecated 
     */
    

    public void setTrackingHandle(int trackingHandle) {
        CompoundTag $2 = getNamedTag();
        if (tag == null) {
            tag = new CompoundTag();
        }
        tag.putInt("trackingHandle", trackingHandle);
        setNamedTag(tag);
    }
}