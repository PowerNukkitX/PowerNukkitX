package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
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
        this.coolDownDuration = getInt();
    }

    @Override
    public void encode() {
        putString(itemCategory);
        putInt(coolDownDuration);
    }
}
