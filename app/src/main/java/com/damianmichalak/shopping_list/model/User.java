package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.guava.Objects;

import java.util.List;

public class User {

    private String name;
    private String uid;
    private List<String> listIDs;

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equal(name, user.name) &&
                Objects.equal(uid, user.uid) &&
                Objects.equal(listIDs, user.listIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, uid, listIDs);
    }

    public List<String> getListIDs() {
        return listIDs;
    }

}
