package eu.bebendorf.ajorm.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EventBus<ObjectType> {

    private List<Method> beforeUpdate = new ArrayList<>();
    private List<Method> afterUpdate = new ArrayList<>();
    private List<Method> beforeCreate = new ArrayList<>();
    private List<Method> afterCreate = new ArrayList<>();
    private List<Method> beforeDelete = new ArrayList<>();
    private List<Method> afterDelete = new ArrayList<>();

    public void check(Method method){
        method.setAccessible(true);
        {
            OnCreate on = method.getDeclaredAnnotation(OnCreate.class);
            if(on!=null)
                if(on.after()) {
                    afterCreate.add(method);
                } else {
                    beforeCreate.add(method);
                }
        }
        {
            OnUpdate on = method.getDeclaredAnnotation(OnUpdate.class);
            if(on!=null)
                if(on.after()) {
                    afterUpdate.add(method);
                } else {
                    beforeUpdate.add(method);
                }
        }
        {
            OnDelete on = method.getDeclaredAnnotation(OnDelete.class);
            if(on!=null)
                if(on.after()) {
                    afterDelete.add(method);
                } else {
                    beforeDelete.add(method);
                }
        }
    }

    public void check(Class clazz){
        System.out.println("Check: "+clazz.getName());
        for(Method method : clazz.getDeclaredMethods())
            check(method);
    }

    public void beforeUpdate(ObjectType object){
        beforeUpdate.forEach(method -> invoke(method,object));
    }

    public void afterUpdate(ObjectType object){
        afterUpdate.forEach(method -> invoke(method,object));
    }

    public void beforeCreate(ObjectType object){
        beforeCreate.forEach(method -> invoke(method,object));
    }

    public void afterCreate(ObjectType object){
        afterCreate.forEach(method -> invoke(method,object));
    }

    public void beforeDelete(ObjectType object){
        beforeDelete.forEach(method -> invoke(method,object));
    }

    public void afterDelete(ObjectType object){
        afterDelete.forEach(method -> invoke(method,object));
    }

    private void invoke(Method method, ObjectType object){
        try {
            if(Modifier.isStatic(method.getModifiers()))
                method.invoke(null, object);
            else
                method.invoke(object);
        }catch(Exception ex){}
    }

}
