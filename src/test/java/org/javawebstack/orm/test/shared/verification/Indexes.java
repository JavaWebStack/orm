package org.javawebstack.orm.test.shared.verification;

import lombok.Builder;
import lombok.ToString;
import org.javawebstack.orm.test.shared.settings.MySQLConnectionContainer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Indexes extends MySQLConnectionContainer {
    String tableName;
    List<IndexInfo> indices = new ArrayList<>();

    public Indexes(String tableName) throws SQLException {
        this.tableName = tableName;
        String query = String.format("SHOW INDEXES FROM %s WHERE `key_name` != 'PRIMARY'", tableName);
        ResultSet resultSet = sql().read(query);
        while (resultSet.next()) {
            indices.add(IndexInfo.builder()
                    .name(resultSet.getString("key_name"))
                    .type(resultSet.getString("index_type"))
                    .unique(resultSet.getInt("non_unique") != 1)
                    .build()
            );
        }
    }

    public void assertHasIndex(String indexName) {
        assertTrue(
                indices.stream().anyMatch(i -> i.name.equals(indexName)),
                String.format("Index %s.%s doesn't exist on table.", tableName, indexName)
        );
    }

    public void assertIsUnique(String indexName) {
        IndexInfo indexInfo = indices.stream().filter(i -> i.name.equals(indexName)).findFirst().orElse(null);
        assertNotNull(indexInfo);
        assertTrue(
                indexInfo.unique,
                String.format("Index %s.%s is not unique.", tableName, indexInfo)
        );
    }

    @Builder
    @ToString
    public static class IndexInfo {
        String name;
        String type;
        boolean unique;
    }
}
