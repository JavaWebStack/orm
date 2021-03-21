package org.javawebstack.orm.test.shared.setup;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.test.shared.settings.MySQLConnectionContainer;

public class ModelSetup extends MySQLConnectionContainer {

    public static <T extends Model> Repo<T> setUpModel(Class<T> clazz) {

        // Converting to Runtime exception to avoid having to declare the thrown error which has no utility
        try {
            ORM.register(clazz, sql());
        } catch (ORMConfigurationException e) {
            throw new RuntimeException(e);
        }

        return Repo.get(clazz);
    }
}
