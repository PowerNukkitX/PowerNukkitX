package cn.nukkit.level.vibration;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.VectorMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventGenericPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.Utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@PowerNukkitXOnly
@Since("1.19.21-r3")
public class SimpleVibrationManager implements VibrationManager{

    protected Set<VibrationListener> listeners = new CopyOnWriteArraySet<>();
    protected Level level;

    public SimpleVibrationManager(Level level) {
        this.level = level;
    }

    @Override
    public void callVibrationEvent(VibrationEvent event) {
        //todo: add plugin event
        for (var listener : listeners) {
            if (listener.getListenerVector().distanceSquared(event.source()) <= Math.pow(listener.getListenRange(), 2) && canVibrationArrive(level, event.source(), listener.getListenerVector()) && listener.onVibrationOccur(event)) {
                this.createVibration(listener, event);
                Server.getInstance().getScheduler().scheduleDelayedTask(() -> {
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

    protected void createVibration(VibrationListener listener, VibrationEvent event){
        var listenerPos = listener.getListenerVector().asVector3f();
        var sourcePos = event.source().asVector3f();
        var tag = new CompoundTag()
                .putCompound("origin", createVec3fTag(sourcePos))
                .putFloat("speed", 20.0f)
                .putCompound("target", listener.isEntity() ? createEntityTargetTag(listener.asEntity()) : createVec3fTag(listenerPos))
                .putFloat("timeToLive", (float) (listenerPos.distance(event.source().asVector3f()) / 20.0));
        LevelEventGenericPacket packet = new LevelEventGenericPacket();
        packet.eventId = LevelEventPacket.EVENT_PARTICLE_VIBRATION_SIGNAL;
        packet.tag = tag;
        Server.broadcastPacket(level.getPlayers().values(), packet);
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
        //先粗略的检查两点形成的长方形范围内是否有羊毛方块
        boolean hasWool = level.getTickCachedCollisionBlocks(new SimpleAxisAlignedBB(from, to), true, false, block -> block.getId() == BlockID.WOOL).length != 0;
        if (hasWool) {
            //若有，则使用高开销算法
            return !VectorMath.getPassByVector3(from, to)
                    .stream()
                    .anyMatch(vec -> level.getTickCachedBlock(vec).getId() == BlockID.WOOL);
        } else {
            return true;
        }
    }
}