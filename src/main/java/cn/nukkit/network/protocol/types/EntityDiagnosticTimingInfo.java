package cn.nukkit.network.protocol.types;

import lombok.Data;

/**
 * @author Kaooot
 */
@Data
public class EntityDiagnosticTimingInfo {

    private String displayName;
    private String entity;
    private long timeInNS;
    private int percentOfTotal;
}