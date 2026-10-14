package org.powernukkitx.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkitx.GameMockExtension;
import org.powernukkitx.Player;
import org.powernukkitx.command.defaults.ClearCommand;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.HumanOffHandInventory;
import org.powernukkitx.item.Item;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(GameMockExtension.class)
class ClearCommandTest {
    private ClearCommand command;
    private CommandSender sender;
    private CommandLogger log;

    @BeforeEach
    void setUp() {
        command = new ClearCommand("clear");
        sender = mock(CommandSender.class);
        log = mock(CommandLogger.class, RETURNS_SELF);
    }

    @Test
    void clearsCustomItemsWithoutData() {
        Player target = target(item("test:custom", 7, 8), item("test:custom", 3, 4));
        assertEquals(1, execute(List.of(target), item("test:custom", 0, 1), null, null));
        verify(target.getInventory()).clear(0);
        verify(target.getOffhandInventory()).clear(0);
        verify(log).addSuccess("commands.clear.success", "target", "12");
    }

    @Test
    void wildcardDataCountsWithoutChangingInventories() {
        Player target = target(item("test:custom", 7, 8), item("test:custom", 3, 4));
        assertEquals(1, execute(List.of(target), item("test:custom", 0, 1), -1, 0));
        verify(log).addSuccess("commands.clear.testing", "target", "12");
        verify(target.getInventory(), never()).clear(anyInt());
        verify(target.getOffhandInventory(), never()).clear(anyInt());
        verify(target.getInventory(), never()).setItem(anyInt(), any(Item.class));
        verify(target.getOffhandInventory(), never()).setItem(anyInt(), any(Item.class));
    }

    @Test
    void explicitDataOnlyClearsMatchingDamage() {
        Player target = target(Item.get(Item.DIAMOND_SWORD, 7), Item.get(Item.DIAMOND_SWORD, 3));
        assertEquals(1, execute(List.of(target), Item.get(Item.DIAMOND_SWORD), 3, -1));
        verify(target.getInventory(), never()).clear(anyInt());
        verify(target.getOffhandInventory()).clear(0);
        verify(log).addSuccess("commands.clear.success", "target", "1");
    }

    @Test
    void explicitZeroDataDoesNotActAsWildcard() {
        Player target = target(Item.get(Item.DIAMOND_SWORD, 7), Item.get(Item.DIAMOND_SWORD, 0));
        assertEquals(1, execute(List.of(target), Item.get(Item.DIAMOND_SWORD), 0, -1));
        verify(target.getInventory(), never()).clear(anyInt());
        verify(target.getOffhandInventory()).clear(0);
    }

    @Test
    void limitedClearUsesRemainingCountInOffhand() {
        Player target = target(item("test:custom", 7, 2), item("test:custom", 3, 8));
        assertEquals(1, execute(List.of(target), item("test:custom", 0, 1), -1, 5));
        verify(target.getInventory()).setItem(eq(0), argThat(slot -> slot.getCount() == 0));
        verify(target.getOffhandInventory()).setItem(eq(0), argThat(slot -> slot.getCount() == 5));
        verify(log).addSuccess("commands.clear.success", "target", "5");
    }

    @Test
    void limitedClearStopsBeforeOffhandWhenLimitIsReached() {
        Player target = target(item("test:custom", 7, 8), item("test:custom", 3, 8));
        assertEquals(1, execute(List.of(target), item("test:custom", 0, 1), -1, 5));
        verify(target.getInventory()).setItem(eq(0), argThat(slot -> slot.getCount() == 3));
        verify(target.getOffhandInventory(), never()).setItem(anyInt(), any(Item.class));
    }

    @Test
    void rejectsNegativeCountBeforeChangingInventories() {
        Player target = target(item("test:custom", 0, 8), Item.AIR);
        assertEquals(0, execute(List.of(target), item("test:custom", 0, 1), -1, -2));
        verify(log).addNumTooSmall(3, -1);
        verifyNoInteractions(target.getInventory(), target.getOffhandInventory());
    }

    @Test
    void rejectsDataBelowWildcardBeforeChangingInventories() {
        Player target = target(item("test:custom", 0, 8), Item.AIR);
        assertEquals(0, execute(List.of(target), item("test:custom", 0, 1), -2, -1));
        verify(log).addNumTooSmall(2, -1);
        verifyNoInteractions(target.getInventory(), target.getOffhandInventory());
    }

    @Test
    void continuesAfterMissingItemsAndCountsOnlySuccessfulTargets() {
        for (int maxCount : new int[]{-1, 0, 5}) {
            Player empty = target(Item.AIR, Item.AIR);
            Player matching = target(item("test:custom", 7, 8), Item.AIR);
            assertEquals(1, execute(List.of(empty, matching), item("test:custom", 0, 1), -1, maxCount));
        }
    }

    @Test
    void reportsFailureWhenNoTargetMatches() {
        for (int maxCount : new int[]{-1, 0, 5}) {
            assertEquals(0, execute(List.of(target(Item.AIR, Item.AIR)), item("test:custom", 0, 1), -1, maxCount));
        }
    }

    @Test
    void clearWithoutItemCountsCustomItemsAndOffhand() {
        Player target = target(item("test:custom", 7, 8), item("test:other", 0, 4));
        assertEquals(1, execute(List.of(target), null, null, null));
        verify(target.getInventory()).clear(0);
        verify(target.getOffhandInventory()).clear(0);
        verify(log).addSuccess("commands.clear.success", "target", "12");
        assertEquals(0, execute(List.of(target(Item.AIR, Item.AIR)), null, null, null));
    }

    private int execute(List<Player> targets, Item item, Integer data, Integer maxCount) {
        ParamList params = mock(ParamList.class);
        Object[] values = {targets, item, data, maxCount};
        for (int i = 0; i < values.length; i++) {
            when(params.hasResult(i)).thenReturn(values[i] != null);
            when(params.getResult(i)).thenReturn(values[i]);
        }
        return command.execute(sender, "clear", Map.entry("default", params), log);
    }

    private Player target(Item main, Item offhand) {
        Player target = mock(Player.class);
        HumanInventory inventory = mock(HumanInventory.class);
        HumanOffHandInventory offhandInventory = mock(HumanOffHandInventory.class);
        when(inventory.getContents()).thenReturn(main.isNull() ? Map.of() : Map.of(0, main));
        when(offhandInventory.getItem(0)).thenReturn(offhand);
        when(target.getInventory()).thenReturn(inventory);
        when(target.getOffhandInventory()).thenReturn(offhandInventory);
        when(target.getViewableName(sender)).thenReturn("target");
        return target;
    }

    private Item item(String id, int data, int count) {
        return new Item(id, data, count, null, false) {
        };
    }
}
