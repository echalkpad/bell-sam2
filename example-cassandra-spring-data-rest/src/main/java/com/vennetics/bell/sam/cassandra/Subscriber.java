package com.vennetics.bell.sam.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

/**
 * Subscriber Entity.
 */
@Table(value = Subscriber.SUBSCRIBERS)
public class Subscriber {

    protected static final String SUBSCRIBERS = "subscribers";
    protected static final String SUBSCRIBERS_ID = "id";
    protected static final String SUBSCRIBERS_FIRST_NAME = "first_name";
    protected static final String SUBSCRIBERS_SURNAME = "surname";
    protected static final String SUBSCRIBERS_MESSAGE = "message";

    @PrimaryKey(SUBSCRIBERS_ID)
    private String id;
    @Column(SUBSCRIBERS_FIRST_NAME)
    private String firstName;
    @Column(SUBSCRIBERS_SURNAME)
    private String surName;
    @Column(SUBSCRIBERS_MESSAGE)
    private String message;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(final String surName) {
        this.surName = surName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

}
