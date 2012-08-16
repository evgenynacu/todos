package ru.nacu.todos.dao;

/**
 * @author quadro
 * @since 6/21/12 6:02 PM
 */
public interface Tables {
    String TODO = "todo";

    interface Columns {
        String _ID = "_id";
        String LABEL = "label";
        String STATUS = "status";
    }
}
