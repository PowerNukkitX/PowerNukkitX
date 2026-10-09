package org.powernukkitx.entity.data.profession;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.level.Sound;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.registry.mappings.MappingRegistries;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Collections;
import java.util.Map;

public abstract class Profession {

    private static final Int2ObjectOpenHashMap<Profession> knownProfessions = new Int2ObjectOpenHashMap<>();
    private static final Map<Integer, Profession> knownProfessionsView = Collections.unmodifiableMap(knownProfessions);
    private static volatile int registrationVersion;

    public static void registerProfession(Profession profession) {
        knownProfessions.put(profession.getIndex(), profession);
        registrationVersion++;
    }

    public static int getRegistrationVersion() {
        return registrationVersion;
    }

    public static Map<Integer, Profession> getProfessions() {
        return knownProfessionsView;
    }

    public static Profession getProfession(int index) {
        return knownProfessions.get(index);
    }

    private final int index;
    private final String blockId;
    private final String name;
    private final Sound workSound;
    private final SoundEvent workSoundEvent;

    public Profession(int index, String blockId, String name, Sound workSound) {
        this(index, blockId, name, workSound, resolveWorkSoundEvent(workSound));
    }

    protected Profession(int index, String blockId, String name, Sound workSound, SoundEvent workSoundEvent) {
        this.index = index;
        this.blockId = blockId;
        this.name = name;
        this.workSound = workSound;
        this.workSoundEvent = workSoundEvent;
    }

    private static SoundEvent resolveWorkSoundEvent(Sound sound) {
        Integer id = MappingRegistries.LEVEL_SOUND_EVENT.get().inverse().get(sound.getSound());
        return id == null || id < 0 || id >= SoundEvent.values().length ? SoundEvent.UNDEFINED : SoundEvent.values()[id];
    }

    public ListTag<CompoundTag> buildTrades(int seed) {
        return new ListTag<>();
    }

    /**
     * Builds the trades of a villager whose biome outfit is {@code clothing}. Professions whose
     * offers depend on where the villager comes from override this, the others fall back on
     * {@link #buildTrades(int)}.
     *
     * @param seed     the villager's trade seed
     * @param clothing the villager's biome outfit
     */
    public ListTag<CompoundTag> buildTrades(int seed, EntityVillagerV2.Clothing clothing) {
        return buildTrades(seed);
    }


    public static void init() {
        registerProfession(new ProfessionFarmer());
        registerProfession(new ProfessionFisherman());
        registerProfession(new ProfessionShepherd());
        registerProfession(new ProfessionFletcher());
        registerProfession(new ProfessionLibrarian());
        registerProfession(new ProfessionCartographer());
        registerProfession(new ProfessionCleric());
        registerProfession(new ProfessionArmor());
        registerProfession(new ProfessionWeapon());
        registerProfession(new ProfessionTool());
        registerProfession(new ProfessionButcher());
        registerProfession(new ProfessionLeather());
        registerProfession(new ProfessionMason());
    }

    public String getBlockID() {
        return this.blockId;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public Sound getWorkSound() {
        return this.workSound;
    }

    /**
     * Gets the level sound event associated with this profession's workstation.
     *
     * @return the workstation level sound event
     */
    public SoundEvent getWorkSoundEvent() {
        return this.workSoundEvent;
    }
}
