package org.powernukkitx.inventory.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Player;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.PlayerCursorInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBundle;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.Optional;

import static org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName.DYNAMIC_CONTAINER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NetworkMappingTest {

    private Player player;
    private HumanInventory inventory;
    private PlayerCursorInventory cursor;

    @BeforeEach
    void setup() {
        player = mock(Player.class);
        inventory = mock(HumanInventory.class);
        cursor = mock(PlayerCursorInventory.class);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getTopWindow()).thenReturn(Optional.empty());
        when(player.getCursorInventory()).thenReturn(cursor);
        when(inventory.getSize()).thenReturn(1);
        when(inventory.getUnclonedItem(0)).thenReturn(Item.AIR);
        when(cursor.getUnclonedItem()).thenReturn(Item.AIR);
    }

    @Test
    void rejectsMissingDynamicId() {
        assertThrows(IllegalArgumentException.class,
                () -> NetworkMapping.getInventory(player, DYNAMIC_CONTAINER, null));
        verifyNoInteractions(inventory, cursor);
    }

    @Test
    void resolvesBundleInTopWindow() {
        ItemBundle bundle = new ItemBundle();
        Inventory top = topWindow(bundle);

        assertSame(bundle.getInventory(), resolve(bundle.getBundleId()));
        verify(top, never()).getContents();
        verifyNoInteractions(inventory, cursor);
    }

    @Test
    void fallsBackToPlayerInventory() {
        ItemBundle bundle = new ItemBundle();
        topWindow(Item.AIR);
        when(inventory.getUnclonedItem(0)).thenReturn(bundle);

        assertSame(bundle.getInventory(), resolve(bundle.getBundleId()));
    }

    @Test
    void resolvesCursorBundleWithMatchingId() {
        ItemBundle bundle = new ItemBundle();
        when(cursor.getUnclonedItem()).thenReturn(bundle);

        assertSame(bundle.getInventory(), resolve(bundle.getBundleId()));
    }

    @Test
    void rejectsCursorBundleWithDifferentId() {
        ItemBundle bundle = new ItemBundle();
        when(cursor.getUnclonedItem()).thenReturn(bundle);

        assertThrows(IllegalArgumentException.class, () -> resolve(bundle.getBundleId() + 1));
    }

    @Test
    void doesNotResolveNestedBundleAsCursorBundle() {
        ItemBundle outer = new ItemBundle();
        ItemBundle inner = new ItemBundle();
        ((org.powernukkitx.inventory.BundleInventory) outer.getInventory()).setItemInternal(0, inner);
        when(cursor.getUnclonedItem()).thenReturn(outer);

        assertThrows(IllegalArgumentException.class, () -> resolve(inner.getBundleId()));
    }

    @Test
    void scansPlayerInventoryOnlyOnceWithoutTopWindow() {
        assertThrows(IllegalArgumentException.class, () -> resolve(-1));

        verify(inventory).getUnclonedItem(0);
        verify(inventory, never()).getContents();
        verify(inventory, never()).getItem(anyInt());
    }

    @Test
    void scansPlayerInventoryOnlyOnceWhenItIsTopWindow() {
        when(player.getTopWindow()).thenReturn(Optional.of(inventory));

        assertThrows(IllegalArgumentException.class, () -> resolve(-1));

        verify(inventory).getUnclonedItem(0);
        verify(inventory, never()).getContents();
    }

    @Test
    void staleChestIdDoesNotShadowInitializedPlayerBundle() {
        ItemBundle own = new ItemBundle();
        int currentId = own.getBundleId();
        ItemBundle stale = new ItemBundle();
        stale.setNbt(new CompoundTag().putInt("bundle_id", currentId));
        topWindow(stale);
        when(inventory.getUnclonedItem(0)).thenReturn(own);

        assertSame(own.getInventory(), resolve(currentId));
        assertNotEquals(currentId, stale.getBundleId());
    }

    @Test
    void uninitializedBundlesReceiveDistinctIds() {
        ItemBundle first = new ItemBundle();
        ItemBundle second = new ItemBundle();

        int firstId = first.getBundleId();
        int secondId = second.getBundleId();

        assertNotEquals(firstId, secondId);
        assertEquals(firstId, first.getBundleId());
        assertEquals(secondId, second.getBundleId());
    }

    private Inventory resolve(int id) {
        return NetworkMapping.getInventory(player, DYNAMIC_CONTAINER, id);
    }

    private Inventory topWindow(Item item) {
        Inventory top = mock(Inventory.class);
        when(top.getSize()).thenReturn(1);
        when(top.getUnclonedItem(0)).thenReturn(item);
        when(player.getTopWindow()).thenReturn(Optional.of(top));
        return top;
    }
}
