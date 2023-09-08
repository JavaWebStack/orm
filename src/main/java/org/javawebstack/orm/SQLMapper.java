package org.javawebstack.orm;

import org.javawebstack.commons.inject.Injector;
import org.javawebstack.orm.exception.ORMQueryException;
import org.javawebstack.orm.mapper.DefaultMapper;
import org.javawebstack.orm.mapper.TypeMapper;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLMapper {

    public static <T extends Model> Map<String, Object> map(Repo<T> repo, T entity) {
        Map<String, Object> values = new HashMap<>();
        for (String fieldName : repo.getInfo().getFields())
            values.put(repo.getInfo().getColumnName(fieldName), getValue(repo, fieldName, entity));
        return values;
    }

    public static <T extends Model> List<T> map(Repo<T> repo, ResultSet rs, List<Class<? extends Model>> joinedModels) {
        List<T> list = new ArrayList<>();
        try {
            while (rs.next()) {
                T t = (T) repo.getInfo().getModelConstructor().newInstance();
                for (Class<? extends Model> model : joinedModels) {
                    Repo<Model> r = Repo.get((Class<Model>) model);
                    Model o = (Model) r.getInfo().getModelConstructor().newInstance();
                    t.internalAddJoinedModel(model, mapBack(r, rs, o));
                }
                list.add(mapBack(repo, rs, t));
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new ORMQueryException(ex);
        }
        return list;
    }

    public static <T extends Model> T mapBack(Repo<T> repo, ResultSet rs, T t) {
        t.setEntryExists(true);
        for (String fieldName : repo.getInfo().getFields()) {
            Object value = getValue(rs, repo.getInfo().getType(fieldName).getJavaType(), repo.getInfo().getTableName(), repo.getInfo().getColumnName(fieldName));
            setValue(repo, fieldName, t, value);
        }
        t.updateOriginal();
        return t;
    }

    public static List<Object> mapParams(Repo<?> repo, List<Object> params) {
        List<Object> result = new ArrayList<>();
        for (Object o : params) {
            if (o == null) {
                result.add(null);
                continue;
            }
            TypeMapper mapper = repo.getInfo().getConfig().getTypeMapper(o.getClass(), 0);
            if (mapper == null) {
                result.add(o);
                continue;
            }
            result.add(mapper.mapToSQL(o, o.getClass()));
        }
        return result;
    }

    private static <T extends Model> Object getValue(Repo<T> repo, String fieldName, T entry) {
        try {
            Object value = repo.getInfo().getField(fieldName).get(entry);
            for (TypeMapper mapper : repo.getInfo().getConfig().getTypeMappers())
                value = mapper.mapToSQL(value, repo.getInfo().getField(fieldName).getType());
            return value;
        } catch (IllegalAccessException e) {
            throw new ORMQueryException(e);
        }
    }

    private static Object getValue(ResultSet rs, Class<?> sqlType, String tableName, String columnName) {
        try {
            try {
                rs.findColumn(columnName);
            } catch (SQLException ex) {
                columnName = tableName + "." + columnName;
            }
            try {
                rs.findColumn(columnName);
            } catch (SQLException ex) {
                return null;
            }
            return rs.getObject(columnName, DefaultMapper.TYPE_MAPPING);
        } catch (SQLException e) {
            throw new ORMQueryException(e);
        }
    }

    private static <T extends Model> void setValue(Repo<T> repo, String fieldName, T entry, Object value) {
        try {
            for (TypeMapper mapper : repo.getInfo().getConfig().getTypeMappers())
                value = mapper.mapToJava(value, repo.getInfo().getField(fieldName).getType());
            repo.getInfo().getField(fieldName).set(entry, value);
        } catch (IllegalAccessException e) {
            throw new ORMQueryException(e);
        }
    }

}
