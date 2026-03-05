package cn.nukkit.entity.components;

/**
 * Bedrock component: {@code minecraft:nameable}.
 *
 * Controls whether an entity can receive a custom name using a name tag
 * and whether that custom name is always visible to players.
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code allow_name_tag_renaming} — If true, players can rename the entity using a name tag (default: true).</li>
 *   <li>{@code always_show} — If true, the custom name is always visible above the entity (default: false).</li>
 * </ul>
 * 
 * @author Curse
 */
public record NameableComponent(Boolean allowNameTagRenaming, Boolean alwaysShow) {

    public NameableComponent {
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return allowNameTagRenaming == null && alwaysShow == null;
    }

    public boolean resolvedAllowNameTagRenaming() {
        return allowNameTagRenaming != null ? allowNameTagRenaming : true;
    }

    public boolean resolvedAlwaysShow() {
        return alwaysShow != null ? alwaysShow : false;
    }

    public static NameableComponent defaults() {
        return new NameableComponent(null, null);
    }
}
