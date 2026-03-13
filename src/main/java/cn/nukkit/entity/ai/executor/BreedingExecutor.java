package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityColor;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityMarkVariant;
import cn.nukkit.entity.EntityVariant;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.entity.data.property.FloatEntityProperty;
import cn.nukkit.entity.data.property.IntEntityProperty;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.Nullable;


/**
 * Breeding executor driven by {@link BreedableComponent}.
 * <p>
 * Finds a compatible in-love mate within range, moves both entities together, then either marks one parent as pregnant
 * or spawns one or more babies with optional genetics/variant/color mutation, property inheritance, and tame inheritance.
 *
 * <p><b>Constructor:</b>
 * <pre>{@code
 * BreedingExecutor(int findingRange, int duration, float moveSpeed)
 * }</pre>
 *
 * <p><b>Parameters:</b>
 * <ul>
 *   <li>{@code findingRange} - search radius in blocks (will be squared internally)</li>
 *   <li>{@code duration} - ticks required before breeding completes</li>
 *   <li>{@code moveSpeed} - temporary movement speed applied while approaching the mate</li>
 * </ul>
 *
 * <p><b>Example usage:</b>
 * <pre>{@code
 * new Behavior(
 *     new BreedingExecutor(16, 100, 0.5f), // 16 block radius, 100 ticks duration
 *     e -> Boolean.TRUE.equals(e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)),
 *     2,
 *     1
 * );
 * }</pre>
 * 
 * @author Curse
 */
public class BreedingExecutor implements IBehaviorExecutor {
    protected final int findingRangeSquared;
    protected final int duration;
    protected final float moveSpeed;

    public BreedingExecutor(int findingRange, int duration, float moveSpeed) {
        this.findingRangeSquared = findingRange > 0 ? findingRange * findingRange : findingRange;
        this.duration = duration;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (entity.canSit() && entity.isSitting()) {
            onInterrupt(entity);
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            return false;
        }

        BreedableComponent breedable = entity.getComponentBreedable();
        if (breedable == null || breedable.isEmpty()) return false;

        if (shouldFindingSpouse(entity)) {
            if (!entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)) return false;

            EntityIntelligent spouse = getNearestValidInLoveMate(entity, breedable);
            if (spouse == null) return true;

            setSpouse(entity, spouse);

            entity.setMovementSpeed(moveSpeed);
            spouse.setMovementSpeed(moveSpeed);

            setBreeding(entity, true);
            setBreedingTick(entity, 0);
            setBreeding(spouse, true);
            setBreedingTick(spouse, 0);
        }

        if (isBreeding(entity)) {
            EntityIntelligent spouse = getSpouse(entity);
            if (spouse == null) {
                clearBreedingState(entity);
                return false;
            }

            updateMove(entity, spouse);

            if (!isBreedingLeader(entity, spouse)) return true;

            int currentTick = getBreedingTick(entity) + 1;
            setBreedingTick(entity, currentTick);

            if (currentTick > duration) {
                breed(entity, spouse);

                clearData(entity);
                clearData(spouse);

                setBreedingTick(entity, 0);
                setBreeding(entity, false);
                setBreedingTick(spouse, 0);
                setBreeding(spouse, false);

                entity.setEnablePitch(false);
                spouse.setEnablePitch(false);

                return false;
            }
        }

        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        EntityIntelligent spouse = getSpouse(entity);

        clearData(entity);
        clearBreedingState(entity);

        entity.setEnablePitch(false);

        if (spouse != null) {
            clearData(spouse);
            clearBreedingState(spouse);
            spouse.setEnablePitch(false);
        }
    }

    // Mate selection (driven by BreedableComponent.breedsWith)
    protected boolean shouldFindingSpouse(EntityIntelligent entity) {
        return entity.getMemoryStorage().isEmpty(CoreMemoryTypes.ENTITY_SPOUSE);
    }

    protected void setSpouse(EntityIntelligent entity1, EntityIntelligent entity2) {
        entity1.getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, entity2);
        entity2.getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPOUSE, entity1);
    }

    protected void clearData(EntityIntelligent entity) {
        entity.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);

        entity.setMoveTarget(null);
        entity.setLookTarget(null);

        entity.setMovementSpeed(entity.getMovementSpeedDefault());
    }

    protected void updateMove(EntityIntelligent entity1, EntityIntelligent entity2) {
        if (entity2 == null) return;

        if (!entity1.isEnablePitch()) entity1.setEnablePitch(true);
        if (!entity2.isEnablePitch()) entity2.setEnablePitch(true);

        if (entity1.getOffsetBoundingBox().intersectsWith(entity2.getOffsetBoundingBox())) return;

        boolean s1 = entity1.isSitting();
        boolean s2 = entity2.isSitting();

        var cloned1 = entity1.clone();
        var cloned2 = entity2.clone();

        entity1.setLookTarget(cloned2);
        entity2.setLookTarget(cloned1);

        if (s1) {
            entity1.setMoveTarget(null);
        } else {
            entity1.setMoveTarget(cloned2);
            entity1.getBehaviorGroup().setForceUpdateRoute(true);
        }

        if (s2) {
            entity2.setMoveTarget(null);
        } else {
            entity2.setMoveTarget(cloned1);
            entity2.getBehaviorGroup().setForceUpdateRoute(true);
        }
    }

    @Nullable
    protected EntityIntelligent getNearestValidInLoveMate(EntityIntelligent entity, BreedableComponent aBreedable) {
        if (entity.level == null) return null;

        double bestDist = -1d;
        EntityIntelligent best = null;

        for (Entity e : entity.level.getEntities()) {
            if (e == null || e == entity) continue;
            if (!(e instanceof EntityIntelligent other)) continue;

            double d2 = other.distanceSquared(entity);
            if (findingRangeSquared > 0 && d2 > findingRangeSquared) continue;

            if (other.isBaby()) continue;
            if (!Boolean.TRUE.equals(other.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE))) continue;
            if (!other.getMemoryStorage().isEmpty(CoreMemoryTypes.ENTITY_SPOUSE)) continue;

            BreedableComponent bBreedable = other.getComponentBreedable();
            if (bBreedable == null) continue;

            if (!isValidMatePair(entity, aBreedable, other, bBreedable)) continue;

            if (bestDist == -1 || d2 < bestDist) {
                bestDist = d2;
                best = other;
            }
        }

        return best;
    }

    protected boolean isValidMatePair(EntityIntelligent a, BreedableComponent aBreedable, EntityIntelligent b, BreedableComponent bBreedable) {
        if (a == null || b == null) return false;
        return allowsMate(a, aBreedable, b) && allowsMate(b, bBreedable, a);
    }

    protected boolean allowsMate(EntityIntelligent self, BreedableComponent selfBreedable, EntityIntelligent other) {
        List<BreedableComponent.BreedsWith> bw = selfBreedable.breedsWith();
        if (bw == null || bw.isEmpty()) {
            return Objects.equals(self.getIdentifier(), other.getIdentifier());
        }

        String otherId = other.getIdentifier();
        for (BreedableComponent.BreedsWith it : bw) {
            if (it == null) continue;
            if (it.mateType() == null) continue;

            if (it.mateType().equals(otherId)) return true;
        }
        return false;
    }

    // Breeding (spawn babies)
    protected void breed(EntityIntelligent parent1, EntityIntelligent parent2) {
        BreedableComponent b1 = parent1.getComponentBreedable();
        BreedableComponent b2 = parent2.getComponentBreedable();
        if (b1 == null || b2 == null) return;

        int now = parent1.level.getTick();

        // Pregnancy flow: set ONE parent pregnant and stop here
        if (b1.resolvedCausesPregnancy() || b2.resolvedCausesPregnancy()) {
            EntityIntelligent pregnant = null;

            boolean p1Preg = parent1.isPregnant();
            boolean p2Preg = parent2.isPregnant();

            if (!p1Preg && p2Preg) pregnant = parent1;
            else if (p1Preg && !p2Preg) pregnant = parent2;
            else if (!p1Preg && !p2Preg) pregnant = ThreadLocalRandom.current().nextBoolean() ? parent1 : parent2;
            else pregnant = parent1;

            if (pregnant != null && !pregnant.isPregnant()) pregnant.setPregnant(true);
            finishBreeding(parent1, parent2, now);
            return;
        }

        String babyId = resolveBabyIdentifier(parent1, b1, parent2, b2);
        if (babyId == null || babyId.isEmpty()) return;

        int babiesToSpawn = resolveBabyCount(b1, b2);

        int spawned = 0;

        for (int i = 0; i < babiesToSpawn; i++) {
            CompoundTag nbt = Entity.getDefaultNBT(parent1.getLocation());

            if (shouldWriteGenetics(b1, b2, parent1, parent2)) {
                writeBabyGeneticsNBT(nbt, b1, b2, parent1, parent2);
            }

            EntityIntelligent baby = (EntityIntelligent) Entity.createEntity(babyId, parent1.getChunk(), nbt);
            if (baby == null) continue;

            baby.setBaby(true);
            baby.getMemoryStorage().put(CoreMemoryTypes.ENTITY_SPAWN_TIME, now);
            baby.getMemoryStorage().put(CoreMemoryTypes.PARENT, parent1);
            baby.setPersistent(true);

            applyBabyVariants(baby, babyId, b1, b2, parent1, parent2);

            try {
                applyPropertyInheritance(baby, b1, b2, parent1, parent2);
            } catch (Throwable t) { }

            if (resolvedInheritTamed(b1, b2)) {
                tryInheritTamed(baby, parent1, parent2);
            }

            baby.spawnToAll();
            spawned++;
        }

        if (spawned <= 0) return;
        finishBreeding(parent1, parent2, now);
    }

    protected void finishBreeding(EntityIntelligent parent1, EntityIntelligent parent2, int time) {
        parent1.setDataFlag(EntityFlag.IN_LOVE, false);
        parent2.setDataFlag(EntityFlag.IN_LOVE, false);

        parent1.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);
        parent2.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);
        parent1.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, false);
        parent2.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, false);
        parent1.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, time);
        parent2.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, time);

        if (parent1 instanceof EntityAnimal ea1) ea1.sendLoveParticles();
        if (parent2 instanceof EntityAnimal ea2) ea2.sendLoveParticles();
    }

    protected int resolveBabyCount(BreedableComponent b1, BreedableComponent b2) {
        float c = Math.max(b1.resolvedExtraBabyChance(), b2.resolvedExtraBabyChance());
        if (c <= 0.0f) return 1;
        if (ThreadLocalRandom.current().nextFloat() >= c) return 1;
        return 1 + ThreadLocalRandom.current().nextInt(1, 16);
    }

    protected boolean resolvedInheritTamed(BreedableComponent b1, BreedableComponent b2) {
        Boolean a = b1.inheritTamed();
        Boolean b = b2.inheritTamed();

        if (a == null && b == null) return false;
        return Boolean.TRUE.equals(a) || Boolean.TRUE.equals(b);
    }

    protected String resolveBabyIdentifier(EntityIntelligent p1, BreedableComponent b1, EntityIntelligent p2, BreedableComponent b2) {
        String p1Id = p1.getIdentifier();
        String p2Id = p2.getIdentifier();

        String baby = findBabyTypeFromBreedsWith(b1, p2Id);
        if (baby != null) return baby;

        baby = findBabyTypeFromBreedsWith(b2, p1Id);
        if (baby != null) return baby;

        return p1Id;
    }

    @Nullable
    protected String findBabyTypeFromBreedsWith(BreedableComponent breedable, String mateId) {
        List<BreedableComponent.BreedsWith> bw = breedable.breedsWith();
        if (bw == null) return null;

        return bw.stream()
            .filter(Objects::nonNull)
            .filter(it -> it.mateType() != null)
            .filter(it -> it.mateType().equals(mateId))
            .map(BreedableComponent.BreedsWith::babyType)
            .filter(b -> b != null && !b.isEmpty())
            .findFirst()
            .orElse(null);
    }

    // Genetics / attributes blending (written to baby NBT)
    protected boolean shouldWriteGenetics(BreedableComponent b1, BreedableComponent b2, EntityIntelligent p1, EntityIntelligent p2) {
        if (b1 == null || b2 == null) return false;
        if (!b1.hasBlendAttributes() || !b2.hasBlendAttributes()) return false;

        Set<String> s1 = new HashSet<>(b1.resolvedBlendAttributes());
        for (String a : b2.resolvedBlendAttributes()) {
            if (s1.contains(a)) return true;
        }
        return false;
    }

    protected void writeBabyGeneticsNBT(CompoundTag nbt, BreedableComponent b1, BreedableComponent b2, EntityIntelligent p1, EntityIntelligent p2) {
        Set<String> common = new LinkedHashSet<>(b1.resolvedBlendAttributes());
        common.retainAll(b2.resolvedBlendAttributes());

        if (common.isEmpty()) return;

        ListTag<CompoundTag> list = null;

        for (String name : common) {
            Attribute attr = blendOneAttribute(name, p1, p2);
            if (attr == null) continue;

            if (list == null) list = new ListTag<>();
            list.add(Attribute.toNBT(attr));
        }

        if (list != null) nbt.putList("Attributes", list);
    }

    @Nullable
    protected Attribute blendOneAttribute(String attributeName, EntityIntelligent p1, EntityIntelligent p2) {
        if (attributeName == null) return null;

        String a = attributeName.trim().toLowerCase(Locale.ROOT);
        if (a.isEmpty()) return null;

        Attribute pa = findEntityAttribute(p1, a);
        Attribute pb = findEntityAttribute(p2, a);
        if (pa == null || pb == null) return null;

        int id = pa.getId();
        Attribute out = Attribute.getAttribute(id);

        float min = Math.min(pa.getDefaultMinimum(), pb.getDefaultMinimum());
        float max = Math.max(pa.getDefaultMaximum(), pb.getDefaultMaximum());

        float v1 = roll(min, max);
        float v2 = roll(min, max);

        float newDefaultMin = Math.min(v1, v2);
        float newDefaultMax = Math.max(v1, v2);

        if (id == Attribute.HEALTH) {
            out.setMinValue(0f);
            out.setMaxValue(newDefaultMax);
        }

        out.setDefaultMinimum(newDefaultMin);
        out.setDefaultMaximum(newDefaultMax);
        out.setDefaultValue(newDefaultMax);
        out.setValue(newDefaultMax);

        return out;
    }

    @Nullable
    protected Attribute findEntityAttribute(EntityIntelligent entity, String fullAttributeName) {
        if (fullAttributeName == null) return null;

        Attribute proto = Attribute.getAttributeByName(fullAttributeName);
        if (proto == null) return null;

        Map<Integer, Attribute> map = entity.getAttributes();
        if (map == null || map.isEmpty()) return null;

        return map.get(proto.getId());
    }

    protected float roll(float min, float max) {
        if (min >= max) return min;
        return (float) (min + ThreadLocalRandom.current().nextDouble() * (max - min));
    }

    // Variants: deny_parents_variant + mutation_factor
    protected void applyBabyVariants(EntityIntelligent baby, String babyId, BreedableComponent b1, BreedableComponent b2, EntityIntelligent p1, EntityIntelligent p2) {
        BreedableComponent.MutationFactor mut = pickMutationFactor(b1, b2);

        if (baby instanceof EntityVariant babyVar) {
            Integer p1Var = (p1 instanceof EntityVariant v1) ? v1.getVariant() : null;
            Integer p2Var = (p2 instanceof EntityVariant v2) ? v2.getVariant() : null;
            Integer base = null;

            if (babyId.equals(p1.getIdentifier()) && p1Var != null) base = p1Var;
            else if (babyId.equals(p2.getIdentifier()) && p2Var != null) base = p2Var;

            if (base == null && p1Var != null && p2Var != null && babyId.equals(p1.getIdentifier()) && babyId.equals(p2.getIdentifier())) {
                base = ThreadLocalRandom.current().nextBoolean() ? p1Var : p2Var;
            }

            if (base != null) {
                int out = base;

                BreedableComponent.DenyParentsVariant deny = pickDenyParentsVariant(b1, b2);
                if (deny != null && p1Var != null && p2Var != null && p1Var.intValue() == p2Var.intValue()) {
                    float chance = (deny.chance() != null) ? deny.chance() : 0.0f;
                    if (chance > 0.0f && ThreadLocalRandom.current().nextFloat() < chance) {
                        int mn = (deny.minVariant() != null) ? deny.minVariant() : 0;
                        int mx = (deny.maxVariant() != null) ? deny.maxVariant() : mn;
                        out = rollIntInclusive(mn, mx);
                    }
                }

                if (mut != null && mut.variant() != null && mut.variant() > 0.0f && ThreadLocalRandom.current().nextFloat() < mut.variant()) {
                    int mn;
                    int mx;

                    if (deny != null && (deny.minVariant() != null || deny.maxVariant() != null)) {
                        mn = (deny.minVariant() != null) ? deny.minVariant() : 0;
                        mx = (deny.maxVariant() != null) ? deny.maxVariant() : mn;
                    } else {
                        int a = (p1Var != null) ? p1Var : out;
                        int b = (p2Var != null) ? p2Var : out;
                        int lo = Math.min(a, b);
                        int hi = Math.max(a, b);
                        mn = Math.max(0, lo - 1);
                        mx = Math.max(mn, hi + 1);
                    }

                    out = rollIntInclusive(mn, mx);
                }

                babyVar.setVariant(out);

                if (mut != null && mut.extraVariant() != null && mut.extraVariant() > 0.0f && ThreadLocalRandom.current().nextFloat() < mut.extraVariant()) {
                    tryMutateExtraVariant(baby, pickDenyParentsVariant(b1, b2));
                }
            }
        }

        applyBabyColor(baby, b1, b2, p1, p2, mut);
    }

    @Nullable
    protected BreedableComponent.DenyParentsVariant pickDenyParentsVariant(BreedableComponent b1, BreedableComponent b2) {
        BreedableComponent.DenyParentsVariant a = b1.denyParentsVariant();
        BreedableComponent.DenyParentsVariant b = b2.denyParentsVariant();
        return (a != null && !a.isEmpty()) ? a : ((b != null && !b.isEmpty()) ? b : null);
    }

    @Nullable
    protected BreedableComponent.MutationFactor pickMutationFactor(BreedableComponent b1, BreedableComponent b2) {
        BreedableComponent.MutationFactor a = b1.mutationFactor();
        BreedableComponent.MutationFactor b = b2.mutationFactor();
        return (a != null && !a.isEmpty()) ? a : ((b != null && !b.isEmpty()) ? b : null);
    }

    protected int rollIntInclusive(int min, int max) {
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    protected void tryMutateExtraVariant(EntityIntelligent baby, @Nullable BreedableComponent.DenyParentsVariant deny) {
        if (!(baby instanceof EntityMarkVariant mv)) return;

        int[] pool = mv.getAllMarkVariant();
        if (pool == null || pool.length == 0) return;

        int current = mv.getMarkVariant();

        int mn = Integer.MIN_VALUE;
        int mx = Integer.MAX_VALUE;

        if (deny != null) {
            if (deny.minVariant() != null) mn = deny.minVariant();
            if (deny.maxVariant() != null) mx = deny.maxVariant();
            if (mn > mx) { int t = mn; mn = mx; mx = t; }
        }

        // 1) Count candidates that pass range AND differ from current
        int count = 0;
        for (int v : pool) {
            if (v < mn || v > mx) continue;
            if (v == current) continue;
            count++;
        }

        // If none, allow current as fallback
        boolean allowCurrent = false;
        if (count == 0) {
            for (int v : pool) {
                if (v < mn || v > mx) continue;
                count++;
            }
            if (count == 0) return;
            allowCurrent = true;
        }

        int pick = ThreadLocalRandom.current().nextInt(count);

        // 2) Select the picked element
        for (int v : pool) {
            if (v < mn || v > mx) continue;
            if (!allowCurrent && v == current) continue;

            if (pick-- == 0) {
                mv.setMarkVariant(v);
                return;
            }
        }
    }

    protected void applyBabyColor(EntityIntelligent baby,
                                BreedableComponent b1, BreedableComponent b2,
                                EntityIntelligent p1, EntityIntelligent p2,
                                @Nullable BreedableComponent.MutationFactor mut) {

        if (!(baby instanceof EntityColor babyColor)) return;
        if (!(p1 instanceof EntityColor c1)) return;
        if (!(p2 instanceof EntityColor c2)) return;

        boolean combine = Boolean.TRUE.equals(b1.combineParentColors()) || Boolean.TRUE.equals(b2.combineParentColors());

        // PRIMARY COLOR (getColor)
        if (c1.hasColor() && c2.hasColor()) {
            DyeColor colorA = c1.getColor();
            DyeColor colorB = c2.getColor();

            if (colorA != null && colorB != null) {
                if (mut != null && mut.color() != null && mut.color() > 0.0f && ThreadLocalRandom.current().nextFloat() < mut.color()) {
                    DyeColor out = pickRandomColorExcluding(babyColor, colorA, colorB);
                    babyColor.setColor(out);
                } else {
                    if (combine) {
                        int out = BreedableComponent.combineParentColorsOrRandom(colorA.getWoolData(), colorB.getWoolData(), ThreadLocalRandom.current());
                        babyColor.setColor(DyeColor.getByWoolData(out));
                    } else {
                        babyColor.setColor(ThreadLocalRandom.current().nextBoolean() ? colorA : colorB);
                    }
                }
            }
        }

        // SECONDARY COLOR (getColor2)
        if (c1.hasColor2() && c2.hasColor2()) {
            DyeColor color2A = c1.getColor2();
            DyeColor color2B = c2.getColor2();

            if (color2A != null && color2B != null) {
                if (combine) {
                    int out2 = BreedableComponent.combineParentColorsOrRandom( color2A.getWoolData(), color2B.getWoolData(), ThreadLocalRandom.current());
                    babyColor.setColor2(DyeColor.getByWoolData(out2));
                } else {
                    babyColor.setColor2(ThreadLocalRandom.current().nextBoolean() ? color2A : color2B);
                }
            }
        }
    }

    protected DyeColor pickRandomColorExcluding(EntityColor ctx, DyeColor a, DyeColor b) {
        DyeColor out = ctx.getRandomColor();
        if (out != null && out != a && out != b) return out;

        for (int i = 0; i < 8; i++) {
            out = ctx.getRandomColor();
            if (out != null && out != a && out != b) return out;
        }

        return out != null ? out : a;
    }

    // inherit_tamed
    protected void tryInheritTamed(EntityIntelligent baby, EntityIntelligent p1, EntityIntelligent p2) {
        if (!p1.isTamed() || !p2.isTamed()) return;

        String owner1 = p1.getOwnerName();
        String owner2 = p2.getOwnerName();

        if (owner1 == null || owner2 == null) return;
        if (!owner1.equals(owner2)) return;

        baby.setOwnerName(owner1);
        baby.setTamed(true);
    }

    // property_inheritance
    protected void applyPropertyInheritance(EntityIntelligent baby, BreedableComponent b1, BreedableComponent b2, EntityIntelligent p1, EntityIntelligent p2) {
        if (baby == null || b1 == null || b2 == null || p1 == null || p2 == null) return;

        Set<String> a = b1.propertyInheritance();
        Set<String> b = b2.propertyInheritance();

        if (a == null || a.isEmpty()) return;
        if (b == null || b.isEmpty()) return;

        LinkedHashSet<String> keys = new LinkedHashSet<>(a);
        keys.retainAll(b);
        if (keys.isEmpty()) return;

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        Map<String, EntityProperty> p1Defs = indexProps(p1.getIdentifier());
        Map<String, EntityProperty> p2Defs = indexProps(p2.getIdentifier());
        Map<String, EntityProperty> babyDefs = indexProps(baby.getIdentifier());

        for (String key : keys) {
            if (key == null || key.isEmpty()) continue;

            EntityProperty d1 = p1Defs.get(key);
            EntityProperty d2 = p2Defs.get(key);
            EntityProperty db = babyDefs.get(key);

            if (d1 == null || d2 == null || db == null) continue;
            if (!d1.getClass().equals(d2.getClass())) continue;
            if (!d1.getClass().equals(db.getClass())) continue;

            Object v1 = readPropValue(p1, d1, key);
            Object v2 = readPropValue(p2, d2, key);

            Object chosen;
            if (v1 == null && v2 == null) continue;
            else if (v1 == null) chosen = v2;
            else if (v2 == null) chosen = v1;
            else if (Objects.equals(v1, v2)) chosen = v1;
            else chosen = rng.nextBoolean() ? v1 : v2;

            writePropValue(baby, db, key, chosen);
        }
    }

    private Map<String, EntityProperty> indexProps(String entityIdentifier) {
        List<EntityProperty> list = EntityProperty.getEntityProperty(entityIdentifier);
        if (list == null || list.isEmpty()) return Collections.emptyMap();

        HashMap<String, EntityProperty> out = new HashMap<>(list.size());
        for (EntityProperty p : list) {
            if (p == null) continue;
            String id = p.getIdentifier();
            if (id == null || id.isEmpty()) continue;
            out.put(id, p);
        }
        return out;
    }

    private Object readPropValue(EntityIntelligent e, EntityProperty def, String key) {
        if (def instanceof IntEntityProperty) {
            return e.getIntEntityProperty(key);
        }
        if (def instanceof FloatEntityProperty) {
            return e.getFloatEntityProperty(key);
        }
        if (def instanceof BooleanEntityProperty) {
            return e.getBooleanEntityProperty(key);
        }
        if (def instanceof EnumEntityProperty) {
            return e.getEnumEntityProperty(key);
        }
        return null;
    }

    private void writePropValue(EntityIntelligent e, EntityProperty def, String key, Object value) {
        if (value == null) return;

        if (def instanceof IntEntityProperty) {
            if (value instanceof Number n) e.setIntEntityProperty(key, n.intValue());
            return;
        }
        if (def instanceof FloatEntityProperty) {
            if (value instanceof Number n) e.setFloatEntityProperty(key, n.floatValue());
            return;
        }
        if (def instanceof BooleanEntityProperty) {
            if (value instanceof Boolean b) e.setBooleanEntityProperty(key, b);
            return;
        }
        if (def instanceof EnumEntityProperty enumDef) {
            if (!(value instanceof String s)) return;
            if (enumDef.findIndex(s) == -1) return;
            e.setEnumEntityProperty(key, s);
        }
    }

    protected boolean isBreedingLeader(EntityIntelligent entity, EntityIntelligent spouse) {
        return entity.getId() < spouse.getId();
    }

    protected void clearBreedingState(EntityIntelligent entity) {
        setBreedingTick(entity, 0);
        setBreeding(entity, false);
    }

    protected int getBreedingTick(EntityIntelligent entity) {
        Integer tick = entity.getMemoryStorage().get(CoreMemoryTypes.BREEDING_TICK);
        return tick != null ? tick : 0;
    }

    protected void setBreedingTick(EntityIntelligent entity, int tick) {
        entity.getMemoryStorage().put(CoreMemoryTypes.BREEDING_TICK, tick);
    }

    protected boolean isBreeding(EntityIntelligent entity) {
        Boolean value = entity.getMemoryStorage().get(CoreMemoryTypes.IS_BREEDING);
        return Boolean.TRUE.equals(value);
    }

    protected void setBreeding(EntityIntelligent entity, boolean value) {
        entity.getMemoryStorage().put(CoreMemoryTypes.IS_BREEDING, value);
    }

    protected @Nullable EntityIntelligent getSpouse(EntityIntelligent entity) {
        Entity spouse = entity.getMemoryStorage().get(CoreMemoryTypes.ENTITY_SPOUSE);
        return spouse instanceof EntityIntelligent intelligent ? intelligent : null;
    }
}
