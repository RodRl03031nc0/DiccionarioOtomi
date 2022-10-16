package com.diccionariootomi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout inputSearch;
    private TextView outputTvSearch;

    private ListView listViewSearch;
    private List<Word> searchList;

    private WordDataSource dataSource;

    private List<Word> wordMListTempAllWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewTempAllWords = findViewById(R.id.main_lv);

        inputSearch = findViewById(R.id.main_til_input_search);
        outputTvSearch = findViewById(R.id.main_tv_output_search);
        Button btnSearch = findViewById(R.id.main_btn_search);

        CreateWords createDic = new CreateWords();

        dataSource = new WordDataSource(this);
        dataSource.openDB();

        if (dataSource.allWords().isEmpty()) {
            createDic.createData(this);
            wordMListTempAllWords = dataSource.allWords();
        }

        ArrayAdapter<Word> adapter = new ArrayAdapter<Word>(this, android.R.layout.simple_list_item_1, wordMListTempAllWords);
        listViewTempAllWords.setAdapter(adapter);

//        btnSearch.setOnClickListener(view -> {
//            searchWordInSpanish();
//        });
    }

    private void searchWordInSpanish() {
        String txt = Objects.requireNonNull(inputSearch.getEditText()).getText().toString().toLowerCase();

        dataSource.searchInSpanish(txt);

        ArrayAdapter<Word> arrayAdapter = new ArrayAdapter<Word>(this, android.R.layout.simple_list_item_1, searchList);
        listViewSearch.setAdapter(arrayAdapter);



        //            try {
//                String word = inputSearch.getEditText().getText().toString().trim();
//                String val = dataSource.searchInSpanish(word);
//                outputSearch.setText(val);
////                searchList = dataSource.searchInSpanish(word);
////                ArrayAdapter<WordM> arrayAdapter = new ArrayAdapter<WordM>(this, android.R.layout.simple_list_item_1, searchList);
////                outputSearch.setAdapter(arrayAdapter);
//            } catch (Exception e) {
//                e.getMessage();
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//            }





//        WordSQLiteOpenHelper sqLiteHelper = new WordSQLiteOpenHelper(this);
//        SQLiteDatabase database = sqLiteHelper.getReadableDatabase();
//
//        String word = inputSearch.getEditText().getText().toString();
//
//        try {
//            if(!word.isEmpty()){
//                Cursor cursor = database.rawQuery("SELECT " + WordSQLiteOpenHelper.COLUMN_OTOMI + " FROM " + WordSQLiteOpenHelper.TABLE_NAME + " WHERE " + WordSQLiteOpenHelper.COLUMN_SPANISH + " = " + word, null);
//
//                if(cursor.moveToFirst()){
//                    //String nan= cursor.getColumnName(1);
//                    outputSearch.setText(cursor.getString(2));
//                    database.close();
//                } else {
//                    Toast.makeText(this, "Busqueda no encontrada", Toast.LENGTH_SHORT).show();
//                    database.close();
//                }
//            }else {
//                Toast.makeText(this, "Ingresa un una busqueda", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (Exception exception) {
//            Log.i("TAG", exception.getMessage());
//            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
//        }


    }
}