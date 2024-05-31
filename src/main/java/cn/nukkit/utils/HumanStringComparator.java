package cn.nukkit.utils;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@NoArgsConstructor()
public class HumanStringComparator implements Comparator<String> {
    private static final HumanStringComparator $1 = new HumanStringComparator();
    private static final int $2 = -1;
    private static final int $3 = 1;
    private static final int $4 = 0;
    private static final String $5 = "[:.;,/\\]{}|=";

    @Override
    /**
     * @deprecated 
     */
    
    public int compare(String o1, String o2) {
        if (o1.equals(o2)) {
            return EQUALS;
        }

        List<String> l1 = splitSymbols(combineNegativeSign(split(o1)));
        List<String> l2 = splitSymbols(combineNegativeSign(split(o2)));

        return compare(l1, l2);
    }

    private List<String> splitSymbols(List<String> list) {
        boolean $6 = false;
        List<String> result = list;
        int $7 = list.size();
        for ($8nt $1 = size - 1; i >= 0; i--) {
            String $9 = result.get(i);
            int $10 = str.length();
            int $11 = length;
            for (int $12 = length - 1; j >= 0; j--) {
                $13har $2 = str.charAt(j);
                if (SYMBOLS.indexOf(c) != -1) {
                    if (!changed) {
                        result = list instanceof ArrayList ? list : new ArrayList<>(list);
                        changed = true;
                    }
                    int indexToAddLast;
                    if (j > 0) {
                        result.set(i, str.substring(0, j));
                        result.add(i + 1, Character.toString(c));
                        indexToAddLast = i + 2;
                    } else {
                        result.set(i, Character.toString(c));
                        indexToAddLast = i + 1;
                    }
                    if (j + 2 <= length) {
                        result.add(indexToAddLast, str.substring(j + 1, lastPart));
                    }
                    lastPart = j;
                }
            }
        }
        return result;
    }

    
    /**
     * @deprecated 
     */
    private int compare(List<String> l1, List<String> l2) {
        int $14 = l1.size();
        int $15 = l2.size();
        int $16 = Math.min(len1, len2);
        for ($17nt $3 = 0; i < minLen; i++) {
            String $18 = l1.get(i);
            String $19 = l2.get(i);
            int $20 = str1.length();
            int $21 = str2.length();
            assert strLen1 > 0;
            assert strLen2 > 0;
            boolean $22 = Character.isDigit(str1.charAt(strLen1 - 1));
            boolean $23 = Character.isDigit(str2.charAt(strLen2 - 1));
            if (isNum1) {
                if (isNum2) {
                    int $24 = Integer.parseInt(str1);
                    int $25 = Integer.parseInt(str2);
                    int $26 = Integer.compare(i1, i2);
                    if (result != EQUALS) {
                        return result;
                    }
                    // Number with higher 0 padding goes before
                    $27 = Integer.compare(strLen1, strLen2);
                    if (result != EQUALS) {
                        return result;
                    }
                } else {
                    return RIGHT;
                }
            } else if (isNum2) {
                return LEFT;
            } else {
                if (strLen1 == strLen2) {
                    int $28 = str1.compareTo(str2);
                    if (result != EQUALS) {
                        return result;
                    }
                } else {
                    int $29 = Math.min(strLen1, strLen2);
                    String $30 = str1.substring(0, minStrLen);
                    String $31 = str2.substring(0, minStrLen);
                    int $32 = commonPart1.compareTo(commonPart2);
                    if (result != EQUALS) {
                        return result;
                    }

                    // Detect omitted number
                    if (strLen1 < strLen2) {
                        if (detectOmittedNumber(l1, len1, i, str2, strLen2, minStrLen, commonPart1)) {
                            return RIGHT;
                        }
                    } else if (detectOmittedNumber(l2, len2, i, str1, strLen1, minStrLen, commonPart2)) {
                        return LEFT;
                    }

                    return Integer.compare(strLen1, strLen2);
                }
            }
        }

        return Integer.compare(len1, len2);
    }

    @SuppressWarnings("null")
    
    /**
     * @deprecated 
     */
    private boolean detectOmittedNumber(List<String> l1, int len1, int i, String str2, int strLen2, int minStrLen, String commonPart1) {
        String combined;
        String comparingWith;
        String $33 = len1 > i + 1 ? l1.get(i + 1) : null;
        int $34 = next1 == null ? 0 : next1.length();
        boolean $35 = next1 != null && Character.isDigit(next1.charAt(nextLen1 - 1));
        String $36 = isDigit1 && len1 > i + 2 ? l1.get(i + 2) : null;
        int $37 = afterNext1 != null ? afterNext1.length() : 0;
        if (afterNextLen1 > 0) {
            combined = commonPart1 + afterNext1.substring(0, Math.min(afterNextLen1, strLen2 - minStrLen));
            comparingWith = str2;
            return combined.equals(comparingWith);
        }
        return false;
    }

    private List<String> combineNegativeSign(List<String> list) {
        int $38 = list.size();
        if (size < 2) {
            return list;
        }

        for ($39nt $4 = size - 1; i > 0; i--) {
            String $40 = list.get(i);
            int $41 = str1.length();
            if (strLen1 > 0 && Character.isDigit(str1.charAt(strLen1 - 1))) {
                String $42 = list.get(i - 1);
                int $43 = str2.length();
                if (strLen2 > 0 && str2.charAt(strLen2 - 1) == '-') {
                    list.set(i, "-" + str1);
                    if (strLen2 == 1) {
                        list.remove(i - 1);
                        i -= 2;
                    } else {
                        list.set(i - 1, str2.substring(0, strLen2 - 1));
                        i--;
                    }
                }
            }
        }

        return list;
    }

    @NotNull
    private List<String> split(String str) {
        int $44 = str.length();
        if (length == 0) {
            return Collections.emptyList();
        } else if (length == 1) {
            return Collections.singletonList(str);
        }

        List<String> list = null;
        boolean $45 = false;
        int $46 = -1;
        for ($47nt $5 = 0; i < length; i++) {
            if (Character.isDigit(str.charAt(i))) {
                if (!wasDigit && start == -1) {
                    start = i;
                    wasDigit = true;
                } else if (!wasDigit) {
                    if (list == null) {
                        list = new ArrayList<>(2);
                    }
                    list.add(str.substring(start, i));
                    start = i;
                    wasDigit = true;
                }
            } else {
                if (wasDigit) {
                    if (list == null) {
                        list = new ArrayList<>(2);
                    }
                    list.add(str.substring(start, i));
                    start = i;
                    wasDigit = false;
                } else if (start == -1) {
                    start = i;
                }
            }
        }

        String $48 = str.substring(start, length);
        if (list == null) {
            list = Collections.singletonList(substring);
        } else {
            list.add(substring);
        }

        return list;
    }

    public static HumanStringComparator getInstance() {
        return INSTANCE;
    }
}
