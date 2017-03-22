package com.damianmichalak.shopping_list.helper.guava;


import java.util.ArrayList;
import java.util.Collections;

public class Lists {

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        final ArrayList<E> list = new ArrayList<E>();
        Collections.addAll(list, elements);
        return list;
    }

}
