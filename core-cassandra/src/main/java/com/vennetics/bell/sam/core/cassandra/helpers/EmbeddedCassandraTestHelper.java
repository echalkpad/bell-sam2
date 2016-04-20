package com.vennetics.bell.sam.core.cassandra.helpers;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Helper class for starting embedded cassandra on unit tests.
 */
public final class EmbeddedCassandraTestHelper {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedCassandraTestHelper.class);

    private EmbeddedCassandraTestHelper() {
        // Checkstyle prevent instantiation of util class
    }

    // Suppress more than one thrown exception
    @SuppressWarnings({"squid:S1160"})
    public static void startCassandraCluster(final String ymlFile,
                                             final int port,
                                             final String cassandraHost,
                                             final String keyspaceCreate,
                                             final String... scripts) throws ConfigurationException, IOException, TTransportException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(ymlFile, 90000L);
        final Cluster cluster = Cluster.builder()
                                       .addContactPoint(cassandraHost)
                                       .withPort(EmbeddedCassandraServerHelper.getNativeTransportPort())
                                       .build();
        logger.debug("Test Server Started at {}:{}",
                     cassandraHost,
                     port);

        final Session session = cluster.connect();
        session.execute(keyspaceCreate);
        logger.debug("Keyspace created...");

        for (final String script : scripts) {
            session.execute(script);
        }
        logger.debug("Tables Created");
    }
}
