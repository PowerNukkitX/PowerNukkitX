package cn.nukkit.entity.data.profession;

import cn.nukkit.level.Sound;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;

import java.util.HashMap;
import java.util.List;

public abstract class Profession {

    private static final HashMap<Integer, Profession> knownProfessions = new HashMap<>();

    public static void registerProfession(Profession profession) {
        knownProfessions.put(profession.getIndex(), profession);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, Profession> getProfessions() {
        return (HashMap<Integer, Profession>) knownProfessions.clone();
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

    public List<NbtMap> buildTrades(int seed) {
        return new ObjectArrayList<>();
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
