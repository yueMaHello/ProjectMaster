package com.example.mayingnan.project301.controller;

import com.example.mayingnan.project301.Bid;
import com.example.mayingnan.project301.Task;
import com.example.mayingnan.project301.User;

import java.util.ArrayList;

/**
 * Created by Xingyuan Yang on 2018-02-25.
 */

public class TaskController {

    public void addTask(Task task){}
    public void deleteTask(Task task){}
    public void requesterUpdateTask(Task task){}
    public void searchTaskByKeyword(String keyword){}
    public void providerSetBid(Task task, Bid bid){}
    public void providerUpdateBid(Task task,Bid bid){}
    public void providerCancelBid(Task task,Bid bid){}


    public ArrayList<Task> searchBiddenTasksOfThisProvider(User provider){
        ArrayList<Task> taskList = new ArrayList<Task> ();
        return taskList;

    }

    public ArrayList<Task> searchAssignTasksOfThisProvider(User provider ){
        ArrayList<Task> taskList = new ArrayList<Task> ();
        return taskList;

    }

    public ArrayList<Task> searchAllTasksOfThisRequester(User Requester){
        ArrayList<Task> taskList = new ArrayList<Task> ();
        return taskList;

    }

    public ArrayList<Task> searchAllRequestingTasks(){
        ArrayList<Task> taskList = new ArrayList<Task> ();
        return taskList;

    }
    public ArrayList<Task> searchTaskByTaskName(String taskname){
        ArrayList<Task> taskList = new ArrayList<Task> ();
        return taskList;

    }


}
