package cn.nukkit.entity.raid;

import cn.nukkit.Player;
import cn.nukkit.command.utils.RawText;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.Effect;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.util.*;

/**
 * Per-level manager for village raids.
 */
public class RaidManager {

    private static final int CHECK_INTERVAL               = 20;
    private static final double VILLAGE_DETECT_RADIUS_SQ  = 96.0 * 96.0;
    private static final double RAID_MERGE_RADIUS_SQ      = 128.0 * 128.0;
    private static final int RAID_OMEN_DURATION           = 500;
    private static final int VILLAGER_CAP_CHECK_INTERVAL  = 6000;
    private static final double VILLAGE_CLUSTER_RADIUS_SQ = 64.0 * 64.0;
    private static final int MAX_VILLAGERS_PER_CLUSTER    = 20;
    private static final int RAID_COOLDOWN_TICKS          = 24000;

    private record OmenEntry(Vector3 villageCenter, int expiryTick) {}
    private record VillageCooldown(Vector3 center, int expiryTick) {}

    private final Level level;
    private final List<Raid> activeRaids = new ArrayList<>();
    private final Map<String, OmenEntry> omenTracked = new HashMap<>();
    private final List<VillageCooldown> villageCooldowns = new ArrayList<>();

    /**
     * Creates the raid manager bound to a single level. One instance exists per loaded level.
     *
     * @param level the level whose raids this manager drives
     */
    public RaidManager(Level level) {
        this.level = level;
    }

    /**
     * Drives every active raid and the omen-to-raid pipeline for this level. Expired raids are moved
     * to a cooldown list, players carrying {@link EffectType#BAD_OMEN} near a village are tracked, and
     * a raid is started once their omen elapses. Heavy scans are throttled by {@code CHECK_INTERVAL}.
     *
     * @param currentTick the current level tick
     */
    public void onTick(int currentTick) {
        activeRaids.removeIf(raid -> {
            if (raid.isEnded()) {
                villageCooldowns.add(new VillageCooldown(raid.getCenter(), currentTick + RAID_COOLDOWN_TICKS));
                return true;
            }
            return false;
        });
        villageCooldowns.removeIf(c -> currentTick >= c.expiryTick());

        for (Raid raid : activeRaids) {
            raid.tick(currentTick);
        }

        if (currentTick % VILLAGER_CAP_CHECK_INTERVAL == 0) {
            enforceVillagerCap();
        }

        if (currentTick % CHECK_INTERVAL != 0) return;

        for (Player player : level.getPlayers().values()) {
            String name = player.getName();

            if (!player.isOnline() || !player.isAlive()) {
                omenTracked.remove(name);
                continue;
            }

            OmenEntry entry = omenTracked.get(name);

            if (entry == null) {
                if (player.hasEffect(EffectType.BAD_OMEN) && !isNearActiveRaid(player)) {
                    EntityVillagerV2 villager = findNearestVillager(player);
                    if (villager != null && !isVillageOnCooldown(villager)) {
                        int amplifier = player.getEffect(EffectType.BAD_OMEN).getAmplifier();
                        player.removeEffect(EffectType.BAD_OMEN);
                        Effect raidOmenAnim = Effect.get(EffectType.RAID_OMEN);
                        raidOmenAnim.setDuration(1);
                        raidOmenAnim.setAmplifier(amplifier);
                        player.addEffect(raidOmenAnim);
                        Effect badOmen = Effect.get(EffectType.BAD_OMEN);
                        badOmen.setDuration(RAID_OMEN_DURATION);
                        badOmen.setAmplifier(amplifier);
                        player.addEffect(badOmen);
                        level.addLevelSoundEvent(player, SoundEvent.APPLY_EFFECT_RAID_OMEN, -1);
                        player.setTitleAnimationTimes(10, 40, 10);
                        player.setRawTextSubTitle(RawText.fromRawText("{\"rawtext\":[{\"translate\":\"subtitles.event.mob_effect.raid_omen\"}]}"));
                        player.setRawTextTitle(RawText.fromRawText("{\"rawtext\":[{\"text\":\" \"}]}"));
                        omenTracked.put(name, new OmenEntry(villager.clone(), currentTick + RAID_OMEN_DURATION));
                    }
                }
            } else if (currentTick >= entry.expiryTick()) {
                omenTracked.remove(name);
                player.removeEffect(EffectType.BAD_OMEN);
                if (player.isAlive() && !isNearActiveRaid(player) && !isVillageOnCooldown(entry.villageCenter())) {
                    EntityVillagerV2 villager = findNearestVillager(player);
                    if (villager != null) {
                        startRaid(entry.villageCenter());
                    }
                }
            }
        }
    }

    private boolean isNearActiveRaid(Player player) {
        for (Raid raid : activeRaids) {
            if (player.distanceSquared(raid.getCenter()) <= RAID_MERGE_RADIUS_SQ) {
                return true;
            }
        }
        return false;
    }

    private boolean isVillageOnCooldown(Vector3 pos) {
        for (VillageCooldown cooldown : villageCooldowns) {
            if (pos.distanceSquared(cooldown.center()) <= RAID_MERGE_RADIUS_SQ) {
                return true;
            }
        }
        return false;
    }

    private EntityVillagerV2 findNearestVillager(Player player) {
        for (Entity entity : level.getEntities()) {
            if (entity instanceof EntityVillagerV2 villager
                    && villager.isAlive()
                    && player.distanceSquared(villager) <= VILLAGE_DETECT_RADIUS_SQ) {
                return villager;
            }
        }
        return null;
    }

    private void enforceVillagerCap() {
        List<EntityVillagerV2> villagers = new ArrayList<>();
        for (Entity e : level.getEntities()) {
            if (e instanceof EntityVillagerV2 v && v.isAlive()) villagers.add(v);
        }
        if (villagers.size() <= MAX_VILLAGERS_PER_CLUSTER) return;

        Set<EntityVillagerV2> assigned = new HashSet<>();
        for (EntityVillagerV2 anchor : villagers) {
            if (assigned.contains(anchor)) continue;
            List<EntityVillagerV2> cluster = new ArrayList<>();
            for (EntityVillagerV2 other : villagers) {
                if (anchor.distanceSquared(other) <= VILLAGE_CLUSTER_RADIUS_SQ) cluster.add(other);
            }
            if (cluster.size() < 3) continue;
            assigned.addAll(cluster);

            if (cluster.size() > MAX_VILLAGERS_PER_CLUSTER) {
                cluster.sort((a, b) -> Integer.compare(b.getAge(), a.getAge()));
                for (int i = MAX_VILLAGERS_PER_CLUSTER; i < cluster.size(); i++) {
                    cluster.get(i).close();
                }
            }
        }
    }

    /**
     * Starts and registers a raid at the given village centre.
     *
     * @param center the village centre the raiders converge on
     * @return the newly started raid
     */
    public Raid startRaid(Vector3 center) {
        Raid raid = new Raid(level, center);
        activeRaids.add(raid);
        return raid;
    }

    /**
     * Starts a raid centred on the position of a nearby villager.
     *
     * @param nearVillager the villager whose position anchors the raid
     * @return the newly started raid
     */
    public Raid startRaid(Entity nearVillager) {
        return startRaid(nearVillager.clone());
    }

    /**
     * Finds an active raid whose centre is within the merge radius of the given position.
     *
     * @param pos the position to test
     * @return the nearby raid, or {@code null} if none is close enough
     */
    public Raid getRaidNear(Vector3 pos) {
        for (Raid raid : activeRaids) {
            if (pos.distanceSquared(raid.getCenter()) <= RAID_MERGE_RADIUS_SQ) {
                return raid;
            }
        }
        return null;
    }

    /**
     * @return an unmodifiable view of the raids currently running in this level.
     */
    public List<Raid> getActiveRaids() {
        return Collections.unmodifiableList(activeRaids);
    }
}
