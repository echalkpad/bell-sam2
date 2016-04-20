package com.vennetics.bell.sam.admin.service.userdetails.command;

import com.vennetics.bell.sam.admin.service.repositories.ApplicationRepository;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;

import java.util.UUID;

/**
 * Command for querying an application by its UUID. Uses hystrix request cache.
 */
public class GetApplicationByIdCommand extends AbstractAdminCrudCommand<CassandraApplication> {

    private final ApplicationRepository applicationRepository;

    private final UUID uuid;

    public GetApplicationByIdCommand(final ApplicationRepository applicationRepository,
                                     final UUID uuid) {
        super("admin.service.GetApplicationById");
        this.applicationRepository = applicationRepository;
        this.uuid = uuid;
    }

    @Override
    protected CassandraApplication run() {

        return applicationRepository.findOne(uuid);
    }

}
