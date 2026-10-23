package org.powernukkitx.molang;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A compiled Molang expression, evaluated against a {@link MolangContext}.
 * <p>
 * Molang is the small expression language Bedrock behaviour packs use for things like
 * block permutation conditions. This is a self-contained evaluator: it does not run the
 * expression through a scripting engine, so pack-authored text never reaches one, and
 * evaluating a compiled expression neither parses nor allocates a scripting context.
 * <p>
 * Supported: {@code query.}/{@code q.} accessors, {@code variable.}/{@code v.} and
 * {@code temp.}/{@code t.} accessors including assignment, the {@code math.*} functions,
 * string, number and boolean literals, arithmetic, comparison and logical operators, the
 * ternary and {@code ??} operators, and {@code ;}-separated statements with {@code return}.
 * Entity-scoping ({@code ->}) and structures Molang only defines for client rendering are
 * not evaluated; they read as {@code 0}.
 * <p>
 * Instances are immutable and safe to evaluate from several threads at once, provided the
 * {@link MolangContext} handed in is not shared between them.
 *
 * @see #compile(String)
 */
@Slf4j
public final class MolangExpression {

    /**
     * Caps the compile cache so a pack cannot grow it without bound. Behaviour packs reuse
     * a small set of conditions across many permutations, so this holds far more than a
     * realistic pack needs.
     */
    private static final int MAX_CACHE_SIZE = 4096;

    private static final Map<String, MolangExpression> CACHE = new ConcurrentHashMap<>();

    /** An expression that always evaluates to {@code 0}. */
    public static final MolangExpression ZERO = new MolangExpression("0", context -> 0.0d);

    /** One evaluable node of a compiled expression. */
    @FunctionalInterface
    interface Node {
        Object evaluate(MolangContext context);
    }

    private final String source;
    private final Node root;

    private MolangExpression(String source, Node root) {
        this.source = source;
        this.root = root;
    }

    /**
     * Compiles an expression, reusing an earlier compilation of the same source.
     * <p>
     * A malformed expression is logged once and yields {@link #ZERO} rather than throwing,
     * because expressions come from pack files: one bad condition should cost that
     * condition and nothing else. Use {@link #parse(String)} where a malformed expression
     * should be reported instead.
     *
     * @param source the expression; null and blank both compile to {@link #ZERO}
     * @return the compiled expression, never null
     */
    public static @NotNull MolangExpression compile(String source) {
        if (source == null || source.isBlank()) {
            return ZERO;
        }
        MolangExpression cached = CACHE.get(source);
        if (cached != null) {
            return cached;
        }
        MolangExpression compiled;
        try {
            compiled = parse(source);
        } catch (MolangParseException e) {
            log.warn("Could not compile Molang expression, it will evaluate as 0: {}", e.getMessage());
            compiled = ZERO;
        }
        if (CACHE.size() < MAX_CACHE_SIZE) {
            CACHE.putIfAbsent(source, compiled);
        }
        return compiled;
    }

    /**
     * Compiles an expression without caching or swallowing failures.
     *
     * @param source the expression
     * @return the compiled expression
     * @throws MolangParseException when the expression is malformed
     */
    public static @NotNull MolangExpression parse(@NotNull String source) {
        return new MolangExpression(source, new MolangParser(source).parse());
    }

    /**
     * @param context the values to resolve accessors against
     * @return the result, one of {@link Double}, {@link Boolean} or {@link String}
     */
    public @NotNull Object evaluate(@NotNull MolangContext context) {
        try {
            return MolangValue.normalize(root.evaluate(context));
        } catch (RuntimeException e) {
            log.debug("Molang expression failed to evaluate, yielding 0: {}", source, e);
            return 0.0d;
        }
    }

    /**
     * @return the result as a condition, which is what a permutation condition needs
     * @see MolangValue#asBoolean(Object)
     */
    public boolean evaluateAsBoolean(@NotNull MolangContext context) {
        return MolangValue.asBoolean(evaluate(context));
    }

    /**
     * @return the result as a number
     * @see MolangValue#asDouble(Object)
     */
    public double evaluateAsDouble(@NotNull MolangContext context) {
        return MolangValue.asDouble(evaluate(context));
    }

    /**
     * @return the source this was compiled from
     */
    public @NotNull String getSource() {
        return source;
    }

    /**
     * Empties the compile cache. Intended for a reload, which recompiles the expressions
     * of the packs that are loaded afterwards.
     */
    public static void clearCache() {
        CACHE.clear();
    }

    public static int cacheSize() {
        return CACHE.size();
    }

    @Override
    public String toString() {
        return "MolangExpression(" + source + ")";
    }
}
