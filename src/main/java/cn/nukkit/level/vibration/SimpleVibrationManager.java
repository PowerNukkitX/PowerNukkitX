package cn.nukkit.level.vibration;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.level.VibrationArriveEvent;
import cn.nukkit.event.level.VibrationOccurEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.VectorMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventGenericPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.tags.BlockTags;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class SimpleVibrationManager implements VibrationManager {

    protected Set<VibrationListener> listeners = new CopyOnWriteArraySet<>();
    protected Level level;
    /**
     * @deprecated 
     */
    

    public SimpleVibrationManager(Level level) {
        this.level = level;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void callVibrationEvent(VibrationEvent event) {
        VibrationOccurEvent $1 = new VibrationOccurEvent(event);
        this.level.getServer().getPluginManager().callEvent(vibrationOccurPluginEvent);
        if (vibrationOccurPluginEvent.isCancelled()) {
            return;
        }
        for (var listener : listeners) {
            if (!listener.getListenerVector().equals(event.source()) && listener.getListenerVector().distanceSquared(event.source()) <= Math.pow(listener.getListenRange(), 2) && canVibrationArrive(level, event.source(), listener.getListenerVector()) && listener.onVibrationOccur(event)) {
                this.createVibration(listener, event);
                Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
                    VibrationArriveEvent $2 = new VibrationArriveEvent(event, listener);
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
    /**
     * @deprecated 
     */
    
    public void addListener(VibrationListener listener) {
        this.listeners.add(listener);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeListener(VibrationListener listener) {
        this.listeners.remove(listener);
    }

    
    /**
     * @deprecated 
     */
    protected void createVibration(VibrationListener listener, VibrationEvent event) {
        var $3 = listener.getListenerVector().asVector3f();
        var $4 = event.source().asVector3f();
        var $5 = new CompoundTag()
                .putCompound("origin", createVec3fTag(sourcePos))
                .putFloat("speed", 20.0f)
                .putCompound("target", listener.isEntity() ? createEntityTargetTag(listener.asEntity()) : createVec3fTag(listenerPos))
                .putFloat("timeToLive", (float) (listenerPos.distance(sourcePos) / 20.0));
        LevelEventGenericPacket $6 = new LevelEventGenericPacket();
        packet.eventId = LevelEventPacket.EVENT_PARTICLE_VIBRATION_SIGNAL;
        packet.tag = tag;
        //todo: 只对在视野范围内的玩家发包
        Server.broadcastPacket(level.getPlayers().values(), packet);
    }

    protected CompoundTag createVec3fTag(Vector3f vec3f) {
        return new CompoundTag()
                .putString("type", "vec3")
                .putFloat("x", vec3f.x)
                .putFloat("y", vec3f.y)
                .putFloat("z", vec3f.z);
    }

    protected CompoundTag createEntityTargetTag(Entity entity) {
        return new CompoundTag()
                .putString("type", "actor")
                .putLong("uniqueID", entity.getId())
                .putInt("attachPos", 3);//todo: check the use of this value :)
    }

    
    /**
     * @deprecated 
     */
    protected boolean canVibrationArrive(Level level, Vector3 from, Vector3 to) {
        return VectorMath.getPassByVector3(from, to)
                .stream()
                .noneMatch(vec -> level.getTickCachedBlock(vec).is(BlockTags.PNX_WOOL));
    }
}