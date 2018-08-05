package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {
    private long personid;
    private String firstName;
    private String lastName;

    public Person() {

    }

    public Person(long personid, String firstName, String lastName) {
        this.personid = personid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @JsonProperty
    public long getPersonid() {
        return personid;
    }

    @JsonProperty
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty
    public String getLastName() {
        return lastName;
    }

}
