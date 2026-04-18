package cn.nukkit.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;

/**
 * @author Kaooot
 */
@Getter
@RequiredArgsConstructor
public class RuntimeBlockDefinition implements BlockDefinition {

    private final int runtimeId;
}