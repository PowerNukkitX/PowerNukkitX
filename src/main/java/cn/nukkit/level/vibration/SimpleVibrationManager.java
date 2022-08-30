package cn.nukkit.level.vibration;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
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
            if (listener.onVibrationOccur(event)) {
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
        var listenerPos = listener.getListenerPosition().asVector3f().floor().add(0.5f, 0.5f, 0.5f);
        var sourcePos = event.source().asVector3f().floor().add(0.5f, 0.5f, 0.5f);
        var tag = new CompoundTag()
                .putCompound("origin", new CompoundTag()
                        .putString("type", "vec3")
                        .putFloat("x", sourcePos.x)
                        .putFloat("y", sourcePos.y)
                        .putFloat("z", sourcePos.z))
                .putFloat("speed", 20.0f)
                .putCompound("target", new CompoundTag()
                        .putString("type", "vec3")
                        .putFloat("x", listenerPos.x)
                        .putFloat("y", listenerPos.y)
                        .putFloat("z", listenerPos.z))
                .putFloat("timeToLive", (float) (listenerPos.distance(event.source().asVector3f()) / 20.0));
        LevelEventGenericPacket packet = new LevelEventGenericPacket();
        packet.eventId = LevelEventPacket.EVENT_PARTICLE_VIBRATION_SIGNAL;
        packet.tag = tag;
        Server.broadcastPacket(listener.getListenerPosition().level.getPlayers().values(), packet);
    }
}