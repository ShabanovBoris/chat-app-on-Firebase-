package com.example.chatapp.userlist;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

;

public class User {
    private String name;
    private String email;
    private String id;
    private int avatarMockUpResourse;
    private boolean hasAvatar = false;
    private String avatarUrl;


    public User(){};

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("id", id);
        result.put("avatarMockUpResourse", avatarMockUpResourse);
        result.put("hasAvatar", hasAvatar);
        result.put("avatarUrl", avatarUrl);







        return result;
    }

    public User(String name, String email, String id, int avatarMockUpResourse) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.avatarMockUpResourse = avatarMockUpResourse;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAvatarMockUpResourse() {
        return avatarMockUpResourse;
    }

    public void setAvatarMockUpResourse(int avatarMockUpResourse) {
        this.avatarMockUpResourse = avatarMockUpResourse;
    }

    public boolean isHasAvatar() {
        return hasAvatar;
    }

    public void setHasAvatar(boolean hasAvatar) {
        this.hasAvatar = hasAvatar;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
