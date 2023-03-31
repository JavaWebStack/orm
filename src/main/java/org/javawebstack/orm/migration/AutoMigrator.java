package org.javawebstack.orm.migration;

import org.javawebstack.orm.Repo;
import org.javawebstack.orm.TableInfo;
import org.javawebstack.orm.annotation.Index;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.wrapper.SQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoMigrator {

    public static void migrate(Repo<?>... repos) {
        migrate(false, repos);
    }

    public static void migrate(boolean fresh, Repo<?>... repos) {
        if (fresh)
            drop(repos);
        Map<SQL, List<String>> tables = new HashMap<>();
        for (Repo<?> repo : repos) {
            if (!tables.containsKey(repo.getConnection())) {
                tables.put(repo.getConnection(), getTables(repo.getConnection()));
            }
            migrateTable(repo.getConnection(), repo.getInfo(), tables.get(repo.getConnection()).contains(repo.getInfo().getTableName()));
        }
    }

    public static void drop(Repo<?>... repos) {
        for (Repo<?> repo : repos) {
            try {
                repo.getConnection().write("DROP TABLE `" + repo.getInfo().getTableName() + "`;");
            } catch (SQLException ignored) {
            }
        }
    }

    private static void migrateTable(SQL sql, TableInfo info, boolean tableExists) {
        List<String> addColumns = new ArrayList<>();
        List<String> updateColumns = new ArrayList<>();
        Map<String, String> columnKeys = tableExists ? getColumnKeys(sql, info.getTableName()) : new HashMap<>();
        List<Object> addValues = new ArrayList<>();
        List<Object> updateValues = new ArrayList<>();
        for (String fieldName : info.getFields()) {
            String columnName = info.getColumnName(fieldName);
            StringBuilder sb = new StringBuilder("`")
                    .append(columnName)
                    .append("` ");
            sb.append(info.getType(fieldName).name());
            String parameterTypes = info.getTypeParameters(fieldName);

            if (parameterTypes != null)
                sb.append('(')
                        .append(parameterTypes)
                        .append(')');
            sb.append(info.isNotNull(fieldName) ? " NOT NULL" : " NULL");
            if (info.isAutoIncrement() && info.getIdField().equals(fieldName))
                sb.append(" AUTO_INCREMENT");
            if (columnKeys.containsKey(columnName)) {
                if (info.getDefault(fieldName) != null) {
                    sb.append(" DEFAULT(?)");
                    updateValues.add(info.getDefault(fieldName));
                }
                updateColumns.add(sb.toString());
            } else {
                if (info.getDefault(fieldName) != null) {
                    sb.append(" DEFAULT(?)");
                    addValues.add(info.getDefault(fieldName));
                }
                addColumns.add(sb.toString());
            }
        }
        if (info.getPrimaryKey() != null) {
            String columnName = info.getColumnName(info.getPrimaryKey());
            if (!columnKeys.containsKey(columnName) || !columnKeys.get(columnName).contains("PRI"))
                addColumns.add("PRIMARY KEY (`" + columnName + "`)");
        }
        for (String uniqueField : info.getUniqueKeys()) {
            String columnName = info.getColumnName(uniqueField);
            if (!columnKeys.containsKey(columnName) || !columnKeys.get(columnName).contains("UNI"))
                addColumns.add("UNIQUE (`" + columnName + "`)");
        }
        if (!tableExists) {
            try {
                sql.write(new StringBuilder("CREATE TABLE `")
                                .append(info.getTableName())
                                .append("` (")
                                .append(String.join(",", addColumns))
                                .append(") DEFAULT CHARSET=utf8mb4;").toString()
                        , addValues.toArray());
            } catch (SQLException throwables) {
                throw new ORMQueryException(throwables);
            }
        } else {
            if (addColumns.size() > 0) {
                try {
                    sql.write(new StringBuilder("ALTER TABLE `")
                                    .append(info.getTableName())
                                    .append("` ADD (")
                                    .append(String.join(",", addColumns))
                                    .append(");").toString()
                            , addValues.toArray());
                } catch (SQLException throwables) {
                    throw new ORMQueryException(throwables);
                }
            }
            if (updateColumns.size() > 0) {
                try {
                    sql.write(new StringBuilder("ALTER TABLE `")
                                    .append(info.getTableName())
                                    .append("` ")
                                    .append(updateColumns.stream().map(c -> "MODIFY COLUMN " + c).collect(Collectors.joining(",")))
                                    .append(";").toString()
                            , updateValues.toArray());
                } catch (SQLException throwables) {
                    throw new ORMQueryException(throwables);
                }
            }
        }

        List<String> existingIndices = getIndices(sql, info.getTableName());
        for (Index index : info.getIndices()) {
            String columns = Stream.of(index.value()).map(info::getColumnName).collect(Collectors.joining(","));
            String id = index.id().length() > 0 ? index.id() : "idx_" + String.join("_", index.value());
            if (existingIndices.contains(id))
                continue;

            StringBuilder sb = new StringBuilder("CREATE ");
            if (index.unique())
                sb.append("UNIQUE ");
            sb.append("INDEX `").append(id).append("` ");
            if (index.type() != Index.Type.AUTO)
                sb.append("USING ").append(index.type().name()).append(" ");
            sb.append("ON `")
                    .append(info.getTableName())
                    .append("` (")
                    .append(columns)
                    .append(");");
            try {
                sql.write(sb.toString());
            } catch (SQLException throwables) {
                throw new ORMQueryException(throwables);
            }
        }
    }

    private static Map<String, String> getColumnKeys(SQL sql, String tableName) {
        try {
            Map<String, String> columnKeys = new HashMap<>();
            ResultSet rs = sql.read("SHOW COLUMNS FROM `" + tableName + "`;");
            while (rs.next()) {
                columnKeys.put(rs.getString(1), rs.getString(4));
            }
            return columnKeys;
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    private static List<String> getIndices(SQL sql, String tableName) {
        try {
            List<String> indices = new ArrayList<>();
            ResultSet rs = sql.read("SHOW INDEX FROM `" + tableName + "`;");
            while (rs.next()) {
                indices.add(rs.getString(1));
            }
            return indices;
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

    private static List<String> getTables(SQL sql) {
        try {
            List<String> tables = new ArrayList<>();
            ResultSet rs = sql.read("SHOW TABLES;");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
            return tables;
        } catch (SQLException throwables) {
            throw new ORMQueryException(throwables);
        }
    }

}
