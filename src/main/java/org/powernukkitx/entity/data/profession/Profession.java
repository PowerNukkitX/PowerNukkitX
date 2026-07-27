package org.powernukkitx.entity.data.profession;

import org.powernukkitx.level.Sound;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;

public abstract class Profession {

    private static final Int2ObjectOpenHashMap<Profession> knownProfessions = new Int2ObjectOpenHashMap<>();
    private static volatile int registrationVersion;

    public static void registerProfession(Profession profession) {
        knownProfessions.put(profession.getIndex(), profession);
        registrationVersion++;
    }

    public static int getRegistrationVersion() {
        return registrationVersion;
    }

    public static HashMap<Integer, Profession> getProfessions() {
        return new HashMap<>(knownProfessions);
    }

    public static Profession getProfession(int index) {
        return knownProfessions.get(index);
    }

    private final int index;
    private final String blockId;
    private final String name;
    private final Sound workSound;

    public Profession(int index, String blockId, String name, Sound workSound) {
        this.index = index;
        this.blockId = blockId;
        this.name = name;
        this.workSound = workSound;
    }

    public ListTag<CompoundTag> buildTrades(int seed) {
        return new ListTag<>();
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
}