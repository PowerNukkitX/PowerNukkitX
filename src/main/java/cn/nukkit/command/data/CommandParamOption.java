package cn.nukkit.command.data;

/**
 * Represents options for command parameters in PowerNukkitX.
 * <p>
 * This enum defines special behaviors and constraints that can be applied to command parameters.
 * Options are used to control auto-completion, semantic validation, and chaining behavior for command arguments.
 * <p>
 * Features:
 * <ul>
 *   <li>{@link #SUPPRESS_ENUM_AUTOCOMPLETION}: Prevents auto-completion for enum parameters.</li>
 *   <li>{@link #HAS_SEMANTIC_CONSTRAINT}: Indicates that the parameter has semantic constraints for validation.</li>
 *   <li>{@link #ENUM_AS_CHAINED_COMMAND}: Treats enum values as chained command options for advanced command structures.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Attach options to {@link CommandParameter} instances to modify argument behavior.</li>
 *   <li>Use in command registration and metadata for fine-grained control over argument handling.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandParameter param = CommandParameter.newEnum("mode", true, new String[] {"easy", "hard"}, CommandParamOption.SUPPRESS_ENUM_AUTOCOMPLETION);
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandParameter
 */
public enum CommandParamOption {
    /**
     * Prevents auto-completion for enum parameters.
     */
    SUPPRESS_ENUM_AUTOCOMPLETION,
    /**
     * Indicates that the parameter has semantic constraints for validation.
     */
    HAS_SEMANTIC_CONSTRAINT,
    /**
     * Treats enum values as chained command options for advanced command structures.
     */
    ENUM_AS_CHAINED_COMMAND
}
