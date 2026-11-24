package org.powernukkitx.network.process.handler;

import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequest;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DestroyAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DropAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.TakeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackNetResult;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackResponsePacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.event.inventory.ItemStackRequestActionEvent;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.PlayerCursorInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.plugin.PluginManager;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemStackRequestHandlerTest {

    private Player player;
    private PluginManager pluginManager;
    private Server server;

    @BeforeEach
    void setup() {
        player = mock(Player.class);
        server = mock(Server.class);
        pluginManager = mock(PluginManager.class);
        HumanInventory inventory = mock(HumanInventory.class);
        PlayerCursorInventory cursor = mock(PlayerCursorInventory.class);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(player.getName()).thenReturn("test");
        when(player.getTopWindow()).thenReturn(Optional.empty());
        when(player.getInventory()).thenReturn(inventory);
        when(player.getCursorInventory()).thenReturn(cursor);
        when(cursor.getUnclonedItem()).thenReturn(Item.AIR);
    }

    @Test
    void rejectsMissingIdDuringTransferEvent() throws Exception {
        assertRejected(new TakeAction(1, dynamicSlot(null), inventorySlot()));
        verifyNoInteractions(pluginManager);
    }

    @Test
    void rejectsUnknownIdDuringTransferEvent() throws Exception {
        assertRejected(new TakeAction(1, dynamicSlot(-1), inventorySlot()));
        verifyNoInteractions(pluginManager);
    }

    @Test
    void rejectsInvalidDropDuringTransferEvent() throws Exception {
        assertRejected(new DropAction(1, dynamicSlot(null), false));
        verifyNoInteractions(pluginManager);
    }

    @Test
    void rejectsInvalidIdDuringProcessorExecution() throws Exception {
        assertRejected(new DestroyAction(1, dynamicSlot(null)));
        verify(pluginManager).callEvent(any(ItemStackRequestActionEvent.class));
    }

    @Test
    void stopsFailedRequestAndProcessesFollowingRequest() throws Exception {
        ItemStackRequestPacket packet = new ItemStackRequestPacket();
        packet.getRequests().add(request(1, new TakeAction(1, dynamicSlot(null), inventorySlot()),
                new DestroyAction(1, dynamicSlot(null))));
        packet.getRequests().add(request(2));

        ItemStackResponsePacket response = handle(packet);

        assertEquals(2, response.getResponses().size());
        assertEquals(ItemStackNetResult.ERROR, response.getResponses().get(0).getResult());
        assertEquals(1, response.getResponses().get(0).getClientRequestId().getID());
        assertEquals(ItemStackNetResult.SUCCESS, response.getResponses().get(1).getResult());
        assertEquals(2, response.getResponses().get(1).getClientRequestId().getID());
        verifyNoInteractions(pluginManager);
    }

    private void assertRejected(ItemStackRequestAction action) throws Exception {
        ItemStackRequestPacket packet = new ItemStackRequestPacket();
        packet.getRequests().add(request(1, action));

        ItemStackResponsePacket response = handle(packet);

        assertEquals(1, response.getResponses().size());
        assertEquals(ItemStackNetResult.ERROR, response.getResponses().getFirst().getResult());
        assertEquals(1, response.getResponses().getFirst().getClientRequestId().getID());
        assertTrue(response.getResponses().getFirst().getContainers().isEmpty());
    }

    private ItemStackResponsePacket handle(ItemStackRequestPacket packet) throws Exception {
        Method method = ItemStackRequestHandler.class.getDeclaredMethod(
                "handleRequests", ItemStackRequestPacket.class, Player.class);
        method.setAccessible(true);
        try (MockedStatic<Server> mocked = mockStatic(Server.class)) {
            mocked.when(Server::getInstance).thenReturn(server);
            assertDoesNotThrow(() -> method.invoke(new ItemStackRequestHandler(), packet, player));
        }
        ArgumentCaptor<ItemStackResponsePacket> response = ArgumentCaptor.forClass(ItemStackResponsePacket.class);
        verify(player).sendPacket(response.capture());
        return response.getValue();
    }

    private ItemStackRequest request(int id, ItemStackRequestAction... actions) {
        return new ItemStackRequest(id, actions, new String[0]);
    }

    private ItemStackRequestSlotInfo dynamicSlot(Integer id) {
        return slot(ContainerEnumName.DYNAMIC_CONTAINER, id);
    }

    private ItemStackRequestSlotInfo inventorySlot() {
        return slot(ContainerEnumName.INVENTORY_CONTAINER, null);
    }

    private ItemStackRequestSlotInfo slot(ContainerEnumName container, Integer id) {
        return new ItemStackRequestSlotInfo(container, 0, 0, new FullContainerName(container, id));
    }
}
