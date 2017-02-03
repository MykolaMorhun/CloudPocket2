package com.cloudpocket.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Describes Cloud Pocket system option.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "CloudPocketOption.getKeys",
                    query = "SELECT option.key FROM CloudPocketOption option"),
        @NamedQuery(name = "CloudPocketOption.findValueByKey",
                    query = "SELECT option.value FROM CloudPocketOption option WHERE option.key = ?1")
})
@Table(name = "system_options")
public class CloudPocketOption {
    @Id
    @Column
    String key;
    @Column
    String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
