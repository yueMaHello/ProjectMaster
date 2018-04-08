package project301.providerActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import project301.Bid;
import project301.Photo;
import project301.R;
import project301.Task;
import project301.allUserActivity.LogInActivity;
import project301.controller.TaskController;

/**
 * user could change profile, see bid history, as well as look into a requesting task.
 * This is the main page of provider; it shows a list of requesting task;
 * @classname : ProviderMainActivity
 * @Date :   18/03/2018
 * @author : Wang Dong
 * @version 1.0
 * @copyright : copyright (c) 2018 CMPUT301W18T25
 */


public class ProviderMainActivity extends AppCompatActivity {

    private Button searchButton;
    private Button editProfileButton;
    private Button viewOnMapButton;
    private Button bidHistoryButton;
    private Button logoutButton;
    private EditText searchEditText;
    private String searchText;
    private ListView availablelist;
    private ArrayList<Task> taskList = new ArrayList<>();
    private Context context;
    private static JestDroidClient client;

    ProviderAdapter adapter;

    private String userId;
    private String taskId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_main);


        //get final intent
        final Intent intent = getIntent();

        //get context
        this.context = getApplicationContext();
        adapter = new ProviderAdapter(this, taskList);


        //get userId
        userId = intent.getExtras().get("userId").toString();

        //settle viewOnMap button
        viewOnMapButton = findViewById(R.id.provider_map_button);
        viewOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProviderMainActivity.this, ProviderMapActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        //settle logout Button
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProviderMainActivity.this, LogInActivity.class);
                //intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        //settle bidHistory button
        bidHistoryButton = findViewById(R.id.provider_bid_button);
        bidHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProviderMainActivity.this, ProviderBidHistoryActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("content","all");
                startActivity(intent);
            }
        });

        //settle editProfile button
        editProfileButton = findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProviderMainActivity.this, ProviderEditInfoActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });


        // settle click on list
        availablelist = findViewById(R.id.provider_list);
        availablelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long r_id) {
                Intent info1 = new Intent(ProviderMainActivity.this, ProviderTaskBidActivity.class);
                Task tempTask = taskList.get(index);
                taskId = tempTask.getId();
                info1.putExtra("taskId",taskId);
                info1.putExtra("info", index);
                //provide the task status for the next activity
                info1.putExtra("status","request");
                info1.putExtra("userId",userId);

                startActivity(info1);
            }
        });

        // to do search button
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText = findViewById(R.id.search_info);
                searchText = searchEditText.getText().toString();
                searchText = searchText.trim();
                if (searchText.isEmpty()){
                    //print error message
                    Toast toast = Toast.makeText(context, "Search Key Cannot be Empty!", Toast.LENGTH_LONG);
                    TextView v1 = toast.getView().findViewById(android.R.id.message);
                    v1.setTextColor(Color.RED);
                    v1.setTextSize(20);
                    v1.setGravity(Gravity.CENTER);
                    toast.show();
                }else {
                    ArrayList<Task> result = new ArrayList<>(); // search result
                    //need search code

                   /* TaskController.searchTaskByKeyword searchTask = new TaskController.searchTaskByKeyword();
                    searchTask.execute(searchText);



                    try {
                        result = searchTask.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    */

                    TaskController taskController = new TaskController();
                    result = taskController.searchByKeyWord(searchText,userId);
                    taskList.clear();
                    taskList.addAll(result);
                    ProviderAdapter adapter = (ProviderAdapter) availablelist.getAdapter();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        //设置在listview上下拉刷新的监听
        ListView mListView = (ListView) findViewById(R.id.provider_list);
        final SwipeRefreshLayout mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_ly);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //设置2秒的时间来执行以下事件
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        renewTheList();
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        renewTheList();


    }

    public void renewTheList(){

        new searchAllBiddenRequestingTasks().execute();

        // Attach the adapter to a ListView
        /* TEST HERE ROMOVE IS PERFORMANCE IS IMPROVED
        ArrayList<Bid> bidList = new ArrayList<Bid>();
        Photo emptyPhoto = new Photo();
        taskList = new ArrayList<>();
        taskList.add(new Task("Fetch","Fetchcar","Michael",null,"bidding","random address",bidList,emptyPhoto));
        */

        ProviderAdapter adapter = new ProviderAdapter(this, taskList);
        adapter.notifyDataSetChanged();
        this.availablelist.setAdapter(adapter);

    }

    public class searchAllBiddenRequestingTasks extends AsyncTask<Void, Void, ArrayList<Task>> {

        protected ArrayList<Task> doInBackground(Void... nul) {
            verifySettings();

            ArrayList<Task> result_tasks = new ArrayList<Task>();

            String queryS =
                    "\n{ \n"+
                            "\"size\" : 30,\n"+
                            "   \"query\" : {\n"+
                            "       \"bool\" : {\n"+
                            "           \"should\" : [\n"+
                            "               { \"term\" : {\"taskStatus\" : \"request\"}}," + "\n"+
                            "               { \"term\" : {\"taskStatus\" : \"bidden\"}}" + "\n"+
                            "           ]\n"+
                            "       }\n"+
                            "   }\n"+
                            "}\n";
            Log.i("Query", "The query was " + queryS);
            Search search = new Search.Builder(queryS)
                    .addIndex("cmput301w18t25")
                    .addType("task")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Task> foundUsers
                            = result.getSourceAsObjectList(Task.class);
                    result_tasks.addAll(foundUsers);
                    Log.i("Success", "Data retrieved from database: ");
                } else {
                    Log.i("Error", "The search query failed");
                }
                // TODO get the results of the query
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return result_tasks;
        }
        protected void onPostExecute(ArrayList<Task> result_tasks){
            taskList.clear();
            taskList.addAll(result_tasks);
            ProviderAdapter adapter = (ProviderAdapter) availablelist.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }
    /**
     * verify ES database setting
     */
    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://192.30.35.214:8080").discoveryEnabled(true).multiThreaded(true);

            DroidClientConfig config = builder.build();
            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
