package com.example.individualapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import static com.example.individualapp.Const.*;

public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createToDoTable = "CREATE TABLE " + TABLE_TODO + " (" +
                COL_ID + " integer PRIMARY KEY AUTOINCREMENT," +
                COL_CREATED_AT + " datetime DEFAULT CURRENT_TIMESTAMP," +
                COL_NAME + " varchar, " +
                COL_IS_COMPLETED + "integer)";
        db.execSQL(createToDoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    boolean addToDo(ToDo todo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, todo.getName());
        //cv.put(COL_IS_COMPLETED, todo.getCompleted());
        long result = db.insert(TABLE_TODO, null, cv);
        return result != -1;
    }

    void updateToDo(ToDo todo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, todo.getName());
        //cv.put(COL_IS_COMPLETED, todo.getCompleted());
        db.update(TABLE_TODO, cv, COL_ID + "=?", new String[]{String.valueOf(todo.getId())});
    }

    void deleteToDo(long todoId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TODO, COL_ID + "=?", new String[]{String.valueOf(todoId)});
    }

    ArrayList<ToDo> getToDos() {
        ArrayList<ToDo> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor queryResult = db.rawQuery("SELECT * from " + TABLE_TODO, null);
        if (queryResult.moveToFirst()) {
            do {
                ToDo todo = new ToDo();
                todo.setId(queryResult.getLong(queryResult.getColumnIndex(COL_ID)));
                todo.setName(queryResult.getString(queryResult.getColumnIndex(COL_NAME)));
                //todo.setCompleted(queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETED)));
                result.add(todo);
            } while (queryResult.moveToNext());
        }
        queryResult.close();
        return result;
    }
}