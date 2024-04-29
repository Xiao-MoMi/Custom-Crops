package net.momirealms.customcrops.api.util;

public class StringUtils {

    public static boolean isCapitalLetter(String item) {
        char[] chars = item.toCharArray();
        for (char character : chars) {
            if ((character < 65 || character > 90) && character != 95) {
                return false;
            }
        }
        return true;
    }

}
