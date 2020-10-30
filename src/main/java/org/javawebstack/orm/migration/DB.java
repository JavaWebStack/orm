package org.javawebstack.orm.migration;

import org.javawebstack.orm.SQLType;
import org.javawebstack.orm.wrapper.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DB {

    private final SQL sql;
    private final String tablePrefix;

    public DB(SQL sql, String tablePrefix){
        this.sql = sql;
        this.tablePrefix = tablePrefix;
    }

    public void table(String name, Consumer<Table> consumer){
        consumer.accept(new Table(name));
    }

    public class Table {

        private String name;
        private final List<Column> columns = new ArrayList<>();

        public Table(String name){
            this.name = name;
        }

        public String fullName() {
            return tablePrefix + name;
        }

        public Column column(String name){
            for(Column column : columns){
                if(column.name.equals(name))
                    return column;
            }
            Column column = new Column(name);
            columns.add(column);
            return column;
        }

        public Column id(){
            return column("id").integer().autoIncrement().primary();
        }

        public Column uuid(){
            return column("uuid").uuid().primary();
        }

        public void rename(String to){
            sql.write("RENAME TABLE `"+fullName()+"` TO `"+tablePrefix + to+"`;");
            this.name = to;
        }

        public void drop(){
            sql.write("DROP TABLE `"+fullName()+"`;");
        }

        public void create(){
            create(false);
        }

        public void create(boolean ifNotExists){
            StringBuilder sb = new StringBuilder("CREATE TABLE ");
            if(ifNotExists)
                sb.append("IF NOT EXISTS ");
            sb.append('`');
            sb.append(fullName());
            sb.append("` (");
            List<String> entries = new ArrayList<>(columns.stream().map(Column::definition).collect(Collectors.toList()));
            columns.forEach(c -> entries.addAll(c.contraints()));
            sb.append(String.join(",", entries));
            sb.append(") DEFAULT CHARSET=utf8mb4;");
            sql.write(sb.toString());
        }

        public class Column {
            private boolean primary = false;
            private boolean unique = false;
            private boolean autoIncrement = false;
            private boolean nullable = false;
            private String after;
            private String name;
            private SQLType type;
            private String size;
            public Column(String name){
                this.name = name;
            }
            public Column string(){
                return string(255);
            }
            public Column string(int size){
                type = SQLType.VARCHAR;
                this.size = String.valueOf(size);
                return this;
            }
            public Column text(int size){
                type = SQLType.TEXT;
                this.size = String.valueOf(size);
                return this;
            }
            public Column text(){
                type = SQLType.TEXT;
                return this;
            }
            public Column integer(int size){
                type = SQLType.INT;
                this.size = String.valueOf(size);
                return this;
            }
            public Column integer(){
                type = SQLType.INT;
                return this;
            }
            public Column bool(){
                type = SQLType.TINYINT;
                this.size = "1";
                return this;
            }
            public Column uuid(){
                type = SQLType.VARCHAR;
                this.size = "36";
                return this;
            }
            public Column enums(String... values){
                type = SQLType.ENUM;
                this.size = String.join(",", values);
                return this;
            }
            public Column primary(){
                this.primary = true;
                return this;
            }
            public Column unique(){
                this.unique = true;
                return this;
            }
            public Column autoIncrement(){
                this.autoIncrement = true;
                return this;
            }
            public Column nullable(){
                this.nullable = true;
                return this;
            }
            public Column after(String after){
                this.after = after;
                return this;
            }
            public Column first(){
                this.after = "";
                return this;
            }
            public void rename(String to){
                sql.write("ALTER TABLE `"+fullName()+"` RENAME COLUMN `"+name+"` TO `"+to+"`;");
                name = to;
            }
            public void drop(){
                sql.write("ALTER TABLE `"+fullName()+"` DROP COLUMN `"+name+"`;");
                columns.remove(this);
            }
            public void add(){
                StringBuilder sb = new StringBuilder("ALTER TABLE `");
                sb.append(fullName());
                sb.append("` ADD ");
                sb.append(definition());
                sb.append(';');
                sql.write(sb.toString());
            }
            public void modify(){
                StringBuilder sb = new StringBuilder("ALTER TABLE `");
                sb.append(fullName());
                sb.append("` MODIFY ");
                sb.append(definition());
                sb.append(';');
                sql.write(sb.toString());
            }
            String definition(){
                StringBuilder sb = new StringBuilder('`');
                sb.append(name);
                sb.append("` ");
                sb.append(type.name());
                if(size != null){
                    sb.append('(');
                    sb.append(size);
                    sb.append(')');
                }
                if(nullable){
                    sb.append(" NULL");
                }else{
                    sb.append(" NOT NULL");
                }
                if(autoIncrement)
                    sb.append(" AUTO_INCREMENT");
                if(after != null){
                    if(after.length() != 0){
                        sb.append(" AFTER `");
                        sb.append(after);
                        sb.append('`');
                    }else{
                        sb.append(" FIRST");
                    }
                }
                return sb.toString();
            }
            List<String> contraints(){
                List<String> constraints = new ArrayList<>(columns.stream().filter(c -> c.primary).map(c -> "PRIMARY KEY (`"+c.name+"`)").collect(Collectors.toList()));
                contraints().addAll(columns.stream().filter(c -> c.unique).map(c -> "UNIQUE (`"+c.name+"`)").collect(Collectors.toList()));
                return constraints;
            }
        }

    }

}
