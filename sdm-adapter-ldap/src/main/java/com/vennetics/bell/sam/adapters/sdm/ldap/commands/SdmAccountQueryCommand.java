package com.vennetics.bell.sam.adapters.sdm.ldap.commands;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.util.CollectionUtils;

import com.vennetics.bell.sam.adapters.ldap.AbstractLdapSearchCommand;
import com.vennetics.bell.sam.adapters.sdm.ldap.support.SdmAccountAttributesMapper;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

/**
 * Query an SdmAccount matching a filter key and value. Optionally also includes
 * the OU in the "where" clause.
 */
public class SdmAccountQueryCommand extends AbstractLdapSearchCommand<SdmAccount> {

    private static final String BLANK_SEARCH_BASE = "";

    private static final Logger logger = LoggerFactory.getLogger(SdmAccountQueryCommand.class);

    private static final String COMMAND_NAME_KEY = "SdmAccountQuery";

    private static final String COMMAND_GROUP_KEY = "ADAPTER-SDM";

    private final Set<SdmQueryFilter> filters;

    /**
     * Query by single attribute constructor.
     *
     * @param ldapTemplate
     *            the template to query
     * @param queryFilter
     *            the filter to apply
     * @param attributes
     *            the attributes to query. Can be null.
     */
    public SdmAccountQueryCommand(final LdapOperations ldapTemplate,
                                  @Nonnull final SdmQueryFilter queryFilter,
                                  final Set<String> attributes) {

        super(COMMAND_GROUP_KEY,
              COMMAND_NAME_KEY,
              ldapTemplate,
              asLdapQuery(new HashSet<>(Arrays.asList(queryFilter)), attributes),
              new SdmAccountAttributesMapper(attributes));

        filters = new HashSet<>();
        filters.add(queryFilter);
    }

    /**
     * Query with OU constructor.
     *
     * @param ldapTemplate
     *            the template to query
     * @param filters
     *            the filters to apply
     * @param attributes
     *            the attributes to query. Can be null.
     */
    public SdmAccountQueryCommand(final LdapOperations ldapTemplate,
                                  @Nonnull final Set<SdmQueryFilter> filters,
                                  final Set<String> attributes) {

        super(COMMAND_GROUP_KEY,
              COMMAND_NAME_KEY,
              ldapTemplate,
              asLdapQuery(filters, attributes),
              new SdmAccountAttributesMapper(attributes));

        this.filters = filters;
    }

    private static LdapQuery asLdapQuery(final Set<SdmQueryFilter> filters,
                                         final Set<String> attributes) {

        final LdapQueryBuilder builder = query().base(BLANK_SEARCH_BASE)
                                                .searchScope(SearchScope.SUBTREE);

        if (!CollectionUtils.isEmpty(attributes)) {
            // Add query attributes if supplied
            builder.attributes(attributes.toArray(new String[0]));
        }

        // Convert to list to allow ordering.
        final List<SdmQueryFilter> filterList = new ArrayList<>(filters);

        // Add where clause
        final ContainerCriteria criteria = builder.where(filterList.get(0).getFilterAttribute())
                                                  .is(filterList.get(0).getFilterValue());

        // Add and clauses if more than one filter
        if (filters.size() > 1) {
            for (int i = 1; i < filters.size(); i++) {
                criteria.and(filterList.get(i).getFilterAttribute())
                        .is(filterList.get(i).getFilterValue());
            }
        }

        return builder;
    }

    @Override
    protected List<SdmAccount> run() {

        logger.debug("Running SDM query against {}", filters);

        final List<SdmAccount> result = search();

        logger.debug("Found: {}", result);

        return result;
    }

}
