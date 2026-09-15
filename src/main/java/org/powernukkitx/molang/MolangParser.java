package org.powernukkitx.molang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Recursive descent parser turning Molang source into a tree of {@link MolangExpression.Node}
 * closures, evaluated without further parsing.
 * <p>
 * Operator precedence, loosest first: {@code ?:} and {@code ??}, {@code ||}, {@code &&},
 * equality, relational, additive, multiplicative, unary, primary. Statements separated by
 * {@code ;} evaluate in order and the expression takes the value of the last one, with
 * {@code return} ending evaluation early.
 */
final class MolangParser {

    private final String source;
    private int position;

    MolangParser(String source) {
        this.source = source;
    }

    /**
     * @throws MolangParseException when the source is malformed or has trailing input
     */
    MolangExpression.Node parse() {
        MolangExpression.Node node = parseStatements();
        skipWhitespace();
        if (position < source.length()) {
            throw error("Unexpected trailing input");
        }
        return node;
    }

    private MolangExpression.Node parseStatements() {
        List<MolangExpression.Node> statements = new ArrayList<>(1);
        while (true) {
            skipWhitespace();
            if (position >= source.length()) {
                break;
            }
            statements.add(parseStatement());
            skipWhitespace();
            if (position < source.length() && source.charAt(position) == ';') {
                position++;
            } else {
                break;
            }
        }
        if (statements.isEmpty()) {
            return context -> 0.0d;
        }
        if (statements.size() == 1) {
            return statements.getFirst();
        }
        List<MolangExpression.Node> ordered = List.copyOf(statements);
        return context -> {
            Object value = 0.0d;
            for (MolangExpression.Node statement : ordered) {
                value = statement.evaluate(context);
            }
            return value;
        };
    }

    private MolangExpression.Node parseStatement() {
        if (matchWord("return")) {
            return parseTernary();
        }
        return parseAssignment();
    }

    /** Handles {@code variable.x = expr}; anything else falls through to an expression. */
    private MolangExpression.Node parseAssignment() {
        int start = position;
        skipWhitespace();
        String accessor = peekAccessorName();
        if (accessor != null) {
            int afterAccessor = position;
            skipWhitespace();
            if (position < source.length() && source.charAt(position) == '='
                    && (position + 1 >= source.length() || source.charAt(position + 1) != '=')) {
                position++;
                MolangExpression.Node value = parseTernary();
                String name = accessor;
                return context -> {
                    Object result = value.evaluate(context);
                    context.setVariable(name, result);
                    return result;
                };
            }
            position = afterAccessor;
            position = start;
        }
        return parseTernary();
    }

    /**
     * @return the variable name when the next token is a {@code variable.}/{@code v.}
     * accessor with no call, leaving the position after it; null otherwise, with the
     * position restored
     */
    private String peekAccessorName() {
        int start = position;
        String prefix = readIdentifier();
        if (prefix == null || !(prefix.equals("variable") || prefix.equals("v") || prefix.equals("temp") || prefix.equals("t"))) {
            position = start;
            return null;
        }
        if (position >= source.length() || source.charAt(position) != '.') {
            position = start;
            return null;
        }
        position++;
        String name = readIdentifier();
        if (name == null) {
            position = start;
            return null;
        }
        return name;
    }

    private MolangExpression.Node parseTernary() {
        MolangExpression.Node condition = parseNullCoalescing();
        skipWhitespace();
        if (position < source.length() && source.charAt(position) == '?') {
            position++;
            MolangExpression.Node ifTrue = parseTernary();
            skipWhitespace();
            if (position < source.length() && source.charAt(position) == ':') {
                position++;
                MolangExpression.Node ifFalse = parseTernary();
                return context -> MolangValue.asBoolean(condition.evaluate(context))
                        ? ifTrue.evaluate(context)
                        : ifFalse.evaluate(context);
            }
            // Molang allows the false branch to be omitted, yielding 0
            return context -> MolangValue.asBoolean(condition.evaluate(context))
                    ? ifTrue.evaluate(context)
                    : 0.0d;
        }
        return condition;
    }

    private MolangExpression.Node parseNullCoalescing() {
        MolangExpression.Node left = parseOr();
        skipWhitespace();
        while (match("??")) {
            MolangExpression.Node right = parseOr();
            MolangExpression.Node current = left;
            // null means the context had no value for the accessor, which is what this
            // falls through on; a resolved 0 is a value and stays
            left = context -> {
                Object value = current.evaluate(context);
                return value == null ? right.evaluate(context) : value;
            };
            skipWhitespace();
        }
        return left;
    }

    private MolangExpression.Node parseOr() {
        MolangExpression.Node left = parseAnd();
        skipWhitespace();
        while (match("||")) {
            MolangExpression.Node right = parseAnd();
            MolangExpression.Node current = left;
            left = context -> MolangValue.asBoolean(current.evaluate(context))
                    || MolangValue.asBoolean(right.evaluate(context));
            skipWhitespace();
        }
        return left;
    }

    private MolangExpression.Node parseAnd() {
        MolangExpression.Node left = parseEquality();
        skipWhitespace();
        while (match("&&")) {
            MolangExpression.Node right = parseEquality();
            MolangExpression.Node current = left;
            left = context -> MolangValue.asBoolean(current.evaluate(context))
                    && MolangValue.asBoolean(right.evaluate(context));
            skipWhitespace();
        }
        return left;
    }

    private MolangExpression.Node parseEquality() {
        MolangExpression.Node left = parseRelational();
        skipWhitespace();
        while (true) {
            if (match("==")) {
                MolangExpression.Node right = parseRelational();
                MolangExpression.Node current = left;
                left = context -> MolangValue.equals(current.evaluate(context), right.evaluate(context));
            } else if (match("!=")) {
                MolangExpression.Node right = parseRelational();
                MolangExpression.Node current = left;
                left = context -> !MolangValue.equals(current.evaluate(context), right.evaluate(context));
            } else {
                return left;
            }
            skipWhitespace();
        }
    }

    private MolangExpression.Node parseRelational() {
        MolangExpression.Node left = parseAdditive();
        skipWhitespace();
        while (true) {
            MolangExpression.Node current = left;
            if (match(">=")) {
                MolangExpression.Node right = parseAdditive();
                left = context -> MolangValue.asDouble(current.evaluate(context)) >= MolangValue.asDouble(right.evaluate(context));
            } else if (match("<=")) {
                MolangExpression.Node right = parseAdditive();
                left = context -> MolangValue.asDouble(current.evaluate(context)) <= MolangValue.asDouble(right.evaluate(context));
            } else if (match(">")) {
                MolangExpression.Node right = parseAdditive();
                left = context -> MolangValue.asDouble(current.evaluate(context)) > MolangValue.asDouble(right.evaluate(context));
            } else if (match("<")) {
                MolangExpression.Node right = parseAdditive();
                left = context -> MolangValue.asDouble(current.evaluate(context)) < MolangValue.asDouble(right.evaluate(context));
            } else {
                return left;
            }
            skipWhitespace();
        }
    }

    private MolangExpression.Node parseAdditive() {
        MolangExpression.Node left = parseMultiplicative();
        skipWhitespace();
        while (true) {
            MolangExpression.Node current = left;
            if (match("+")) {
                MolangExpression.Node right = parseMultiplicative();
                left = context -> MolangValue.asDouble(current.evaluate(context)) + MolangValue.asDouble(right.evaluate(context));
            } else if (matchMinus()) {
                MolangExpression.Node right = parseMultiplicative();
                left = context -> MolangValue.asDouble(current.evaluate(context)) - MolangValue.asDouble(right.evaluate(context));
            } else {
                return left;
            }
            skipWhitespace();
        }
    }

    private MolangExpression.Node parseMultiplicative() {
        MolangExpression.Node left = parseUnary();
        skipWhitespace();
        while (true) {
            MolangExpression.Node current = left;
            if (match("*")) {
                MolangExpression.Node right = parseUnary();
                left = context -> MolangValue.asDouble(current.evaluate(context)) * MolangValue.asDouble(right.evaluate(context));
            } else if (match("/")) {
                MolangExpression.Node right = parseUnary();
                left = context -> {
                    double divisor = MolangValue.asDouble(right.evaluate(context));
                    // Molang yields 0 rather than an infinity or a NaN
                    return divisor == 0.0d ? 0.0d : MolangValue.asDouble(current.evaluate(context)) / divisor;
                };
            } else {
                return left;
            }
            skipWhitespace();
        }
    }

    private MolangExpression.Node parseUnary() {
        skipWhitespace();
        if (match("!")) {
            MolangExpression.Node operand = parseUnary();
            return context -> !MolangValue.asBoolean(operand.evaluate(context));
        }
        if (matchMinus()) {
            MolangExpression.Node operand = parseUnary();
            return context -> -MolangValue.asDouble(operand.evaluate(context));
        }
        return parsePrimary();
    }

    private MolangExpression.Node parsePrimary() {
        skipWhitespace();
        if (position >= source.length()) {
            throw error("Unexpected end of expression");
        }
        char current = source.charAt(position);

        if (current == '(') {
            position++;
            MolangExpression.Node inner = parseTernary();
            skipWhitespace();
            if (position >= source.length() || source.charAt(position) != ')') {
                throw error("Unclosed parenthesis");
            }
            position++;
            return inner;
        }
        if (current == '\'' || current == '"') {
            String text = readString(current);
            return context -> text;
        }
        if (Character.isDigit(current) || current == '.') {
            double value = readNumber();
            return context -> value;
        }
        String identifier = readIdentifier();
        if (identifier == null) {
            throw error("Unexpected character '" + current + "'");
        }
        return parseAccessor(identifier);
    }

    private MolangExpression.Node parseAccessor(String identifier) {
        switch (identifier) {
            case "true" -> {
                return context -> Boolean.TRUE;
            }
            case "false" -> {
                return context -> Boolean.FALSE;
            }
            default -> {
            }
        }

        if (position >= source.length() || source.charAt(position) != '.') {
            // a bare word is not something Molang defines; treat it as an unset value
            return context -> 0.0d;
        }
        position++;
        String member = readIdentifier();
        if (member == null) {
            throw error("Expected a name after '" + identifier + ".'");
        }

        List<MolangExpression.Node> arguments = parseArgumentList();
        return switch (identifier) {
            case "query", "q" -> queryNode(member, arguments);
            case "math" -> MolangMath.node(member, arguments, this);
            // an unresolved accessor stays null so that '??' can tell absent from zero;
            // every coercion treats null as 0, and evaluate() normalises the result
            case "variable", "v", "temp", "t", "context", "c" -> context -> context.variable(member);
            // an unknown namespace is not an error in Molang, it simply has no value
            default -> context -> 0.0d;
        };
    }

    private MolangExpression.Node queryNode(String member, List<MolangExpression.Node> arguments) {
        if (arguments.isEmpty()) {
            return context -> context.query(member, EMPTY_ARGUMENTS);
        }
        List<MolangExpression.Node> ordered = List.copyOf(arguments);
        return context -> {
            Object[] values = new Object[ordered.size()];
            for (int i = 0; i < values.length; i++) {
                values[i] = ordered.get(i).evaluate(context);
            }
            return context.query(member, values);
        };
    }

    private static final Object[] EMPTY_ARGUMENTS = new Object[0];

    /**
     * @return the parsed arguments, empty when the accessor was written without a call
     */
    private List<MolangExpression.Node> parseArgumentList() {
        skipWhitespace();
        if (position >= source.length() || source.charAt(position) != '(') {
            return List.of();
        }
        position++;
        List<MolangExpression.Node> arguments = new ArrayList<>(2);
        skipWhitespace();
        if (position < source.length() && source.charAt(position) == ')') {
            position++;
            return arguments;
        }
        while (true) {
            arguments.add(parseTernary());
            skipWhitespace();
            if (position >= source.length()) {
                throw error("Unclosed argument list");
            }
            char current = source.charAt(position);
            if (current == ',') {
                position++;
                continue;
            }
            if (current == ')') {
                position++;
                return arguments;
            }
            throw error("Expected ',' or ')' in argument list");
        }
    }

    MolangParseException error(String message) {
        return new MolangParseException(message, source, position);
    }

    private void skipWhitespace() {
        while (position < source.length() && Character.isWhitespace(source.charAt(position))) {
            position++;
        }
    }

    private boolean match(String token) {
        skipWhitespace();
        if (source.startsWith(token, position)) {
            position += token.length();
            return true;
        }
        return false;
    }

    /** Distinguishes the {@code -} operator from the {@code ->} Molang uses for scoping. */
    private boolean matchMinus() {
        skipWhitespace();
        if (position < source.length() && source.charAt(position) == '-'
                && (position + 1 >= source.length() || source.charAt(position + 1) != '>')) {
            position++;
            return true;
        }
        return false;
    }

    private boolean matchWord(String word) {
        skipWhitespace();
        if (!source.startsWith(word, position)) {
            return false;
        }
        int end = position + word.length();
        if (end < source.length() && isIdentifierPart(source.charAt(end))) {
            return false;
        }
        position = end;
        return true;
    }

    private String readIdentifier() {
        skipWhitespace();
        int start = position;
        while (position < source.length() && isIdentifierPart(source.charAt(position))) {
            position++;
        }
        if (start == position) {
            return null;
        }
        return source.substring(start, position).toLowerCase(Locale.ENGLISH);
    }

    private static boolean isIdentifierPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private String readString(char quote) {
        position++;
        int start = position;
        while (position < source.length() && source.charAt(position) != quote) {
            position++;
        }
        if (position >= source.length()) {
            throw error("Unterminated string");
        }
        String text = source.substring(start, position);
        position++;
        return text;
    }

    private double readNumber() {
        int start = position;
        while (position < source.length()
                && (Character.isDigit(source.charAt(position)) || source.charAt(position) == '.')) {
            position++;
        }
        try {
            return Double.parseDouble(source.substring(start, position));
        } catch (NumberFormatException e) {
            throw error("Malformed number");
        }
    }
}
