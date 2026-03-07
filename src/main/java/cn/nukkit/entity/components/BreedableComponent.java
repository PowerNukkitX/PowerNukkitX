package cn.nukkit.entity.components;

import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.EntityFilter;

import java.util.*;


/**
 * Bedrock component: {@code minecraft:breedable}.
 *
 * Defines breeding behavior and inheritance rules for entities.
 * This component is a data definition only; actual breeding requires
 * appropriate AI sensors/executors (for example the server's breeding
 * behavior executor and its target selection logic).
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code love_filters} — Optional filters that gate entering love mode.</li>
 *   <li>{@code allow_sitting} — Whether sitting entities may breed (default: false).</li>
 *   <li>{@code blend_attributes} — Optional attribute identifiers to blend for offspring (ex: health, movement, jump strength).</li>
 *   <li>{@code breed_cooldown} — Cooldown between successful breeds in seconds (default: 60).</li>
 *   <li>{@code breed_items} — Items that trigger love mode (normalized to namespaced ids).</li>
 *   <li>{@code breeds_with} — Mate compatibility rules (mate type + baby type).</li>
 *   <li>{@code causes_pregnancy} — If true, breeding sets pregnancy instead of immediately spawning a baby (default: false).</li>
 *   <li>{@code deny_parents_variant} — Optional rule to prevent inheriting parent variants within a range (chance + min/max).</li>
 *   <li>{@code environment_requirements} — Optional environmental blocks/count/radius requirements.</li>
 *   <li>{@code extra_baby_chance} — Additional chance to spawn an extra baby (accepts 0..1 or 0..100) (default: 0).</li>
 *   <li>{@code inherit_tamed} — If true, offspring inherits tamed state (default: false).</li>
 *   <li>{@code mutation_factor} — Optional mutation factors for variant/extra_variant/color (0..1 or 0..100).</li>
 *   <li>{@code combine_parent_colors} — If true, applies dye mixing rules when both parents provide a dye color (default: false).</li>
 *   <li>{@code require_full_health} — If true, parents must be at full health to breed (default: false).</li>
 *   <li>{@code require_tame} — If true, entity must be tamed to breed (default: true).</li>
 *   <li>{@code property_inheritance} — Set of entity properties to inherit from parents (normalized to namespaced ids).</li>
 * </ul>
 * 
 * @author Curse
 */
public record BreedableComponent(
        EntityFilter loveFilters,
        Boolean allowSitting,
        List<String> blendAttributes,
        Float breedCooldown,
        Set<String> breedItems,
        List<BreedsWith> breedsWith,
        Boolean causesPregnancy,
        DenyParentsVariant denyParentsVariant,
        List<EnvironmentRequirement> environmentRequirements,
        Float extraBabyChance,
        Boolean inheritTamed,
        MutationFactor mutationFactor,
        Boolean combineParentColors,
        Boolean requireFullHealth,
        Boolean requireTame,
        Set<String> propertyInheritance
    ) {

    public BreedableComponent(EntityFilter loveFilter, Set<String> breedItems, List<BreedsWith> breedsWith, Boolean requireTame) {
        this(
            loveFilter,
            null,
            null,
            null,
            breedItems,
            breedsWith,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            requireTame,
            null
        );
    }

    public BreedableComponent(Set<String> breedItems, List<BreedsWith> breedsWith, Boolean requireTame) {
        this(
            null,
            null,
            null,
            null,
            breedItems,
            breedsWith,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            requireTame,
            null
        );
    }

    public BreedableComponent {
        if (breedCooldown != null && !Float.isFinite(breedCooldown)) breedCooldown = null;
        if (breedCooldown != null) breedCooldown = Math.max(0.0f, breedCooldown);

        if (extraBabyChance != null && !Float.isFinite(extraBabyChance)) extraBabyChance = null;
        extraBabyChance = normalizePercent(extraBabyChance);

        // Blend attributes
        if (blendAttributes != null) {
            ArrayList<String> out = new ArrayList<>(blendAttributes.size());
            for (String it : blendAttributes) {
                if (it == null) continue;

                String s = it.trim().toLowerCase(Locale.ROOT);
                if (s.isEmpty()) continue;

                if (!s.contains(":")) {
                    s = "minecraft:" + s;
                }

                out.add(s);
            }
            blendAttributes = out.isEmpty() ? null : Collections.unmodifiableList(out);
        }

        // Breed items
        if (breedItems != null) {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            for (String it : breedItems) {
                if (it == null) continue;

                String s = it.trim().toLowerCase(Locale.ROOT);
                if (s.isEmpty()) continue;

                if (!s.contains(":")) {
                    s = "minecraft:" + s;
                }

                out.add(s);
            }

            breedItems = out.isEmpty() ? null : Collections.unmodifiableSet(out);
        }

        // Property inheritance
        if (propertyInheritance != null) {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            for (String it : propertyInheritance) {
                if (it == null) continue;

                String s = it.trim().toLowerCase(Locale.ROOT);
                if (s.isEmpty()) continue;

                if (!s.contains(":")) {
                    s = "minecraft:" + s;
                }

                out.add(s);
            }

            propertyInheritance = out.isEmpty() ? null : Collections.unmodifiableSet(out);
        }

        // Breeds with
        if (breedsWith != null) {
            ArrayList<BreedsWith> out = new ArrayList<>(breedsWith.size());
            for (BreedsWith bw : breedsWith) {
                if (bw == null || bw.isEmpty()) continue;
                out.add(bw);
            }
            breedsWith = out.isEmpty() ? null : Collections.unmodifiableList(out);
        }

        // Environmental requirements
        if (environmentRequirements != null) {
            ArrayList<EnvironmentRequirement> out = new ArrayList<>(environmentRequirements.size());
            for (EnvironmentRequirement er : environmentRequirements) {
                if (er == null || er.isEmpty()) continue;
                out.add(er);
            }
            environmentRequirements = out.isEmpty() ? null : Collections.unmodifiableList(out);
        }
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return loveFilters == null
                && allowSitting == null
                && blendAttributes == null
                && breedCooldown == null
                && breedItems == null
                && breedsWith == null
                && causesPregnancy == null
                && denyParentsVariant == null
                && environmentRequirements == null
                && extraBabyChance == null
                && inheritTamed == null
                && mutationFactor == null
                && combineParentColors == null
                && requireFullHealth == null
                && requireTame == null
                && propertyInheritance == null;
    }

    public boolean resolvedAllowSitting() {
        return allowSitting != null ? allowSitting : false;
    }

    public boolean hasBlendAttributes() {
        return blendAttributes != null && !blendAttributes.isEmpty();
    }

    public List<String> resolvedBlendAttributes() {
        return blendAttributes != null ? blendAttributes : Collections.emptyList();
    }

    public float resolvedBreedCooldown() {
        return breedCooldown != null ? breedCooldown : 60.0f;
    }

    public boolean resolvedCausesPregnancy() {
        return causesPregnancy != null ? causesPregnancy : false;
    }

    public float resolvedExtraBabyChance() {
        return extraBabyChance != null ? extraBabyChance : 0.0f;
    }

    public boolean resolvedCombineParentColors() {
        return combineParentColors != null ? combineParentColors : false;
    }

    public boolean resolvedRequireFullHealth() {
        return requireFullHealth != null ? requireFullHealth : false;
    }

    public boolean resolvedRequireTame() {
        return requireTame != null ? requireTame : true;
    }

    public Set<String> resolvedBreedItems() {
        return breedItems != null ? breedItems : Collections.emptySet();
    }

    public Set<String> resolvedPropertyInheritance() {
        return propertyInheritance != null ? propertyInheritance : Collections.emptySet();
    }

    public static List<String> blendAttributesOf(String... attributeNames) {
        if (attributeNames == null || attributeNames.length == 0) return null;

        ArrayList<String> out = new ArrayList<>(attributeNames.length);
        for (String n : attributeNames) {
            if (n == null) continue;
            String s = n.trim();
            if (!s.isEmpty()) out.add(s);
        }

        return out.isEmpty() ? null : out;
    }

    public static List<String> blendAttributesOf(int... attributeIds) {
        if (attributeIds == null || attributeIds.length == 0) return null;

        ArrayList<String> out = new ArrayList<>(attributeIds.length);
        for (int id : attributeIds) {
            Attribute attr = Attribute.getAttribute(id);
            if (attr == null) continue;

            String name = attr.getName();
            if (name == null) continue;

            String s = name.trim();
            if (!s.isEmpty()) out.add(s);
        }

        return out.isEmpty() ? null : out;
    }

    public static BreedableComponent defaults() {
        return new BreedableComponent(
                null,
                null, null, null,
                null, null,
                null, null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private static Float normalizePercent(Float v) {
        if (v == null) return null;
        if (!Float.isFinite(v)) return null;

        float x = v;
        if (x > 1.0f && x <= 100.0f) x = x / 100.0f;

        if (x < 0.0f) x = 0.0f;
        if (x > 1.0f) x = 1.0f;
        return x;
    }

    // ---------------------------
    // Color combine (Dye rules)
    // ---------------------------
    private static final Map<Integer, Map<Integer, Integer>> DYE_COMBOS = buildDyeCombos();

    private static Map<Integer, Map<Integer, Integer>> buildDyeCombos() {
        HashMap<Integer, Map<Integer, Integer>> m = new HashMap<>();
        class H {
            void put(int a, int b, int out) {
                m.computeIfAbsent(a, k -> new HashMap<>()).put(b, out);
                m.computeIfAbsent(b, k -> new HashMap<>()).put(a, out);
            }
        }
        H h = new H();

        h.put(0, 14, 6);   // white + red = pink
        h.put(0, 13, 5);   // white + green = lime
        h.put(0, 11, 3);   // white + blue = light_blue
        h.put(0, 15, 7);   // white + black = gray
        h.put(7, 0, 8);    // gray + white = light_gray
        h.put(14, 4, 1);   // red + yellow = orange
        h.put(14, 11, 10); // red + blue = purple
        h.put(4, 11, 13);  // yellow + blue = green
        h.put(11, 13, 9);  // blue + green = cyan
        h.put(10, 6, 2);   // purple + pink = magenta

        HashMap<Integer, Map<Integer, Integer>> out = new HashMap<>(m.size());
        for (Map.Entry<Integer, Map<Integer, Integer>> e : m.entrySet()) {
            out.put(e.getKey(), Collections.unmodifiableMap(e.getValue()));
        }
        return Collections.unmodifiableMap(out);
    }

    // If dyes are compatible -> returns mixed dye; otherwise returns one parent color randomly
    public static int combineParentColorsOrRandom(int parentAColor, int parentBColor, Random rng) {
        int a = clampDye(parentAColor);
        int b = clampDye(parentBColor);

        if (a == b) return a;

        Map<Integer, Integer> row = DYE_COMBOS.get(a);
        if (row != null) {
            Integer mixed = row.get(b);
            if (mixed != null) return mixed;
        }

        return (rng != null && rng.nextBoolean()) ? a : b;
    }

    private static int clampDye(int v) {
        if (v < 0) return 0;
        if (v > 15) return 15;
        return v;
    }

    public static record BreedsWith(String mateType, String babyType) {
        public BreedsWith {
            mateType = sanitizeId(mateType);
            babyType = sanitizeId(babyType);
        }

        public boolean isEmpty() {
            return mateType == null && babyType == null;
        }
    }

    public static record DenyParentsVariant(Float chance, Integer minVariant, Integer maxVariant) {
        public DenyParentsVariant {
            if (chance != null && !Float.isFinite(chance)) chance = null;
            chance = normalizePercent(chance);

            Integer mn = minVariant;
            Integer mx = maxVariant;

            if (mn != null && mn < 0) mn = 0;
            if (mx != null && mx < 0) mx = 0;

            if (mn != null && mx != null) {
                int a = Math.min(mn, mx);
                int b = Math.max(mn, mx);
                mn = a;
                mx = b;
            }

            minVariant = mn;
            maxVariant = mx;
        }

        public boolean isEmpty() {
            return chance == null && minVariant == null && maxVariant == null;
        }
    }

    public static record EnvironmentRequirement(Set<String> blockTypes, Integer count, Float radius) {
        public EnvironmentRequirement {
            if (blockTypes != null) {
                LinkedHashSet<String> out = new LinkedHashSet<>();
                for (String it : blockTypes) {
                    if (it == null) continue;

                    String s = it.trim().toLowerCase(Locale.ROOT);
                    if (s.isEmpty()) continue;

                    if (!s.contains(":")) {
                        s = "minecraft:" + s;
                    }

                    out.add(s);
                }
                blockTypes = out.isEmpty() ? null : Collections.unmodifiableSet(out);
            }

            if (count != null && count < 0) count = 0;

            if (radius != null && !Float.isFinite(radius)) radius = null;
            if (radius != null) {
                float r = radius;
                if (r < 0.0f) r = 0.0f;
                if (r > 16.0f) r = 16.0f;
                radius = r;
            }
        }

        public boolean isEmpty() {
            return blockTypes == null && count == null && radius == null;
        }
    }

    public static record MutationFactor(Float variant, Float extraVariant, Float color) {
        public MutationFactor {
            if (variant != null && !Float.isFinite(variant)) variant = null;
            if (extraVariant != null && !Float.isFinite(extraVariant)) extraVariant = null;
            if (color != null && !Float.isFinite(color)) color = null;

            variant = normalizePercent(variant);
            extraVariant = normalizePercent(extraVariant);
            color = normalizePercent(color);
        }

        public boolean isEmpty() {
            return variant == null && extraVariant == null && color == null;
        }
    }

    private static String sanitizeId(String s) {
        if (s == null) return null;
        String v = s.trim();
        return v.isEmpty() ? null : v;
    }
}