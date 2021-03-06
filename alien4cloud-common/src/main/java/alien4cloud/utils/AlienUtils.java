package alien4cloud.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public final class AlienUtils {

    public static final String DEFAULT_PREFIX_SEPARATOR = "_";
    public static final String COLON_SEPARATOR = ":";

    private AlienUtils() {

    }

    public static String putValueCommaSeparatedInPosition(String values, String valueToPut, int position) {
        String[] valuesArray;
        String separator = ",";
        int positionInArray = position - 1;
        if (StringUtils.isBlank(values)) {
            valuesArray = new String[0];
        } else {
            valuesArray = values.split(separator);
        }
        StringBuilder toReturnBuilder = new StringBuilder("");
        if (valuesArray.length >= position) {
            valuesArray[positionInArray] = valueToPut;
            String separatorToAppend;
            for (int i = 0; i < valuesArray.length; i++) {
                separatorToAppend = i + 1 == valuesArray.length ? "" : ",";
                toReturnBuilder.append(valuesArray[i]).append(separatorToAppend);
            }
        } else {
            int nbSeparatorToAdd = 0;
            if (StringUtils.isBlank(values)) {
                toReturnBuilder = new StringBuilder("");
                nbSeparatorToAdd = position - 1;
            } else {
                toReturnBuilder = new StringBuilder(values);
                nbSeparatorToAdd = position - valuesArray.length;
            }
            for (int i = 0; i < nbSeparatorToAdd; i++) {
                toReturnBuilder.append(separator);
            }
            toReturnBuilder.append(valueToPut);
        }
        return toReturnBuilder.toString();
    }

    /**
     * prefix a string with another
     *
     * @param separator
     * @param toPrefix
     * @param prefixes
     * @return
     */
    public static String prefixWith(String separator, String toPrefix, String... prefixes) {
        if (toPrefix == null) {
            return null;
        }
        if (ArrayUtils.isEmpty(prefixes)) {
            return toPrefix;
        }
        String finalSeparator = separator == null ? DEFAULT_PREFIX_SEPARATOR : separator;
        StringBuilder builder = new StringBuilder();
        for (String prefix : prefixes) {
            builder.append(prefix).append(finalSeparator);
        }
        return builder.append(toPrefix).toString();
    }

    /**
     * prefix a string with another using the default separator "_"
     *
     * @param toPrefix
     * @param prefixes
     * @return
     */
    public static String prefixWithDefaultSeparator(String toPrefix, String... prefixes) {
        return prefixWith(DEFAULT_PREFIX_SEPARATOR, toPrefix, prefixes);
    }
}
