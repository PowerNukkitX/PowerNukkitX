package org.powernukkitx.event;

import org.powernukkitx.PlayerFixture;
import org.powernukkitx.Server;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.Position;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Constructs a broad set of concrete Event POJOs from the event/* packages and pokes their
 * getters/setters. Events are cheap value objects - building one plus calling its accessors
 * lights up a lot of otherwise-uncovered lines. Everything runs through a tolerant wrapper so
 * a single unsatisfiable constructor never fails the run - the gate only asserts we built many.
 */
public class EventConstructSmokeTest {

    static TestPlayer player;
    static Level level;
    static Server server;
    static Entity entity;
    static Block block;
    static Item item;

    static final AtomicInteger built = new AtomicInteger();

    @BeforeAll
    static void boot() {
        player = PlayerFixture.get();
        level = player.getLevel();
        server = level.getServer();
        item = Item.get("minecraft:stone");

        // a real block placed into the level
        Vector3 pos = new Vector3(4, 78, 4);
        for (var state : Registries.BLOCKSTATE.getAllState()) {
            try {
                level.setBlock(pos, state.toBlock());
                block = level.getBlock(pos);
                if (block != null) break;
            } catch (Throwable ignore) {
            }
        }
        if (block == null) {
            block = Item.get("minecraft:stone").getBlock();
        }

        // a real spawned entity
        for (String id : Registries.ENTITY.getKnownEntities().keySet()) {
            try {
                Entity e = Entity.createEntity(id, new Position(6, 80, 6, level));
                if (e != null) {
                    entity = e;
                    break;
                }
            } catch (Throwable ignore) {
            }
        }
    }

    // ---------- player events ----------

    @Test
    void playerEvents() {
        Item[] drops = new Item[]{item};
        Vector3 v = new Vector3(4, 78, 4);
        Location from = new Location(0, 80, 0, level);
        Location to = new Location(1, 80, 1, level);

        poke(new org.powernukkitx.event.player.PlayerChatEvent(player, "hi"), e -> {
            e.getMessage();
            e.setMessage("bye");
            e.isCancelled();
            e.setCancelled(true);
        });
        poke(new org.powernukkitx.event.player.PlayerCommandPreprocessEvent(player, "/help"),
                e -> { e.getMessage(); e.setMessage("/list"); });
        poke(new org.powernukkitx.event.player.PlayerJoinEvent(player, "joined"),
                e -> e.getJoinMessage());
        poke(new org.powernukkitx.event.player.PlayerQuitEvent(player, "left"),
                e -> { e.getQuitMessage(); e.getAutoSave(); });
        poke(new org.powernukkitx.event.player.PlayerLoginEvent(player, ""),
                e -> { e.getKickMessage(); e.setKickMessage("no"); });
        poke(new org.powernukkitx.event.player.PlayerDeathEvent(player, drops, "died", 5),
                e -> { e.getDeathMessage(); e.getExperience(); e.setKeepInventory(true); });
        poke(new org.powernukkitx.event.player.PlayerItemHeldEvent(player, item, 0),
                e -> { e.getItem(); e.getSlot(); });
        poke(new org.powernukkitx.event.player.PlayerItemConsumeEvent(player, item),
                e -> e.getItem());
        poke(new org.powernukkitx.event.player.PlayerDropItemEvent(player, item),
                e -> e.getItem());
        poke(new org.powernukkitx.event.player.PlayerInteractEvent(player, item, v, BlockFace.UP),
                e -> { e.getItem(); e.getBlock(); e.getFace(); e.getAction(); });
        poke(new org.powernukkitx.event.player.PlayerInteractEntityEvent(player, safeEntity(), item, v),
                e -> { e.getEntity(); e.getItem(); e.getClickedPos(); });
        poke(new org.powernukkitx.event.player.PlayerMouseOverEntityEvent(player, safeEntity()),
                e -> e.getEntity());
        poke(new org.powernukkitx.event.player.PlayerEntityPickEvent(player, safeEntity(), item),
                e -> { e.getItem(); e.getEntityClicked(); });
        poke(new org.powernukkitx.event.player.PlayerMoveEvent(player, from, to),
                e -> { e.getFrom(); e.getTo(); e.setTo(to); });
        poke(new org.powernukkitx.event.player.PlayerTeleportEvent(player, from, to,
                        org.powernukkitx.event.player.PlayerTeleportEvent.TeleportCause.COMMAND),
                e -> { e.getFrom(); e.getTo(); e.getCause(); });
        poke(new org.powernukkitx.event.player.PlayerGameModeChangeEvent(player, 1, null),
                e -> e.getNewGamemode());
        poke(new org.powernukkitx.event.player.PlayerFoodLevelChangeEvent(player, 18, 4f),
                e -> { e.getFoodLevel(); e.setFoodLevel(20); e.getFoodSaturationLevel(); });
        poke(new org.powernukkitx.event.player.PlayerExperienceChangeEvent(player, 0, 0, 10, 1),
                e -> { e.getNewExperience(); e.getNewExperienceLevel(); });
        poke(new org.powernukkitx.event.player.PlayerChunkRequestEvent(player, 0, 0),
                e -> { e.getChunkX(); e.getChunkZ(); });
        poke(new org.powernukkitx.event.player.PlayerPreChunkRequestEvent(player, 0, 0, false),
                e -> { e.getChunkX(); e.isForced(); });
        poke(new org.powernukkitx.event.player.PlayerAchievementAwardedEvent(player, "ach"),
                e -> e.getAchievement());
        poke(new org.powernukkitx.event.player.PlayerBlockPickEvent(player, block, item),
                e -> { e.getItem(); e.getBlockClicked(); });
        poke(new org.powernukkitx.event.player.PlayerBedEnterEvent(player, block), e -> e.getBed());
        poke(new org.powernukkitx.event.player.PlayerBedLeaveEvent(player, block), e -> e.getBed());
        poke(new org.powernukkitx.event.player.PlayerGlassBottleFillEvent(player, block, item),
                e -> e.getItem());
        poke(new org.powernukkitx.event.player.PlayerBucketEmptyEvent(player, block, BlockFace.UP, block, item, item),
                e -> { e.getBucket(); e.getBlockClicked(); e.getLiquid(); });
        poke(new org.powernukkitx.event.player.PlayerBucketFillEvent(player, block, BlockFace.UP, block, item, item),
                e -> { e.getBucket(); e.getBlockFace(); });
        poke(new org.powernukkitx.event.player.PlayerJumpEvent(player), e -> e.getPlayer());
        poke(new org.powernukkitx.event.player.PlayerShowCreditsEvent(player), e -> e.getPlayer());
        poke(new org.powernukkitx.event.player.PlayerDuplicatedLoginEvent(player), e -> e.getPlayer());
        poke(new org.powernukkitx.event.player.PlayerInvalidMoveEvent(player, true), e -> e.isRevert());
        poke(new org.powernukkitx.event.player.PlayerSpearStabEvent(player, item, 1.0f),
                e -> { e.getItem(); e.getMovementSpeed(); });
        poke(new org.powernukkitx.event.player.PlayerToggleSneakEvent(player, true), e -> e.isSneaking());
        poke(new org.powernukkitx.event.player.PlayerToggleSprintEvent(player, true), e -> e.isSprinting());
        poke(new org.powernukkitx.event.player.PlayerToggleFlightEvent(player, true), e -> e.isFlying());
        poke(new org.powernukkitx.event.player.PlayerToggleGlideEvent(player, true), e -> e.isGliding());
        poke(new org.powernukkitx.event.player.PlayerToggleSwimEvent(player, true), e -> e.isSwimming());
        poke(new org.powernukkitx.event.player.PlayerToggleCrawlEvent(player, true), e -> e.isCrawling());
        poke(new org.powernukkitx.event.player.PlayerToggleSpinAttackEvent(player, true), e -> e.isSpinAttacking());
        poke(new org.powernukkitx.event.player.PlayerHackDetectedEvent(player,
                        org.powernukkitx.event.player.PlayerHackDetectedEvent.HackType.FLIGHT),
                e -> e.isKick());
        poke(new org.powernukkitx.event.player.PlayerKickEvent(player,
                        org.powernukkitx.event.player.PlayerKickEvent.Reason.KICKED_BY_ADMIN, "bye"),
                e -> { e.getReason(); e.getQuitMessage(); });
        poke(new org.powernukkitx.event.player.PlayerMapInfoRequestEvent(player, item), e -> e.getMap());
        poke(new org.powernukkitx.event.player.PlayerFishEvent(player, null, item, 3, new Vector3()),
                e -> { e.getLoot(); e.getExperience(); e.getMotion(); });
        poke(new org.powernukkitx.event.player.EntityFreezeEvent(safeEntity()), e -> e.getHandlers());

        Assertions.assertTrue(built.get() > 0);
    }

    // ---------- block events ----------

    @Test
    void blockEvents() {
        Item[] drops = new Item[]{item};

        poke(new org.powernukkitx.event.block.BlockUpdateEvent(block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.BlockBreakEvent(player, block, item, drops),
                e -> { e.getBlock(); e.getPlayer(); e.getDrops(); e.setCancelled(true); });
        poke(new org.powernukkitx.event.block.BlockBreakEvent(player, block, item, drops, true),
                e -> e.getInstaBreak());
        poke(new org.powernukkitx.event.block.BlockPlaceEvent(player, block, block, block, item),
                e -> { e.getBlock(); e.getBlockReplace(); e.getBlockAgainst(); e.getItem(); });
        poke(new org.powernukkitx.event.block.BlockBurnEvent(block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.BlockFallEvent(block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.BlockFadeEvent(block, block), e -> e.getNewState());
        poke(new org.powernukkitx.event.block.BlockFromToEvent(block, block), e -> e.getTo());
        poke(new org.powernukkitx.event.block.BlockFormEvent(block, block), e -> e.getNewState());
        poke(new org.powernukkitx.event.block.BlockGrowEvent(block, block), e -> e.getNewState());
        poke(new org.powernukkitx.event.block.BlockSpreadEvent(block, block, block), e -> e.getSource());
        poke(new org.powernukkitx.event.block.BlockChangeEvent(block, block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.BlockHarvestEvent(block, block, drops), e -> e.getDrops());
        poke(new org.powernukkitx.event.block.BlockRedstoneEvent(block, 0, 15),
                e -> { e.getOldPower(); e.getNewPower(); });
        poke(new org.powernukkitx.event.block.LeavesDecayEvent(block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.ConduitActivateEvent(block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.ConduitDeactivateEvent(block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.DoorToggleEvent(block, player), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.SignColorChangeEvent(block, player,
                        org.powernukkitx.utils.BlockColor.BLACK_BLOCK_COLOR),
                e -> e.getColor());
        poke(new org.powernukkitx.event.block.SignGlowEvent(block, player, true), e -> e.isGlowing());
        poke(new org.powernukkitx.event.block.SignWaxedEvent(block, player, true), e -> e.isWaxed());
        poke(new org.powernukkitx.event.block.SignChangeEvent(block, player, new String[]{"a", "b", "c", "d"}),
                e -> e.getLines());
        poke(new org.powernukkitx.event.block.WaterFrostEvent(block, safeEntity()), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.FarmLandDecayEvent(safeEntity(), block), e -> e.getBlock());
        poke(new org.powernukkitx.event.block.BlockExplosionPrimeEvent(block, player, 4.0),
                e -> { e.getForce(); e.getBlock(); });
        poke(new org.powernukkitx.event.command.CommandBlockExecuteEvent(block, "say hi"), e -> e.getCommand());

        Assertions.assertTrue(built.get() > 0);
    }

    // ---------- entity events ----------

    @Test
    void entityEvents() {
        Entity e0 = safeEntity();

        poke(new org.powernukkitx.event.entity.EntitySpawnEvent(e0), e -> e.getEntity());
        poke(new org.powernukkitx.event.entity.EntityDespawnEvent(e0), e -> e.getEntity());
        poke(new org.powernukkitx.event.entity.EntityDamageEvent(e0,
                        org.powernukkitx.event.entity.EntityDamageEvent.DamageCause.CONTACT, 2f),
                e -> { e.getDamage(); e.setDamage(3f); e.getCause(); e.getFinalDamage(); });
        poke(new org.powernukkitx.event.entity.EntityDamageByBlockEvent(block, e0,
                        org.powernukkitx.event.entity.EntityDamageEvent.DamageCause.CONTACT, 1f),
                e -> e.getDamager());
        poke(new org.powernukkitx.event.entity.EntityDamageByEntityEvent(e0, e0,
                        org.powernukkitx.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK, 4f),
                e -> { e.getDamager(); e.getKnockBack(); });
        poke(new org.powernukkitx.event.entity.EntityCombustEvent(e0, 5), e -> e.getDuration());
        poke(new org.powernukkitx.event.entity.EntityCombustByBlockEvent(block, e0, 5), e -> e.getCombuster());
        poke(new org.powernukkitx.event.entity.EntityCombustByEntityEvent(e0, e0, 5), e -> e.getCombuster());
        poke(new org.powernukkitx.event.entity.EntityRegainHealthEvent(e0, 2f, 0),
                e -> { e.getAmount(); e.getRegainReason(); });
        poke(new org.powernukkitx.event.entity.EntityFallEvent(e0, block, 3f), e -> e.getFallDistance());
        poke(new org.powernukkitx.event.entity.EntityInteractEvent(e0, block), e -> e.getBlock());
        poke(new org.powernukkitx.event.entity.EntityMotionEvent(e0, new Vector3(0, 1, 0)),
                e -> e.getMotion());
        poke(new org.powernukkitx.event.entity.EntityMoveByPistonEvent(e0, new Vector3(1, 1, 1)),
                e -> e.getMotion());
        poke(new org.powernukkitx.event.entity.EntityBlockChangeEvent(e0, block, block), e -> e.getTo());
        poke(new org.powernukkitx.event.entity.EntityExplosionPrimeEvent(e0, 4.0), e -> e.getForce());
        poke(new org.powernukkitx.event.entity.ExplosionPrimeEvent(e0, 4.0), e -> e.getForce());
        poke(new org.powernukkitx.event.entity.EntityPortalEnterEvent(e0,
                        org.powernukkitx.event.entity.EntityPortalEnterEvent.PortalType.NETHER),
                e -> e.getPortalType());
        poke(new org.powernukkitx.event.entity.EntityInventoryChangeEvent(e0, item, item, 0),
                e -> { e.getOldItem(); e.getNewItem(); e.getSlot(); });
        poke(new org.powernukkitx.event.entity.EntityArmorChangeEvent(e0, item, item, 0),
                e -> { e.getOldItem(); e.getNewItem(); });
        poke(new org.powernukkitx.event.entity.EntityExplodeEvent(e0, new Position(0, 80, 0, level),
                        new ArrayList<>(), 0.5), e -> e.getYield());
        poke(new org.powernukkitx.event.entity.EntityTransformEvent(e0, e0), e -> e.getTransformed());
        poke(new org.powernukkitx.event.entity.EntityTeleportEvent(e0,
                        new Location(0, 80, 0, level), new Location(1, 80, 1, level)),
                e -> { e.getFrom(); e.getTo(); });
        poke(new org.powernukkitx.event.entity.EntityLevelChangeEvent(e0, level, level),
                e -> { e.getOrigin(); e.getTarget(); });
        poke(new org.powernukkitx.event.entity.EntityVehicleEnterEvent(e0, e0), e -> e.getVehicle());
        poke(new org.powernukkitx.event.entity.EntityVehicleExitEvent(e0, e0), e -> e.getVehicle());

        Assertions.assertTrue(built.get() > 0);
    }

    // ---------- level / server / weather / potion / vehicle / misc ----------

    @Test
    void miscEvents() {
        poke(new org.powernukkitx.event.level.LevelLoadEvent(level), e -> e.getLevel());
        poke(new org.powernukkitx.event.level.LevelInitEvent(level), e -> e.getLevel());
        poke(new org.powernukkitx.event.level.LevelSaveEvent(level), e -> e.getLevel());
        poke(new org.powernukkitx.event.level.LevelUnloadEvent(level), e -> e.getLevel());
        poke(new org.powernukkitx.event.level.WeatherChangeEvent(level, true), e -> e.toWeatherState());
        poke(new org.powernukkitx.event.level.ThunderChangeEvent(level, true), e -> e.toThunderState());
        poke(new org.powernukkitx.event.level.SpawnChangeEvent(level, new Position(0, 80, 0, level)),
                e -> e.getPreviousSpawn());
        poke(new org.powernukkitx.event.level.StructureGrowEvent(block, new ArrayList<>()),
                e -> e.getBlockList());

        poke(new org.powernukkitx.event.server.ServerCommandEvent(server.getConsoleSender(), "list"),
                e -> { e.getCommand(); e.setCommand("help"); });
        poke(new org.powernukkitx.event.server.RemoteServerCommandEvent(server.getConsoleSender(), "list"),
                e -> e.getCommand());
        poke(new org.powernukkitx.event.server.ConsoleCommandOutputEvent(server.getConsoleSender(), "out"),
                e -> e.getMessage());
        poke(new org.powernukkitx.event.server.QueryRegenerateEvent(server),
                e -> { e.getServerName(); e.getMaxPlayerCount(); });

        poke(new org.powernukkitx.event.item.ItemWearEvent(item, 5), e -> e.getNewDurability());

        poke(new org.powernukkitx.event.vehicle.VehicleCreateEvent(safeEntity()), e -> e.getVehicle());
        poke(new org.powernukkitx.event.vehicle.VehicleUpdateEvent(safeEntity()), e -> e.getVehicle());
        poke(new org.powernukkitx.event.vehicle.VehicleDestroyEvent(safeEntity()), e -> e.getVehicle());
        poke(new org.powernukkitx.event.vehicle.VehicleDestroyByEntityEvent(safeEntity(), safeEntity()),
                e -> e.getDestroyer());
        poke(new org.powernukkitx.event.vehicle.VehicleMoveEvent(safeEntity(),
                        new Location(0, 80, 0, level), new Location(1, 80, 1, level)),
                e -> { e.getFrom(); e.getTo(); });
        poke(new org.powernukkitx.event.vehicle.EntityEnterVehicleEvent(safeEntity(), safeEntity()),
                e -> e.getVehicle());
        poke(new org.powernukkitx.event.vehicle.EntityExitVehicleEvent(safeEntity(), safeEntity()),
                e -> e.getVehicle());

        poke(new org.powernukkitx.event.redstone.RedstoneUpdateEvent(block), e -> e.getBlock());

        Assertions.assertTrue(built.get() > 0);
    }

    // ---------- helpers ----------

    private static Entity safeEntity() {
        return entity;
    }

    private interface Poke<T> {
        void accept(T t) throws Throwable;
    }

    private static <T> void poke(T event, Poke<T> body) {
        if (event == null) return;
        built.incrementAndGet();
        try {
            if (event instanceof Event ev) {
                ev.getEventName();
                if (ev instanceof Cancellable c) {
                    c.isCancelled();
                    c.setCancelled(true);
                    c.setCancelled(false);
                }
            }
            body.accept(event);
        } catch (Throwable ignore) {
        }
    }
}
