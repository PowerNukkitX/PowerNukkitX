package cn.nukkit.network.protocol.types;

import lombok.Data;

/**
 * @author Kaooot
 */
@Data
public class SystemDiagnosticTimingInfo {

    private String displayName;
    private long systemIndex;
    private long timeInNS;
    private int percentOfTotal;
}