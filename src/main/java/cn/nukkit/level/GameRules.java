package cn.nukkit.level;

import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.connection.util.HandleByteBuf;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static cn.nukkit.level.GameRule.*;


public class GameRules {
    private final @Nonnull EnumMap<GameRule, Value<?>> gameRules = new EnumMap<>(GameRule.class);
    private boolean stale;

    private GameRules() {
    }

    public static GameRules getDefault() {
        GameRules gameRules = new GameRules();

        gameRules.gameRules.put(COMMAND_BLOCKS_ENABLED, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(COMMAND_BLOCK_OUTPUT, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_DAYLIGHT_CYCLE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_ENTITY_DROPS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_FIRE_TICK, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_INSOMNIA, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_IMMEDIATE_RESPAWN, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(DO_MOB_LOOT, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_MOB_SPAWNING, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_TILE_DROPS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DO_WEATHER_CYCLE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(DROWNING_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FALL_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FIRE_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FREEZE_DAMAGE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(FUNCTION_COMMAND_LIMIT, new Value<>(Type.INTEGER, 10000));
        gameRules.gameRules.put(KEEP_INVENTORY, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(MAX_COMMAND_CHAIN_LENGTH, new Value<>(Type.INTEGER, 65536));
        gameRules.gameRules.put(MOB_GRIEFING, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(NATURAL_REGENERATION, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(PVP, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(RANDOM_TICK_SPEED, new Value<>(Type.INTEGER, 3));
        gameRules.gameRules.put(SEND_COMMAND_FEEDBACK, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_COORDINATES, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(SHOW_DEATH_MESSAGES, new Value<>(Type.BOOLEAN, true));

        @SuppressWarnings("deprecation") GameRule deprecated = SHOW_DEATH_MESSAGE;
        gameRules.gameRules.put(deprecated, gameRules.gameRules.get(SHOW_DEATH_MESSAGES));

        gameRules.gameRules.put(SPAWN_RADIUS, new Value<>(Type.INTEGER, 5));
        gameRules.gameRules.put(TNT_EXPLODES, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_TAGS, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(EXPERIMENTAL_GAMEPLAY, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(PLAYERS_SLEEPING_PERCENTAGE, new Value<>(Type.INTEGER, 100));
        gameRules.gameRules.put(DO_LIMITED_CRAFTING, new Value<>(Type.BOOLEAN, false));
        gameRules.gameRules.put(RESPAWN_BLOCKS_EXPLODE, new Value<>(Type.BOOLEAN, true));
        gameRules.gameRules.put(SHOW_BORDER_EFFECT, new Value<>(Type.BOOLEAN, true));

        return gameRules;
    }

    public Map<GameRule, Value<?>> getGameRules() {
        return ImmutableMap.copyOf(gameRules);
    }

    public boolean isStale() {
        return stale;
    }

    public void refresh() {
        stale = false;
    }

    public <V> void setGameRule(GameRule gameRule, V value, Type type) {
        if (!gameRules.containsKey(gameRule)) {
            throw new IllegalArgumentException("Gamerule does not exist");
        }
        ((Value<V>) gameRules.get(gameRule)).setValue(value, type);
        stale = true;
    }

    public void setGameRule(GameRule gameRule, boolean value) {
        setGameRule(gameRule, value, Type.BOOLEAN);
    }

    public void setGameRule(GameRule gameRule, int value) {
        setGameRule(gameRule, value, Type.INTEGER);
    }

    public void setGameRule(GameRule gameRule, float value) {
        setGameRule(gameRule, value, Type.FLOAT);
    }

    public void setGameRule(GameRule gameRule, String value) throws IllegalArgumentException {
        Preconditions.checkNotNull(gameRule, "gameRule");
        Preconditions.checkNotNull(value, "value");

        switch (getGameRuleType(gameRule)) {
            case BOOLEAN -> {
                if (value.equalsIgnoreCase("true")) {
                    setGameRule(gameRule, true);
                } else if (value.equalsIgnoreCase("false")) {
                    setGameRule(gameRule, false);
                } else {
                    throw new IllegalArgumentException("Was not a boolean");
                }
            }
            case INTEGER -> setGameRule(gameRule, Integer.parseInt(value));
            case FLOAT -> setGameRule(gameRule, Float.parseFloat(value));
        }
    }

    public boolean getBoolean(GameRule gameRule) {
        return gameRules.get(gameRule).getValueAsBoolean();
    }

    public int getInteger(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsInteger();
    }

    public float getFloat(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getValueAsFloat();
    }

    public String getString(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).value.toString();
    }

    public Type getGameRuleType(GameRule gameRule) {
        Preconditions.checkNotNull(gameRule, "gameRule");
        return gameRules.get(gameRule).getType();
    }

    public boolean hasRule(GameRule gameRule) {
        return gameRules.containsKey(gameRule);
    }

    public GameRule[] getRules() {
        return gameRules.keySet().toArray(EMPTY_ARRAY);
    }

    // TODO: This needs to be moved out since there is not a separate compound tag in the LevelDB format for Game Rules.
    public CompoundTag writeNBT() {
        CompoundTag nbt = new CompoundTag();

        for (Entry<GameRule, Value<?>> entry : gameRules.entrySet()) {
            nbt.putString(entry.getKey().getName(), entry.getValue().value.toString());
        }

        return nbt;
    }

    public void readNBT(CompoundTag nbt) {
        Preconditions.checkNotNull(nbt);
        for (String key : nbt.getTags().keySet()) {
            Optional<GameRule> gameRule = GameRule.parseString(key);
            if (!gameRule.isPresent()) {
                continue;
            }

            setGameRule(gameRule.get(), nbt.getString(key));
        }
    }

    public enum Type {
        UNKNOWN {
            @Override
            void write(HandleByteBuf pk, Value<?> value) {
            }
        },
        BOOLEAN {
            @Override
            void write(HandleByteBuf pk, Value<?> value) {
                pk.writeBoolean(value.getValueAsBoolean());
            }
        },
        INTEGER {
            @Override
            void write(HandleByteBuf pk, Value<?> value) {
                pk.writeUnsignedVarInt(value.getValueAsInteger());
            }
        },
        FLOAT {
            @Override
            void write(HandleByteBuf pk, Value<?> value) {
                pk.writeFloatLE(value.getValueAsFloat());
            }
        };

        abstract void write(HandleByteBuf pk, Value<?> value);
    }

    public static class Value<T> {
        private final Type type;
        private T value;
        private boolean canBeChanged;

        public Value(Type type, T value) {
            this.type = type;
            this.value = value;
        }

        private void setValue(T value, Type type) {
            if (this.type != type) {
                throw new UnsupportedOperationException("Rule not of type " + type.name().toLowerCase());
            }
            this.value = value;
        }

        public boolean isCanBeChanged() {
            return this.canBeChanged;
        }

        public void setCanBeChanged(boolean canBeChanged) {
            this.canBeChanged = canBeChanged;
        }

        public Type getType() {
            return this.type;
        }

        public Tag getTag() {
            return switch (this.type) {
                case BOOLEAN -> new ByteTag(getValueAsBoolean() ? 1 : 0);
                case INTEGER -> new IntTag(getValueAsInteger());
                case FLOAT -> new FloatTag(getValueAsFloat());
                case UNKNOWN -> throw new IllegalArgumentException("unknown type");
            };
        }

        private boolean getValueAsBoolean() {
            if (type != Type.BOOLEAN) {
                throw new UnsupportedOperationException("Rule not of type boolean");
            }
            return (Boolean) value;
        }

        private int getValueAsInteger() {
            if (type != Type.INTEGER) {
                throw new UnsupportedOperationException("Rule not of type integer");
            }
            return (Integer) value;
        }

        private float getValueAsFloat() {
            if (type != Type.FLOAT) {
                throw new UnsupportedOperationException("Rule not of type float");
            }
            return (Float) value;
        }

        public void write(HandleByteBuf stream) {
            stream.writeBoolean(this.canBeChanged);
            stream.writeUnsignedVarInt(type.ordinal());
            type.write(stream, this);
        }
    }
}
