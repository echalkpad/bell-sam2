package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter.commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberThirdPartyModelConstants;
import com.vennetics.microservices.common.core.gateway.adapter.command.AbstractHystrixGatewayCommand;

/**
 * A HystrixCommand to invoke HasFeatureCodes on the internal SUBX service
 */

public class HasFeatureCodesCommand extends
                                    AbstractHystrixGatewayCommand<ResponseEntity<FeatureCodes>> {

    private static final Logger logger = LoggerFactory.getLogger(HasFeatureCodesCommand.class);

    private final String serviceIdentifier;
    private final String mdn;
    private final String affiliateId;
    private final List<String> featureCodes;
    private final RestTemplate template;

    /**
     * Sole constructor.
     *
     * @param serviceIdentifier
     *            the identifier of the service to be used.
     * @param mdn
     *            The mdn for which feature code enablement is to be checked.
     * @param affiliateId
     *            The affiliate for which feature code enablement is to be
     *            checked.
     * @param featureCodes
     *            The feature codes to be checked.
     * @param template
     *            The {@link RestTemplate} to be used to address the service.
     */
    public HasFeatureCodesCommand(final String serviceIdentifier,
                                  final String mdn,
                                  final String affiliateId,
                                  final List<String> featureCodes,
                                  final RestTemplate template) {
        super(serviceIdentifier, "HasFeatureCodes");
        this.serviceIdentifier = serviceIdentifier;
        this.mdn = mdn;
        this.affiliateId = affiliateId;
        this.featureCodes = featureCodes;
        this.template = template;
    }

    @Override
    protected ResponseEntity<FeatureCodes> runCommand() {
        logger.debug("HasFeatureCodesCommand mdn:{} affiliateId:{} featureCodes:{}",
                  mdn,
                  affiliateId,
                  featureCodes);

        final String uri = buildUrl();

        logger.debug("Using URI {}", uri);
        final ResponseEntity<FeatureCodes> responseEntity = template.getForEntity(buildUrl(),
                                                                               FeatureCodes.class);

        logger.debug("HasFeatureCodesCommand result {}", responseEntity);
        return responseEntity;
    }

    private String buildUrl() {
        return UriComponentsBuilder.newInstance()
                                   .scheme("http")
                                   .host(serviceIdentifier)
                                   .pathSegment(SubscriberThirdPartyModelConstants.PATH_FRAGMENT_API,
                                                SubscriberThirdPartyModelConstants.PATH_FRAGMENT_SUBSCRIBER,
                                                SubscriberThirdPartyModelConstants.PATH_FRAGMENT_FEATURE_CODES)
                                   .queryParam(SubscriberThirdPartyModelConstants.QPARAM_MDN, mdn)
                                   .queryParam(SubscriberThirdPartyModelConstants.QPARAM_AFFILIATE_ID,
                                               affiliateId)
                                   .queryParam(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                               featureCodes.toArray())
                                   .toUriString();
    }
}
