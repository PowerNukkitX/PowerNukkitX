package org.powernukkitx.entity.ai.behaviorgroup;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.behavior.BehaviorState;
import org.powernukkitx.entity.ai.behavior.IBehavior;
import org.powernukkitx.entity.ai.memory.MemoryType;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * Renders the live state of a behavior group into the name tag of its entity, for the
 * {@link EntityAI.DebugOption#BEHAVIOR} and {@link EntityAI.DebugOption#MEMORY} debug options.
 */
public class BehaviorDebugRenderer {

    private static final String ACTIVE_COLOR = "§b";
    private static final String INACTIVE_COLOR = "§7";

    private final IBehaviorGroup behaviorGroup;

    public BehaviorDebugRenderer(IBehaviorGroup behaviorGroup) {
        this.behaviorGroup = behaviorGroup;
    }

    public void render(EntityIntelligent entity) {
        var builder = new StringBuilder();
        if (EntityAI.checkDebugOption(EntityAI.DebugOption.MEMORY)) {
            appendMemories(builder, behaviorGroup.getMemoryStorage().getAll());
        }
        if (EntityAI.checkDebugOption(EntityAI.DebugOption.BEHAVIOR)) {
            appendBehaviors(builder, behaviorGroup.getCoreBehaviors());
            appendBehaviors(builder, behaviorGroup.getBehaviors());
        }
        entity.setNameTag(builder.toString());
        entity.setNameTagAlwaysVisible(true);
    }

    private void appendMemories(StringBuilder builder, Map<MemoryType<?>, ?> memories) {
        memories.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<MemoryType<?>, ?> entry) -> entry.getKey().getIdentifier().getPath()).reversed())
                .forEach(memory -> builder.append("§e").append(memory.getKey().getIdentifier().getPath())
                        .append("=")
                        .append(INACTIVE_COLOR).append(memory.getValue())
                        .append("\n"));
        builder.append("\n\n");
    }

    private void appendBehaviors(StringBuilder builder, Collection<IBehavior> behaviors) {
        if (behaviors == null || behaviors.isEmpty()) return;
        behaviors.stream()
                .sorted(Comparator.comparingInt(IBehavior::getPriority).reversed())
                .forEach(behavior -> builder.append(behavior.getBehaviorState() == BehaviorState.ACTIVE ? ACTIVE_COLOR : INACTIVE_COLOR)
                        .append(behavior)
                        .append("\n"));
        builder.append("\n\n");
    }
}
