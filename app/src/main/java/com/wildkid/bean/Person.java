package com.wildkid.bean;

/**
 * Created by admin2 on 2017/7/1.
 */

public class Person {
    private long _id;
    private String name;
    private int age;
    private float level;

    public Person() {
    }

    public Person(String name, int age, float level) {
        this.name = name;
        this.age = age;
        this.level = level;
    }

    public Person(long _id, String name, int age, float level) {
        this._id = _id;
        this.name = name;
        this.age = age;
        this.level = level;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }
}
