package cn.nukkit.entity.data.profession;

import cn.nukkit.api.PowerNukkitXInternal;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import java.util.HashMap;

public abstract class Profession {

    private static final HashMap<Integer, Profession> knownProfessions = new HashMap<>();

    public static void registerProfession(Profession profession) {
        knownProfessions.put(profession.getIndex(), profession);
    }

    public static HashMap<Integer, Profession> getProfessions() {
        return (HashMap<Integer, Profession>) knownProfessions.clone();
    }

    public static Profession getProfession(int index) {
        return knownProfessions.get(index);
    }

    private final int index;
    private final int blockid;
    private final String name;

    public Profession(int index, int blockid, String name) {
        this.index = index;
        this.blockid = blockid;
        this.name = name;
    }

    public ListTag<Tag> buildTrades(int seed) {
        ListTag<Tag> recipes = new ListTag<>("Recipes");
        return recipes;
    }

    @PowerNukkitXInternal
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

    public int getBlockID() {
        return this.blockid;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }
}
