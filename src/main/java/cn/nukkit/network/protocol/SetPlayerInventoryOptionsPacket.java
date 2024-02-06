package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.inventory.InventoryLayout;
import cn.nukkit.network.protocol.types.inventory.InventoryTabLeft;
import cn.nukkit.network.protocol.types.inventory.InventoryTabRight;

public class SetPlayerInventoryOptionsPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_PLAYER_INVENTORY_OPTIONS_PACKET;

    public InventoryTabLeft leftTab;
    public InventoryTabRight rightTab;
    public boolean filtering;
    public InventoryLayout layout;
    public InventoryLayout craftingLayout;

    @Override
    public int pid() {
        return (byte) NETWORK_ID;
    }

    @Override
    public void decode() {
        this.leftTab = InventoryTabLeft.VALUES[this.getVarInt()];
        this.rightTab = InventoryTabRight.VALUES[this.getVarInt()];
        this.filtering = this.getBoolean();
        this.layout = InventoryLayout.VALUES[this.getVarInt()];
        this.craftingLayout = InventoryLayout.VALUES[this.getVarInt()];
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.leftTab.ordinal());
        this.putVarInt(this.rightTab.ordinal());
        this.putBoolean(this.filtering);
        this.putVarInt(this.layout.ordinal());
        this.putVarInt(this.craftingLayout.ordinal());
    }
}
