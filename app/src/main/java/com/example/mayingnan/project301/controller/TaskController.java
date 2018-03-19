package com.example.mayingnan.project301.controller;

import com.example.mayingnan.project301.Bid;
import com.example.mayingnan.project301.OnAsyncTaskCompleted;
import com.example.mayingnan.project301.Task;
import com.example.mayingnan.project301.User;
import com.example.mayingnan.project301.utilities.TaskUtil;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * @classname : TaskController
 * @class Detail :
 *
 * @Date :   18/03/2018
 * @author : Yuqi Zhang
 * @author : Yue Ma
 * @version 1.0
 * @copyright : copyright (c) 2018 CMPUT301W18T25
 */

public class TaskController {

    private Task current_task;
    private static JestDroidClient client;

    public static class deleteAllTasks extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... search_parameters) {
            verifySettings();
            ArrayList<Task> tasks = new ArrayList<Task>();

            String query = "{ \"size\": 500 }" ;
            Log.i("Query", "The query was " + query);
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w18t25")
                    .addType("task")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Task> foundTasks
                            = result.getSourceAsObjectList(Task.class);
                    tasks.addAll(foundTasks);
                } else {
                    Log.i("Error", "The search query failed");
                }
                // TODO get the results of the query
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            for (Task task:tasks){
                Delete delete = new Delete.Builder(task.getId()).index("cmput301w18t25").type("task").build();

                try {
                    client.execute(delete);

                } catch (Exception e) {
                    Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
                }

            }
            return null;

        }
    }

    public static class addTask extends AsyncTask<Task, Void, Void> {
        public OnAsyncTaskCompleted listener;
        @Override

        protected Void doInBackground(Task... a_task) {
            verifySettings();

            a_task[0].setTaskStatus("request");
            Index index = new Index.Builder(a_task[0]).index("cmput301w18t25").type("task").build();

            try {
                // where is the client?
                DocumentResult result = client.execute(index);
                //Log.i("TEST client result", result.getId());
                if(result.isSucceeded())
                {
                    a_task[0].setId(result.getId());

                    String query = TaskUtil.serializer(a_task[0]);

                    Index index2 = new Index.Builder(query)
                            .index("cmput301w18t25").type("task").id(a_task[0].getId()).build();
                    try {
                        DocumentResult result2 = client.execute(index2);
                        if (result.isSucceeded()) {
                            Log.i("Debug", "Successful update user profile");
                        } else {
                            Log.i("Error", "We failed to update user profile to elastic search!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("Error", "We failed to connect Elasticsearch server");
                    }
                    Log.i("Success","Elasticsearch ");

                }
                else
                {
                    Log.i("Error","Elasticsearch was not able to add the task");
                }
            }
            catch (Exception e) {
                Log.i("Error", "The application failed to build and send the tasks");
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {

                Log.i("Debug", ",");

        }
    }

    public static class getTaskById extends AsyncTask<String, Void, Task> {

        private String id;

        @Override
        protected Task doInBackground(String... arg_id) {
            verifySettings();

            Task task = new Task();

            String query = "{ \n"+
                    "\"query\":{\n"+
                    "\"term\":{\"_id\":\""+arg_id[0]+"\"}\n"+

                    "}\n"+"}";

            Log.i("Query", "The query was " + query);
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w18t25")
                    .addType("task")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    task = result.getSourceAsObject(Task.class);
                    Log.i("Success",task.getId());
                } else {
                    Log.i("Error", "The task search query failed");
                }
                // TODO get the results of the query
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            return task;
        }
    }

    public static class deleteTaskById extends AsyncTask<String, Void, Void>{

        private String id;

        public deleteTaskById(String arg_id){
            this.id = arg_id;
        }

        @Override
        protected Void doInBackground(String... idToDelete) {
            verifySettings();

            String query = "{ \n"+
                    "\"query\":{\n"+
                    "\"term\":{\"_id\":\""+idToDelete[0]+"\"}\n"+

                    "}\n"+"}";

            Log.i("Query", "The query was " + query);

            Delete delete = new Delete.Builder(idToDelete[0]).index("cmput301w18t25").type("task").build();
            try {
                DocumentResult result = client.execute(delete);
                if (result.isSucceeded()) {
                    Log.i("Success", "Successful delete");
                } else {
                    Log.i("Error", "Elastic search was not able to deletes.");
                }
            } catch (Exception e) {
                Log.i("Error", "We failed to add a request to elastic search!");
                e.printStackTrace();
            }

            return null;
        }
    }

    public static class requesterUpdateTask extends AsyncTask<Task, Void, Void>{

        @Override
        protected Void doInBackground(Task... single_task) {

            verifySettings();

            String query = TaskUtil.serializer(single_task[0]);

            Index index = new Index.Builder(query)
                    .index("cmput301w18t25").type("task").id(single_task[0].getId()).build();
            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    Log.i("Debug", "Successful update user profile");
                } else {
                    Log.i("Error", "We failed to update user profile to elastic search!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "We failed to connect Elasticsearch server");
            }
            return null;
        }
    }

    public static class providerSetBid extends AsyncTask<Void, Void, Void>{
        //new MyTask(int foo, long bar, double arple).execute();

        Task current_task;
        Bid current_bid;

        public providerSetBid(Task current_task, Bid current_bid){
            this.current_task = current_task;
            this.current_bid = current_bid;
            this.current_task.addBid(this.current_bid);
        }

        @Override
        protected Void doInBackground(Void... nul) {

            verifySettings();

            String query = TaskUtil.serializer(this.current_task);

            this.current_task.setTaskStatus("bidden");
            Index index = new Index.Builder(query)
                    .index("cmput301w18t25").type("task").id(this.current_task.getId()).build();
            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    Log.i("Success", "Successful update the bid");
                } else {
                    Log.i("Error", "We failed to update new bid to elastic search!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "We failed to connect Elasticsearch server");
            }

            String query2 = "{ \n"+
                    "\"query\":{\n"+
                    "\"term\":{\"userId\":\""+this.current_bid.getProviderId()+"\"}\n"+
                    "}\n"+"}";

            Index index2 = new Index.Builder(query2)
                    .index("cmput301w18t25").type("userst").id(this.current_bid.getProviderId()).build();

            //UserListController.getAUserById getAUserById = new UserListController.getAUserById();
            //getAUserById.execute(this.current_bid.getProviderId());
            Log.i("Report", this.current_bid.getProviderId());
            try {
                DocumentResult result = client.execute(index2);
                if (result.isSucceeded()) {
                    User foundUser = result.getSourceAsObject(User.class);

                    foundUser.addProviderBiddenTask(this.current_task.getId());

                    try {
                        DocumentResult result2 = client.execute(index2);
                        if (result2.isSucceeded()) {
                            User foundUser2 = result.getSourceAsObject(User.class);
                            foundUser2.addProviderBiddenTask(current_task.getId());

                            // update user here
                            UserListController.updateUser updateUser = new UserListController.updateUser();
                            updateUser.execute(foundUser2);

                            Log.i("Success", "Successful cancel provider's bid");
                        } else {
                            Log.i("Error", "We failed to cancel provider's bid to elastic search!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("Error", "We failed to connect Elasticsearch server");
                    }

                    Log.i("Success", "Successful cancel provider's bid");
                } else {
                    Log.i("Error", "We failed to cancel provider's bid to elastic search!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Error", "We failed to connect Elasticsearch server");
            }
            return null;
        }
    }

    public static class providerCancelBid extends AsyncTask<Void, Void, Boolean>{

        Task current_task;
        String providerId;
        Boolean rt_val;

        public providerCancelBid(Task current_task, String providerId){
            this.current_task = current_task;
            this.providerId = providerId;
            this.rt_val = this.current_task.cancelBid(this.providerId);
        }

        @Override
        protected Boolean doInBackground(Void... nul) {
            if (this.rt_val){
                verifySettings();

                String query = TaskUtil.serializer(this.current_task);

                Index index = new Index.Builder(query)
                        .index("cmput301w18t25").type("task").id(this.current_task.getId()).build();
                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        Log.i("Success", "Successful cancel provider's bid");
                    } else {
                        Log.i("Error", "We failed to cancel provider's bid to elastic search!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("Error", "We failed to connect Elasticsearch server");
                }
                return true;
            }
            else {
                return false;
            }

        }
    }

    //TODO test it

    public static class providerGetBiddenTasks extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... providerID) {
            ArrayList<String> rtTasks = new ArrayList<>();
            User foundUser;

            verifySettings();

            String query = "{ \n"+
                    "\"query\":{\n"+
                    "\"term\":{\"userId\":\""+providerID[0]+"\"}\n"+
                    "}\n"+"}";

            Index index = new Index.Builder(query)
                    .index("cmput301w18t25").type("userst").id(providerID[0]).build();
            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    foundUser = result.getSourceAsObject(User.class);
                    rtTasks = foundUser.getProviderBiddenTask();
                    Log.i("Success", "Successful cancel provider's bid");
                } else {
                    rtTasks = null;
                    Log.i("Error", "We failed to cancel provider's bid to elastic search!");
                }
            } catch (IOException e) {
                rtTasks = null;
                e.printStackTrace();
                Log.i("Error", "We failed to connect Elasticsearch server");
            }
            return rtTasks;
        }
    }

    // TODO change provider variable to requester variable

    public static class requesterGetBiddenTasks extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... providerID) {
            ArrayList<String> rtTasks = new ArrayList<>();
            User foundUser;

            verifySettings();

            String query = "{ \n"+
                    "\"query\":{\n"+
                    "\"term\":{\"userId\":\""+providerID[0]+"\"}\n"+
                    "}\n"+"}";

            Index index = new Index.Builder(query)
                    .index("cmput301w18t25").type("userst").id(providerID[0]).build();
            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    foundUser = result.getSourceAsObject(User.class);
                    rtTasks = foundUser.getProviderBiddenTask();
                    Log.i("Success", "Successful cancel provider's bid");
                } else {
                    rtTasks = null;
                    Log.i("Error", "We failed to cancel provider's bid to elastic search!");
                }
            } catch (IOException e) {
                rtTasks = null;
                e.printStackTrace();
                Log.i("Error", "We failed to connect Elasticsearch server");
            }
            return rtTasks;
        }
    }

    public static class searchBiddenTasksOfThisProvider extends AsyncTask<Void, Void, ArrayList<Task>>{
        //ArrayList<Task> taskList = new ArrayList<Task> ();
        String providerId;

        public searchBiddenTasksOfThisProvider(String providerId){
            this.providerId = providerId;
        }

        protected ArrayList<Task> doInBackground(Void... nul) {
            verifySettings();

            ArrayList<Task> result_tasks = new ArrayList<Task>();

            String query =
                    "\n{ \n"+
                    "   \"query\" : {\n"+
                    "       \"bool\" : {\n"+
                    "           \"must\" : [\n"+
                    "               { \"term\" : {\"taskStatus\" : " + "\"bidden\"}}," + "\n"+
                    "               { \"term\" : {\"taskProvider\" : " + "\"tester\"}}" + "\n"+
                    "           ]\n"+
                    "       }\n"+
                    "   }\n"+
                    "}\n";

            Log.i("Query", "The query was " + query );
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w18t25")
                    .addType("task")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Task> rt
                            = result.getSourceAsObjectList(Task.class);
                    result_tasks.addAll(rt);
                    Log.i("Success", "Data retrieved from database: " + Integer.toString(rt.size()));
                } else {
                    Log.i("Error", "The search query failed");
                }
                // TODO get the results of the query
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return result_tasks;
        }

    }

    //TODO do test for this method, which should be extremely similar to bidden tasks
    public static class searchAssignTasksOfThisProvider extends AsyncTask<String, Void, ArrayList<Task>>{

        protected ArrayList<Task> doInBackground(String... providerId) {
            verifySettings();

            ArrayList<Task> result_tasks = new ArrayList<Task>();

            String query =
                    "\n{ \n"+
                            "   \"query\" : {\n"+
                            "       \"bool\" : {\n"+
                            "           \"must\" : [\n"+
                            "               { \"term\" : {\"taskStatus\" : " + "\"assigned\"}}," + "\n"+
                            "               { \"term\" : {\"taskProvider\" : \"" + providerId[0] + "\"}}" + "\n"+
                            "           ]\n"+
                            "       }\n"+
                            "   }\n"+
                            "}\n";

            Log.i("Query", "The query was " + query);
            Search search = new Search.Builder(query)
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

    }

    //TODO do test for this method, which should be extremely similar to bidden tasks
    public static class searchAllTasksOfThisRequester extends AsyncTask<String, Void, ArrayList<Task>>{

        protected ArrayList<Task> doInBackground(String... requesterId) {
            verifySettings();

            ArrayList<Task> result_tasks = new ArrayList<Task>();

            String query =
                    "\n{ \n"+
                            "   \"query\" : {\n"+
                            "       \"bool\" : {\n"+
                            "           \"must\" : [\n"+
                            "               { \"term\" : {\"taskRequester\" : \"" + requesterId[0] + "\"}}" + "\n"+
                            "           ]\n"+
                            "       }\n"+
                            "   }\n"+
                            "}\n";

            Log.i("Query", "The query was " + query);
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w18t25")
                    .addType("task")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Task> foundResults
                            = result.getSourceAsObjectList(Task.class);
                    result_tasks.addAll(foundResults);
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


    }

    //TODO do test for this method, which should be extremely similar to bidden tasks
    public static class searchAllRequestingTasks extends AsyncTask<Void, Void, ArrayList<Task>>{

        protected ArrayList<Task> doInBackground(Void... nul) {
            verifySettings();

            ArrayList<Task> result_tasks = new ArrayList<Task>();

            String query =
                    "\n{ \n"+
                            "   \"query\" : {\n"+
                            "       \"bool\" : {\n"+
                            "           \"must\" : [\n"+
                            "               { \"term\" : {\"taskStatus\" : \"request\"}}" + "\n"+
                            "           ]\n"+
                            "       }\n"+
                            "   }\n"+
                            "}\n";

            Log.i("Query", "The query was " + query);
            Search search = new Search.Builder(query)
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

    }

    public static class searchTaskByKeyword extends AsyncTask<String, Void, ArrayList<Task>>  {
        protected ArrayList<Task> doInBackground(String... keywords) {
            verifySettings();

            String [] search_parameters = keywords[0].split("\\s+");
            ArrayList<Task> result_tasks = new ArrayList<Task>();
            /*
            String query =
                    "\n{     \n"+
                            "   \"query\" : {\n"+
                            "       \"bool\" : {\n"+
                            "           \"must\" : [\n"+
                            "               { \"multi_match\" : {\n" +
                            "                   \"query\" : \""+ search_parameters[0] +"\", \n" +
                            "                   \"fields : [ \"taskName\", \"taskDetails\" ] \n " +
                            "                   }  \n" +
                            "               }\n" +
                            "           ]\n"+
                            "       }\n"+
                            "   }\n"+
                            "}\n";
            */
            String query =
                    "\n{ \n"+
                            "\"size\" : 10,\n"+
                            "   \"query\" : {\n"+
                            "       \"bool\" : {\n"+
                            "           \"must\" : [\n"+
                            "               { \"multi_match\" : {\"query\" : \"" +keywords[0] +"\", \"fields\" : [ \"taskName\", \"taskDetails\"] }}" + "\n"+
                            "           ]\n"+
                            "       }\n"+
                            "   }\n"+
                            "}\n";
            String pre_query =
                    "\n{     \n"+
                    "   \"query\" : {\n"+
                    "       \"bool\" : {\n"+
                    "           \"must\" : [\n"+
                    "               { \"multi_match\" : {\n" +
                    "                   \"query\" : \""+ search_parameters[0] +"\", \n" +
                    "                   \"fields : [ \"taskName\", \"taskDetails\" ]}  \n" +
                    "               }";

            String body_query = "";
            for (int i = 1; i < search_parameters.length; i++){
                body_query +=
                        "               , \n" +
                        "               { \"multi_match\" : {\n" +
                        "                   \"query\" : \""+ search_parameters[i] +"\", \n" +
                        "                   \"fields : [ \"taskName\", \"taskDetails\" ]}  \n" +
                        "               }";
            }
            String post_query =
                    "           ]\n"+
                    "       }\n"+
                    "   }\n"+
                    "}\n";

            String final_query = pre_query + body_query + post_query;
            Log.i("Query", "The query was " + query);
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w18t25")
                    .addType("user")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<Task> foundUsers
                            = result.getSourceAsObjectList(Task.class);
                    result_tasks.addAll(foundUsers);
                } else {
                    Log.i("Error", "The search query failed");
                }
                // TODO get the results of the query
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return result_tasks;
        }
    }

    public boolean testTrue(String name){
        return true;
    } //created by wdong2 for testing

    public boolean testFalse(String name){
        return false;
    } //created by wdong2 for testing

    // no need to use it, providerSetBis is good enough
    public void providerUpdateBid(Task task,Bid bid){}

    public static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

    public static class yuqi8delete extends AsyncTask<String, Void, Void>{

        private String id;

        public yuqi8delete(String arg_id){
            this.id = arg_id;
        }

        @Override
        protected Void doInBackground(String... idToDelete) {
            verifySettings();

            String query = "{ \n"+
                    "\"size\" : 1000" +

                    "}\n"+"}";

            Log.i("Query", "The query was " + query);

            Delete delete = new Delete.Builder(idToDelete[0]).index("cmput301w18t25").type("task").build();
            try {
                DocumentResult result = client.execute(delete);
                if (result.isSucceeded()) {
                    Log.i("Success", "Successful delete");
                } else {
                    Log.i("Error", "Elastic search was not able to deletes.");
                }
            } catch (Exception e) {
                Log.i("Error", "We failed to add a request to elastic search!");
                e.printStackTrace();
            }

            return null;
        }
    }

}
