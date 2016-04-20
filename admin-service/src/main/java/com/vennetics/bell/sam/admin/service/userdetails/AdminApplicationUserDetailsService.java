package com.vennetics.bell.sam.admin.service.userdetails;

import com.vennetics.bell.sam.admin.service.repositories.ApplicationRepository;
import com.vennetics.bell.sam.admin.service.repositories.ClientRepository;
import com.vennetics.bell.sam.admin.service.userdetails.command.GetApplicationByIdCommand;
import com.vennetics.bell.sam.admin.service.userdetails.command.GetUuidByClientIdCommand;
import com.vennetics.bell.sam.admin.service.util.IAdminServiceTypeMapper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.microservices.common.api.model.admin.util.ApplicationUserDetailsBuilder;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.IApplicationDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

@Service
public class AdminApplicationUserDetailsService implements IApplicationDetailsService {

    private final ApplicationRepository applicationRepository;

    private final ClientRepository clientRepository;

    private final IAdminServiceTypeMapper typeMapper;

    @Autowired
    public AdminApplicationUserDetailsService(final ApplicationRepository applicationRepository,
                                              final ClientRepository clientRepository,
                                              final IAdminServiceTypeMapper typeMapper) {
        super();
        this.applicationRepository = applicationRepository;
        this.clientRepository = clientRepository;
        this.typeMapper = typeMapper;
    }

    @Override
    public Observable<ApplicationUserDetails> loadByClientId(final String clientId) {


        return doWork(clientId);
    }

    private Observable<ApplicationUserDetails> doWork(final String clientId) {
        return new GetUuidByClientIdCommand(clientRepository, clientId).observe()

                                                                       .filter(uuid -> uuid != null)

                                                                       .flatMap(uuid -> new GetApplicationByIdCommand(applicationRepository,
                                                                                                                      uuid).observe())

                                                                       .filter(app -> app != null)

                                                                       .map(app -> createApplicationUserDetails(app))

                                                                       .singleOrDefault(null);
    }

    private ApplicationUserDetails createApplicationUserDetails(final CassandraApplication cassandraApplication) {
        final ApplicationUserDetailsBuilder builder = new ApplicationUserDetailsBuilder(cassandraApplication.getClientId(),
                                                                                        cassandraApplication.getSecret(),
                                                                                        cassandraApplication.getAccountState());


        builder.withRoles(cassandraApplication.getRoleIds());
        builder.withServices(typeMapper.toAllowedServices(cassandraApplication.getAllowedServiceIds(),
                                                          typeMapper.toServicePolicies(cassandraApplication)));
        builder.withPolicies(typeMapper.toApplicationPolicies(typeMapper.toApplicationPolicies(cassandraApplication)));

        return builder.build();

    }

}
