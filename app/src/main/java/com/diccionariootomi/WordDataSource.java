package com.diccionariootomi;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class WordDataSource {
    SQLiteOpenHelper openHelper;
    SQLiteDatabase database;

    private static final String[] allColumn = {
            WordOpenHelper.COLUMN_ID,
            WordOpenHelper.COLUMN_SPANISH,
            WordOpenHelper.COLUMN_OTOMI
    };
    public WordDataSource(Context context) {
        openHelper = new WordOpenHelper(context);
    }

    //    ****************************************************************************************************

    private static final String returnOtomi = WordOpenHelper.COLUMN_OTOMI;
    private static final String[] returnSpanish = {
            WordOpenHelper.COLUMN_SPANISH
    };

    //    ****************************************************************************************************

    public void openDB() {
        database = openHelper.getWritableDatabase();
    }

    public void closeDB() {
        openHelper.close();
    }

    public void create(Word word) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WordOpenHelper.COLUMN_SPANISH, word.getSpanish());
        contentValues.put(WordOpenHelper.COLUMN_OTOMI, word.getOtomi());
        database.insert(WordOpenHelper.TABLE_NAME, null, contentValues);
    }

    public List<Word> allWords() {
        Cursor cursor = database.query(WordOpenHelper.TABLE_NAME, allColumn, null, null, null, null, null);
        List<Word> wordMList = cursorToList(cursor);
        return wordMList;
    }

    @SuppressLint("Range")
    public List<Word> cursorToList(Cursor cursor) {
        List<Word> wordMList = new ArrayList<Word>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Word wordM = new Word();
                wordM.setId(cursor.getInt(cursor.getColumnIndex(WordOpenHelper.COLUMN_ID)));
                wordM.setSpanish(cursor.getString(cursor.getColumnIndex(WordOpenHelper.COLUMN_SPANISH)));
                wordM.setOtomi(cursor.getString(cursor.getColumnIndex(WordOpenHelper.COLUMN_OTOMI)));
                wordMList.add(wordM);
            }
        }
        return wordMList;
    }

    //    ****************************************************************************************************
//    public String searchInSpanish() {
//        Cursor cursor = database.query(WordSQLiteOpenHelper.TABLE_NAME, null, null, null, null, null, null);
//        List<WordM> wordMList = cursorToListInSpanish(cursor);
//        String s = wordMList.toString();
//        return s;
//    }
//
//    @SuppressLint("Range")
//    public List<WordM> cursorToListInSpanish(Cursor cursor) {
//        List<WordM> wordMList = new ArrayList<WordM>();
//        if (cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                WordM wordM = new WordM();
////                wordM.setId(cursor.getInt(cursor.getColumnIndex(WordSQLiteOpenHelper.COLUMN_ID)));
////                wordM.setSpanish(cursor.getString(cursor.getColumnIndex(WordSQLiteOpenHelper.COLUMN_SPANISH)));
//                wordM.setOtomi(cursor.getString(cursor.getColumnIndex(WordSQLiteOpenHelper.COLUMN_OTOMI)));
//                wordMList.add(wordM);
//            }
//        }
//        return wordMList;
//    }


    @SuppressLint("Range")
    public String searchInSpanish(String wordInSpanish) {
//        String s = database.execSQL("SELECT " + WordSQLiteOpenHelper.COLUMN_OTOMI + " FROM " + WordSQLiteOpenHelper.TABLE_NAME + " WHERE " + WordSQLiteOpenHelper.COLUMN_SPANISH + " = " + wordInSpanish);
        Cursor cursor = database.rawQuery("SELECT " + WordOpenHelper.COLUMN_OTOMI + " FROM " + WordOpenHelper.TABLE_NAME + " WHERE "  + " lower(" + WordOpenHelper.COLUMN_SPANISH + ")" + " = " + "lower(" + wordInSpanish + ")", null);
//        SELECT "tijeras" from products WHERE ID = 4
        return cursor.getString(cursor.getColumnIndex(WordOpenHelper.COLUMN_OTOMI));
    }

//    public ArrayList<WordM> searchInSpanish(String word) {
////        "SELECT " + WordSQLiteOpenHelper.COLUMN_OTOMI + " FROM " + WordSQLiteOpenHelper.TABLE_NAME + " WHERE " + WordSQLiteOpenHelper.COLUMN_SPANISH + " = " + spanishWord);
////        Cursor cursor = database.rawQuery("SELECT " + WordSQLiteOpenHelper.COLUMN_OTOMI + " FROM " + WordSQLiteOpenHelper.TABLE_NAME + " WHERE "  + " lower(" + WordSQLiteOpenHelper.COLUMN_SPANISH + ")" + " = " + "lower(" + word + ")", null);
//        Cursor cursor = database.rawQuery("SELECT * FROM " + WordSQLiteOpenHelper.TABLE_NAME + " WHERE "  + " lower(" + WordSQLiteOpenHelper.COLUMN_SPANISH + ")" + " = " + "lower(" + word + ")", null);
////        Cursor cursord = database.rawQuery("select translation from EN_FR where lower(word) = lower(\""+word+"\");", null);
//        ArrayList<WordM> arrayListTranslation = new ArrayList<WordM>();
//        cursor.moveToFirst();
//        for(int i = 0; i < cursor.getCount(); i++) {
//            arrayListTranslation.add(new WordM(word, cursor.getString(2)));
//            cursor.moveToNext();
//        }
//        return arrayListTranslation;
//    }
//**************************************Example***************************************************
//    SQLiteOpenHelper sqLiteHelper = new SQLiteOpenHelper(this, "admin", null, 1);
//    SQLiteDatabase database = sqLiteHelper.getWritableDatabase();
//
//    String codigo = code.getEditText().getText().toString();
//
//        if(!codigo.isEmpty()){
//        Cursor cursor = database.rawQuery("select description, price from product  where code =" + codigo, null);
//
//        if(cursor.moveToFirst()){
//            //String nan= cursor.getColumnName(1);
//            description.getEditText().setText(cursor.getString(0));
//            price.getEditText().setText(cursor.getString(1));
//            database.close();
//            change.setEnabled(true);
//            delete.setEnabled(true);
//        } else {
//            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
//            database.close();
//        }
//    }else {
//        Toast.makeText(this, "Ingresa un codigo", Toast.LENGTH_SHORT).show();
//    }

    public Cursor getWordMatches(String query, String[] columns) {
        String selection = WordOpenHelper.COLUMN_OTOMI + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(WordOpenHelper.TABLE_NAME);

        Cursor cursor = builder.query(openHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

}
