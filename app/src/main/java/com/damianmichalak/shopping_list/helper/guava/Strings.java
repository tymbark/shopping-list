package com.damianmichalak.shopping_list.helper.guava;

import javax.annotation.Nullable;

public class Strings {

    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0; // string.isEmpty() in Java 6
    }

    public static boolean isNotNullAndNotEmpty(@Nullable String string) {
        return string != null && string.length() != 0;
    }

    public static String nullToEmpty(@Nullable String string) {
        return (string == null) ? "" : string;
    }

}
