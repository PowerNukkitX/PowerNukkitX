package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.inventory.InventoryLayout;
import cn.nukkit.network.protocol.types.inventory.InventoryTabLeft;
import cn.nukkit.network.protocol.types.inventory.InventoryTabRight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPlayerInventoryOptionsPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.SET_PLAYER_INVENTORY_OPTIONS_PACKET;

    private InventoryTabLeft leftTab;
    private InventoryTabRight rightTab;
    private boolean filtering;
    private InventoryLayout layout;
    private InventoryLayout craftingLayout;

    @Override
    public int pid() {
        return (byte) NETWORK_ID;
    }

    @Override
    public void decode() {
        this.setLeftTab(InventoryTabLeft.VALUES[this.getVarInt()]);
        this.setRightTab(InventoryTabRight.VALUES[this.getVarInt()]);
        this.setFiltering(this.getBoolean());
        this.setLayout(InventoryLayout.VALUES[this.getVarInt()]);
        this.setCraftingLayout(InventoryLayout.VALUES[this.getVarInt()]);
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
