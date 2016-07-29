package com.bush.android24_wordqureyservice;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bush.android24_wordqureyservice.helper.MySQLiteOpenHelper;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context mContext = this;
    private ListView listView_main;
    private TextView textView_empty;

    private MySQLiteOpenHelper dbHelper = null;
    private SimpleCursorAdapter adapter = null;
    private Cursor cursor = null;
    private SQLiteDatabase dbConn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDB();

        initView();
    }

    private void initDB() {
        dbHelper = new MySQLiteOpenHelper(mContext);
        dbConn = dbHelper.dbConn;
    }

    /**
     *
     */
    private void initView() {
        textView_empty = (TextView) findViewById(R.id.textView_empty);
        listView_main = (ListView) findViewById(R.id.listView_main);

        //加载数据到ListView
        reloadListView();

        listView_main.setEmptyView(textView_empty);

        //给ListView增加上下文菜单
        registerForContextMenu(listView_main);

        //如果实现批量删除，则可以采用以下方式
        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            registerForContextMenu(listView_main);
        } else {
            listView_main.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView_main.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.contextmenu_listview_main, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            for (int i = 0; i < cursor.getCount(); i++) {
                                if (listView_main.isItemChecked(i)) {
                                    long _id = adapter.getItemId(i);
                                    dbConn.delete("tb_words", "_id =?", new String[]{_id+""});
                                }
                            }
                            //执行ListView的刷新
                            reloadListView();

                            //mode.finish();
                            break;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                      boolean checked) {

                }
            });
        }
        */
    }

    /**
     * 重新加载ListView，实现页面刷新
     */
    private void reloadListView() {
        //使用多参数crud方法
        cursor = dbConn.query("tb_words", null, null, null, null, null, "word asc", null);
        adapter = new SimpleCursorAdapter(mContext,
                R.layout.item_listview_main,
                cursor,
                new String[]{"word", "detail"},
                new int[]{R.id.textView_item_word, R.id.textView_item_detail},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView_main.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                //创建自定义对话框，实现数据添加
                View dialog_view = getLayoutInflater().inflate(R.layout.dialog_main, null);
                final EditText editText_dialog_word = (EditText) dialog_view.findViewById(R.id
                        .editText_dialog_word);
                final EditText editText_dialog_detail = (EditText) dialog_view.findViewById(R.id
                        .editText_dialog_detail);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setIcon(R.mipmap.ic_launcher)
                        .setTitle("添加数据")
                        .setView(dialog_view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //执行数据库的添加操作
                                String word = editText_dialog_word.getText() + "";
                                String detail = editText_dialog_detail.getText() + "";
                                ContentValues values = new ContentValues();
                                values.put("word", word);
                                values.put("detail", detail);
                                //执行insert添加新数据
                                long _id = dbConn.insert("tb_words", null, values);
                                Toast.makeText(mContext, "添加完成" + _id, Toast.LENGTH_SHORT).show();

                                //执行ListView的刷新
                                reloadListView();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //      通过adapter的getItem方法，将游标指针指向到position所对应的数据
        Cursor cursor_item = (Cursor) adapter.getItem(info.position);
        //从游标已经指向到相应数据的Cursor中取出希望得到的数据
        String word = cursor_item.getString(cursor_item.getColumnIndex("word"));
        menu.setHeaderIcon(R.mipmap.ic_launcher);
        //给上下文菜单设置title
        menu.setHeaderTitle(word);
        getMenuInflater().inflate(R.menu.contextmenu_listview_main, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) (item
                .getMenuInfo());
        final long id = info.id;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.mipmap.ic_launcher).setNegativeButton("取消", null);

        switch (item.getItemId()) {
            case R.id.action_delete:
                builder.setTitle("提示")
                        .setMessage("确认删除吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int count = dbConn.delete("tb_words", "_id=?", new String[]{id +
                                        ""});
                                Toast.makeText(mContext, "删除完成" + count, Toast.LENGTH_SHORT).show();

                                //刷新listView
                                reloadListView();
                            }
                        })
                        .show();
                break;

            case R.id.action_update:
                View update_view = getLayoutInflater().inflate(R.layout.dialog_main, null);
                final EditText editText_dialog_word = (EditText) update_view.findViewById(R.id
                        .editText_dialog_word);
                final EditText editText_dialog_detail = (EditText) update_view.findViewById(R.id
                        .editText_dialog_detail);

                //执行查询，将查询到的数据添加到两个EditText控件上
                Cursor cursor_item = dbConn.query("tb_words", null, "_id=?", new String[]{id + ""},
                        null, null, null, null);
                cursor_item.moveToFirst();
                String word = cursor_item.getString(cursor_item.getColumnIndex("word"));
                String detail = cursor_item.getString(cursor_item.getColumnIndex("detail"));
                cursor_item.close();

                editText_dialog_word.setText(word);
                editText_dialog_detail.setText(detail);

                builder.setTitle("修改数据")
                        .setView(update_view)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String word = editText_dialog_word.getText() + "";
                                String detail = editText_dialog_detail.getText() + "";
                                ContentValues values = new ContentValues();
                                values.put("word", word);
                                values.put("detail", detail);
                                int count = dbConn.update("tb_words", values, "_id=?", new
                                        String[]{id + ""});
                                Toast.makeText(mContext, "更新完成" + count, Toast.LENGTH_SHORT).show();

                                //刷新ListView
                                reloadListView();
                            }
                        })
                        .show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }
}
