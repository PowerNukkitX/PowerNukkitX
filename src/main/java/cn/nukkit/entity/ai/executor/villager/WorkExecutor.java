package cn.nukkit.entity.ai.executor.villager;


import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCrops;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.executor.NearbyFlatRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class WorkExecutor extends NearbyFlatRandomRoamExecutor {

    int stayTick = 0;
    int walkTick = 0;

    public WorkExecutor() {
        super(CoreMemoryTypes.SITE_BLOCK, 0.3f, 16, 60);
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if(entity instanceof EntityVillagerV2 villager) {
            Block site = villager.getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
            if(stayTick < 100) {
                if(site.distance(villager) < 1.5f) {
                    setLookTarget(villager, site);
                    stayTick++;
                    if(stayTick == 40 || stayTick == 90) villager.getLevel().addSound(villager, Profession.getProfession(villager.getProfession()).getWorkSound());
                }
                if(stayTick == 99) removeRouteTarget(villager);
            } else {
                walkTick++;
                switch (villager.getProfession()) {
                    case 1: {
                        if(walkTick%10 == 0) {
                            villager.setMovementSpeed(0.3f);
                            double minDistance = Float.MAX_VALUE;
                            Block nearest = null;
                            for(Block block : Arrays.stream(villager.getLevel().getCollisionBlocks(villager.getBoundingBox().grow(9, 2, 9), false, true)).filter(block -> block instanceof BlockCrops crops && crops.isFullyGrown()).toList()) {
                                double distance = block.distance(villager);
                                if(distance < minDistance) {
                                    minDistance = distance;
                                    nearest = block;
                                }
                            }
                            if(nearest != null) {
                                if(minDistance < 1.5f) {
                                    villager.getLevel().breakBlock(nearest);
                                    villager.getInventory().addItem(nearest.getDrops(Item.AIR));
                                    villager.getLevel().setBlock(nearest, nearest.getProperties().getDefaultState().toBlock());
                                    removeLookTarget(villager);
                                } else {
                                    if(entity.getMoveTarget() == null) {
                                        Vector2 horizontal = new Vector2(nearest.x - entity.x, nearest.z - entity.z);
                                        horizontal = horizontal.multiply(1 - 1/horizontal.length());
                                        Vector3 target = new Vector3(entity.x + horizontal.x, nearest.y, entity.z + horizontal.y);
                                        setLookTarget(entity, target);
                                        setRouteTarget(entity, target);
                                    }
                                }
                                break;
                            } else super.execute(villager);
                        } else break;
                    }
                    default: super.execute(villager);
                }
            }
            if(walkTick >= 300) {
                setTarget(villager);
                walkTick = 0;
                stayTick = 0;
            }
        }
        return true;
    }

    public void setTarget(EntityIntelligent entity) {
        Block site = entity.getMemoryStorage().get(CoreMemoryTypes.SITE_BLOCK);
        Vector2 horizontal = new Vector2(site.x - entity.x, site.z - entity.z);
        horizontal = horizontal.multiply(1 - 1/horizontal.length());
        Vector3 target = new Vector3(entity.x + horizontal.x, site.y, entity.z + horizontal.y);
        setLookTarget(entity, target);
        setRouteTarget(entity, target);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if(entity instanceof EntityVillagerV2 villager) {
            int shift = getShiftLength(entity.getLevel().getDayTime());
            if(entity.getMemoryStorage().get(CoreMemoryTypes.LAST_REFILL_SHIFT) != shift) {
                this.stayTick = 100;
                this.walkTick = 200;
                villager.getRecipes().getAll().forEach(tag -> tag.putInt("uses", 0));
                entity.getMemoryStorage().put(CoreMemoryTypes.LAST_REFILL_SHIFT, shift);
            }
            if(stayTick < 100) setTarget(entity);
        }
        super.onStart(entity);
    }

    public int getShiftLength(int daytime) {
        if(daytime >= 0 && daytime < 8000) return 0;
        if(daytime >= 10000 && daytime < 11000) return 1;
        return -1;
    }
}
