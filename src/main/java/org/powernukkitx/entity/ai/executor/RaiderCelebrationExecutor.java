package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.level.Sound;
import org.powernukkitx.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

public class RaiderCelebrationExecutor implements IBehaviorExecutor {

    protected final Sound sound;
    protected final int duration;
    protected final int minJumpInterval;
    protected final int jumpIntervalSpread;
    protected final int minSoundInterval;
    protected final int soundIntervalSpread;
    protected int currentTick = 0;
    protected int nextJumpTick = 0;
    protected int nextSoundTick = 0;

    public RaiderCelebrationExecutor(Sound sound, int duration, int minJumpInterval, int jumpIntervalSpread,
                                     int minSoundInterval, int soundIntervalSpread) {
        this.sound = sound;
        this.duration = duration;
        this.minJumpInterval = minJumpInterval;
        this.jumpIntervalSpread = jumpIntervalSpread;
        this.minSoundInterval = minSoundInterval;
        this.soundIntervalSpread = soundIntervalSpread;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (currentTick >= nextSoundTick) {
            entity.level.addSound(entity, sound);
            nextSoundTick = currentTick + minSoundInterval + random.nextInt(soundIntervalSpread + 1);
        }
        if (currentTick >= nextJumpTick && entity.isOnGround() && entity.riding == null) {
            entity.setMotion(new Vector3(0, entity.getJumpingMotion(0.4), 0));
            nextJumpTick = currentTick + minJumpInterval + random.nextInt(jumpIntervalSpread + 1);
        }
        return ++currentTick <= duration;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        currentTick = 0;
        nextJumpTick = 0;
        nextSoundTick = 0;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stopCelebrating(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stopCelebrating(entity);
    }

    protected void stopCelebrating(EntityIntelligent entity) {
        currentTick = 0;
        nextJumpTick = 0;
        nextSoundTick = 0;
        entity.getMemoryStorage().put(CoreMemoryTypes.CELEBRATING, false);
    }
}
