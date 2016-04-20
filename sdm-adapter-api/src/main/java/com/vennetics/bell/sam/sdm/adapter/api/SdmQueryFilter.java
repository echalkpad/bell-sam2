package com.vennetics.bell.sam.sdm.adapter.api;

/**
 * Tuple object for querying by attr=value in SDM.
 */
public final class SdmQueryFilter {

    private final String filterAttribute;

    private final String filterValue;

    public SdmQueryFilter(final String filterAttribute, final String filterValue) {
        super();
        this.filterAttribute = filterAttribute;
        this.filterValue = filterValue;
    }

    public String getFilterAttribute() {
        return filterAttribute;
    }

    public String getFilterValue() {
        return filterValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (filterAttribute == null ? 0 : filterAttribute.hashCode());
        result = prime * result + (filterValue == null ? 0 : filterValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SdmQueryFilter other = (SdmQueryFilter) obj;
        if (filterAttribute == null) {
            if (other.filterAttribute != null) {
                return false;
            }
        } else if (!filterAttribute.equals(other.filterAttribute)) {
            return false;
        }
        if (filterValue == null) {
            if (other.filterValue != null) {
                return false;
            }
        } else if (!filterValue.equals(other.filterValue)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SdmQueryFilter [filterAttribute=" + filterAttribute + ", filterValue=" + filterValue
                        + "]";
    }

}
