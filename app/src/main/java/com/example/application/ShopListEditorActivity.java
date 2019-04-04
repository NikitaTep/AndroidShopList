package com.example.application;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.app.AlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class ShopListEditorActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    @BindView(R.id.selectedShopList) ListView selectedListView;
    @BindView(R.id.nameItemEditText) EditText nameItemEditText;
    @BindView(R.id.amountEditText) EditText amountEditText;

    SimpleCursorAdapter adapter1;
    Cursor shopListCursor1;
    SQLiteDatabase db1;

    DBHelper dbHelper1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_list_editor);
        ButterKnife.bind(this);
        // setSupportActionBar(toolbar);
        dbHelper1 = new DBHelper(this);
        db1 = dbHelper1.getWritableDatabase();
        initListAdapter();
    }

    private void initListAdapter() {
        String title = getIntent().getStringExtra("title");
        shopListCursor1 = db1.query("shoplists", new String[] {"_id", "title", "name", "amount", "unit", "availability"},
                "title = ?", new String[] { title }, null, null, null);
        String[] from = new String[] { "name", "amount", "unit", "availability" };
        int[] to = new int[] { R.id.name, R.id.amount, R.id.unit, R.id.availability};
        adapter1 = new SimpleCursorAdapter(this,
                R.layout.advanced_shoplist_item, shopListCursor1, from, to,
                CursorAdapter.FLAG_AUTO_REQUERY);
        selectedListView.setAdapter(adapter1);
        selectedListView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_list_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onUpButtonClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("myLogs", "=== LongClick ===");
        String itemId = String.valueOf(adapter1.getItemId(position));
        deleteAlert(itemId);
        return true;
    }

    @OnClick(R.id.buttonAdd)
    public void onAddButtonClick(View view) {
        ContentValues cv = new ContentValues();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
       try {
           String nameItem = nameItemEditText.getText().toString();
           Log.d("myLogs", "nameItem = " + nameItem);
           if (!nameItem.equals("")) {
               cv.put("unit", spinner.getSelectedItem().toString());
               cv.put("name", nameItem);
               cv.put("amount", Integer.parseInt(amountEditText.getText().toString()));
               cv.put("availability", "not buyed");
               cv.put("title", getIntent().getStringExtra("title"));
               db1.insert("shoplists", null, cv);
               db1.delete("shoplists", "name=? and title=? ", new String[]{"",getIntent().getStringExtra("title")});
               restartActivity();
           }
           else callAlert();
       }catch (Exception e){
           callAlert();
       }
    }

    @OnItemClick(R.id.selectedShopList)
    public void onListItemClicked(int position) {
        String itemId = String.valueOf(adapter1.getItemId(position));
        if (itemId.equalsIgnoreCase("")) {
            return;
        }

        Cursor cursor = db1.query("shoplists", null,
                "_id = ?", new String[] { itemId }, null, null, null);
        cursor.moveToNext();
        String itemvailability = cursor.getString(cursor.getColumnIndex("availability"));
        ContentValues cv = new ContentValues();
        Log.d("myLogs", "itemvailability = " + itemvailability);

        if (itemvailability.equals("not buyed"))
            cv.put("availability", "buyed");
        else
            cv.put("availability", "not buyed");

        db1.update("shoplists", cv, "_id = ?", new String[] { itemId });
        restartActivity();
    }

    public void callAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopListEditorActivity.this);
        builder.setTitle("Запись не добавлена!")
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

    public void deleteAlert(String itemId){
        final String str = itemId;
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Удалить текущую запись?");  // заголовок
        ad.setMessage(""); // сообщение
        ad.setPositiveButton("да",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("myLogs", "ID delete = " + str);
                        db1.delete("shoplists", "_id = " + str, null);
                        restartActivity();
                    }
                });
        ad.setNegativeButton("нет",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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


    public void onUpButtonClick() {
        setResult(RESULT_CANCELED);
        db1.close();
        finish();
    }
}
