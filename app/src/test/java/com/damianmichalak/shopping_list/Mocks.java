package com.damianmichalak.shopping_list;

import com.damianmichalak.shopping_list.model.api_models.Product;
import com.damianmichalak.shopping_list.model.api_models.User;

import java.util.Random;

public class Mocks {

    public static Product product() {
        return new Product("ID" + new Random().nextInt(10), "ID" + new Random().nextInt(10), System.currentTimeMillis(), 0);
    }

    public static User user() {
        return new User("NAME" + new Random().nextInt(10));
    }

    public static User userEmpty() {
        return new User();
    }

}
