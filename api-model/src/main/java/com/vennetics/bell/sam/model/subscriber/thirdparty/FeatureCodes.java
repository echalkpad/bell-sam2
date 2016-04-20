package com.vennetics.bell.sam.model.subscriber.thirdparty;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A POJO to represent the FeatureCodes
 *
 */
@XmlRootElement(name = "featureCodes")
public final class FeatureCodes {

    @XmlElement(name = "codes")
    private List<Boolean> codesPresent;

    public FeatureCodes() {
        // For JSON deserialsation.
    }

    public FeatureCodes(final List<Boolean> codesPresent) {
        this.codesPresent = codesPresent;
    }

    public List<Boolean> getCodesPresent() {
        if (codesPresent == null) {
            codesPresent = new ArrayList<>();
        }
        return codesPresent;
    }

    @Override
    public int hashCode() {
        return codesPresent != null ? codesPresent.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final FeatureCodes that = (FeatureCodes) obj;

        return !(codesPresent != null ? !codesPresent.equals(that.codesPresent)
                        : that.codesPresent != null);

    }

    @Override
    public String toString() {
        String result = "FeatureCodes{codesPresent=" + codesPresent;
        return result + '}';
    }
}
