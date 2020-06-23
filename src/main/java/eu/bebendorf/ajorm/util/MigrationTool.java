package eu.bebendorf.ajorm.util;

import eu.bebendorf.ajorm.Repo;
import eu.bebendorf.ajorm.TableInfo;
import eu.bebendorf.ajorm.wrapper.SQL;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MigrationTool {

    public static void migrate(Repo<?> repo){
        migrate(repo.getConnection(), repo.getInfo());
    }

    public static void migrate(SQL connection, TableInfo info){
        List<String> entries = new ArrayList<>();
        List<String> keyEntries = new ArrayList<>();
        for(String fName : info.getFields()){
            String cName = info.getColumnName(fName);
            StringBuilder entryBuilder = new StringBuilder("`"+cName+"` "+info.getSQLType(fName));
            if(info.isNotNull(fName)) {
                entryBuilder.append(" NOT NULL");
            }else{
                if(info.getTargetType(fName).equals(Timestamp.class))
                    entryBuilder.append(" NULL");
            }
            if(info.getIdField().equals(fName) && info.isAutoIncrement())
                entryBuilder.append(" AUTO_INCREMENT");
            entries.add(entryBuilder.toString());
            if(info.getPrimaryKey().equals(fName)){
                keyEntries.add("PRIMARY KEY (`"+cName+"`)");
            }
        }
        entries.addAll(keyEntries);
        connection.write("CREATE TABLE IF NOT EXISTS `"+info.getTableName()+"` ("+String.join(",", entries)+") DEFAULT CHARSET=utf8mb4;");
    }

}
