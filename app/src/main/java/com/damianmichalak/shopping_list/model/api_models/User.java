package com.damianmichalak.shopping_list.model.api_models;

import com.damianmichalak.shopping_list.helper.guava.Objects;

public class User {

    private String name;

    public String getName() {
        return name;
    }

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equal(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
