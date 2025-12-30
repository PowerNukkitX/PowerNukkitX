package cn.nukkit.command.tree.node;

import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.tree.ParamList;

/**
 * Abstract base class for generic command parameter nodes in PowerNukkitX command trees.
 * <p>
 * Plugins should extend this class to implement custom command parameter nodes. Provides default implementations for value management,
 * result checking, reset, and initialization with command metadata.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Manages the parsed value and optional state.</li>
 *   <li>Provides default implementations for get, hasResult, reset, isOptional, and init.</li>
 *   <li>Intended for extension by plugin developers for custom parameter types.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Extend this class to implement a custom command parameter node.</li>
 *   <li>Used as the base for all built-in and custom parameter nodes.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * public class MyCustomNode extends ParamNode<MyType> { ... }
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see IParamNode
 * @see ParamList
 * @since PowerNukkitX 1.19.50
 */
public abstract class ParamNode<T> implements IParamNode<T> {
    protected T value = null;
    protected boolean optional;
    protected ParamList paramList;

    @Override
    @SuppressWarnings("unchecked")
    public <E> E get() {
        if (value == null) return null;
        else return (E) value;
    }

    @Override
    public boolean hasResult() {
        return value != null;
    }

    @Override
    public void reset() {
        if (this.value != null) this.value = null;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public ParamList getParamList() {
        return paramList;
    }

    @Override
    public IParamNode<T> init(ParamList parent, String name, boolean optional, CommandParamType type, CommandEnum enumData, String postFix) {
        this.paramList = parent;
        this.optional = optional;
        return this;
    }
}
