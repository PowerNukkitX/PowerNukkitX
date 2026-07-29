package org.powernukkitx.level.raid;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.BossBarColor;
import org.powernukkitx.utils.DummyBossBar;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.util.*;

public class Raid {

    public enum RaidState {
        ONGOING,
        VICTORY,
        DEFEAT,
        ENDED
    }

    @SuppressWarnings("unchecked")
    private static final Map<String, Integer>[][] WAVES = new Map[3][];

    static {
        WAVES[0] = new Map[]{
            Map.of(EntityID.PILLAGER, 3),
            Map.of(EntityID.PILLAGER, 3, EntityID.VINDICATOR, 1),
            Map.of(EntityID.PILLAGER, 3, EntityID.VINDICATOR, 2, EntityID.WITCH, 1, EntityID.RAVAGER, 1)
        };
        WAVES[1] = new Map[]{
            Map.of(EntityID.PILLAGER, 4, EntityID.VINDICATOR, 1),
            Map.of(EntityID.PILLAGER, 3, EntityID.VINDICATOR, 2),
            Map.of(EntityID.PILLAGER, 3, EntityID.VINDICATOR, 2, EntityID.WITCH, 1),
            Map.of(EntityID.PILLAGER, 4, EntityID.VINDICATOR, 2, EntityID.WITCH, 1, EntityID.RAVAGER, 1),
            Map.of(EntityID.PILLAGER, 2, EntityID.VINDICATOR, 3, EntityID.WITCH, 1, EntityID.EVOCATION_ILLAGER, 2, EntityID.RAVAGER, 1)
        };
        WAVES[2] = new Map[]{
            Map.of(EntityID.PILLAGER, 4, EntityID.VINDICATOR, 1),
            Map.of(EntityID.PILLAGER, 4, EntityID.VINDICATOR, 2),
            Map.of(EntityID.PILLAGER, 4, EntityID.VINDICATOR, 2, EntityID.WITCH, 1),
            Map.of(EntityID.PILLAGER, 4, EntityID.VINDICATOR, 2, EntityID.WITCH, 1, EntityID.RAVAGER, 1),
            Map.of(EntityID.PILLAGER, 3, EntityID.VINDICATOR, 2, EntityID.WITCH, 1, EntityID.EVOCATION_ILLAGER, 3),
            Map.of(EntityID.PILLAGER, 4, EntityID.VINDICATOR, 4, EntityID.EVOCATION_ILLAGER, 1, EntityID.WITCH, 1, EntityID.RAVAGER, 1),
            Map.of(EntityID.PILLAGER, 3, EntityID.VINDICATOR, 5, EntityID.EVOCATION_ILLAGER, 2, EntityID.WITCH, 2, EntityID.RAVAGER, 1)
        };
    }

    private static final int WAVE_DELAY        = 300;
    private static final int RAID_EXPIRY_TICKS = 48000;

    private static final double VILLAGE_RADIUS_SQ = 128.0 * 128.0;
    private static final double NOTIFY_RADIUS_SQ  =  96.0 *  96.0;

    private final Level level;
    private final Vector3 center;
    private final int totalWaves;
    private final int difficultyIdx;

    private int currentWave          = 0;
    private int currentWaveTotalMobs = 0;
    private int raidAge              = 0;
    private int waveActiveTick       = 0;
    private RaidState state          = RaidState.ONGOING;
    private final Set<Entity> raidMobs = new HashSet<>();
    private boolean waitingNextWave  = true;
    private int waitTick             = 0;
    private final Random random      = new Random();

    private final Map<String, DummyBossBar> bossBars = new HashMap<>();
    private int bossBarUpdateTick = 0;

    public Raid(Level level, Vector3 center) {
        this.level = level;
        this.center = center.clone();
        int difficulty = Server.getInstance().getDifficulty();
        this.difficultyIdx = (difficulty <= 1) ? 0 : (difficulty == 2) ? 1 : 2;
        this.totalWaves    = WAVES[difficultyIdx].length;
        level.addLevelSoundEvent(this.center, SoundEvent.BELL, -1);
        for (Player p : level.getPlayers().values()) {
            if (p.isOnline() && p.distanceSquared(center) <= NOTIFY_RADIUS_SQ) {
                p.playOmenScreenAnimation();
            }
        }
    }

    public void tick(int currentTick) {
        if (state != RaidState.ONGOING) return;

        raidAge++;

        if (raidAge >= RAID_EXPIRY_TICKS) {
            onExpiry();
            return;
        }

        if (raidAge % 100 == 0 && !hasVillagerNearby()) {
            onDefeat();
            return;
        }

        boolean updateBar = (++bossBarUpdateTick >= 2);
        if (updateBar) bossBarUpdateTick = 0;

        if (waitingNextWave) {
            waitTick++;
            if (updateBar) {
                float pct = Math.min(100f, waitTick * 100f / WAVE_DELAY);
                updateBossBars(pct, "%raid.name");
            }
            if (waitTick >= WAVE_DELAY) {
                if (currentWave >= totalWaves) {
                    state = RaidState.VICTORY;
                    onVictory();
                } else {
                    spawnNextWave();
                }
            }
            return;
        }

        if (raidAge % 5 == 0) {
            raidMobs.removeIf(e -> !e.isAlive() || e.closed);
        }

        waveActiveTick++;

        if (updateBar) {
            int alive = (int) raidMobs.stream().filter(e -> e.isAlive() && !e.closed).count();
            float pct = currentWaveTotalMobs > 0 ? Math.max(0f, alive * 100f / currentWaveTotalMobs) : 0f;
            String barTitle = (alive > 0 && alive <= 3)
                    ? "%raid.name - %raid.progress " + alive
                    : "%raid.name";
            updateBossBars(pct, barTitle);
        }

        if (raidMobs.isEmpty()) {
            if (currentWave >= totalWaves) {
                state = RaidState.VICTORY;
                onVictory();
            } else {
                waitingNextWave = true;
                waitTick = 0;
            }
        }
    }

    private void spawnNextWave() {
        Map<String, Integer> wave = WAVES[difficultyIdx][currentWave];
        currentWave++;
        waitingNextWave      = false;
        waitTick             = 0;
        waveActiveTick       = 0;
        currentWaveTotalMobs = 0;

        double angle  = random.nextDouble() * 2 * Math.PI;
        double dist   = 16 + random.nextDouble() * 12;
        double groupX = center.x + Math.cos(angle) * dist;
        double groupZ = center.z + Math.sin(angle) * dist;

        for (Map.Entry<String, Integer> entry : wave.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                spawnMob(entry.getKey(), groupX, groupZ);
            }
        }

        playHorn();
    }

    private void spawnMob(String entityId, double groupX, double groupZ) {
        double spawnX = groupX + (random.nextDouble() - 0.5) * 4;
        double spawnZ = groupZ + (random.nextDouble() - 0.5) * 4;
        int cx = ((int) spawnX) >> 4;
        int cz = ((int) spawnZ) >> 4;
        var chunk = level.getChunk(cx, cz, false);
        if (chunk == null) return;
        int safeY = level.getHighestBlockAt((int) spawnX, (int) spawnZ) + 1;
        Vector3 spawnPos = new Vector3(spawnX + 0.5, safeY, spawnZ + 0.5);
        CompoundTag nbt = Entity.getDefaultNBT(spawnPos);
        Entity entity = Registries.ENTITY.provideEntity(entityId, chunk, nbt);
        if (entity != null) {
            entity.getNbt().putBoolean("IsRaider", true);
            entity.despawnable = false;
            entity.spawnToAll();
            if (entity instanceof EntityIntelligent intelligent) {
                intelligent.getMemoryStorage().put(CoreMemoryTypes.STAY_NEARBY, center);
            }
            raidMobs.add(entity);
            currentWaveTotalMobs++;
        }
    }

    private void playHorn() {
        Vector3 hornPos = center;
        if (!raidMobs.isEmpty()) {
            List<Entity> alive = raidMobs.stream().filter(e -> e.isAlive() && !e.closed).toList();
            if (!alive.isEmpty()) {
                hornPos = alive.get(random.nextInt(alive.size()));
            }
        }
        level.addLevelSoundEvent(hornPos, SoundEvent.RAID_HORN, -1);
    }

    private boolean hasVillagerNearby() {
        for (Entity e : level.getEntities()) {
            if (e instanceof EntityVillagerV2 && e.isAlive() && e.distanceSquared(center) <= VILLAGE_RADIUS_SQ) {
                return true;
            }
        }
        return false;
    }

    private void updateBossBars(float percent, String title) {
        Set<String> nearbyNames = new HashSet<>();
        for (Player p : level.getPlayers().values()) {
            if (!p.isOnline()) continue;
            if (p.distanceSquared(center) > NOTIFY_RADIUS_SQ) continue;
            nearbyNames.add(p.getName());

            if (!p.isAlive()) {
                DummyBossBar existing = bossBars.remove(p.getName());
                if (existing != null) existing.destroy();
                continue;
            }

            DummyBossBar bar = bossBars.get(p.getName());
            if (bar == null) {
                bar = new DummyBossBar.Builder(p)
                        .text(title)
                        .color(BossBarColor.RED)
                        .length(percent)
                        .build();
                bar.create();
                bossBars.put(p.getName(), bar);
            } else {
                bar.setText(title);
                bar.setLength(percent);
                bar.updateBossEntityPosition();
            }
        }

        bossBars.entrySet().removeIf(entry -> {
            if (!nearbyNames.contains(entry.getKey())) {
                entry.getValue().destroy();
                return true;
            }
            return false;
        });
    }

    private void destroyAllBossBars() {
        bossBars.values().forEach(DummyBossBar::destroy);
        bossBars.clear();
    }

    private void onVictory() {
        state = RaidState.ENDED;
        destroyAllBossBars();
        Effect hero = Effect.get(EffectType.VILLAGE_HERO);
        hero.setDuration(48000);
        hero.setAmplifier(0);
        for (Player p : level.getPlayers().values()) {
            if (p.distanceSquared(center) <= NOTIFY_RADIUS_SQ) {
                p.addEffect(hero);
                p.sendTranslatedTitle("raid.victory", 20, 60, 20);
            }
        }
        raidMobs.forEach(e -> { if (e.isAlive()) e.kill(); });
        raidMobs.clear();
    }

    private void onDefeat() {
        state = RaidState.ENDED;
        destroyAllBossBars();
        for (Player p : level.getPlayers().values()) {
            if (p.distanceSquared(center) <= NOTIFY_RADIUS_SQ) {
                p.sendTranslatedTitle("raid.defeat", 20, 60, 20);
            }
        }
        for (Entity mob : raidMobs) {
            if (mob.isAlive() && !mob.closed) {
                level.addLevelSoundEvent(mob, SoundEvent.CELEBRATE, -1, mob.getIdentifier(), false, false);
                mob.despawnable = true;
            }
        }
        raidMobs.clear();
    }

    private void onExpiry() {
        state = RaidState.ENDED;
        destroyAllBossBars();
        for (Player p : level.getPlayers().values()) {
            if (p.distanceSquared(center) <= NOTIFY_RADIUS_SQ) {
                p.sendTranslatedTitle("raid.expiry", 10, 40, 10);
            }
        }
        raidMobs.forEach(e -> { if (e.isAlive()) e.despawnable = true; });
        raidMobs.clear();
    }

    public boolean isEnded() { return state == RaidState.ENDED; }

    public RaidState getState() { return state; }

    public Vector3 getCenter() { return center.clone(); }
}
