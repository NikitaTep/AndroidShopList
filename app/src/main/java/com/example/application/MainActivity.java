package com.example.application;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends Activity implements AdapterView.OnItemLongClickListener {

   // @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.shopList) ListView shopListView;
    @BindView(R.id.titleEditText) EditText titleEditText;
    SimpleCursorAdapter adapter;
    Cursor shopListCursor;
    SQLiteDatabase db;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        initListAdapter();
    }

    private void initListAdapter() {
        shopListCursor = db.query(true,"shoplists", new String[] {"_id", "title", "name", "amount", "unit", "availability"}, null, null, "title", null, null, null);
        String[] from = new String[] { "title" };
        int[] to = new int[] { R.id.nameText};
        adapter = new SimpleCursorAdapter(this,
                R.layout.shoplist_item, shopListCursor, from, to,
                CursorAdapter.FLAG_AUTO_REQUERY);
        shopListView.setAdapter(adapter);
        shopListView.setOnItemLongClickListener(this);
    }

    @OnItemClick(R.id.shopList)
    public void onListItemClicked(int position) {
        db = dbHelper.getWritableDatabase();
        String itemId = String.valueOf(adapter.getItemId(position));
        Cursor cursor = db.query("shoplists", null,
                "_id = ?", new String[] { itemId }, null, null, null);
        cursor.moveToNext();
        String itemTitle = cursor.getString(cursor.getColumnIndex("title"));
        Log.d("myLogs", "cursor.getColumnIndex = " +  itemTitle);
        Intent intent = new Intent(this, ShopListEditorActivity.class);
        cursor.close();
        intent.putExtra("title", itemTitle);
        db.close();
        startActivityForResult(intent, 1);

    }

    @OnClick(R.id.buttonCreateList)
    public void onCreateListButtonClick(View view) {
        ContentValues cv = new ContentValues();
        try {
            String itemTitle = titleEditText.getText().toString();
            Log.d("myLogs", "=== itemTitle ===" + itemTitle);
            if (!itemTitle.equals("")) {
                cv.put("unit", "");
                cv.put("name", "");
                cv.put("amount", "");
                cv.put("availability", "");
                cv.put("title",itemTitle);
                db.insert("shoplists", null, cv);
                restartActivity();
            }
            else callAlert();
        }catch (Exception e){
            Log.d("myLogs", "=== Exception ===");
            callAlert();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        db = dbHelper.getWritableDatabase();
        Log.d("myLogs", "=== LongClick ===");
        String itemId = String.valueOf(adapter.getItemId(position));
        Cursor cursor = db.query("shoplists", null,
                "_id = ?", new String[] { itemId }, null, null, null);
        cursor.moveToNext();
        String itemTitle = cursor.getString(cursor.getColumnIndex("title"));
        deleteListAlert(itemTitle);
        return true;
    }

    public void callAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Список не создан!")
                .setMessage("введены некорретные данные")
                .setNegativeButton("Назад",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void deleteListAlert(String itemTitle){
        db = dbHelper.getWritableDatabase();
        final String str = itemTitle;
        Log.d("myLogs", " itemTitle " + str);

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Выберете операцию со списком");  // заголовок
        ad.setMessage(""); // сообщение
        ad.setPositiveButton("удалить",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            db.delete("shoplists", "title = ?" , new String[]{str});
                            db.close();
                            restartActivity();
                        }
                    });
        ad.setNegativeButton("отмена",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        ad.setNeutralButton("копировать",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues cv = new ContentValues();
                        Cursor cursor = db.query("shoplists", null,
                                "title = ?", new String[] { str }, null, null, null);
                        cursor.moveToFirst();
                        while (cursor.isAfterLast() == false){
                            cv.put("title", "Copy " + str);
                            cv.put("name", cursor.getString(cursor.getColumnIndex("name")));
                            cv.put("amount", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("amount"))));
                            cv.put("unit", cursor.getString(cursor.getColumnIndex("unit")));
                            cv.put("availability", cursor.getString(cursor.getColumnIndex("availability")));
                            Log.d("myLogs", "=== ContentValues ===" + cv);
                            db.insert("shoplists", null, cv);
                            cursor.moveToNext();
                        }
                        restartActivity();
                    }
                });
        AlertDialog alert = ad.create();
        alert.show();
    }

    public void restartActivity() {
        super.onRestart();
        Intent i = getIntent();
        finish();
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) restartActivity();
            return;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
