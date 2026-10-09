package org.powernukkitx.level.vibration;

import org.powernukkitx.Server;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.level.VibrationArriveEvent;
import org.powernukkitx.event.level.VibrationOccurEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.math.VectorMath;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.tags.BlockTags;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventGenericPacket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class SimpleVibrationManager implements VibrationManager {
    protected final Set<VibrationListener> listeners = new CopyOnWriteArraySet<>();
    protected final Set<VibrationListener> activeListeners = ConcurrentHashMap.newKeySet();
    protected final Set<VibrationListener> particleListeners = ConcurrentHashMap.newKeySet();
    protected final ConcurrentHashMap<VibrationListener, Integer> selectorTicks = new ConcurrentHashMap<>();
    protected final Level level;

    public SimpleVibrationManager(Level level) {
        this.level = level;
    }

    @Override
    public void callVibrationEvent(VibrationEvent event) {
        int currentTick = level.getTick();

        if (event.initiator() instanceof Entity e && e.getDataFlag(ActorFlags.SILENT)) {
            return;
        }

        VibrationOccurEvent vibrationOccurPluginEvent = new VibrationOccurEvent(event);
        this.level.getServer().getPluginManager().callEvent(vibrationOccurPluginEvent);
        if (vibrationOccurPluginEvent.isCancelled()) {
            return;
        }

        for (var listener : listeners) {
            Vector3 listenerPos = listener.getListenerVector();
            double distanceSquared = listenerPos.distanceSquared(event.source());

            double range = listener.getListenRange();
            if (listenerPos.equals(event.source())) continue;
            if (distanceSquared > range * range) continue;
            if (listener.canReceiveOnlyIfAdjacentChunksAreTicking() && !areAdjacentChunksTicking(listenerPos)) continue;
            if (!canVibrationArrive(level, event.source(), listenerPos)) continue;
            if (isBusy(listener, currentTick)) continue;
            if (!listener.onVibrationOccur(event)) continue;

            addCandidate(listener, event, (float) Math.sqrt(distanceSquared), currentTick);
        }
    }

    @Override
    public void addListener(VibrationListener listener) {
        this.listeners.add(listener);
        if (VibrationListenerStorage.hasScheduledVibration(getOwnerNbt(listener))) {
            this.activeListeners.add(listener);
        }
    }

    @Override
    public void removeListener(VibrationListener listener) {
        this.listeners.remove(listener);
        this.activeListeners.remove(listener);
        this.particleListeners.remove(listener);
        this.selectorTicks.remove(listener);
    }

    @Override
    public void tick() {
        int currentTick = level.getTick();
        for (var listener : activeListeners) {
            tickListener(listener, currentTick);
        }
    }

    protected boolean isBusy(VibrationListener listener, int currentTick) {
        CompoundTag data = getListenerData(listener);
        if (data.containsCompound(VibrationListenerStorage.TAG_PENDING)) return true;

        CompoundTag selector = data.getCompound(VibrationListenerStorage.TAG_SELECTOR);
        if (!selector.containsCompound(VibrationListenerStorage.TAG_SELECTOR_CONTEXT)) return false;

        Integer selectorTick = selectorTicks.get(listener);
        return selectorTick == null || selectorTick != currentTick;
    }

    protected void addCandidate(VibrationListener listener, VibrationEvent event, float distance, int currentTick) {
        CompoundTag data = getListenerData(listener);
        CompoundTag selector = data.getCompound(VibrationListenerStorage.TAG_SELECTOR);

        if (selector.containsCompound(VibrationListenerStorage.TAG_SELECTOR_CONTEXT) && Integer.valueOf(currentTick).equals(selectorTicks.get(listener))) {
            CompoundTag current = selector.getCompound(VibrationListenerStorage.TAG_SELECTOR_CONTEXT);
            int distanceCompare = Float.compare(distance, current.getFloat(VibrationListenerStorage.TAG_PENDING_DISTANCE));
            int currentFrequency = VibrationType.fromEventId(current.getInt(VibrationListenerStorage.TAG_PENDING_VIBRATION)).frequency;
            if (distanceCompare > 0 || (distanceCompare == 0 && event.type().frequency <= currentFrequency)) return;
        }

        selector.putInt(VibrationListenerStorage.TAG_SELECTOR_TICK, currentTick)
                .putCompound(VibrationListenerStorage.TAG_SELECTOR_CONTEXT, createContext(event, distance));
        selectorTicks.put(listener, currentTick);
        activeListeners.add(listener);
        markDirty(listener);
    }

    protected void tickListener(VibrationListener listener, int currentTick) {
        CompoundTag data = getListenerData(listener);
        CompoundTag selector = data.getCompound(VibrationListenerStorage.TAG_SELECTOR);
        boolean started = false;

        if (!data.containsCompound(VibrationListenerStorage.TAG_PENDING)) {
            if (!selector.containsCompound(VibrationListenerStorage.TAG_SELECTOR_CONTEXT)) {
                activeListeners.remove(listener);
                selectorTicks.remove(listener);
                return;
            }

            Integer selectorTick = selectorTicks.get(listener);
            if (selectorTick != null && selectorTick == currentTick) return;

            CompoundTag pending = selector.getCompound(VibrationListenerStorage.TAG_SELECTOR_CONTEXT);
            selector.remove(VibrationListenerStorage.TAG_SELECTOR_TICK, VibrationListenerStorage.TAG_SELECTOR_CONTEXT);
            selectorTicks.remove(listener);

            data.putInt(VibrationListenerStorage.TAG_EVENT, pending.getInt(VibrationListenerStorage.TAG_PENDING_VIBRATION));
            data.putCompound(VibrationListenerStorage.TAG_PENDING, pending);
            data.putInt(VibrationListenerStorage.TAG_TICKS, (int) pending.getFloat(VibrationListenerStorage.TAG_PENDING_DISTANCE));
            started = true;
        }

        CompoundTag pending = data.getCompound(VibrationListenerStorage.TAG_PENDING);
        int ticks = data.getInt(VibrationListenerStorage.TAG_TICKS);
        VibrationEvent event = restoreEvent(pending);

        if (particleListeners.add(listener)) {
            float timeToLive = started ? pending.getFloat(VibrationListenerStorage.TAG_PENDING_DISTANCE) / 20.0f : ticks / 20.0f;
            createVibration(listener, event, Math.max(0, timeToLive));
        }

        if (ticks > 0) {
            ticks--;
            if (ticks > 0) {
                data.putInt(VibrationListenerStorage.TAG_TICKS, ticks);
                markDirty(listener);
                return;
            }
        }

        data.remove(VibrationListenerStorage.TAG_PENDING, VibrationListenerStorage.TAG_TICKS);
        particleListeners.remove(listener);
        activeListeners.remove(listener);
        markDirty(listener);

        VibrationArriveEvent vibrationArrivePluginEvent = new VibrationArriveEvent(event, listener);
        this.level.getServer().getPluginManager().callEvent(vibrationArrivePluginEvent);
        if (!vibrationArrivePluginEvent.isCancelled()) {
            listener.onVibrationArrive(event);
        }
    }

    protected CompoundTag createContext(VibrationEvent event, float distance) {
        CompoundTag context = new CompoundTag()
                .putFloat(VibrationListenerStorage.TAG_PENDING_DISTANCE, distance)
                .putLong(VibrationListenerStorage.TAG_PENDING_SOURCE, event.sourceUniqueId())
                .putInt(VibrationListenerStorage.TAG_PENDING_VIBRATION, event.type().eventId)
                .putInt(VibrationListenerStorage.TAG_PENDING_X, event.source().getFloorX())
                .putInt(VibrationListenerStorage.TAG_PENDING_Y, event.source().getFloorY())
                .putInt(VibrationListenerStorage.TAG_PENDING_Z, event.source().getFloorZ());
        if (event.projectileOwnerUniqueId() != 0) {
            context.putLong(VibrationListenerStorage.TAG_PENDING_PROJECTILE, event.projectileOwnerUniqueId());
        }
        return context;
    }

    protected VibrationEvent restoreEvent(CompoundTag context) {
        long sourceUniqueId = context.getLong(VibrationListenerStorage.TAG_PENDING_SOURCE);
        long projectileOwnerUniqueId = context.getLong(VibrationListenerStorage.TAG_PENDING_PROJECTILE);
        Entity initiator = sourceUniqueId != 0 ? level.getEntityByUniqueId(sourceUniqueId) : null;
        if (initiator == null && projectileOwnerUniqueId != 0) {
            initiator = level.getEntityByUniqueId(projectileOwnerUniqueId);
        }

        return new VibrationEvent(
                initiator,
                new Vector3(
                        context.getInt(VibrationListenerStorage.TAG_PENDING_X),
                        context.getInt(VibrationListenerStorage.TAG_PENDING_Y),
                        context.getInt(VibrationListenerStorage.TAG_PENDING_Z)
                ),
                VibrationType.fromEventId(context.getInt(VibrationListenerStorage.TAG_PENDING_VIBRATION)),
                sourceUniqueId,
                projectileOwnerUniqueId
        );
    }

    protected CompoundTag getListenerData(VibrationListener listener) {
        return VibrationListenerStorage.getListener(getOwnerNbt(listener));
    }

    protected CompoundTag getOwnerNbt(VibrationListener listener) {
        if (listener instanceof BlockEntity blockEntity) return blockEntity.getNbt();
        if (listener instanceof Entity entity) return entity.getNbt();
        throw new IllegalStateException("Unsupported persistent vibration listener " + listener.getClass().getName());
    }

    protected void markDirty(VibrationListener listener) {
        if (listener instanceof BlockEntity blockEntity) {
            if (blockEntity.chunk != null) blockEntity.chunk.setChanged();
        } else if (listener instanceof Entity entity && entity.chunk != null) {
            entity.chunk.setChanged();
        }
    }

    protected void createVibration(VibrationListener listener, VibrationEvent event, float timeToLive) {
        var listenerPos = listener.getListenerVector().asVector3f();
        var sourcePos = event.source().asVector3f();
        var tag = NbtMap.builder()
                .putCompound("origin", createVec3fTag(sourcePos))
                .putFloat("speed", 20.0f)
                .putCompound("target", listener.isEntity() ? createEntityTargetTag(listener.asEntity()) : createVec3fTag(listenerPos))
                .putFloat("timeToLive", timeToLive)
                .build();
        LevelEventGenericPacket packet = new LevelEventGenericPacket();
        packet.setType(LevelEvent.PARTICLE_VIBRATION_SIGNAL);
        packet.setTag(tag);
        // TODO: Packets are only sent to players within the player's field of view.
        Server.broadcastPacket(level.getPlayers().values(), packet);
    }

    protected NbtMap createVec3fTag(Vector3f vec3f) {
        return NbtMap.builder()
                .putString("type", "vec3")
                .putFloat("x", vec3f.x)
                .putFloat("y", vec3f.y)
                .putFloat("z", vec3f.z)
                .build();
    }

    protected NbtMap createEntityTargetTag(Entity entity) {
        return NbtMap.builder()
                .putString("type", "actor")
                .putLong("uniqueID", entity.runtimeId())
                .putInt("attachPos", 3)
                .build();
    }

    protected boolean areAdjacentChunksTicking(Vector3 pos) {
        int chunkX = pos.getFloorX() >> 4;
        int chunkZ = pos.getFloorZ() >> 4;
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                if (!level.isChunkTicking(x, z)) return false;
            }
        }
        return true;
    }

    protected boolean canVibrationArrive(Level level, Vector3 from, Vector3 to) {
        for (Vector3 vec : VectorMath.getPassByVector3(from, to)) {
            if (level.getTickCachedBlock(vec).hasTag(BlockTags.PNX_WOOL)) {
                return false;
            }
        }
        return true;
    }
}
