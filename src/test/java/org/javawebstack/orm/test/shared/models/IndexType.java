package org.javawebstack.orm.test.shared.models;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Index;

import java.util.UUID;

@Index({"key", "value"})
@Index(value = {"id", "key"}, id = "idx_custom")
@Index(value = {"key"}, unique = true)
public class IndexType extends Model {
    @Column
    UUID id;
    @Column
    String key;
    @Column
    String value;
}
