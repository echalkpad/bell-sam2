package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberThirdPartyModelConstants;
import com.vennetics.microservices.common.core.gateway.adapter.command.AbstractHystrixGatewayCommand;

/**
 * A HystrixCommand to invoke GetSubscriberProfile on the internal SUBX service
 */

public class GetSubscriberProfileCommand extends
                                         AbstractHystrixGatewayCommand<ResponseEntity<SubscriberProfile>> {

    private static final Logger logger = LoggerFactory.getLogger(GetSubscriberProfileCommand.class);

    private final String serviceIdentifier;
    private final String searchFilter;
    private final String filterValue;
    private final RestTemplate template;

    /**
     * Sole constructor.
     *
     * @param serviceIdentifier
     *            the identifier of the service to be used.
     * @param searchFilter
     *            One of (MDN,SUBID,MEID,UUID,IMSI)
     * @param filterValue
     *            The value of the filter
     * @param template
     *            The {@link RestTemplate} to be used to address the service.
     */
    public GetSubscriberProfileCommand(final String serviceIdentifier,
                                       final String searchFilter,
                                       final String filterValue,
                                       final RestTemplate template) {
        super(serviceIdentifier, "GetSubscriberProfile");
        this.searchFilter = searchFilter;
        this.serviceIdentifier = serviceIdentifier;
        this.filterValue = filterValue;
        this.template = template;
    }

    @Override
    protected ResponseEntity<SubscriberProfile> runCommand() {
        logger.debug("GetSubscriberProfileCommand searchFilter:{} filterValue:{}",
                     searchFilter,
                     filterValue);

        final ResponseEntity<SubscriberProfile> result = template.getForEntity(buildUrl(),
                                                                               SubscriberProfile.class);

        logger.debug("getSubscriberProfile result {}", result);
        return result;
    }

    private String buildUrl() {

        return UriComponentsBuilder.newInstance()
                                   .scheme("http")
                                   .host(serviceIdentifier)
                                   .pathSegment(SubscriberThirdPartyModelConstants.PATH_FRAGMENT_API,
                                                SubscriberThirdPartyModelConstants.PATH_FRAGMENT_SUBSCRIBER,
                                                SubscriberThirdPartyModelConstants.PATH_FRAGMENT_SUBCRIBER_PROFILE)
                                   .queryParam(SubscriberThirdPartyModelConstants.QPARAM_SEARCH_FILTER,
                                               searchFilter)
                                   .queryParam(SubscriberThirdPartyModelConstants.QPARAM_FILTER_VALUE,
                                               filterValue)
                                   .toUriString();
    }
}
