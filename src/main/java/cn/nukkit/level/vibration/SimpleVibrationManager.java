package cn.nukkit.level.vibration;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.VectorMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventGenericPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

import java.util.HashSet;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public class SimpleVibrationManager implements VibrationManager{

    protected Set<VibrationListener> listeners = new HashSet<>();

    @Override
    public void callVibrationEvent(VibrationEvent event) {
        //todo: add plugin event
        for (var listener : listeners) {
            if (listener.onVibrationOccur(event) && canVibrationArrive(event.source().getLevel(), event.source(), listener.getListenerPosition())) {
                this.createVibration(listener, event);
                Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
                    listener.onVibrationArrive(event);
                }, (int) event.source().distance(listener.getListenerPosition()));
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

    protected void createVibration(VibrationListener listener, VibrationEvent event){
        var listenerPos = listener.getListenerPosition().asVector3f();
        var sourcePos = event.source().asVector3f();
        var tag = new CompoundTag()
                .putCompound("origin", createVec3fTag(sourcePos))
                .putFloat("speed", 20.0f)
                .putCompound("target", listener.isEntity() ? createEntityTargetTag(listener.asEntity()) : createVec3fTag(listenerPos))
                .putFloat("timeToLive", (float) (listenerPos.distance(event.source().asVector3f()) / 20.0));
        LevelEventGenericPacket packet = new LevelEventGenericPacket();
        packet.eventId = LevelEventPacket.EVENT_PARTICLE_VIBRATION_SIGNAL;
        packet.tag = tag;
        Server.broadcastPacket(listener.getListenerPosition().level.getPlayers().values(), packet);
    }

    protected CompoundTag createVec3fTag(Vector3f vec3f){
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

    protected boolean canVibrationArrive(Level level, Vector3 from, Vector3 to) {
        return VectorMath.getPassByVector3(from, to)
                .stream()
                .map(vec -> level.getBlockIdAt((int) vec.x, (int) vec.y, (int) vec.z))
                .allMatch(id -> id != BlockID.WOOL);
    }
}