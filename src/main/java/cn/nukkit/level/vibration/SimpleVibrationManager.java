package cn.nukkit.level.vibration;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.level.VibrationArriveEvent;
import cn.nukkit.event.level.VibrationOccurEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.VectorMath;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.tags.BlockTags;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.LevelEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.LevelEventGenericPacket;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SimpleVibrationManager implements VibrationManager {

    protected Set<VibrationListener> listeners = new CopyOnWriteArraySet<>();
    protected Level level;

    public SimpleVibrationManager(Level level) {
        this.level = level;
    }

    @Override
    public void callVibrationEvent(VibrationEvent event) {
        if(event.initiator() instanceof Entity e && e.getDataFlag(ActorFlags.SILENT)) return;

        VibrationOccurEvent vibrationOccurPluginEvent = new VibrationOccurEvent(event);
        this.level.getServer().getPluginManager().callEvent(vibrationOccurPluginEvent);
        if (vibrationOccurPluginEvent.isCancelled()) {
            return;
        }
        for (var listener : listeners) {
            if (!listener.getListenerVector().equals(event.source()) && listener.getListenerVector().distanceSquared(event.source()) <= Math.pow(listener.getListenRange(), 2) && canVibrationArrive(level, event.source(), listener.getListenerVector()) && listener.onVibrationOccur(event)) {
                this.createVibration(listener, event);
                this.level.getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
                    VibrationArriveEvent vibrationArrivePluginEvent = new VibrationArriveEvent(event, listener);
                    this.level.getServer().getPluginManager().callEvent(vibrationArrivePluginEvent);
                    if (vibrationArrivePluginEvent.isCancelled()) {
                        return;
                    }
                    listener.onVibrationArrive(event);
                }, (int) event.source().distance(listener.getListenerVector()));
            }
        }
    }

    @Override
    public void addListener(VibrationListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(VibrationListener listener) {
        this.listeners.remove(listener);
    }

    protected void createVibration(VibrationListener listener, VibrationEvent event) {
        if(event.initiator() instanceof Entity e && e.getDataFlag(ActorFlags.SILENT)) return;

        var listenerPos = listener.getListenerVector().asVector3f();
        var sourcePos = event.source().asVector3f();
        var tag = NbtMap.builder()
                .putCompound("origin", createVec3fTag(sourcePos))
                .putFloat("speed", 20.0f)
                .putCompound("target", listener.isEntity() ? createEntityTargetTag(listener.asEntity()) : createVec3fTag(listenerPos))
                .putFloat("timeToLive", (float) (listenerPos.distance(sourcePos) / 20.0))
                .build();
        LevelEventGenericPacket packet = new LevelEventGenericPacket();
        packet.setType(LevelEvent.PARTICLE_VIBRATION_SIGNAL);
        packet.setTag(tag);
        //todo: Packets are only sent to players within the player's field of view.
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
                .putLong("uniqueID", entity.getId())
                .putInt("attachPos", 3) //todo: check the use of this value :)
                .build();
    }

    protected boolean canVibrationArrive(Level level, Vector3 from, Vector3 to) {
        return VectorMath.getPassByVector3(from, to)
                .stream()
                .noneMatch(vec -> level.getTickCachedBlock(vec).hasTag(BlockTags.PNX_WOOL));
    }
}