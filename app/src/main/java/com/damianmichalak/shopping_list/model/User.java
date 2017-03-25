package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.guava.Objects;
import com.google.firebase.database.PropertyName;

import java.util.List;

public class User {

    private String name;

    @PropertyName("shopping_list_access")
    private List<String> listIDs;

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equal(name, user.name) &&
                Objects.equal(listIDs, user.listIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, listIDs);
    }

    public List<String> getListIDs() {
        return listIDs;
    }

}
