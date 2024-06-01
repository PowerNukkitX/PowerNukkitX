package cn.nukkit.block;

import cn.nukkit.block.property.type.BlockPropertyType;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.HashUtils;
import cn.nukkit.utils.Identifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.Getter;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Cool_Loong
 */
public final class BlockProperties {
    @Getter
    private final String identifier;
    private final Set<BlockPropertyType<?>> propertyTypeSet;
    private final Map<Short, BlockState> specialValueMap;
    @Getter
    private final BlockState defaultState;
    private final byte bitSize;

    public BlockProperties(String identifier, BlockPropertyType<?>... properties) {
        this(identifier, Set.of(), properties);
    }

    public BlockProperties(String identifier, Set<String> blockTags, BlockPropertyType<?>... properties) {
        Identifier.assertValid(identifier);
        BlockTags.register(identifier, blockTags);
        this.identifier = identifier.intern();
        this.propertyTypeSet = Sets.newHashSet(properties);

        byte specialValueBits = 0;
        for (var value : this.propertyTypeSet) specialValueBits += value.getBitSize();
        this.bitSize = specialValueBits;
        if (this.bitSize <= 16) {
            Pair<Map<Integer, BlockStateImpl>, BlockStateImpl> mapBlockStatePair = initStates();
            var blockStateHashMap = mapBlockStatePair.left();
            this.defaultState = mapBlockStatePair.right();
            this.specialValueMap = blockStateHashMap
                    .values()
                    .stream()
                    .collect(Collectors.toMap(BlockStateImpl::specialValue, Function.identity(), (v1, v2) -> v1, Short2ObjectOpenHashMap::new));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Pair<Map<Integer, BlockStateImpl>, BlockStateImpl> initStates() {
        List<BlockPropertyType<?>> propertyTypeList = this.propertyTypeSet.stream().toList();
        int size = propertyTypeList.size();
        if (size == 0) {
            BlockStateImpl blockState = new BlockStateImpl(identifier, new BlockPropertyType.BlockPropertyValue<?, ?, ?>[]{});
            return Pair.of(new Int2ObjectArrayMap<>(new int[]{blockState.blockStateHash()}, new BlockStateImpl[]{blockState}), blockState);
        }
        Int2ObjectOpenHashMap<BlockStateImpl> blockStates = new Int2ObjectOpenHashMap<>();

        // to keep track of next element in each of
        // the n arrays
        int[] indices = new int[size];

        // initialize with first element's index
        Arrays.fill(indices, 0);

        while (true) {
            // Generate BlockState
            ImmutableList.Builder<BlockPropertyType.BlockPropertyValue<?, ?, ?>> values = ImmutableList.builder();
            for (int i = 0; i < size; ++i) {
                BlockPropertyType<?> type = propertyTypeList.get(i);
                values.add(type.tryCreateValue(type.getValidValues().get(indices[i])));
            }
            BlockStateImpl state = new BlockStateImpl(identifier, values.build().toArray(BlockPropertyType.BlockPropertyValue<?, ?, ?>[]::new));
            blockStates.put(state.blockStateHash(), state);

            // find the rightmost array that has more
            // elements left after the current element
            // in that array
            int next = size - 1;
            while (next >= 0 && (indices[next] + 1 >= propertyTypeList.get(next).getValidValues().size())) {
                next--;
            }

            // no such array is found so no more
            // combinations left
            if (next < 0) break;

            // if found move to next element in that
            // array
            indices[next]++;

            // for all arrays to the right of this
            // array current index again points to
            // first element
            for (int i = next + 1; i < size; i++) {
                indices[i] = 0;
            }
        }
        int defaultStateHash = HashUtils.computeBlockStateHash(this.identifier, propertyTypeSet.stream().map(p -> p.tryCreateValue(p.getDefaultValue())).collect(Collectors.toList()));
        BlockStateImpl defaultState = null;
        for (var s : blockStates.values()) {
            if (s.blockStateHash() == defaultStateHash) {
                defaultState = s;
                break;
            }
        }
        if (defaultState == null)
            throw new IllegalArgumentException("Can't find default block state for block: " + identifier);
        return Pair.of(blockStates, defaultState);
    }

    public BlockState getBlockState(short specialValue) {
        return specialValueMap.get(specialValue);
    }

    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> BlockState getBlockState(PROPERTY property, DATATYPE value) {
        return defaultState.setPropertyValue(this, property, value);
    }

    public BlockState getBlockState(BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue) {
        return defaultState.setPropertyValue(this, propertyValue);
    }

    public BlockState getBlockState(BlockPropertyType.BlockPropertyValue<?, ?, ?>... values) {
        return defaultState.setPropertyValues(this, values);
    }

    public byte getSpecialValueBits() {
        return bitSize;
    }

    public boolean containBlockState(BlockState blockState) {
        return this.specialValueMap.containsValue(blockState);
    }

    public boolean containBlockState(short specialValue) {
        return this.specialValueMap.containsKey(specialValue);
    }

    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> boolean containProperty(PROPERTY property) {
        return propertyTypeSet.contains(property);
    }

    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> DATATYPE getPropertyValue(int specialValue, PROPERTY p) {
        return getBlockState((short) specialValue).getPropertyValue(p);
    }

    @UnmodifiableView
    public Set<BlockPropertyType<?>> getPropertyTypeSet() {
        return Collections.unmodifiableSet(propertyTypeSet);
    }

    @UnmodifiableView
    public Map<Short, BlockState> getSpecialValueMap() {
        return Collections.unmodifiableMap(specialValueMap);
    }
}
