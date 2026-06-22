package cn.nukkit.network.protocol.types;

import lombok.Data;

/**
 * @author Kaooot
 */
@Data
public class WhiskerScopeDataSummary {

    private String indentation;
    private String label;
    private long totalHighCostNS;
    private long totalMidCostNS;
    private long totalLowCostNS;
}