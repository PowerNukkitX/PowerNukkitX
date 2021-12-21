package cn.nukkit;

import cn.nukkit.block.BlockID;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.event.vehicle.VehicleMoveEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.Network;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.*;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.powernukkit.tests.api.MockEntity;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(PowerNukkitExtension.class)
class PlayerTest {
    private final Long clientId = 32L;
    private final String clientIp = "1.2.3.4";
    private final int clientPort = 3232;
    
    @MockLevel
    Level level;
    
    @Mock
    SourceInterface sourceInterface;

    @MockEntity
    EntityPig pig;

    @MockEntity
    EntityBoat boat;

    Skin skin;
    
    Player player;

    private MoveEntityAbsolutePacket buildMoveEntityAbsolutePacket(long eid) {
        MoveEntityAbsolutePacket packet = new MoveEntityAbsolutePacket();
        packet.eid = eid;
        packet.x = player.getX() - 1;
        packet.y = player.getY() - 1;
        packet.z = player.getZ() - 1;
        packet.yaw = 2.0;
        packet.headYaw = 2.0;
        packet.pitch = 2.0;
        packet.encode();
        return packet;
    }

    @Test
    void moveEntityAbsolutePacketRidingBoat() {
        boat.mountEntity(player);
        assertSame(boat, player.getRiding());
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.handleDataPacket(buildMoveEntityAbsolutePacket(boat.getId()));
        verify(player.getServer().getPluginManager(), new Times(1)).callEvent(any(DataPacketReceiveEvent.class));
        verify(player.getServer().getPluginManager(), new Times(1)).callEvent(any(VehicleMoveEvent.class));
    }

    @Test
    void moveEntityAbsolutePacketRidingPig() {
        pig.mountEntity(player);
        assertSame(pig, player.getRiding());
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.handleDataPacket(buildMoveEntityAbsolutePacket(pig.getId()));
        verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(VehicleMoveEvent.class));
    }

    @Test
    void moveEntityAbsolutePacketNotRiding() {
        assertNull(player.getRiding());
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.handleDataPacket(buildMoveEntityAbsolutePacket(player.getId()));
        verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(VehicleMoveEvent.class));
    }

    @Test
    void moveEntityAbsolutePacketNotSpawned() {
        assertTrue(player.isAlive());
        player.spawned = false;
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.handleDataPacket(buildMoveEntityAbsolutePacket(player.getId()));
        verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(VehicleMoveEvent.class));
    }

    @Test
    void moveEntityAbsolutePacketNotAlive() {
        player.setHealth(0);
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.handleDataPacket(buildMoveEntityAbsolutePacket(player.getId()));
        verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(VehicleMoveEvent.class));
    }

    @Test
    void emotePacketNotSpawned() {
        EmotePacket packet = new EmotePacket();
        packet.runtimeId = player.getId();
        packet.emoteID = "emote";
        packet.encode();
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.spawned = false;
        player.getViewers().put(1, player);
        player.handleDataPacket(packet);
        verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(DataPacketSendEvent.class));
    }

    @Test
    void emotePacketBadId() {
        EmotePacket packet = new EmotePacket();
        packet.runtimeId = player.getId() + 1;
        packet.emoteID = "emote";
        packet.encode();
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.getViewers().put(1, player);
        player.handleDataPacket(packet);
        verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(DataPacketSendEvent.class));
    }

    @Test
    void emotePacketOk() {
        EmotePacket packet = new EmotePacket();
        packet.runtimeId = player.getId();
        packet.emoteID = "emote";
        packet.encode();
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.getViewers().put(1, player);
        player.handleDataPacket(packet);
        verify(player.getServer().getPluginManager(), new Times(1)).callEvent(any(DataPacketSendEvent.class));
    }

    @Test
    void sendCommandDataNotSpawned() {
        player.spawned = false;
        SimpleCommandMap filled = new SimpleCommandMap(player.getServer());
        when(player.getServer().getCommandMap()).thenReturn(filled);
        assertFalse(filled.getCommands().isEmpty());
        when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
        player.setOp(true);
        when(player.getServer().isOp(eq(player.getName()))).thenReturn(true);
        assertTrue(player.isOp());
        player.sendCommandData();
        verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(DataPacketSendEvent.class));
    }

    @Test
    void sendCommandDataZeroCommands() {
        player.spawned = true;
        player.connected = true;
        SimpleCommandMap zero = new SimpleCommandMap(player.getServer());
        zero.getCommands().clear();
        when(player.getServer().getCommandMap()).thenReturn(zero);
        assertTrue(zero.getCommands().isEmpty());
        PluginManager previous = player.getServer().getPluginManager();
        player.setOp(true);
        when(player.getServer().isOp(eq(player.getName()))).thenReturn(true);
        assertTrue(player.isOp());
        try {
            when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
            player.sendCommandData();
            verify(player.getServer().getPluginManager(), new Times(0)).callEvent(any(DataPacketSendEvent.class));
        } finally {
            lenient().when(player.getServer().getPluginManager()).thenReturn(previous);
        }
    }

    @Test
    void sendCommandDataSpawned() {
        player.spawned = true;
        player.connected = true;
        SimpleCommandMap filled = new SimpleCommandMap(player.getServer());
        when(player.getServer().getCommandMap()).thenReturn(filled);
        assertFalse(filled.getCommands().isEmpty());
        PluginManager previous = player.getServer().getPluginManager();
        player.setOp(true);
        when(player.getServer().isOp(eq(player.getName()))).thenReturn(true);
        assertTrue(player.isOp());
        try {
            when(player.getServer().getPluginManager()).thenReturn(mock(PluginManager.class));
            player.sendCommandData();
            verify(player.getServer().getPluginManager(), new Times(1)).callEvent(any(DataPacketSendEvent.class));
        } finally {
            lenient().when(player.getServer().getPluginManager()).thenReturn(previous);
        }
    }

    @Test
    void setButtonText() {
        player.setButtonText("button.text");
        assertEquals("button.text", player.getDataPropertyString(Entity.DATA_INTERACTIVE_TAG));
        assertEquals("button.text", player.getButtonText());
    }

    @Test
    void tooManyFailedLoginAttempts() {
        PluginManager pluginManager = mock(PluginManager.class);
        when(player.getServer().getPluginManager()).thenReturn(pluginManager);
        Player player = new Player(sourceInterface, clientId, clientIp, clientPort);

        FilterTextPacket packet = new FilterTextPacket();
        packet.text = "asd";
        packet.fromServer = false;

        Network network = new Network(Server.getInstance());
        when(Server.getInstance().getNetwork()).thenReturn(network);

        player.handleDataPacket(packet);
        verify(pluginManager, times(0)).callEvent(any());

        packet.encode();
        player.handleDataPacket(packet.compress(2));
        verify(pluginManager, times(1)).callEvent(any());

        for (int i = 2; i <= 100; i++) {
            player.handleDataPacket(packet);
            assertFalse(player.closed);
        }

        player.handleDataPacket(packet);
        assertTrue(player.closed);
    }

    @Test
    void armorDamage() {
        player.attack(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1));
        PlayerInventory inventory = player.getInventory();
        
        ////// Block in armor content ////////
        inventory.setArmorContents(new Item[]{
                Item.getBlock(BlockID.WOOL),
                Item.getBlock(BlockID.WOOL, 1),
                Item.getBlock(BlockID.WOOL, 2),
                Item.getBlock(BlockID.WOOL, 3)
        });
        for (int i = 0; i < 100; i++) {
            player.setHealth(20);
            player.attack(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1));
            player.entityBaseTick(20);
        }
        assertEquals(Arrays.asList(
                Item.getBlock(BlockID.WOOL),
                Item.getBlock(BlockID.WOOL, 1),
                Item.getBlock(BlockID.WOOL, 2),
                Item.getBlock(BlockID.WOOL, 3)
        ), Arrays.asList(inventory.getArmorContents()));

        ////// Valid armor in armor content ///////
        inventory.setArmorContents(new Item[]{
                Item.get(ItemID.LEATHER_CAP),
                Item.get(ItemID.LEATHER_TUNIC),
                Item.get(ItemID.LEATHER_PANTS),
                Item.get(ItemID.LEATHER_BOOTS)
        });
        for (int i = 0; i < 100; i++) {
            player.setHealth(20);
            player.attack(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1));
            player.entityBaseTick(20);
        }
        assertEquals(Arrays.asList(
                Item.getBlock(BlockID.AIR),
                Item.getBlock(BlockID.AIR),
                Item.getBlock(BlockID.AIR),
                Item.getBlock(BlockID.AIR)
        ), Arrays.asList(inventory.getArmorContents()));

        ////// Unbreakable armor in armor content ///////
        List<Item> items = Arrays.asList(
                Item.get(ItemID.LEATHER_CAP),
                Item.get(ItemID.LEATHER_TUNIC),
                Item.get(ItemID.LEATHER_PANTS),
                Item.get(ItemID.LEATHER_BOOTS)
        );
        items.forEach(item -> item.setNamedTag(new CompoundTag().putBoolean("Unbreakable", true)));
        Item[] array = new Item[items.size()];
        for (int i = 0; i < items.size(); i++) {
            array[i] = items.get(i).clone();
        }
        inventory.setArmorContents(array);
        for (int i = 0; i < 100; i++) {
            player.setHealth(20);
            player.attack(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 1));
            player.entityBaseTick(20);
        }
        assertEquals(ItemID.LEATHER_CAP, items.get(0).getId());
        assertTrue(items.get(2).isUnbreakable());
        assertEquals(items, Arrays.asList(inventory.getArmorContents()));
    }

    @Test
    void dupeCommand() {
        Item stick = Item.get(ItemID.STICK);
        Item air = Item.getBlock(BlockID.AIR, 0, 0);
        
        player.getInventory().addItem(stick);
        List<NetworkInventoryAction> actions = new ArrayList<>();
        NetworkInventoryAction remove = new NetworkInventoryAction();
        remove.sourceType = NetworkInventoryAction.SOURCE_CONTAINER;
        remove.windowId = 0;
        remove.stackNetworkId = 1;
        remove.inventorySlot = 0;
        remove.oldItem = stick;
        remove.newItem = air;
        actions.add(remove);

        for (int slot = 1; slot < 35; slot++) {
            if (slot > 1) {
                actions.add(remove);
            }
            
            NetworkInventoryAction add = new NetworkInventoryAction();
            add.sourceType = NetworkInventoryAction.SOURCE_CONTAINER;
            add.windowId = 0;
            add.stackNetworkId = 1;
            add.inventorySlot = slot;
            add.oldItem = air;
            add.newItem = stick;
            
            actions.add(add);
        }

        InventoryTransactionPacket packet = new InventoryTransactionPacket();
        packet.actions = actions.toArray(NetworkInventoryAction.EMPTY_ARRAY);
        
        player.handleDataPacket(packet);

        int count = countItems(stick);
        assertEquals(1, count);
    }
    
    private int countItems(Item item) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            Item inv = player.getInventory().getItem(i);
            if (item.equals(inv)) {
                count += inv.getCount();
            }
        }
        return count;
    }

    @BeforeEach
    void setUp() {
        /// Setup Level ///
        doReturn(new Position(100,64,200, level)).when(level).getSafeSpawn();
        
        /// Setup Server ///
        doReturn(level).when(Server.getInstance()).getDefaultLevel();
        
        /// Setup skin ///
        skin = new Skin();
        skin.setSkinId("test");
        skin.setSkinData(new BufferedImage(64, 32, BufferedImage.TYPE_INT_BGR));
        assertTrue(skin.isValid());
        
        /// Make player login ///
        player = new Player(sourceInterface, clientId, clientIp, clientPort);
        LoginPacket loginPacket = new LoginPacket();
        loginPacket.username = "TestPlayer";
        loginPacket.protocol = ProtocolInfo.CURRENT_PROTOCOL;
        loginPacket.clientId = 2L;
        loginPacket.clientUUID = new UUID(3, 3);
        loginPacket.skin = skin;
        loginPacket.putLInt(2);
        loginPacket.put("{}".getBytes());
        loginPacket.putLInt(0);
        player.handleDataPacket(loginPacket);
        player.completeLoginSequence();
        
        assertTrue(player.isOnline(), "Failed to make the fake player login");
        
        player.doFirstSpawn();
    }
}
