package org.javawebstack.orm;

public interface Observer<T extends Model> {

    default void creating(T model) {
    }

    default void created(T model) {
    }

    default void updating(T model) {
    }

    default void updated(T model) {
    }

    default void saving(T model) {
    }

    default void saved(T model) {
    }

    default void deleting(T model) {
    }

    default void deleted(T model) {
    }

    default void restoring(T model) {
    }

    default void restored(T model) {
    }

}
