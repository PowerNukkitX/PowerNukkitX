package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PlayerStartItemCoolDownPacket extends DataPacket {
    private String itemCategory;
    private int coolDownDuration;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_START_ITEM_COOL_DOWN_PACKET;
    }

    @Override
    public void decode() {
        this.itemCategory = getString();
        this.coolDownDuration = getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        putString(itemCategory);
        putVarInt(coolDownDuration);
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public int getCoolDownDuration() {
        return coolDownDuration;
    }

    public void setCoolDownDuration(int coolDownDuration) {
        this.coolDownDuration = coolDownDuration;
    }
}
