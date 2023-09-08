package org.javawebstack.orm;

import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.migration.AutoMigrator;
import org.javawebstack.orm.wrapper.SQL;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ORM {

    private static final Map<Class<? extends Model>, Repo<?>> repositories = new HashMap<>();

    public static <T extends Model> Repo<T> repo(Class<T> model) {
        return (Repo<T>) repositories.get(model);
    }

    public static <T extends Model> Repo<T> register(Class<T> model, SQL sql, ORMConfig config) throws ORMConfigurationException {
        Repo<T> repo = new Repo<>(model, sql, config);
        repositories.put(model, repo);
        return repo;
    }

    public static <T extends Model> Repo<T> register(Class<T> model, SQL sql) throws ORMConfigurationException {
        return register(model, sql, new ORMConfig());
    }

    public static void register(Package p, SQL sql, ORMConfig config) throws ORMConfigurationException {
        for (Class<? extends Model> model : new Reflections(p.getName()).getSubTypesOf(Model.class)) {
            if (!Modifier.isAbstract(model.getModifiers()))
                ORM.register(model, sql, config);
        }
    }

    public static void register(Package p, SQL sql) throws ORMConfigurationException {
        register(p, sql, new ORMConfig());
    }

    public static void unregister(Class<? extends Model> model) {
        repositories.remove(model);
    }

    public static void unregister(Repo<?> model) {
        repositories.remove(model.getInfo().getModelClass());
    }

    public static List<Class<? extends Model>> getModels() {
        return new ArrayList<>(repositories.keySet());
    }

    public static List<Repo<?>> getRepos() {
        return new ArrayList<>(repositories.values());
    }

    public static void autoDrop() {
        AutoMigrator.drop(repositories.values().toArray(new Repo<?>[0]));
    }

    public static void autoMigrate() {
        autoMigrate(false);
    }

    public static void autoMigrate(boolean fresh) {
        AutoMigrator.migrate(fresh, repositories.values().toArray(new Repo<?>[0]));
    }

    public static void reset() {
        repositories.clear();
    }

}
