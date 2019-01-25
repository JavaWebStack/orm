package eu.bebendorf.ajorm;

public abstract class Model {

    private static Table table;

    public static void init(Table table){
        Model.table = table;
    }

    public static Table<Object, Object> getTable(){
        return table;
    }

    public static Object getById(Object id){
        return table.queryById(id);
    }

    public static void deleteById(Object id){
        table.deleteById(id);
    }

    public void create(){
        table.delete(this);
    }

    public void update(){
        table.update(this);
    }

    public void delete(){
        table.delete(this);
    }

}
