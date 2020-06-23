package eu.bebendorf.ajorm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Model {

    private static Method saveMethod;
    private static Method deleteMethod;
    private static Method finalDeleteMethod;
    private static Method restoreMethod;
    private static Method refreshMethod;

    static {
        try {
            saveMethod = Repo.class.getMethod("save", Model.class);
            deleteMethod = Repo.class.getMethod("delete", Model.class);
            finalDeleteMethod = Repo.class.getMethod("finalDelete", Model.class);
            restoreMethod = Repo.class.getMethod("restore", Model.class);
            refreshMethod = Repo.class.getMethod("refresh", Model.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(){
        try {
            saveMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(){
        try {
            deleteMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void finalDelete(){
        try {
            finalDeleteMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void restore(){
        try {
            restoreMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh(){
        try {
            refreshMethod.invoke(AJORM.repo(getClass()), this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
