package org.javawebstack.orm.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SQLDriverFactory {
    private Map<String, Supplier<SQL>> suppliers = new HashMap<>();
    private Map<String, String> properties;

    public SQLDriverFactory(Map<String, String> properties) {
        this.properties = properties;
        addDefaultDrivers();
    }

    public void registerDriver (String name, Supplier<SQL> supplier) {
        suppliers.put(name, supplier);
    }

    private void addDefaultDrivers() {
        registerDriver("sqlite", () -> new SQLite(properties.get("file")));
        registerDriver("mysql", () -> new MySQL(
                properties.get("host"),
                Integer.parseInt(properties.get("port")),
                properties.get("name"),
                properties.get("user"),
                properties.get("password")
        ));
    }

    public SQL getDriver(String name) throws SQLDriverNotFoundException {
        Supplier<SQL> supplier = suppliers.get(name);
        if (supplier == null)
            throw new SQLDriverNotFoundException(name);
        return supplier.get();
    }
}
