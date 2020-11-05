package org.javawebstack.orm.migration;

import org.javawebstack.orm.ORM;
import org.javawebstack.orm.ORMConfig;
import org.javawebstack.orm.Repo;
import org.javawebstack.orm.exception.ORMConfigurationException;
import org.javawebstack.orm.wrapper.SQL;
import org.reflections.Reflections;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Migrator {

    private final DB db;
    private final List<Migration> migrations = new ArrayList<>();

    public Migrator(SQL sql, ORMConfig config){
        this.db = new DB(sql, config.getTablePrefix());
        try {
            ORM.register(MigrationState.class, sql, config);
        } catch (ORMConfigurationException ignored) {}
    }

    public Migrator add(Migration... migrations){
        this.migrations.addAll(Arrays.asList(migrations));
        return this;
    }

    public Migrator add(Package p){
        Reflections reflections = new Reflections(p.getName());
        reflections.getSubTypesOf(Migration.class).forEach(c -> {
            try {
                add(c.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {}
        });
        return this;
    }

    public void migrate(){
        migrate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date.from(Instant.now())));
    }

    public void migrate(String version){
        Map<String, MigrationState> states = new HashMap<>();
        Repo.get(MigrationState.class).stream().forEach(m -> states.put(m.getName(), m));
        after(version).stream().sorted(Comparator.comparing(Migration::version).reversed()).forEach(m -> {
            if(states.containsKey(m.name())){
                m.down(db);
                states.get(m.name()).delete();
                states.remove(m.name());
            }
        });
        before(version).stream().sorted(Comparator.comparing(Migration::version)).forEach(m -> {
            if(!states.containsKey(m.name())){
                m.up(db);
                MigrationState state = new MigrationState();
                state.setName(m.name());
                state.save();
                states.put(state.getName(), state);
            }
        });
    }

    private List<Migration> before(String version){
        return new ArrayList<>(migrations.stream().filter(m -> m.version().compareTo(version) < 0).collect(Collectors.toList()));
    }

    private List<Migration> after(String version){
        return new ArrayList<>(migrations.stream().filter(m -> m.version().compareTo(version) >= 0).collect(Collectors.toList()));
    }

}
