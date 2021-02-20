package org.javawebstack.orm.test;

import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.annotation.Column;
import org.javawebstack.orm.annotation.Dates;
import org.javawebstack.orm.annotation.SoftDelete;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

public class DatesTest extends ORMTestCase {
    @Test
    public void testTimes() throws ORMConfigurationException {
        ORMConfig config = new ORMConfig()
                .setDefaultSize(255);
        ORM.register(TimesModel.class, sql(), config);
        ORM.autoMigrate(true);

        TimesModel timesModel = new TimesModel();
        timesModel.save();

        timesModel = Repo.get(TimesModel.class).get(timesModel.id);
        assertNotNull(timesModel.cratedAt);

        // updated_at test
        timesModel.exampleField = " ";
        timesModel.save();

        timesModel = Repo.get(TimesModel.class).get(timesModel.id);
        assertNotNull(timesModel.updatedAt);

        // deleted_at test
        timesModel.delete();

        timesModel = Repo.get(TimesModel.class).query().withDeleted().where("id", timesModel.id).first();
        assertNotNull(timesModel.deletedAt);
    }

    @Dates
    @SoftDelete
    public static class TimesModel extends Model {
        @Column
        public int id;

        @Column
        public String exampleField;

        @Column
        public Timestamp cratedAt;

        @Column
        public Timestamp updatedAt;

        @Column
        public Timestamp deletedAt;
    }
}
