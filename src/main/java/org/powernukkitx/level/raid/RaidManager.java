package org.powernukkitx.level.raid;

import org.powernukkitx.Player;
import org.powernukkitx.command.utils.RawText;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.util.*;

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

    public RaidManager(Level level) {
        this.level = level;
    }

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

    public Raid startRaid(Vector3 center) {
        Raid raid = new Raid(level, center);
        activeRaids.add(raid);
        return raid;
    }

    public Raid startRaid(Entity nearVillager) {
        return startRaid(nearVillager.clone());
    }

    public Raid getRaidNear(Vector3 pos) {
        for (Raid raid : activeRaids) {
            if (pos.distanceSquared(raid.getCenter()) <= RAID_MERGE_RADIUS_SQ) {
                return raid;
            }
        }
        return null;
    }

    public List<Raid> getActiveRaids() {
        return Collections.unmodifiableList(activeRaids);
    }
}
