package com.frsarker.todotask;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends AppCompatActivity {

    DBHelper mydb;
    LinearLayout empty;
    NestedScrollView scrollView;
    LinearLayout todayContainer, tomorrowContainer, otherContainer;
    NoScrollListView taskListToday, taskListTomorrow, taskListUpcoming;
    ArrayList<HashMap<String, String>> todayList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tomorrowList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> upcomingList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);
        empty = findViewById(R.id.empty);
        scrollView = findViewById(R.id.scrollView);
        todayContainer = findViewById(R.id.todayContainer);
        tomorrowContainer = findViewById(R.id.tomorrowContainer);
        otherContainer = findViewById(R.id.otherContainer);
        taskListToday = findViewById(R.id.taskListToday);
        taskListTomorrow = findViewById(R.id.taskListTomorrow);
        taskListUpcoming = findViewById(R.id.taskListUpcoming);
        TextView emptyTxt = findViewById(R.id.emptyTxt);
        float radius = emptyTxt.getTextSize() / 5;
        BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL);
        emptyTxt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        emptyTxt.getPaint().setMaskFilter(filter);
//        empty.setDrawingCacheEnabled(true);
//        empty.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        empty.layout(0,0, empty.getMeasuredWidth(), empty.getMeasuredHeight());
//        empty.buildDrawingCache(true);
//        Bitmap b = Bitmap.createBitmap(empty.getDrawingCache());
//        Blurry.with(this).radius(25).sampling(2).onto(empty);
//        ImageView img = findViewById(R.id.imageView3);


    }

    public void openAddModifyTask(View view) {
        startActivity(new Intent(this, AddModifyTask.class));
    }


    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }


    public void populateData() {
        mydb = new DBHelper(this);

        runOnUiThread(new Runnable() {
            public void run() {
                fetchDataFromDB();
            }
        });
    }


    public void fetchDataFromDB() {
        todayList.clear();
        tomorrowList.clear();
        upcomingList.clear();

        Cursor today = mydb.getTodayTask();
        Cursor tomorrow = mydb.getTomorrowTask();
        Cursor upcoming = mydb.getUpcomingTask();

        loadDataList(today, todayList);
        loadDataList(tomorrow, tomorrowList);
        loadDataList(upcoming, upcomingList);


        if (todayList.isEmpty() && tomorrowList.isEmpty() && upcomingList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            if (todayList.isEmpty()) {
                todayContainer.setVisibility(View.GONE);
            } else {
                todayContainer.setVisibility(View.VISIBLE);
                loadListView(taskListToday, todayList);
            }

            if (tomorrowList.isEmpty()) {
                tomorrowContainer.setVisibility(View.GONE);
            } else {
                tomorrowContainer.setVisibility(View.VISIBLE);
                loadListView(taskListTomorrow, tomorrowList);
            }

            if (upcomingList.isEmpty()) {
                otherContainer.setVisibility(View.GONE);
            } else {
                otherContainer.setVisibility(View.VISIBLE);
                loadListView(taskListUpcoming, upcomingList);
            }
        }
    }


    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> mapToday = new HashMap<String, String>();
                mapToday.put("id", cursor.getString(0).toString());
                mapToday.put("task", cursor.getString(1).toString());
                mapToday.put("date", cursor.getString(2).toString());
                mapToday.put("status", cursor.getString(3).toString());
                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }

    public void loadListView(NoScrollListView listView, final ArrayList<HashMap<String, String>> dataList) {
        ListTaskAdapter adapter = new ListTaskAdapter(this, dataList, mydb);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(MainActivity.this, AddModifyTask.class);
                i.putExtra("isModify", true);
                i.putExtra("id", dataList.get(+position).get("id"));
                startActivity(i);
            }
        });
    }
}
