package de.flower.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author flowerrrr
 */
public final class Strings {

    private Strings() {
    }

    /**
     * Converts ACamelCaseString to a-camel-case-string.
     * @param s
     * @return
     */
    public static String camelCaseToHyphen(final String s) {
        return StringUtils.join(splitCamelCase(s), "-");
    }

    public static String[] splitCamelCase(final String s) {
        String out = s.replaceAll(
                 String.format("%s|%s|%s",
                         "(?<=[A-Z])(?=[A-Z][a-z])",
                         "(?<=[^A-Z])(?=[A-Z])",
                         "(?<=[A-Za-z])(?=[^A-Za-z])"
                 ),
                 " "
         );
        return StringUtils.split(out, " ");
    }

    /**
     * Converts ACamelCaseXMLString to aCamelCaseXmlString
     * @param s
     * @return
     */
    public static String uncapitalize(final String s) {
        String[] split = splitCamelCase(s);
        // lowercase all strings and re-capitalize first char of 2..n parts
        for (int i = 0; i < split.length; i++) {
            String tmp = split[i];
            tmp = tmp.toLowerCase();
            if (i > 0) {
                tmp = StringUtils.capitalize(tmp);
            }
            split[i] = tmp;
        }
        return StringUtils.join(split);
    }
}