package cn.nukkit.level.blockstateupdater.util.tagupdater;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CompoundTagUpdater implements Comparable<CompoundTagUpdater> {

    private static final Predicate<CompoundTagEditHelper> COMPOUND_FILTER = helper -> helper.getTag() instanceof Map;

    private final Builder builder = new Builder();
    private final List<Predicate<CompoundTagEditHelper>> filters = new ArrayList<>();
    private final List<Consumer<CompoundTagEditHelper>> updaters = new ArrayList<>();
    private final int version;

    public CompoundTagUpdater(int version) {
        this.version = version;
    }

    private static String getTagValue(Object tag) {
        if (tag == null) {
            return "END";
        } else if (tag instanceof Byte || tag instanceof Short || tag instanceof Integer || tag instanceof Long ||
                tag instanceof Float || tag instanceof Double) {
            return String.valueOf(tag);
        } else if (tag instanceof String) {
            return (String) tag;
        } else if (tag instanceof Boolean) {
            return tag == Boolean.TRUE ? "1" : "0";
        }
        throw new IllegalArgumentException("Invalid tag " + tag.getClass().getSimpleName());
    }

    public int getVersion() {
        return version;
    }

    public boolean update(Map<String, Object> tag) {
        CompoundTagEditHelper filterHelper = new CompoundTagEditHelper(tag);
        for (Predicate<CompoundTagEditHelper> filter : this.filters) {
            if (!filter.test(filterHelper)) {
                return false;
            }
        }

        CompoundTagEditHelper updaterHelper = new CompoundTagEditHelper(tag);
        for (Consumer<CompoundTagEditHelper> updater : this.updaters) {
            updater.accept(updaterHelper);
        }
        return true;
    }

    Builder builder() {
        return builder;
    }

    @Override
    public int compareTo(CompoundTagUpdater o) {
        return Integer.compare(this.version, o.version);
    }

    private static class TagNamePredicate implements Predicate<CompoundTagEditHelper> {
        private final String name;

        private TagNamePredicate(String name) {
            this.name = name;
        }

        @Override
        public boolean test(CompoundTagEditHelper helper) {
            Object tag = helper.getTag();
            return tag instanceof Map && ((Map<String, Object>) tag).containsKey(name);
        }
    }

    private static class TryAddPredicate implements Predicate<CompoundTagEditHelper> {
        private final String name;

        private TryAddPredicate(String name) {
            this.name = name;
        }

        @Override
        public boolean test(CompoundTagEditHelper helper) {
            Object tag = helper.getTag();
            return tag instanceof Map && !((Map<String, Object>) tag).containsKey(name);
        }
    }

    public class Builder {

        public Builder addByte(String name, byte value) {
            CompoundTagUpdater.this.filters.add(COMPOUND_FILTER);
            CompoundTagUpdater.this.updaters.add(helper -> helper.getCompoundTag().put(name, value));
            return this;
        }

        public Builder tryAdd(String name, Object value) {
            CompoundTagUpdater.this.filters.add(new TryAddPredicate(name));
            CompoundTagUpdater.this.updaters.add(helper -> helper.getCompoundTag().put(name, value));
            return this;
        }

        public Builder addInt(String name, int value) {
            CompoundTagUpdater.this.filters.add(COMPOUND_FILTER);
            CompoundTagUpdater.this.updaters.add(helper -> helper.getCompoundTag().put(name, value));
            return this;
        }

        public Builder addCompound(String name) {
            CompoundTagUpdater.this.filters.add(COMPOUND_FILTER);
            CompoundTagUpdater.this.updaters.add(helper -> helper.getCompoundTag().put(name, new LinkedHashMap<>()));
            return this;
        }

        public Builder edit(String name, Consumer<CompoundTagEditHelper> function) {
            CompoundTagUpdater.this.filters.add(new TagNamePredicate(name));
            CompoundTagUpdater.this.updaters.add(helper -> helper.pushChild(name));
            CompoundTagUpdater.this.updaters.add(function);
            CompoundTagUpdater.this.updaters.add(CompoundTagEditHelper::popChild);
            return this;
        }

        public Builder regex(String name, String regex) {
            return this.match(name, regex, true);
        }

        public Builder match(String name, String match) {
            return this.match(name, match, false);
        }

        public Builder match(String name, String match, boolean regex) {
            Pattern pattern = regex ? Pattern.compile(match) : null;

            CompoundTagUpdater.this.filters.add(helper -> {
                Object tag = helper.getTag();
                if (!(tag instanceof Map)) {
                    return false;
                }
                Map<String, Object> compound = (Map<String, Object>) tag;
                if (!compound.containsKey(name)) {
                    return false;
                }

                boolean success = match.isEmpty();
                if (success) {
                    return success;
                }

                Object matchTag = compound.get(name);
                if (regex) {
                    success = pattern.matcher(getTagValue(matchTag)).matches();
                } else {
                    success = match.equals(getTagValue(matchTag));
                }
                return success;
            });
            return this;
        }

        public Builder popVisit() {
            CompoundTagUpdater.this.filters.add(helper -> {
                if (helper.canPopChild()) {
                    helper.popChild();
                    return true;
                }
                return false;
            });
            CompoundTagUpdater.this.updaters.add(CompoundTagEditHelper::popChild);
            return this;
        }

        public Builder remove(String name) {
            CompoundTagUpdater.this.filters.add(new TagNamePredicate(name));
            CompoundTagUpdater.this.updaters.add(helper -> {
                helper.getCompoundTag().remove(name);
            });
            return this;
        }

        public Builder rename(String from, String to) {
            CompoundTagUpdater.this.filters.add(new TagNamePredicate(from));
            CompoundTagUpdater.this.updaters.add(helper -> {
                Map<String, Object> tag = helper.getCompoundTag();
                tag.put(to, tag.remove(from));
            });
            return this;
        }

        public Builder tryEdit(String name, Consumer<CompoundTagEditHelper> function) {
            CompoundTagUpdater.this.updaters.add(helper -> {
                Object tag = helper.getTag();
                if (tag instanceof Map) {
                    Map<String, Object> compoundTag = (Map<String, Object>) tag;
                    if (compoundTag.containsKey(name)) {
                        helper.pushChild(name);
                        function.accept(helper);
                        helper.popChild();
                    }
                }
            });
            return this;
        }

        public Builder visit(String name) {
            CompoundTagUpdater.this.filters.add(helper -> {
                Object tag = helper.getTag();
                if (tag instanceof Map && ((Map<String, Object>) tag).containsKey(name)) {
                    helper.pushChild(name);
                    return true;
                }
                return false;
            });
            CompoundTagUpdater.this.updaters.add(helper -> helper.pushChild(name));
            return this;
        }

        public CompoundTagUpdater build() {
            return CompoundTagUpdater.this;
        }
    }
}
