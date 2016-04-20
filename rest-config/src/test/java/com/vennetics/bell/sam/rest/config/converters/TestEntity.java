package com.vennetics.bell.sam.rest.config.converters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestEntity", propOrder = {
        "field1",
        "field2",
        "field3"
})
@XmlRootElement(name = "TestEntity")
@SuppressWarnings({"PMD.UnusedPrivateField"})
public class TestEntity {
    private String field1;
    private String field2;
    @XmlElement(name = "field99")
    private String field3;

    private SubEntity emptyEntity;

    public TestEntity() {
    }

    public TestEntity(final String field1, final String field2, final String field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public String getField3() {
        return field3;
    }

    protected static class SubEntity {
        private String sub1;

    }
}

