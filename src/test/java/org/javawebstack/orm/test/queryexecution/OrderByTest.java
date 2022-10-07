package org.javawebstack.orm.test.queryexecution;

import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.test.ORMTestCase;
import org.javawebstack.orm.test.shared.models.OnlyIdModel;
import org.javawebstack.orm.test.shared.setup.ModelSetup;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderByTest extends ORMTestCase {

    @Test
    void testOrderByCanPullResults() {
        ModelSetup.setUpModel(OnlyIdModel.class);
        ORM.autoMigrate(true);

        new OnlyIdModel().save();
        new OnlyIdModel().save();
        new OnlyIdModel().save();

        assertDoesNotThrow(() -> Repo.get(OnlyIdModel.class)
                .query()
                .order("id")
                .get()
        );
    }

    @Test
    void testOrderByWorksWithAsc() {
        ModelSetup.setUpModel(OnlyIdModel.class);
        ORM.autoMigrate(true);

        new OnlyIdModel().save();
        new OnlyIdModel().save();
        new OnlyIdModel().save();

        List<OnlyIdModel> orderedList = Repo.get(OnlyIdModel.class)
                .query()
                .order("id", false)
                .get();

        for(int i = 1; i <= 3; i++) {
            assertEquals(i, orderedList.get(i - 1).getId());
        }
    }

    @Test
    void testOrderByWorksWithDesc() {
        ModelSetup.setUpModel(OnlyIdModel.class);
        ORM.autoMigrate(true);

        new OnlyIdModel().save();
        new OnlyIdModel().save();
        new OnlyIdModel().save();

        List<OnlyIdModel> orderedList = Repo.get(OnlyIdModel.class)
                .query()
                .order("id", true)
                .get();

        for(int i = 3; i >= 1; i--) {
            assertEquals(i, orderedList.get(3 - i).getId());
        }
    }
}
