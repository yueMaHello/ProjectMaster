package project301.providerActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import project301.R;
import project301.Task;
import java.util.ArrayList;

/**
 * This class is an adapter for the assigned task arrayList to show on the UI
 * @classname : ProviderBiddenAdapter
 * @Date :   18/03/2018
 * @author : Wang Dong
 * @version 1.0
 * @copyright : copyright (c) 2018 CMPUT301W18T25
 */


@SuppressWarnings({"ALL", "ConstantConditions"})
public class ProviderAssignedAdapter extends ArrayAdapter<Task> {
    private String userId;

    public ProviderAssignedAdapter(Context context, ArrayList<Task> users) {
        super(context, 0, users);
    }


    public void setId(String id){
        userId = id;
    }

    @SuppressWarnings("ConstantConditions")
    @Override

    /**
     get the view of the list information;
     load some info into each task in the task list
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_assigned, parent, false);
        }

        // Lookup view for data population
        TextView task_name = convertView.findViewById(R.id.adapter_name);
        TextView task_requester = convertView.findViewById(R.id.adapter_requester);
        TextView task_acceptBid = convertView.findViewById(R.id.adapter_acceptBid);
        TextView task_status = convertView.findViewById(R.id.adapter_status);

        // Return the completed view to render on screen
        //noinspection ConstantConditions
        //get taskName
        String taskName = task.getTaskName().toString();

        //get task requester
        String taskRequester;
        if (task.getTaskRequester()==null){
            taskRequester = "";
        }else{
            taskRequester = task.getTaskRequester().toString();
        }

        //get task accept bid
        String acceptBid;
        if (task.getChoosenBid().getBidAmount()==null){
            acceptBid = "";
        }else{
            acceptBid = Double.toString(task.findLowestbid());
        }

        //get taskStatus
        String taskStatus;
        if (task.getTaskStatus()==null){
            taskStatus = "";
        }else{
            taskStatus = task.getTaskStatus();
        }

        //set task info
        task_name.setText(taskName);
        task_requester.setText(taskRequester);
        task_acceptBid.setText(acceptBid);
        task_status.setText(taskStatus);

        //Log.i("a",task.getTaskAddress().toString());
        return convertView;
    }
}