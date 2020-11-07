package org.javawebstack.orm.exception;

public class ORMQueryException extends RuntimeException {

    public ORMQueryException(Throwable parent){
        this(parent.getMessage());
    }

    public ORMQueryException(String message){
        super(message);
    }

}
