package org.powernukkitx.level.raid;

import org.powernukkitx.Player;
import org.powernukkitx.command.utils.RawText;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.village.Village;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;

import java.util.*;

public class RaidManager {

    private static final int CHECK_INTERVAL     = 20;
    private static final int RAID_OMEN_DURATION = 500;
    private static final int RAID_COOLDOWN_TICKS = 24000;

    private record OmenEntry(UUID villageUuid, int expiryTick) {}

    private final Level level;
    private final Map<UUID, Raid> activeRaids = new HashMap<>();
    private final Map<String, OmenEntry> omenTracked = new HashMap<>();
    private final Map<UUID, Integer> villageCooldowns = new HashMap<>();

    public RaidManager(Level level) {
        this.level = level;
    }

    public void onTick(int currentTick) {
        activeRaids.entrySet().removeIf(entry -> {
            if (entry.getValue().isEnded()) {
                villageCooldowns.put(entry.getKey(), currentTick + RAID_COOLDOWN_TICKS);
                return true;
            }
            return false;
        });
        villageCooldowns.entrySet().removeIf(entry -> currentTick >= entry.getValue());

        for (Raid raid : activeRaids.values()) {
            raid.tick(currentTick);
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
                if (player.hasEffect(EffectType.BAD_OMEN)) {
                    Village village = level.getVillageManager().getVillageAt(player.asBlockVector3()).orElse(null);
                    if (village != null && village.isValid() && isEligible(village.uuid())) {
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
                        omenTracked.put(name, new OmenEntry(village.uuid(), currentTick + RAID_OMEN_DURATION));
                    }
                }
            } else if (currentTick >= entry.expiryTick()) {
                omenTracked.remove(name);
                player.removeEffect(EffectType.BAD_OMEN);
                if (player.isAlive() && isEligible(entry.villageUuid())) {
                    Village village = level.getVillageManager().getVillage(entry.villageUuid());
                    if (village != null && village.isValid()) {
                        startRaid(village);
                    }
                }
            }
        }
    }

    private boolean isEligible(UUID villageUuid) {
        return !activeRaids.containsKey(villageUuid) && !villageCooldowns.containsKey(villageUuid);
    }

    public Raid startRaid(Village village) {
        Raid raid = new Raid(level, village);
        activeRaids.put(village.uuid(), raid);
        return raid;
    }

    public Raid getActiveRaid(UUID villageUuid) {
        return activeRaids.get(villageUuid);
    }

    public Collection<Raid> getActiveRaids() {
        return Collections.unmodifiableCollection(activeRaids.values());
    }
}
