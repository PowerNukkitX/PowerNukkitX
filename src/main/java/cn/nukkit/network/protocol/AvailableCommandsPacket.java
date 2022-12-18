package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.command.data.*;
import cn.nukkit.network.protocol.types.CommandEnumConstraintData;
import cn.nukkit.utils.BinaryStream;
import com.nukkitx.network.util.Preconditions;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.ToString;

import java.util.*;
import java.util.function.ObjIntConsumer;

import static cn.nukkit.utils.Utils.dynamic;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Made the arg type constants dynamic because they can change in Minecraft updates")
@ToString
public class AvailableCommandsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.AVAILABLE_COMMANDS_PACKET;

    private static final ObjIntConsumer<BinaryStream> WRITE_BYTE = (s, v) -> s.putByte((byte) v);
    private static final ObjIntConsumer<BinaryStream> WRITE_SHORT = BinaryStream::putLShort;
    private static final ObjIntConsumer<BinaryStream> WRITE_INT = BinaryStream::putLInt;

    public static final int ARG_FLAG_VALID = 0x100000;
    public static final int ARG_FLAG_ENUM = 0x200000;
    public static final int ARG_FLAG_POSTFIX = 0x1000000;
    public static final int ARG_FLAG_SOFT_ENUM = 0x4000000;

    public static final int ARG_TYPE_INT = dynamic(1);
    public static final int ARG_TYPE_FLOAT = dynamic(3);
    public static final int ARG_TYPE_VALUE = dynamic(4);
    public static final int ARG_TYPE_WILDCARD_INT = dynamic(5);
    public static final int ARG_TYPE_OPERATOR = dynamic(6);
    public static final int ARG_TYPE_COMPARE_OPERATOR = dynamic(7);
    public static final int ARG_TYPE_TARGET = dynamic(8);
    public static final int ARG_TYPE_WILDCARD_TARGET = dynamic(10);

    public static final int ARG_TYPE_FILE_PATH = dynamic(17);

    public static final int ARG_TYPE_FULL_INTEGER_RANGE = dynamic(23);

    public static final int ARG_TYPE_EQUIPMENT_SLOT = dynamic(37);
    public static final int ARG_TYPE_STRING = dynamic(39);
    public static final int ARG_TYPE_BLOCK_POSITION = dynamic(47);
    public static final int ARG_TYPE_POSITION = dynamic(48);

    public static final int ARG_TYPE_MESSAGE = dynamic(51);
    public static final int ARG_TYPE_RAWTEXT = dynamic(53);
    public static final int ARG_TYPE_JSON = dynamic(57);
    public static final int ARG_TYPE_BLOCK_STATES = dynamic(67);
    public static final int ARG_TYPE_COMMAND = dynamic(70);

    public Map<String, CommandDataVersions> commands;
    @Deprecated
    public final Map<String, List<String>> softEnums = new HashMap<>();
    public final List<CommandEnumConstraintData> constraints = new ObjectArrayList<>();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        //non
    }

    @Override
    public void encode() {
        this.reset();
        Set<String> enumValuesSet = new ObjectOpenHashSet<>();
        Set<String> postfixSet = new ObjectOpenHashSet<>();
        Set<CommandEnum> enumsSet = new ObjectOpenHashSet<>();
        Set<CommandEnum> softEnumsSet = new ObjectOpenHashSet<>();

        // Get all enum values
        for (var entry : commands.entrySet()) {
            var data = entry.getValue().versions.get(0);
            if (data.aliases != null) {
                enumValuesSet.addAll(data.aliases.getValues());
                enumsSet.add(data.aliases);
            }

            for (CommandParameter[] overload : data.overloads.values().stream().map(o -> o.input.parameters).toList()) {
                for (CommandParameter parameter : overload) {
                    CommandEnum commandEnumData = parameter.enumData;
                    if (commandEnumData != null) {
                        if (commandEnumData.isSoft()) {
                            softEnumsSet.add(commandEnumData);
                        } else {
                            enumValuesSet.addAll(commandEnumData.getValues());
                            enumsSet.add(commandEnumData);
                        }
                    }

                    String postfix = parameter.postFix;
                    if (postfix != null) {
                        postfixSet.add(postfix);
                    }
                }
            }
        }

        // Add Constraint Enums
        // Not need it for now
        /*for(CommandEnumData enumData : packet.getConstraints().stream().map(CommandEnumConstraintData::getEnumData).collect(Collectors.toList())) {
            if (enumData.isSoft()) {
                softEnumsSet.add(enumData);
            } else {
                enumsSet.add(enumData);
            }
            enumValuesSet.addAll(Arrays.asList(enumData.getValues()));
        }*/

        List<String> enumValues = new ObjectArrayList<>(enumValuesSet);
        List<String> postFixes = new ObjectArrayList<>(postfixSet);
        List<CommandEnum> enums = new ObjectArrayList<>(enumsSet);
        List<CommandEnum> softEnums = new ObjectArrayList<>(softEnumsSet);

        putUnsignedVarInt(enumValues.size());
        for (var enumValue : enumValues) {
            this.putString(enumValue);
        }
        putUnsignedVarInt(postFixes.size());
        for (var postFix : postFixes) {
            this.putString(postFix);
        }

        this.writeEnums(enumValues, enums);

        this.putUnsignedVarInt(commands.size());
        for (var entry : commands.entrySet()) {
            this.writeCommand(entry, enums, softEnums, postFixes);
        }

        this.putUnsignedVarInt(softEnums.size());
        for (var softEnum : softEnums) {
            this.writeCommandEnum(softEnum);
        }

        // Constraints
        // Not need it for now
        /*helper.writeArray(buffer, packet.getConstraints(), (buf, constraint) -> {
            helper.writeCommandEnumConstraints(buf, constraint, enums, enumValues);
        });*/

        this.putUnsignedVarInt(0);
    }

    private void writeEnums(List<String> values, List<CommandEnum> enums) {
        // Determine width of enum index
        ObjIntConsumer<BinaryStream> indexWriter;
        int valuesSize = values.size();
        if (valuesSize < 0x100) {//256
            indexWriter = WRITE_BYTE;
        } else if (valuesSize < 0x10000) {//65536
            indexWriter = WRITE_SHORT;
        } else {
            indexWriter = WRITE_INT;
        }
        // Write enums
        putUnsignedVarInt(enums.size());
        for (CommandEnum commandEnum : enums) {
            this.putString(commandEnum.getName());

            this.putUnsignedVarInt(commandEnum.getValues().size());
            for (String value : commandEnum.getValues()) {
                int index = values.indexOf(value);
                Preconditions.checkArgument(index > -1, "Invalid enum value detected: " + value);
                indexWriter.accept(this, index);
            }
        }
    }

    private void writeCommand(Map.Entry<String, CommandDataVersions> commandEntry, List<CommandEnum> enums, List<CommandEnum> softEnums, List<String> postFixes) {
        var commandData = commandEntry.getValue().versions.get(0);
        this.putString(commandEntry.getKey());
        this.putString(commandData.description);
        int flags = 0;
        for (CommandData.Flag flag : commandData.flags) {
            flags |= 1 << flag.ordinal();
        }
        this.putLShort(flags);
        this.putByte((byte) commandData.permission);

        CommandEnum aliases = commandData.aliases;
        this.putLInt(commandData.aliases == null ? -1 : enums.indexOf(commandData.aliases));

        Collection<CommandOverload> overloads = commandData.overloads.values();
        this.putUnsignedVarInt(overloads.size());
        for (CommandOverload overload : overloads) {
            this.putUnsignedVarInt(overload.input.parameters.length);
            for (CommandParameter param : overload.input.parameters) {
                this.writeParameter(param, enums, softEnums, postFixes);
            }
        }
    }

    private void writeParameter(CommandParameter param, List<CommandEnum> enums, List<CommandEnum> softEnums, List<String> postFixes) {
        this.putString(param.name);

        int index;
        boolean postfix = false;
        boolean enumData = false;
        boolean softEnum = false;
        if (param.postFix != null) {
            postfix = true;
            index = postFixes.indexOf(param.postFix);
        } else if (param.enumData != null) {
            if (param.enumData.isSoft()) {
                softEnum = true;
                index = softEnums.indexOf(param.enumData);
            } else {
                enumData = true;
                index = enums.indexOf(param.enumData);
            }
        } else if (param.type != null) {
            index = param.type.getId();
        } else {
            throw new IllegalStateException("No param type specified: " + param);
        }

        int value = index;
        if (enumData) {
            value |= ARG_FLAG_ENUM;
        }
        if (softEnum) {
            value |= ARG_FLAG_SOFT_ENUM;
        }
        if (postfix) {
            value |= ARG_FLAG_POSTFIX;
        } else {
            value |= ARG_FLAG_VALID;
        }
        this.putLInt(value);
        this.putBoolean(param.optional);

        byte options = 0;
        if (param.paramOptions != null) {
            for (CommandParamOption option : param.paramOptions) {
                options |= 1 << option.ordinal();
            }
        }
        this.putByte(options);
    }

    private void writeCommandEnum(CommandEnum enumData) {
        Preconditions.checkNotNull(enumData, "enumData");

        this.putString(enumData.getName());

        List<String> values = enumData.getValues();
        this.putUnsignedVarInt(values.size());
        for (String value : values) {
            this.putString(value);
        }
    }

}
