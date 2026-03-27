package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;

/**
 * Legacy "beg / interested" look behavior.
 *
 * <p>This executor only makes the entity look at the nearest feeding player and sets
 * {@link EntityFlag#INTERESTED} (Bedrock's "beg" / "curious stare" visual state).
 * It does <b>not</b> handle movement, tempting logic, item selection, or scared/flee behavior.</p>
 *
 * <p>In Bedrock there is a distinction between:</p>
 * <ul>
 *   <li><b>Beg</b>: look-only behavior that typically sets {@link EntityFlag#INTERESTED}.</li>
 *   <li><b>Tempt</b>: luring behavior that typically sets {@link EntityFlag#TEMPTED} and may include movement, stop distance, scared logic, sounds, etc.</li>
 * </ul>
 *
 * <p>Use {@link BegExecutor} for the beg / interested visual behavior, or use
 * {@link TemptExecutor} (ground) / {@link FloatTemptExecutor} (floating) for proper tempting behavior.</p>
 */
@Deprecated(forRemoval = true, since = "2.0.0")
public class LookAtFeedingPlayerExecutor implements EntityControl, IBehaviorExecutor {
    @Override
    public boolean execute(EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        var vector3 = entity.getMemoryStorage().get(CoreMemoryTypes.NEAREST_FEEDING_PLAYER);
        if (vector3 != null) {
            setLookTarget(entity, vector3);
            entity.setDataFlag(EntityFlag.INTERESTED, true);
            return true;
        } else return false;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(EntityFlag.INTERESTED, false);
        }
        removeLookTarget(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setEnablePitch(false);
        if (entity.getMemoryStorage().isEmpty(CoreMemoryTypes.NEAREST_FEEDING_PLAYER)) {
            entity.setDataFlag(EntityFlag.INTERESTED, false);
        }
        removeLookTarget(entity);
    }
}
