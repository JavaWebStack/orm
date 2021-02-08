package org.javawebstack.orm.migration;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.util.KeyType;

import java.sql.Timestamp;

@Dates
public class MigrationState extends Model {

    @Column(id = true, ai = false, key = KeyType.PRIMARY)
    private String name;
    @Column
    private Timestamp createdAt;
    @Column
    private Timestamp updatedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

}
