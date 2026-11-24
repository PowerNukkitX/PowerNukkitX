package org.powernukkitx.command.defaults;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.HumanOffHandInventory;
import org.powernukkitx.item.Item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClearCommandTest {

    private ClearCommand command;
    private CommandSender sender;
    private ParamList params;
    private HumanInventory inventory;
    private HumanOffHandInventory offhand;
    private CommandLogger log;

    @BeforeEach
    void setup() {
        command = mock(ClearCommand.class, CALLS_REAL_METHODS);
        sender = mock(CommandSender.class);
        params = mock(ParamList.class);
        inventory = mock(HumanInventory.class);
        offhand = mock(HumanOffHandInventory.class);
        log = mock(CommandLogger.class, RETURNS_SELF);
        Player target = mock(Player.class);
        when(target.getInventory()).thenReturn(inventory);
        when(target.getOffhandInventory()).thenReturn(offhand);
        when(target.getViewableName(sender)).thenReturn("target");
        when(params.hasResult(anyInt())).thenReturn(true);
        doReturn(List.of(target)).when(params).getResult(0);
        doReturn(stack(1)).when(params).getResult(1);
        doReturn(-1).when(params).getResult(2);
        doReturn(5).when(params).getResult(3);
        when(inventory.getContents()).thenReturn(Map.of());
        when(offhand.getItem(0)).thenReturn(Item.AIR);
    }

    @Test
    void rejectedInventoryChangePreservesItemsAndContinuesToNextSlot() {
        Item rejected = stack(4);
        Item accepted = stack(8);
        Map<Integer, Item> contents = new LinkedHashMap<>();
        contents.put(0, rejected);
        contents.put(1, accepted);
        when(inventory.getContents()).thenReturn(contents);
        when(inventory.setItem(eq(1), any(Item.class))).thenReturn(true);

        assertEquals(1, execute());
        assertEquals(4, rejected.getCount());
        assertEquals(8, accepted.getCount());
        verify(inventory).setItem(eq(1), argThat(item -> item.getCount() == 3));
        verify(log).addSuccess("commands.clear.success", "target", "5");
    }

    @Test
    void rejectedInventoryChangeDoesNotReportSuccess() {
        Item original = stack(8);
        when(inventory.getContents()).thenReturn(Map.of(0, original));

        assertEquals(0, execute());
        assertEquals(8, original.getCount());
        verify(log).addError("commands.clear.failure.no.items", "target");
    }

    @Test
    void rejectedOffhandChangeDoesNotReportSuccess() {
        Item original = stack(8);
        when(offhand.getItem(0)).thenAnswer(invocation -> original.clone());

        assertEquals(0, execute());
        assertEquals(8, original.getCount());
        verify(offhand).setItem(eq(0), argThat(item -> item.getCount() == 3));
        verify(log).addError("commands.clear.failure.no.items", "target");
    }

    @Test
    void acceptedOffhandChangeReportsRemovedCount() {
        when(offhand.getItem(0)).thenReturn(stack(8));
        when(offhand.setItem(eq(0), any(Item.class))).thenReturn(true);

        assertEquals(1, execute());
        verify(offhand).setItem(eq(0), argThat(item -> item.getCount() == 3));
        verify(log).addSuccess("commands.clear.success", "target", "5");
    }

    private int execute() {
        return command.execute(sender, "clear", Map.entry("default", params), log);
    }

    private Item stack(int count) {
        return new Item("test:clear", 0, count, null, false) {};
    }
}
