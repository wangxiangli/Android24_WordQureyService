package com.bush.android24_wordqureyservice.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.bush.android24_wordqureyservice.helper.MySQLiteOpenHelper;
import com.bush.wordaidl.IWordAidlInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 嘉华盛世 on 2016-07-28.
 */
public class WordService extends Service {
    private MySQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper=new MySQLiteOpenHelper(this);
        database=dbHelper.getReadableDatabase();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }
    class MyBind extends IWordAidlInterface.Stub{

        @Override
        public String getValue(String word) throws RemoteException {
            String sql="select * from tb_words where word like ?";
            Cursor cursor=database.rawQuery(sql,new String[]{"%" + word + "%"});
            List<Map<String ,String >> list=getListFromCursor(cursor);
//            List<Map<String ,String>> list = dbHelper.selectList(sql, new String[]{word + "%"});
            return list.toString();
        }
        public List<Map<String ,String >> getListFromCursor(Cursor cursor){
            List<Map<String ,String >> list=new ArrayList<>();
            while (cursor.moveToNext()){
                Map<String ,String > map=new HashMap<>();
                for (int i=0;i<cursor.getColumnCount();i++){
                    map.put(cursor.getColumnName(i),cursor.getString(i));
                }
                list.add(map);
            }
            return list;
        }
    }
}
