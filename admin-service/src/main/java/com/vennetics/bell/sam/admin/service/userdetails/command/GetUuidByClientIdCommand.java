package com.vennetics.bell.sam.admin.service.userdetails.command;

import com.vennetics.bell.sam.admin.service.repositories.ClientRepository;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Query command for UUID lookup by client ID. Uses hystrix request cache in
 * case of multiple calls in request context.
 */
public class GetUuidByClientIdCommand extends AbstractAdminCrudCommand<UUID> {

    private static final Logger logger = LoggerFactory.getLogger(GetUuidByClientIdCommand.class);


    private final ClientRepository clientRepository;

    private final String clientId;

    public GetUuidByClientIdCommand(final ClientRepository clientRepository,
                                    final String clientId) {
        super("admin.service.GetUuidByClientId");
        this.clientRepository = clientRepository;
        this.clientId = clientId;
    }

    @Override
    protected UUID run() {

        final CassandraClient result = clientRepository.findOne(clientId);

        logger.debug("Found result: {} for clientId: {}", result, clientId);
        return result != null ? result.getApplicationId() : null;
    }

}
