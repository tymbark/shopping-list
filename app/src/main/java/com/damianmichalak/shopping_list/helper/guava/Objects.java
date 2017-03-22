package com.damianmichalak.shopping_list.helper.guava;

import java.util.Arrays;

import javax.annotation.Nullable;

public class Objects {

    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(@Nullable Object... objects) {
        return Arrays.hashCode(objects);
    }

}
