package cn.nukkit.entity.mob;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.RandomTimeRangeEvaluator;
import cn.nukkit.entity.ai.executor.RandomRoamExecutor;
import cn.nukkit.entity.ai.executor.WardenSniffExecutor;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationListener;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.Set;

public class EntityWarden extends EntityWalkingMob implements VibrationListener {

    public static final int NETWORK_ID = 131;

    private IBehaviorGroup behaviorGroup;

    protected int lastDetectTime = Server.getInstance().getTick();

    protected boolean waitForVibration = false;

    public EntityWarden(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null){
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(),
                    Set.of(
                            new Behavior(new WardenSniffExecutor((int) (4.2*20)), new RandomTimeRangeEvaluator(5*20, 10*20), 2),
                            new Behavior(new RandomRoamExecutor(0.1f, 12, 100, true,-1,true,10), (entity -> true), 1)
                    ),
                    Set.of(),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return this.behaviorGroup;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(500);
        this.setDataProperty(new IntEntityData(Entity.DATA_HEARTBEAT_INTERVAL_TICKS, 20));
        this.setDataProperty(new IntEntityData(Entity.DATA_HEARTBEAT_SOUND_EVENT, 442));
    }

    @Override
    public String getOriginalName() {
        return "Warden";
    }

    @Override
    public Vector3 getListenerVector() {
        return this.clone();
    }

    @Override
    public boolean onVibrationOccur(VibrationEvent event) {
        if (event.source().equals(this.clone())) return false;
        if (Server.getInstance().getTick() - this.lastDetectTime >= 40 && !waitForVibration) {
            this.waitForVibration = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onVibrationArrive(VibrationEvent event) {
        this.waitForVibration = false;
        this.lastDetectTime = Server.getInstance().getTick();
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.VIBRATION_DETECTED;
        Server.broadcastPacket(this.level.getPlayers().values(), pk);
    }

    @Override
    public double getListenRange() {
        return 16;
    }

    @Override
    public void spawnToAll() {
        super.spawnToAll();
        this.level.getVibrationManager().addListener(this);
    }

    @Override
    public void despawnFromAll() {
        super.despawnFromAll();
        this.level.getVibrationManager().removeListener(this);
    }

    @Override
    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        //anti-kb
    }
}
