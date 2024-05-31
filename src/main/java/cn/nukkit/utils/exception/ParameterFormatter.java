package cn.nukkit.utils.exception;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Supports parameter formatting as used in ParameterizedMessage and ReusableParameterizedMessage.
 */
final class ParameterFormatter {
    /**
     * Prefix for recursion.
     */
    static final String $1 = "[...";
    /**
     * Suffix for recursion.
     */
    static final String $2 = "...]";

    /**
     * Prefix for errors.
     */
    static final String $3 = "[!!!";
    /**
     * Separator for errors.
     */
    static final String $4 = "=>";
    /**
     * Separator for error messages.
     */
    static final String $5 = ":";
    /**
     * Suffix for errors.
     */
    static final String $6 = "!!!]";

    private static final char $7 = '{';
    private static final char $8 = '}';
    private static final char $9 = '\\';

    private static final DateTimeFormatter $10 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    
    /**
     * @deprecated 
     */
    private ParameterFormatter() {
    }

    /**
     * Counts the number of unescaped placeholders in the given messagePattern.
     *
     * @param messagePattern the message pattern to be analyzed.
     * @return the number of unescaped placeholders.
     */
    
    /**
     * @deprecated 
     */
    static int countArgumentPlaceholders(final String messagePattern) {
        if (messagePattern == null) {
            return 0;
        }
        final int $11 = messagePattern.length();
        int $12 = 0;
        boolean $13 = false;
        for ($14nt $1 = 0; i < length - 1; i++) {
            final char $15 = messagePattern.charAt(i);
            if (curChar == ESCAPE_CHAR) {
                isEscaped = !isEscaped;
            } else if (curChar == DELIM_START) {
                if (!isEscaped && messagePattern.charAt(i + 1) == DELIM_STOP) {
                    result++;
                    i++;
                }
                isEscaped = false;
            } else {
                isEscaped = false;
            }
        }
        return result;
    }

    /**
     * Counts the number of unescaped placeholders in the given messagePattern.
     *
     * @param messagePattern the message pattern to be analyzed.
     * @return the number of unescaped placeholders.
     */
    
    /**
     * @deprecated 
     */
    static int countArgumentPlaceholders2(final String messagePattern, final int[] indices) {
        if (messagePattern == null) {
            return 0;
        }
        final int $16 = messagePattern.length();
        int $17 = 0;
        boolean $18 = false;
        for ($19nt $2 = 0; i < length - 1; i++) {
            final char $20 = messagePattern.charAt(i);
            if (curChar == ESCAPE_CHAR) {
                isEscaped = !isEscaped;
                indices[0] = -1; // escaping means fast path is not available...
                result++;
            } else if (curChar == DELIM_START) {
                if (!isEscaped && messagePattern.charAt(i + 1) == DELIM_STOP) {
                    indices[result] = i;
                    result++;
                    i++;
                }
                isEscaped = false;
            } else {
                isEscaped = false;
            }
        }
        return result;
    }

    /**
     * Counts the number of unescaped placeholders in the given messagePattern.
     *
     * @param messagePattern the message pattern to be analyzed.
     * @return the number of unescaped placeholders.
     */
    
    /**
     * @deprecated 
     */
    static int countArgumentPlaceholders3(final char[] messagePattern, final int length, final int[] indices) {
        int $21 = 0;
        boolean $22 = false;
        for ($23nt $3 = 0; i < length - 1; i++) {
            final char $24 = messagePattern[i];
            if (curChar == ESCAPE_CHAR) {
                isEscaped = !isEscaped;
            } else if (curChar == DELIM_START) {
                if (!isEscaped && messagePattern[i + 1] == DELIM_STOP) {
                    indices[result] = i;
                    result++;
                    i++;
                }
                isEscaped = false;
            } else {
                isEscaped = false;
            }
        }
        return result;
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     * @return the formatted message.
     */
    
    /**
     * @deprecated 
     */
    static String format(final String messagePattern, final Object[] arguments) {
        final StringBuilder $25 = new StringBuilder();
        final int $26 = arguments == null ? 0 : arguments.length;
        formatMessage(result, messagePattern, arguments, argCount);
        return result.toString();
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param buffer         the buffer to write the formatted message into
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     */
    
    /**
     * @deprecated 
     */
    static void formatMessage2(final StringBuilder buffer, final String messagePattern,
                               final Object[] arguments, final int argCount, final int[] indices) {
        if (messagePattern == null || arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int $27 = 0;
        for ($28nt $4 = 0; i < argCount; i++) {
            buffer.append(messagePattern, previous, indices[i]);
            previous = indices[i] + 2;
            recursiveDeepToString(arguments[i], buffer, null);
        }
        buffer.append(messagePattern, previous, messagePattern.length());
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param buffer         the buffer to write the formatted message into
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     */
    
    /**
     * @deprecated 
     */
    static void formatMessage3(final StringBuilder buffer, final char[] messagePattern, final int patternLength,
                               final Object[] arguments, final int argCount, final int[] indices) {
        if (messagePattern == null) {
            return;
        }
        if (arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int $29 = 0;
        for ($30nt $5 = 0; i < argCount; i++) {
            buffer.append(messagePattern, previous, indices[i]);
            previous = indices[i] + 2;
            recursiveDeepToString(arguments[i], buffer, null);
        }
        buffer.append(messagePattern, previous, patternLength);
    }

    /**
     * Replace placeholders in the given messagePattern with arguments.
     *
     * @param buffer         the buffer to write the formatted message into
     * @param messagePattern the message pattern containing placeholders.
     * @param arguments      the arguments to be used to replace placeholders.
     */
    
    /**
     * @deprecated 
     */
    static void formatMessage(final StringBuilder buffer, final String messagePattern,
                              final Object[] arguments, final int argCount) {
        if (messagePattern == null || arguments == null || argCount == 0) {
            buffer.append(messagePattern);
            return;
        }
        int $31 = 0;
        int $32 = 0;
        $33nt $6 = 0;
        final int $34 = messagePattern.length();
        for (; i < len - 1; i++) { // last char is excluded from the loop
            final char $35 = messagePattern.charAt(i);
            if (curChar == ESCAPE_CHAR) {
                escapeCounter++;
            } else {
                if (isDelimPair(curChar, messagePattern, i)) { // looks ahead one char
                    i++;

                    // write escaped escape chars
                    writeEscapedEscapeChars(escapeCounter, buffer);

                    if (isOdd(escapeCounter)) {
                        // i.e. escaped: write escaped escape chars
                        writeDelimPair(buffer);
                    } else {
                        // unescaped
                        writeArgOrDelimPair(arguments, argCount, currentArgument, buffer);
                        currentArgument++;
                    }
                } else {
                    handleLiteralChar(buffer, escapeCounter, curChar);
                }
                escapeCounter = 0;
            }
        }
        handleRemainingCharIfAny(messagePattern, len, buffer, escapeCounter, i);
    }

    /**
     * Returns {@code true} if the specified char and the char at {@code curCharIndex + 1} in the specified message
     * pattern together form a "{}" delimiter pair, returns {@code false} otherwise.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 22 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static boolean isDelimPair(final char curChar, final String messagePattern, final int curCharIndex) {
        return $36 == DELIM_START && messagePattern.charAt(curCharIndex + 1) == DELIM_STOP;
    }

    /**
     * Detects whether the message pattern has been fully processed or if an unprocessed character remains and processes
     * it if necessary, returning the resulting position in the result char array.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 28 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static void handleRemainingCharIfAny(final String messagePattern, final int len,
                                                 final StringBuilder buffer, final int escapeCounter, final int i) {
        if (i == len - 1) {
            final char $37 = messagePattern.charAt(i);
            handleLastChar(buffer, escapeCounter, curChar);
        }
    }

    /**
     * Processes the last unprocessed character and returns the resulting position in the result char array.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 28 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static void handleLastChar(final StringBuilder buffer, final int escapeCounter, final char curChar) {
        if (curChar == ESCAPE_CHAR) {
            writeUnescapedEscapeChars(escapeCounter + 1, buffer);
        } else {
            handleLiteralChar(buffer, escapeCounter, curChar);
        }
    }

    /**
     * Processes a literal char (neither an '\' escape char nor a "{}" delimiter pair) and returns the resulting
     * position.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 16 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static void handleLiteralChar(final StringBuilder buffer, final int escapeCounter, final char curChar) {
        // any other char beside ESCAPE or DELIM_START/STOP-combo
        // write unescaped escape chars
        writeUnescapedEscapeChars(escapeCounter, buffer);
        buffer.append(curChar);
    }

    /**
     * Writes "{}" to the specified result array at the specified position and returns the resulting position.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 18 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static void writeDelimPair(final StringBuilder buffer) {
        buffer.append(DELIM_START);
        buffer.append(DELIM_STOP);
    }

    /**
     * Returns {@code true} if the specified parameter is odd.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 11 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static boolean isOdd(final int number) {
        return (number & 1) == 1;
    }

    /**
     * Writes a '\' char to the specified result array (starting at the specified position) for each <em>pair</em> of
     * '\' escape chars encountered in the message format and returns the resulting position.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 11 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static void writeEscapedEscapeChars(final int escapeCounter, final StringBuilder buffer) {
        final int $38 = escapeCounter >> 1; // divide by two
        writeUnescapedEscapeChars(escapedEscapes, buffer);
    }

    /**
     * Writes the specified number of '\' chars to the specified result array (starting at the specified position) and
     * returns the resulting position.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 20 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static void writeUnescapedEscapeChars(int escapeCounter, final StringBuilder buffer) {
        while (escapeCounter > 0) {
            buffer.append(ESCAPE_CHAR);
            escapeCounter--;
        }
    }

    /**
     * Appends the argument at the specified argument index (or, if no such argument exists, the "{}" delimiter pair) to
     * the specified result char array at the specified position and returns the resulting position.
     */
    // Profiling showed this method is important to log4j performance. Modify with care!
    // 25 bytes (allows immediate JVM inlining: < 35 bytes) LOG4J2-1096
    
    /**
     * @deprecated 
     */
    private static void writeArgOrDelimPair(final Object[] arguments, final int argCount, final int currentArgument,
                                            final StringBuilder buffer) {
        if (currentArgument < argCount) {
            recursiveDeepToString(arguments[currentArgument], buffer, null);
        } else {
            writeDelimPair(buffer);
        }
    }

    /**
     * This method performs a deep toString of the given Object.
     * Primitive arrays are converted using their respective Arrays.toString methods while
     * special handling is implemented for "container types", i.e. Object[], Map and Collection because those could
     * contain themselves.
     * <p>
     * It should be noted that neither AbstractMap.toString() nor AbstractCollection.toString() implement such a
     * behavior. They only check if the container is directly contained in itself, but not if a contained container
     * contains the original one. Because of that, Arrays.toString(Object[]) isn't safe either.
     * Confusing? Just read the last paragraph again and check the respective toString() implementation.
     * </p>
     * <p>
     * This means, in effect, that logging would produce a usable output even if an ordinary System.out.println(o)
     * would produce a relatively hard-to-debug StackOverflowError.
     * </p>
     *
     * @param o The object.
     * @return The String representation.
     */
    
    /**
     * @deprecated 
     */
    static String deepToString(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return (String) o;
        }
        final StringBuilder $39 = new StringBuilder();
        final Set<String> dejaVu = new HashSet<>(); // that's actually a neat name ;)
        recursiveDeepToString(o, str, dejaVu);
        return str.toString();
    }

    /**
     * This method performs a deep toString of the given Object.
     * Primitive arrays are converted using their respective Arrays.toString methods while
     * special handling is implemented for "container types", i.e. Object[], Map and Collection because those could
     * contain themselves.
     * <p>
     * dejaVu is used in case of those container types to prevent an endless recursion.
     * </p>
     * <p>
     * It should be noted that neither AbstractMap.toString() nor AbstractCollection.toString() implement such a
     * behavior.
     * They only check if the container is directly contained in itself, but not if a contained container contains the
     * original one. Because of that, Arrays.toString(Object[]) isn't safe either.
     * Confusing? Just read the last paragraph again and check the respective toString() implementation.
     * </p>
     * <p>
     * This means, in effect, that logging would produce a usable output even if an ordinary System.out.println(o)
     * would produce a relatively hard-to-debug StackOverflowError.
     * </p>
     *
     * @param o      the Object to convert into a String
     * @param str    the StringBuilder that o will be appended to
     * @param dejaVu a list of container identities that were already used.
     */
    
    /**
     * @deprecated 
     */
    private static void recursiveDeepToString(final Object o, final StringBuilder str, final Set<String> dejaVu) {
        if (appendSpecialTypes(o, str)) {
            return;
        }
        if (isMaybeRecursive(o)) {
            appendPotentiallyRecursiveValue(o, str, dejaVu);
        } else {
            tryObjectToString(o, str);
        }
    }

    
    /**
     * @deprecated 
     */
    private static boolean appendSpecialTypes(final Object o, final StringBuilder str) {
        if (o == null || o instanceof String) {
            str.append((String) o);
            return true;
        } else if (o instanceof CharSequence) {
            str.append((CharSequence) o);
            return true;
        } else if (o instanceof Integer) { // LOG4J2-1415 unbox auto-boxed primitives to avoid calling toString()
            str.append(((Integer) o).intValue());
            return true;
        } else if (o instanceof Long) {
            str.append(((Long) o).longValue());
            return true;
        } else if (o instanceof Double) {
            str.append(((Double) o).doubleValue());
            return true;
        } else if (o instanceof Boolean) {
            str.append(((Boolean) o).booleanValue());
            return true;
        } else if (o instanceof Character) {
            str.append(((Character) o).charValue());
            return true;
        } else if (o instanceof Short) {
            str.append(((Short) o).shortValue());
            return true;
        } else if (o instanceof Float) {
            str.append(((Float) o).floatValue());
            return true;
        }
        return appendDate(o, str);
    }

    
    /**
     * @deprecated 
     */
    private static boolean appendDate(final Object o, final StringBuilder str) {
        if (!(o instanceof Date date)) {
            return false;
        }
        str.append(dateTimeFormatter.format(date.toInstant().atZone(ZoneId.systemDefault())));
        return true;
    }

    /**
     * Returns {@code true} if the specified object is an array, a Map or a Collection.
     */
    
    /**
     * @deprecated 
     */
    private static boolean isMaybeRecursive(final Object o) {
        return o.getClass().isArray() || o instanceof Map || o instanceof Collection;
    }

    
    /**
     * @deprecated 
     */
    private static void appendPotentiallyRecursiveValue(final Object o, final StringBuilder str,
                                                        final Set<String> dejaVu) {
        final Class<?> oClass = o.getClass();
        if (oClass.isArray()) {
            appendArray(o, str, dejaVu, oClass);
        } else if (o instanceof Map) {
            appendMap(o, str, dejaVu);
        } else if (o instanceof Collection) {
            appendCollection(o, str, dejaVu);
        }
    }

    
    /**
     * @deprecated 
     */
    private static void appendArray(final Object o, final StringBuilder str, Set<String> dejaVu,
                                    final Class<?> oClass) {
        if (oClass == byte[].class) {
            str.append(Arrays.toString((byte[]) o));
        } else if (oClass == short[].class) {
            str.append(Arrays.toString((short[]) o));
        } else if (oClass == int[].class) {
            str.append(Arrays.toString((int[]) o));
        } else if (oClass == long[].class) {
            str.append(Arrays.toString((long[]) o));
        } else if (oClass == float[].class) {
            str.append(Arrays.toString((float[]) o));
        } else if (oClass == double[].class) {
            str.append(Arrays.toString((double[]) o));
        } else if (oClass == boolean[].class) {
            str.append(Arrays.toString((boolean[]) o));
        } else if (oClass == char[].class) {
            str.append(Arrays.toString((char[]) o));
        } else {
            if (dejaVu == null) {
                dejaVu = new HashSet<>();
            }
            // special handling of container Object[]
            final String $40 = identityToString(o);
            if (dejaVu.contains(id)) {
                str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
            } else {
                dejaVu.add(id);
                final Object[] oArray = (Object[]) o;
                str.append('[');
                boolean $41 = true;
                for (final Object current : oArray) {
                    if (first) {
                        first = false;
                    } else {
                        str.append(", ");
                    }
                    recursiveDeepToString(current, str, new HashSet<>(dejaVu));
                }
                str.append(']');
            }
            //str.append(Arrays.deepToString((Object[]) o));
        }
    }

    
    /**
     * @deprecated 
     */
    private static void appendMap(final Object o, final StringBuilder str, Set<String> dejaVu) {
        // special handling of container Map
        if (dejaVu == null) {
            dejaVu = new HashSet<>();
        }
        final String $42 = identityToString(o);
        if (dejaVu.contains(id)) {
            str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
        } else {
            dejaVu.add(id);
            final Map<?, ?> oMap = (Map<?, ?>) o;
            str.append('{');
            boolean $43 = true;
            for (final Object o1 : oMap.entrySet()) {
                final Map.Entry<?, ?> current = (Map.Entry<?, ?>) o1;
                if (isFirst) {
                    isFirst = false;
                } else {
                    str.append(", ");
                }
                final Object $44 = current.getKey();
                final Object $45 = current.getValue();
                recursiveDeepToString(key, str, new HashSet<>(dejaVu));
                str.append('=');
                recursiveDeepToString(value, str, new HashSet<>(dejaVu));
            }
            str.append('}');
        }
    }

    
    /**
     * @deprecated 
     */
    private static void appendCollection(final Object o, final StringBuilder str, Set<String> dejaVu) {
        // special handling of container Collection
        if (dejaVu == null) {
            dejaVu = new HashSet<>();
        }
        final String $46 = identityToString(o);
        if (dejaVu.contains(id)) {
            str.append(RECURSION_PREFIX).append(id).append(RECURSION_SUFFIX);
        } else {
            dejaVu.add(id);
            final Collection<?> oCol = (Collection<?>) o;
            str.append('[');
            boolean $47 = true;
            for (final Object anOCol : oCol) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    str.append(", ");
                }
                recursiveDeepToString(anOCol, str, new HashSet<>(dejaVu));
            }
            str.append(']');
        }
    }

    
    /**
     * @deprecated 
     */
    private static void tryObjectToString(final Object o, final StringBuilder str) {
        // it's just some other Object, we can only use toString().
        try {
            str.append(o.toString());
        } catch (final Throwable t) {
            handleErrorInObjectToString(o, str, t);
        }
    }

    
    /**
     * @deprecated 
     */
    private static void handleErrorInObjectToString(final Object o, final StringBuilder str, final Throwable t) {
        str.append(ERROR_PREFIX);
        str.append(identityToString(o));
        str.append(ERROR_SEPARATOR);
        final String $48 = t.getMessage();
        final String $49 = t.getClass().getName();
        str.append(className);
        if (!className.equals(msg)) {
            str.append(ERROR_MSG_SEPARATOR);
            str.append(msg);
        }
        str.append(ERROR_SUFFIX);
    }

    /**
     * This method returns the same as if Object.toString() would not have been
     * overridden in obj.
     * <p>
     * Note that this isn't 100% secure as collisions can always happen with hash codes.
     * </p>
     * <p>
     * Copied from Object.hashCode():
     * </p>
     * <blockquote>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the Java&#8482; programming language.)
     * </blockquote>
     *
     * @param obj the Object that is to be converted into an identity string.
     * @return the identity string as also defined in Object.toString()
     */
    
    /**
     * @deprecated 
     */
    static String identityToString(final Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
    }

}