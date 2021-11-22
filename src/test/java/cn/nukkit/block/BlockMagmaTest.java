package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.potion.Effect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.powernukkit.tests.api.MockEntity;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.api.MockPlayer;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(PowerNukkitExtension.class)
class BlockMagmaTest {

    @MockLevel
    Level level;

    @MockPlayer(position = {0, 2, 0})
    Player player;

    @MockEntity(position = {0, 2, 0})
    Entity entity;

    @Captor
    ArgumentCaptor<EntityDamageEvent> damageEventCaptor;

    BlockMagma magma;

    @BeforeEach
    void setUp() {
        magma = (BlockMagma) BlockState.of(BlockID.MAGMA).getBlock(level, 0, 1, 0);
        level.setBlock(magma, magma);
    }

    @Test
    void onEntityCollide_Player_Normal() {
        magma.onEntityCollide(player);
        verify(player).attack(damageEventCaptor.capture());
        List<EntityDamageEvent> damages = damageEventCaptor.getAllValues();
        assertEquals(1, damages.size());
        assertEquals(EntityDamageByBlockEvent.class, damages.get(0).getClass());
        EntityDamageByBlockEvent event = (EntityDamageByBlockEvent) damages.get(0);
        assertEquals(1, event.getDamage());
        assertEquals(player, event.getEntity());
        assertEquals(magma, event.getDamager());
        assertEquals(EntityDamageEvent.DamageCause.HOT_FLOOR, event.getCause());
    }

    @Test
    void onEntityCollide_Entity_Normal() {
        magma.onEntityCollide(entity);
        verify(entity).attack(damageEventCaptor.capture());
        List<EntityDamageEvent> damages = damageEventCaptor.getAllValues();
        assertEquals(1, damages.size());
        assertEquals(EntityDamageByBlockEvent.class, damages.get(0).getClass());
        EntityDamageByBlockEvent event = (EntityDamageByBlockEvent) damages.get(0);
        assertEquals(1, event.getDamage());
        assertEquals(entity, event.getEntity());
        assertEquals(magma, event.getDamager());
        assertEquals(EntityDamageEvent.DamageCause.HOT_FLOOR, event.getCause());
    }

    @Test
    void onEntityCollide_Entity_FireResistanceEffect() {
        entity.addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE));
        magma.onEntityCollide(entity);
        verify(entity, times(0)).attack(any());
    }

    @Test
    void onEntityCollide_Player_FrostWalker() {
        Item boots = Item.get(ItemID.GOLD_BOOTS);
        boots.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_FROST_WALKER));
        player.getInventory().setBoots(boots);

        magma.onEntityCollide(player);
        verify(player, times(0)).attack(any());
    }

    @Test
    void onEntityCollide_Player_Creative() {
        player.setGamemode(Player.CREATIVE);
        magma.onEntityCollide(player);
        verify(player, times(0)).attack(any());
    }

    @Test
    void onEntityCollide_Player_Spectator() {
        player.setGamemode(Player.SPECTATOR);
        magma.onEntityCollide(player);
        verify(player, times(0)).attack(any());
    }

    @Test
    void onEntityCollide_Player_Sneaking() {
        player.setSneaking(true);
        magma.onEntityCollide(player);
        verify(player, times(0)).attack(any());
    }
}
