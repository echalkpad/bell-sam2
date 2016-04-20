package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import com.vennetics.bell.sam.registry.ServiceConstants;
import com.vennetics.microservices.common.core.soap.gateway.adapter.errors.exceptions.InvalidServiceIndentifierException;

import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.Attribute;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.SearchFilter;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0._interface.Subscriber;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.PolicyException;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.ServiceException;

/**
 * Implements ParlayX {@link Subscriber} interface, adapting SOAP to internal
 * REST api.
 */

@WebService(
                endpointInterface = "generated.ca.bell.wsdl.thirdparty.subscriber.v1_0._interface.Subscriber")
@Component
public class SubscriberThirdPartyEndpoint implements Subscriber {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriberThirdPartyEndpoint.class);

    @Autowired
    private ISubscriberThirdPartyAdapter subxService;

    @Resource
    private WebServiceContext webServiceContext;

    @Override
    public String getSubscriberPassword(final String subsMDN,
                                        final String affiliateID) throws PolicyException,
                                                                  ServiceException {
        return null;
    }

    @Override
    public List<Attribute> getSubscriberProfile(final SearchFilter searchFilter,
                                                final String filterValue) throws PolicyException,
                                                                          ServiceException {
        LOG.debug(">>> getSubscriberProfile ");

        final SubscriberProfile profile = getSubscriberProfileFromService(extractServiceIdentiferFromUrl(),
                                                                          searchFilter,
                                                                          filterValue);
        LOG.debug("getSubscriberProfile profile:{}", profile);

        final List<Attribute> result = mapProfileToAttributeList(profile);

        LOG.debug("getSubscriberProfile result:{}", result);
        return result;
    }

    @Override
    public boolean changeSubscriberPassword(final String subsMDN,
                                            final String oldPassword,
                                            final String newPassword,
                                            final String confirmNewPassword,
                                            final String affiliateID) throws PolicyException,
                                                                      ServiceException {
        return false;
    }

    @Override
    public List<Boolean> hasFeatureCodes(final String subsMdn,
                                         final String affiliateId,
                                         final List<String> listOfFeatureCodes) throws PolicyException,
                                                                                ServiceException {
        LOG.debug(">>> hasFeatureCodes ");

        final FeatureCodes featureCodes = getFeatureCodesFromService(extractServiceIdentiferFromUrl(),
                                                                     subsMdn,
                                                                     affiliateId,
                                                                     listOfFeatureCodes);
        LOG.debug("hasFeatureCodes featureCodes:{}", featureCodes);

        final List<Boolean> result = featureCodes.getCodesPresent();

        LOG.debug("hasFeatureCodes result:{}", result);
        return result;
    }

    private List<Attribute> mapProfileToAttributeList(final SubscriberProfile profile) {
        return profile.getAttributes()
                      .stream()
                      .map(SubscriberThirdPartyEndpoint::newAttribute)
                      .collect(Collectors.toList());
    }

    private SubscriberProfile getSubscriberProfileFromService(final String serviceIdentifier,
                                                              final SearchFilter searchFilter,
                                                              final String filterValue) {
        return subxService.getSubscriberProfile(serviceIdentifier,
                                                mapSearchFilterType(searchFilter),
                                                filterValue)
                          .getBody();
    }

    private FeatureCodes getFeatureCodesFromService(final String serviceIdentifier,
                                                    final String subsMdn,
                                                    final String affiliateId,
                                                    final List<String> featureCodes) {
        return subxService.hasFeatureCodes(serviceIdentifier, subsMdn, affiliateId, featureCodes)
                          .getBody();
    }

    // Map external search filter type (defined by WSDL) to internal.
    private static SearchFilterType mapSearchFilterType(final SearchFilter searchFilter) {
        return SearchFilterType.valueOf(searchFilter.toString());
    }

    @SuppressWarnings({ "squid:UnusedPrivateMethod" }) // It is used! But
                                                       // sonarqube doesn't
                                                       // recognise it.
    private static Attribute newAttribute(final com.vennetics.bell.sam.model.subscriber.thirdparty.Attribute entry) {
        final Attribute attribute = new Attribute();
        attribute.setKey(entry.getName());
        attribute.setValue(entry.getValue());
        return attribute;
    }

    private String extractServiceIdentiferFromUrl() {
        final HttpServletRequest request = getCurrentServletRequest();
        LOG.debug("request path {}", request.getRequestURI());
        return extractServiceIdentifier(request.getRequestURI());
    }

    private HttpServletRequest getCurrentServletRequest() {
        return (HttpServletRequest) webServiceContext.getMessageContext()
                                                     .get(MessageContext.SERVLET_REQUEST);
    }

    private static String extractServiceIdentifier(final String requestURI) {
        final Pattern pattern = Pattern.compile(".*" + ServiceConstants.BELL_SUBSCRIBER_URL
                        + "/([^/]*)/.*");
        final Matcher matcher = pattern.matcher(requestURI);

        if (matcher.matches()) {
            LOG.debug("ServiceIdentifier = {}", matcher.group(1));
            return matcher.group(1);
        }
        // This really should not happen. The CxfConfiguration
        // associates the CXFServlet with URLs that
        // contain the valid service identifiers.
        throw new InvalidServiceIndentifierException(requestURI);
    }
}
