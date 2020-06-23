package eu.bebendorf.ajorm;

import eu.bebendorf.ajorm.exception.AJORMConfigurationException;
import eu.bebendorf.ajorm.wrapper.SQL;

import java.util.HashMap;
import java.util.Map;

public class AJORM {

    private static Map<Class<?>, Repo<?>> repositories = new HashMap<>();

    public static <T extends Model> Repo<T> repo(Class<T> model){
        return (Repo<T>) repositories.get(model);
    }

    public static <T extends Model> Repo<T> register(Class<T> model, SQL sql, AJORMConfig config) throws AJORMConfigurationException {
        Repo<T> repo = new Repo<>(model, sql, config);
        repositories.put(model, repo);
        return repo;
    }

    public static <T extends Model> Repo<T> register(Class<T> model, SQL sql) throws AJORMConfigurationException {
        return register(model, sql, new AJORMConfig());
    }

    public static <T extends Model> Repo<T> registerAndMigrate(Class<T> model, SQL sql, AJORMConfig config) throws AJORMConfigurationException {
        Repo<T> repo = new Repo<>(model, sql, config);
        repo.migrate();
        repositories.put(model, repo);
        return repo;
    }

    public static <T extends Model> Repo<T> registerAndMigrate(Class<T> model, SQL sql) throws AJORMConfigurationException {
        return registerAndMigrate(model, sql, new AJORMConfig());
    }

    public static void unregister(Class<? extends Model> model){
        repositories.remove(model);
    }

    public static void unregister(Repo<?> model){
        repositories.remove(model.getInfo().getModelClass());
    }

}
