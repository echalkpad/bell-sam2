package com.vennetics.bell.sam.cassandra.utils;

import com.vennetics.bell.sam.cassandra.Subscriber;

import java.util.UUID;

public final class CassandraTestUtils {
    
    private CassandraTestUtils() {
    }

    public static final String TEST_FIRSTNAME = "TestFirstName_";
    public static final String TEST_SURNAME = "TestSurname_";
    public static final String TEST_MESSAGE = "TestMessage_";
    public static final String UPDATED_TEST_FIRSTNAME = "UpdatedTestFirstName_";
    public static final String UPDATED_TEST_SURNAME = "UpdatedTestSurname_";
    public static final String UPDATED_TEST_MESSAGE = "UpdatedTestMessage_";
    public static final CharSequence CASS_TABLE_NAME = "subscribers";
    public static final String KEYSPACE_NAME = "bell_sam_test";
    public static final String CASSANDRA_HOST = "localhost";

    public static final String KEYSPACE_CREATE = "CREATE KEYSPACE IF NOT EXISTS bell_sam_test WITH replication = "
            + "{'class': 'SimpleStrategy', 'replication_factor': '1'} AND durable_writes = true;";
    public static final String TABLE_CREATE = "CREATE TABLE bell_sam_test.subscribers (id text PRIMARY KEY, "
            + "first_name text, surname text, message text);";

    public static Subscriber createDummySubscriber() {
        String subscriberId = UUID.randomUUID().toString();

        Subscriber subscriber = new Subscriber();
        subscriber.setId(subscriberId);
        subscriber.setFirstName(CassandraTestUtils.TEST_FIRSTNAME + subscriberId);
        subscriber.setSurName(CassandraTestUtils.TEST_SURNAME + subscriberId);
        subscriber.setMessage(CassandraTestUtils.TEST_MESSAGE + subscriberId);

        return subscriber;
    }

}
