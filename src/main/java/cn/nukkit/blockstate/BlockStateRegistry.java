package cn.nukkit.blockstate;

import cn.nukkit.Server;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.blockproperty.*;
import cn.nukkit.blockproperty.exception.BlockPropertyNotFoundException;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.LinkedCompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.*;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 保存着从{@link BlockState} -> runtimeid 的注册表
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@UtilityClass
@ParametersAreNonnullByDefault
@Log4j2
public class BlockStateRegistry {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public final int BIG_META_MASK = 0xFFFFFFFF;
    public static final AtomicInteger blockPaletteVersion = new AtomicInteger(0);
    private final ExecutorService asyncStateRemover = Executors.newSingleThreadExecutor(t -> new Thread(t, "BlockStateRegistry#asyncStateRemover"));
    private final Pattern BLOCK_ID_NAME_PATTERN = Pattern.compile("^blockid:(\\d+)$");
    private Registration updateBlockRegistration;
    private final Map<BlockState, Registration> blockStateRegistration = new ConcurrentHashMap<>();
    private final Map<String, Registration> stateIdRegistration = new ConcurrentHashMap<>();
    private final Int2ObjectMap<Registration> runtimeIdRegistration = new Int2ObjectOpenHashMap<>();

    @PowerNukkitXOnly
    @Since("1.20.0-r3")
    private final Int2ObjectMap<Registration> blockStateHashRegistration = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<String> blockIdToPersistenceName = new Int2ObjectOpenHashMap<>();
    private final Map<String, Integer> persistenceNameToBlockId = new LinkedHashMap<>();
    private byte[] blockPaletteBytes;
    private List<String> knownStateIds;

    //<editor-fold desc="static initialization" defaultstate="collapsed">
    static {
        init();
    }
    //</editor-fold>

    private void init() {
        //<editor-fold desc="Loading block_ids.csv" defaultstate="collapsed">
        try (InputStream stream = Server.class.getModule().getResourceAsStream("block_ids.csv")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block_ids.csv");
            }

            int count = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    count++;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    Preconditions.checkArgument(parts.length == 2 || parts[0].matches("^[0-9]+$"));
                    if (parts.length > 1 && parts[1].startsWith("minecraft:")) {
                        int id = Integer.parseInt(parts[0]);
                        blockIdToPersistenceName.put(id, parts[1]);
                        persistenceNameToBlockId.put(parts[1], id);
                    }
                }
            } catch (Exception e) {
                throw new IOException("Error reading the line " + count + " of the block_ids.csv", e);
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>

        //<editor-fold desc="Loading canonical_block_states.nbt" defaultstate="collapsed">
        List<CompoundTag> tags = new ArrayList<>();
        knownStateIds = new ArrayList<>();
        try (InputStream stream = Server.class.getModule().getResourceAsStream("canonical_block_states.nbt")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                int runtimeId = 0;
                while (bis.available() > 0) {
                    CompoundTag tag = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
                    tag.putInt("runtimeId", runtimeId++);
                    tag.putInt("blockId", persistenceNameToBlockId.getOrDefault(tag.getString("name").toLowerCase(Locale.ENGLISH), -1));
                    tags.add(tag);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        //</editor-fold>
        Integer infoUpdateRuntimeId = null;

        Set<String> warned = new HashSet<>();

        for (CompoundTag state : tags) {
            int blockId = state.getInt("blockId");
            int runtimeId = state.getInt("runtimeId");
            String name = state.getString("name").toLowerCase();
            if (name.equals("minecraft:unknown")) {
                infoUpdateRuntimeId = runtimeId;
            }

            // Special condition: minecraft:wood maps 3 blocks, minecraft:wood, minecraft:log and minecraft:log2
            // All other cases, register the name normally
            if (isNameOwnerOfId(name, blockId)) {
                registerPersistenceName(blockId, name);
                registerStateId(state, runtimeId);
            } else if (blockId == -1) {
                if (RuntimeItems.getRuntimeMapping().fromIdentifier(name) == null) {
                    if (warned.add(name)) {
                        log.warn("Unknown block id for the block named {}", name);
                    }
                }
                registerStateId(state, runtimeId);
            }
        }
        //update block_ids.csv
        /*TreeMap<Integer, String> ids = new TreeMap<>(Integer::compare);
        for (var block : warned) {
            ids.put(255 - RuntimeItems.getRuntimeMapping().getNetworkIdByNamespaceId(block).getAsInt(), block);
        }
        for (var entry : ids.entrySet()) {
            try {
                var path = Path.of("block_ids.txt");
                if (!path.toFile().exists()) path.toFile().createNewFile();
                Files.writeString(path, entry.getKey() + "," + entry.getValue() + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/

        blockPaletteVersion.set(tags.get(0).getInt("version"));

        if (infoUpdateRuntimeId == null) {
            throw new IllegalStateException("Could not find the minecraft:info_update runtime id!");
        }

        updateBlockRegistration = findRegistrationByRuntimeId(infoUpdateRuntimeId);

        try {
            blockPaletteBytes = NBTIO.write(tags, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @SneakyThrows
    private void registerStateId(CompoundTag block, int runtimeId) {
        String stateId = getStateId(block);
        CompoundTag pureTag = block
                .clone()
                .remove("blockId")
                .remove("version")
                .remove("runtimeId");
        if (!pureTag.contains("states"))
            pureTag.putCompound("states", new CompoundTag());
        else pureTag.putCompound("states", new CompoundTag(new TreeMap<>(pureTag.getCompound("states").getTags())));
        Registration registration = new Registration(null, runtimeId, MinecraftNamespaceComparator.fnv1a_32(NBTIO.write(pureTag, ByteOrder.LITTLE_ENDIAN)), block);

        Registration old = stateIdRegistration.putIfAbsent(stateId, registration);
        if (old != null && !old.equals(registration)) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:" + old + ", New:" + runtimeId + ", State:" + stateId);
        }
        knownStateIds.add(stateId);
        runtimeIdRegistration.put(runtimeId, registration);
        blockStateHashRegistration.put(registration.blockStateHash, registration);
    }

    private void registerState(int blockId, int meta, CompoundTag originalState, int runtimeId) {
        BlockState state = BlockState.of(blockId, meta);
        Registration registration = new Registration(state, runtimeId, state.getBlock().computeBlockStateHash(), null);

        Registration old = blockStateRegistration.putIfAbsent(state, registration);
        if (old != null && !registration.equals(old)) {
            throw new UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:" + old + ", New:" + runtimeId + ", State:" + state);
        }
        runtimeIdRegistration.put(runtimeId, registration);
        blockStateHashRegistration.put(registration.blockStateHash, registration);

        stateIdRegistration.remove(getStateId(originalState));
        stateIdRegistration.remove(state.getLegacyStateId());
    }

    private boolean isNameOwnerOfId(String name, int blockId) {
        return blockId != -1 && !name.equals("minecraft:wood") || blockId == BlockID.WOOD_BARK;
    }

    @NotNull
    private String getStateId(CompoundTag block) {
        Map<String, String> propertyMap = new TreeMap<>(HumanStringComparator.getInstance());
        for (Tag tag : block.getCompound("states").getAllTags()) {
            propertyMap.put(tag.getName(), tag.parseValue().toString());
        }

        String blockName = block.getString("name").toLowerCase(Locale.ENGLISH);
        Preconditions.checkArgument(!blockName.isEmpty(), "Couldn't find the block name!");
        StringBuilder stateId = new StringBuilder(blockName);
        propertyMap.forEach((name, value) -> stateId.append(';').append(name).append('=').append(value));
        return stateId.toString();
    }

    @Nullable
    private static Registration findRegistrationByRuntimeId(int runtimeId) {
        return runtimeIdRegistration.get(runtimeId);
    }

    @Nullable
    private BlockState buildStateFromCompound(CompoundTag block) {
        String name = block.getString("name").toLowerCase(Locale.ENGLISH);
        Integer id = getBlockId(name);
        if (id == null) {
            //处理在调用getBlockStateByRuntimeId时，遇到在block_mappings.json中方块的情况
            String stateId = getStateId(block);
            String fullId = RuntimeItemMapping.getBlockMapping().inverse().get(stateId);
            if (fullId != null) {
                List<String> sId = StringUtils.fastSplit(":", fullId);
                int blockId = Integer.parseInt(sId.get(0));
                int blockData = Integer.parseInt(sId.get(1));
                return BlockState.of(blockId, blockData);
            }
            return null;
        }

        BlockState state = BlockState.of(id);
        CompoundTag properties = block.getCompound("states");
        for (Tag tag : properties.getAllTags()) {
            state = state.withProperty(tag.getName(), tag.parseValue().toString());
        }

        return state;
    }

    private static NoSuchElementException runtimeIdNotRegistered(int runtimeId) {
        return new NoSuchElementException("The block id for the runtime id " + runtimeId + " is not registered");
    }

    private Registration getRegistration(BlockState state) {
        return blockStateRegistration.computeIfAbsent(state, BlockStateRegistry::findRegistration);
    }

    private Registration findRegistration(final BlockState state) {
        // Special case for PN-96 PowerNukkit#210 where the world contains blocks like 0:13, 0:7, etc
        if (state.getBlockId() == BlockID.AIR) {
            Registration airRegistration = blockStateRegistration.get(BlockState.AIR);
            if (airRegistration != null) {
                return new Registration(state, airRegistration.runtimeId, airRegistration.blockStateHash, null);
            }
        }

        Registration registration = findRegistrationByStateId(state);
        removeStateIdsAsync(registration);
        return registration;
    }

    private Registration findRegistrationByStateId(BlockState state) {
        Registration registration;
        try {
            registration = stateIdRegistration.remove(state.getStateId());
            if (registration != null) {
                registration.state = state;
                registration.originalBlock = null;
                return registration;
            }
        } catch (Exception e) {
            try {
                log.fatal("An error has occurred while trying to get the stateId of state: "
                                + "{}:{}"
                                + " - {}"
                                + " - {}",
                        state.getBlockId(),
                        state.getDataStorage(),
                        state.getProperties(),
                        blockIdToPersistenceName.get(state.getBlockId()),
                        e);
            } catch (Exception e2) {
                e.addSuppressed(e2);
                log.fatal("An error has occurred while trying to get the stateId of state: {}:{}",
                        state.getBlockId(), state.getDataStorage(), e);
            }
        }

        try {
            registration = stateIdRegistration.remove(state.getLegacyStateId());
            if (registration != null) {
                registration.state = state;
                registration.originalBlock = null;
                return registration;
            }
        } catch (Exception e) {
            log.fatal("An error has occurred while trying to parse the legacyStateId of {}:{}", state.getBlockId(), state.getDataStorage(), e);
        }
        return logDiscoveryError(state);
    }

    private void removeStateIdsAsync(@Nullable Registration registration) {
        if (registration != null && registration != updateBlockRegistration) {
            asyncStateRemover.submit(() -> stateIdRegistration.values().removeIf(r -> r.runtimeId == registration.runtimeId));
        }
    }

    private Registration logDiscoveryError(BlockState state) {
        log.error("Found an unknown BlockId:Meta combination: {}:{}"
                        + " - {}"
                        + " - {}"
                        + " - {}"
                        + ", trying to repair or replacing with an \"UPDATE!\" block.",
                state.getBlockId(), state.getDataStorage(), state.getStateId(), state.getProperties(),
                blockIdToPersistenceName.get(state.getBlockId())
        );
        return updateBlockRegistration;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public int getBlockIdByRuntimeId(int runtimeId) {
        Registration registration = findRegistrationByRuntimeId(runtimeId);
        if (registration == null) {
            throw runtimeIdNotRegistered(runtimeId);
        }
        BlockState state = registration.state;
        if (state != null) {
            return state.getBlockId();
        }
        CompoundTag originalBlock = registration.originalBlock;
        if (originalBlock == null) {
            throw runtimeIdNotRegistered(runtimeId);
        }
        try {
            state = buildStateFromCompound(originalBlock);
        } catch (BlockPropertyNotFoundException e) {
            String name = originalBlock.getString("name").toLowerCase(Locale.ENGLISH);
            Integer id = getBlockId(name);
            if (id == null) {
                throw runtimeIdNotRegistered(runtimeId);
            }
            return id;
        }
        if (state != null) {
            registration.state = state;
            registration.originalBlock = null;
        } else {
            throw runtimeIdNotRegistered(runtimeId);
        }
        return state.getBlockId();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getRuntimeId(BlockState state) {
        String blockMapping = RuntimeItemMapping.getBlockMapping().getOrDefault(state.getBlockId() + ":" + state.getDataStorage().intValue(), null);
        if (blockMapping != null) {
            return stateIdRegistration.get(blockMapping).runtimeId;
        }
        return getRegistration(state).runtimeId;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getRuntimeId(int blockId) {
        return getRuntimeId(BlockState.of(blockId));
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "getRuntimeId(BlockState state)", since = "1.3.0.0-PN")
    public int getRuntimeId(int blockId, int meta) {
        return getRuntimeId(BlockState.of(blockId, meta));
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    @Nullable
    public String getKnownBlockStateIdByRuntimeId(int runtimeId) {
        if (runtimeId >= 0 && runtimeId < knownStateIds.size()) {
            return knownStateIds.get(runtimeId);
        }
        return null;
    }

    @PowerNukkitOnly
    @Since("1.5.2.0-PN")
    public int getKnownRuntimeIdByBlockStateId(String stateId) {
        int result = knownStateIds.indexOf(stateId);
        if (result != -1) {
            return result;
        }
        BlockState state;
        try {
            state = BlockState.of(stateId);
        } catch (NoSuchElementException | IllegalStateException | IllegalArgumentException ignored) {
            return -1;
        }
        String fullStateId = state.getStateId();
        return knownStateIds.indexOf(fullStateId);
    }

    @PowerNukkitXOnly
    @Since("1.20.0-r3")
    public int getRuntimeIdByBlockStateHash(int blockStateHash) {
        var reg = blockStateHashRegistration.get(blockStateHash);
        if (reg != null) return reg.runtimeId;
        else return -1;
    }

    /**
     * @return {@code null} if the runtime id does not matches any known block state.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public BlockState getBlockStateByRuntimeId(int runtimeId) {
        Registration registration = findRegistrationByRuntimeId(runtimeId);
        if (registration == null) {
            return null;
        }
        BlockState state = registration.state;
        if (state != null) {
            return state;
        }
        CompoundTag originalBlock = registration.originalBlock;
        if (originalBlock != null) {
            state = buildStateFromCompound(originalBlock);
            if (state != null) {
                registration.state = state;
                registration.originalBlock = null;
            }
        }
        return state;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public List<String> getPersistenceNames() {
        return new ArrayList<>(persistenceNameToBlockId.keySet());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public String getPersistenceName(int blockId) {
        String persistenceName = blockIdToPersistenceName.get(blockId);
        if (persistenceName == null) {
            String fallback = "blockid:" + blockId;
            log.warn("The persistence name of the block id {} is unknown! Using {} as an alternative!", blockId, fallback);
            registerPersistenceName(blockId, fallback);
            return fallback;
        }
        return persistenceName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void registerPersistenceName(int blockId, String persistenceName) {
        synchronized (blockIdToPersistenceName) {
            String newName = persistenceName.toLowerCase();
            String oldName = blockIdToPersistenceName.putIfAbsent(blockId, newName);
            if (oldName != null && !persistenceName.equalsIgnoreCase(oldName)) {
                throw new UnsupportedOperationException("The persistence name registration tried to replaced a name. Name:" + persistenceName + ", Old:" + oldName + ", Id:" + blockId);
            }
            Integer oldId = persistenceNameToBlockId.putIfAbsent(newName, blockId);
            if (oldId != null && blockId != oldId) {
                blockIdToPersistenceName.remove(blockId);
                throw new UnsupportedOperationException("The persistence name registration tried to replaced an id. Name:" + persistenceName + ", OldId:" + oldId + ", Id:" + blockId);
            }
        }
    }


    @PowerNukkitOnly
    @Since("1.6.0.0-PNX")
    public synchronized static OK<?> registerCustomBlockState(List<CustomBlock> blockCustoms) {
        //清空原本的数据
        blockStateRegistration.clear();
        stateIdRegistration.clear();
        runtimeIdRegistration.clear();
        knownStateIds.clear();
        //按照每组方块(因为每个方块可能有多种状态,将他们归为一个List)的namespace(形如minecraft:xxx)升序排序(遍历时Hash值小的在前面)
        SortedMap<String, List<CompoundTag>> namespace2Nbt = new TreeMap<>(MinecraftNamespaceComparator::compareFNV);
        //处理原版方块
        try (InputStream stream = Server.class.getModule().getResourceAsStream("canonical_block_states.nbt")) {
            if (stream == null) {
                return new OK<>(false, "Unable to locate block state nbt!");
            }
            try (BufferedInputStream bis = new BufferedInputStream(stream)) {
                while (bis.available() > 0) {
                    CompoundTag tag = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true);
                    var name = tag.getString("name");
                    tag.putInt("blockId", persistenceNameToBlockId.getOrDefault(tag.getString("name").toLowerCase(), -1));
                    if (!namespace2Nbt.containsKey(name)) {
                        //每个方块可能有多种状态,将他们归为一个ArrayList
                        namespace2Nbt.put(name, new ArrayList<>());
                    }
                    namespace2Nbt.get(name).add(tag);
                }
            }
        } catch (IOException e) {
            return new OK<>(false, e);
        }

        //处理自定义方块
        for (var blockCustom : blockCustoms) {
            var namespace = blockCustom.getNamespaceId();
            blockIdToPersistenceName.put(blockCustom.getId(), namespace);
            persistenceNameToBlockId.put(namespace, blockCustom.getId());
            CompoundTag nbt = new CompoundTag()
                    .putInt("blockId", blockCustom.getId())
                    .putString("name", namespace)
                    .putInt("version", blockPaletteVersion.get())
                    .putCompound("states", new CompoundTag("states"));
            var nbtList = new ArrayList<CompoundTag>();

            // 多状态方块注册
            if (blockCustom instanceof Block block) {
                var properties = block.getProperties().getAllProperties()
                        .stream().map(BlockProperties.RegisteredBlockProperty::getProperty).toList();
                List<CompoundTag> stateNbtList = null;
                for (var eachProperty : properties) {
                    var newStateNbtList = new LinkedList<CompoundTag>();
                    if (stateNbtList == null) {
                        if (eachProperty instanceof BooleanBlockProperty) {
                            newStateNbtList.add(new LinkedCompoundTag("states").putBoolean(eachProperty.getName(), false));
                            newStateNbtList.add(new LinkedCompoundTag("states").putBoolean(eachProperty.getName(), true));
                        } else if (eachProperty instanceof IntBlockProperty intBlockProperty) {
                            for (int i = intBlockProperty.getMinValue(); i <= intBlockProperty.getMaxValue(); i++) {
                                newStateNbtList.add(new LinkedCompoundTag("states").putInt(eachProperty.getName(), i));
                            }
                        } else if (eachProperty instanceof UnsignedIntBlockProperty unsignedIntBlockProperty) {
                            for (long i = unsignedIntBlockProperty.getMinValue(); i <= unsignedIntBlockProperty.getMaxValue(); i++) {
                                newStateNbtList.add(new LinkedCompoundTag("states").putInt(eachProperty.getName(), (int) i));
                            }
                        } else if (eachProperty instanceof ArrayBlockProperty<?> arrayBlockProperty) {
                            if (arrayBlockProperty.isOrdinal()) {
                                var universe = arrayBlockProperty.getUniverse();
                                for (int i = 0, universeLength = universe.length; i < universeLength; i++) {
                                    newStateNbtList.add(new LinkedCompoundTag("states").putInt(eachProperty.getName(), i));
                                }
                            } else {
                                for (var each : arrayBlockProperty.getUniverse()) {
                                    newStateNbtList.add(new LinkedCompoundTag("states").putString(eachProperty.getName(), each.toString()));
                                }
                            }
                        }
                    } else {
                        for (var stateNbt : stateNbtList) {
                            if (eachProperty instanceof BooleanBlockProperty) {
                                newStateNbtList.add(stateNbt.copy().putBoolean(eachProperty.getName(), false));
                                newStateNbtList.add(stateNbt.copy().putBoolean(eachProperty.getName(), true));
                            } else if (eachProperty instanceof IntBlockProperty intBlockProperty) {
                                for (int i = intBlockProperty.getMinValue(); i <= intBlockProperty.getMaxValue(); i++) {
                                    newStateNbtList.add(stateNbt.copy().putInt(eachProperty.getName(), i));
                                }
                            } else if (eachProperty instanceof UnsignedIntBlockProperty unsignedIntBlockProperty) {
                                for (long i = unsignedIntBlockProperty.getMinValue(); i <= unsignedIntBlockProperty.getMaxValue(); i++) {
                                    newStateNbtList.add(stateNbt.copy().putInt(eachProperty.getName(), (int) i));
                                }
                            } else if (eachProperty instanceof ArrayBlockProperty<?> arrayBlockProperty) {
                                if (arrayBlockProperty.isOrdinal()) {
                                    var universe = arrayBlockProperty.getUniverse();
                                    for (int i = 0, universeLength = universe.length; i < universeLength; i++) {
                                        newStateNbtList.add(stateNbt.copy().putInt(eachProperty.getName(), i));
                                    }
                                } else {
                                    for (var each : arrayBlockProperty.getUniverse()) {
                                        newStateNbtList.add(stateNbt.copy().putString(eachProperty.getName(), each.toString()));
                                    }
                                }
                            }
                        }
                    }
                    stateNbtList = newStateNbtList;
                }
                if (stateNbtList != null) {
                    for (var each : stateNbtList) {
                        nbtList.add(nbt.copy().putCompound("states", each));
                    }
                } else {
                    nbtList.add(nbt.clone());
                }
                namespace2Nbt.put(blockCustom.getNamespaceId(), nbtList);
            } else {
                nbtList.add(nbt.clone());
                namespace2Nbt.put(blockCustom.getNamespaceId(), nbtList);
            }
        }
        List<CompoundTag> tags = new ArrayList<>();
        Set<String> warned = new HashSet<>();
        Integer infoUpdateRuntimeId = null;
        //由排序好的序列计算runtimeId(递增)
        int runtimeId = 0;
        for (var namespace : namespace2Nbt.keySet()) {
            for (var nbt : namespace2Nbt.get(namespace)) {
                nbt.putInt("runtimeId", runtimeId);
                tags.add(nbt);
                int block = nbt.getInt("blockId");
                String name = nbt.getString("name").toLowerCase();
                if (name.equals("minecraft:unknown")) {
                    infoUpdateRuntimeId = runtimeId;
                }
                if (isNameOwnerOfId(name, block)) {
                    //注册stateIdRegistration和runtimeIdRegistration
                    registerStateId(nbt, runtimeId);
                } else if (block == -1) {
                    if (RuntimeItems.getRuntimeMapping().fromIdentifier(name) == null) {
                        if (warned.add(name)) {
                            log.warn("Unknown block id for the block named {}", name);
                        }
                    }
                    registerStateId(nbt, runtimeId);
                }
                ++runtimeId;
            }
        }
        if (infoUpdateRuntimeId == null) {
            return new OK<>(false, new IllegalStateException("Could not find the minecraft:info_update runtime id!"));
        }

        updateBlockRegistration = findRegistrationByRuntimeId(infoUpdateRuntimeId);

        try {
            blockPaletteBytes = NBTIO.write(tags, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            e.printStackTrace();
            return new OK<>(false, e);
        }
        return new OK<Void>(true);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public synchronized static void deleteCustomBlockState() {
        blockStateRegistration.clear();
        stateIdRegistration.clear();
        runtimeIdRegistration.clear();
        persistenceNameToBlockId.clear();
        blockIdToPersistenceName.clear();
        knownStateIds.clear();
        init();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBlockPaletteDataVersion() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        Object obj = blockPaletteBytes;
        return obj.hashCode();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public byte[] getBlockPaletteBytes() {
        return blockPaletteBytes.clone();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void putBlockPaletteBytes(BinaryStream stream) {
        stream.put(blockPaletteBytes);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getBlockPaletteLength() {
        return blockPaletteBytes.length;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void copyBlockPaletteBytes(byte[] target, int targetIndex) {
        System.arraycopy(blockPaletteBytes, 0, target, targetIndex, blockPaletteBytes.length);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings({"deprecation", "squid:CallToDepreca"})
    @NotNull
    public BlockProperties getProperties(int blockId) {
        int fullId = blockId << Block.DATA_BITS;
        if (blockId > Block.MAX_BLOCK_ID && Block.ID_TO_CUSTOM_BLOCK.get(blockId) instanceof Block block1) {
            return block1.getProperties();
        }
        Block block;
        if (fullId >= Block.fullList.length || fullId < 0 || (block = Block.fullList[fullId]) == null) {
            return BlockUnknown.PROPERTIES;
        }
        return block.getProperties();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public MutableBlockState createMutableState(int blockId) {
        return getProperties(blockId).createMutableState(blockId);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public MutableBlockState createMutableState(int blockId, int bigMeta) {
        MutableBlockState blockState = createMutableState(blockId);
        blockState.setDataStorageFromInt(bigMeta);
        return blockState;
    }

    /**
     * @throws InvalidBlockStateException
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public MutableBlockState createMutableState(int blockId, Number storage) {
        MutableBlockState blockState = createMutableState(blockId);
        blockState.setDataStorage(storage);
        return blockState;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getUpdateBlockRegistration() {
        return updateBlockRegistration.runtimeId;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public Integer getBlockId(String persistenceName) {
        Integer blockId = persistenceNameToBlockId.get(persistenceName);
        if (blockId != null) {
            return blockId;
        }

        Matcher matcher = BLOCK_ID_NAME_PATTERN.matcher(persistenceName);
        if (matcher.matches()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getFallbackRuntimeId() {
        return updateBlockRegistration.runtimeId;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getFallbackBlockState() {
        return updateBlockRegistration.state;
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r1")
    public static void close() {
        asyncStateRemover.shutdownNow();
    }

    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    private static class Registration {
        @Nullable
        private BlockState state;
        private final int runtimeId;
        private final int blockStateHash;
        @Nullable
        private CompoundTag originalBlock;
    }

    @Data
    public static class MappingEntry {
        private final int legacyName;
        private final int damage;
    }
}
