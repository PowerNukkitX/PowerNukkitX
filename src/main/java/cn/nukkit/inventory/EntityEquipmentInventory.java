package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.MobEquipmentPacket;

import java.util.HashSet;
import java.util.Set;


public class EntityEquipmentInventory extends BaseInventory {

    private final Entity entity;


    /**
     * @param entity an Entity which implements {@link InventoryHolder}.
     * @throws ClassCastException if the entity does not implements {@link InventoryHolder}
     */


    public EntityEquipmentInventory(Entity entity) {
        super((InventoryHolder) entity, InventoryType.ENTITY_EQUIPMENT);
        this.entity = entity;
    }

    @Override
    public String getName() {
        return "Entity Equipment";
    }

    @Override
    public int getSize() {
        return 2;
    }


    public Entity getEntity() {
        return entity;
    }
    
    @Override
    public InventoryHolder getHolder() {
        return this.holder;
    }

    @Override
    public void sendSlot( int index, Player... players ) {
        for ( Player player : players ) {
            this.sendSlot( index, player );
        }
    }

    @Override
    public void sendSlot( int index, Player player ) {
        MobEquipmentPacket mobEquipmentPacket = new MobEquipmentPacket();
        mobEquipmentPacket.eid = this.entity.getId();
        mobEquipmentPacket.inventorySlot = mobEquipmentPacket.hotbarSlot = index;
        mobEquipmentPacket.item = this.getItem( index );
        player.dataPacket( mobEquipmentPacket );
    }

    @Override
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>(this.viewers);
        viewers.addAll(entity.getViewers().values());
        return viewers;
    }

    @Override
    public boolean open( Player who ) {
        return this.viewers.add( who );
    }

    @Override
    public void onClose( Player who ) {
        this.viewers.remove( who );
    }


    public Item getItemInHand() {
        return this.getItem( MAIN_HAND);
    }


    public Item getItemInOffhand() {
        return this.getItem( OFFHAND );
    }


    public boolean setItemInHand(Item item){
        return this.setItem(MAIN_HAND,item);
    }


    public boolean setItemInHand( Item item, boolean send ) {
        return this.setItem( MAIN_HAND, item, send );
    }


    public boolean setItemInOffhand(Item item, boolean send ) {
        return this.setItem( OFFHAND, item, send );
    }

    @Override
    public void sendContents( Player target ) {
        this.sendSlot( MAIN_HAND, target );
        this.sendSlot( OFFHAND, target );
    }

    @Override
    public void sendContents( Player... target ) {
        for ( Player player : target ) {
            this.sendContents( player );
        }
    }
}
