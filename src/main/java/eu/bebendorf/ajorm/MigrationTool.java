package eu.bebendorf.ajorm;

import java.util.ArrayList;
import java.util.List;

public class MigrationTool {

    public static void migrate(Table table){
        SQL sql = table.getConnection();
        TableInfo info = table.getInfo();
        List<String> entries = new ArrayList<>();
        List<String> keyEntries = new ArrayList<>();
        for(String fName : info.getFieldNames()){
            String cName = info.getColName(fName);
            StringBuilder entryBuilder = new StringBuilder("`"+cName+"` "+info.getSQLSizedType(fName));
            entryBuilder.append(" NOT NULL");
            if(info.isAutoIncrement(fName))
                entryBuilder.append(" AUTO_INCREMENT");
            entries.add(entryBuilder.toString());
            if(info.isPrimaryKey(fName)){
                keyEntries.add("PRIMARY KEY (`"+cName+"`)");
            }
        }
        entries.addAll(keyEntries);
        sql.write("CREATE TABLE IF NOT EXISTS `"+table.getTableName()+"` ("+String.join(",", entries)+") DEFAULT CHARSET=utf8mb4;");
    }

}
