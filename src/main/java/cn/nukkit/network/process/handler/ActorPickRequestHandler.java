package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.player.PlayerEntityPickEvent;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.SpawnEggPickable;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import cn.nukkit.utils.Identifier;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.protocol.bedrock.packet.ActorPickRequestPacket;

/**
 * @author Kaooot
 */
@Slf4j
public class ActorPickRequestHandler implements PacketHandler<ActorPickRequestPacket> {

    @Override
    public void handle(ActorPickRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        final Player player = playerHandle.player;
        final Entity entity = player.level.getEntity(packet.getActorID());
        if (entity == null) {
            log.debug("{}: Entity pick request contained an invalid entity", playerHandle.getUsername());
            return;
        }
        if (entity.getLocation().distanceSquared(player) > 100) {
            log.debug("{}: Entity pick request for an entity too far away", playerHandle.getUsername());
            return;
        }
        if (entity.isCustomEntity() && (entity.getCustomEntityDefinition() == null || !entity.getCustomEntityDefinition().hasSpawnEgg())) {
            log.debug("{}: Entity pick request for a custom entity without spawn egg", playerHandle.getUsername());
            return;
        }
        final Identifier identifier = Identifier.tryParse(entity.getIdentifier());
        if (identifier == null) {
            log.debug("{}: Failed to parse an identifier for entity: {}", playerHandle.getUsername(), entity.getName());
            return;
        }
        final Identifier spawnEggIdentifier = Identifier.of(
                identifier.getNamespace(),
                identifier.getPath() + "_spawn_egg"
        );
        if (spawnEggIdentifier == null) {
            log.debug("{}: Spawn Egg not found for entity: {}", playerHandle.getUsername(), entity.getName());
            return;
        }
        final Item item = Item.get(spawnEggIdentifier.toString());
        if (!(item instanceof SpawnEggPickable)) {
            log.debug("{}: Spawn Egg not found for entity: {}", playerHandle.getUsername(), entity.getName());
            return;
        }

        if (packet.isWithData()) {
            final NbtMap nbt = this.getCleanedNBT(entity);
            if (nbt != null) {
                ((SpawnEggPickable) item).setEntityNBT(nbt);
                item.setLore("+(DATA)");
            }
        }

        final PlayerEntityPickEvent pickEvent = new PlayerEntityPickEvent(player, entity, item);
        if (player.isSpectator()) {
            log.debug("Got entity-pick request from {} when in spectator mode", player.getName());
            pickEvent.setCancelled();
        }

        player.getServer().getPluginManager().callEvent(pickEvent);

        if (!pickEvent.isCancelled()) {
            boolean itemExists = false;
            int itemSlot = -1;
            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                if (player.getInventory().getItem(slot).equals(pickEvent.getItem())) {
                    if (slot < player.getInventory().getHotbarSize()) {
                        player.getInventory().setHeldItemSlot(slot);
                    } else {
                        itemSlot = slot;
                    }
                    itemExists = true;
                    break;
                }
            }

            for (int slot = 0; slot < player.getInventory().getHotbarSize(); slot++) {
                if (player.getInventory().getItem(slot).isNull()) {
                    if (!itemExists && player.isCreative()) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInMainHand(pickEvent.getItem());
                        return;
                    } else if (itemSlot > -1) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInMainHand(player.getInventory().getItem(itemSlot));
                        player.getInventory().clear(itemSlot, true);
                        return;
                    }
                }
            }

            if (!itemExists && player.isCreative()) {
                Item itemInHand = player.getInventory().getItemInMainHand();
                player.getInventory().setItemInMainHand(pickEvent.getItem());
                if (!player.getInventory().isFull()) {
                    for (int slot = 0; slot < HumanInventory.ARMORS_INDEX; slot++) {
                        if (player.getInventory().getItem(slot).isNull()) {
                            player.getInventory().setItem(slot, itemInHand);
                            break;
                        }
                    }
                }
            } else if (itemSlot > -1) {
                Item itemInHand = player.getInventory().getItemInMainHand();
                player.getInventory().setItemInMainHand(player.getInventory().getItem(itemSlot));
                player.getInventory().setItem(itemSlot, itemInHand);
            }
        }
    }

    private NbtMap getCleanedNBT(Entity entity) {
        entity.saveNBT();
        final NbtMapBuilder tag = entity.namedTag.toBuilder();
        tag.remove("Pos");
        tag.remove("Motion");
        tag.remove("OnGround");
        tag.remove("Rotation");
        tag.remove("uuid");
        if (!tag.isEmpty()) {
            return tag.build();
        } else {
            return null;
        }
    }
}