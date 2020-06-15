package team.charlie.yetanotherfitnesstracker.ui.community.friends;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.ConfirmFriendRequest;
import team.charlie.yetanotherfitnesstracker.api.GetCurrentFriendsRequest;
import team.charlie.yetanotherfitnesstracker.api.GetFriendsAchievementRequest;
import team.charlie.yetanotherfitnesstracker.api.RejectFriendRequest;
import team.charlie.yetanotherfitnesstracker.ui.AchievementUnlocked;

public class FriendsFragment extends Fragment implements ApiClientActivity,FriendsItemClickListener {

    private static final String TAG = "FriendsFragment";
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private FriendsAdapter adapter;
    public static ArrayList<FriendsItems> friendsItemsArrayList = new ArrayList<>();
    ArrayAdapter<FriendsItems> arrayAdapter;
    String confirmEmailID="";
    String rejectEmailID = "";
    Context newContext;
    FriendsFragment fragment;

    View root;
    View parentView;
    ListView listView;
    List<FriendsItems> friendsItemsList = new ArrayList<FriendsItems>();
    SearchAllFriendsFragment searchAllfriendFragment;

    public  FriendsFragment(View parentView){
        this.parentView  = parentView;

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        if(searchItem!=null)
        {
            searchView = (SearchView)searchItem.getActionView();
        }
        if(searchView!=null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit",query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i("onQueryTextChange",newText);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
       // final = (SearchView) MenuItemCompat.getActionView(menuItem);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                return false;
                default:
                    break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
    public void setValues(){
        FriendsItems[] friendsItems = new FriendsItems[]{
                new FriendsItems("Rahul","rahul@gmail.com","ACC"),
                new FriendsItems("Rahil","rahil@gmail.com","REQ"),
                new FriendsItems("Jain","jain@gmail.com","ACC"),
                new FriendsItems("James","james@gmail.com","ACC"),
                new FriendsItems("Holmes","holmes@gmail.com","ACC"),
                new FriendsItems("John","john@gmail.com","ACC"),
                new FriendsItems("DOw","dow@gmail.com","REQ"),
                new FriendsItems("DOe","doe@gmail.com","REQ"),
                new FriendsItems("Ashwin","ashwin@gmail.com","REQ"),
                new FriendsItems("Himansh","himansh@gmail.com","ACC"),
                new FriendsItems("Holmes","holmes@gmail.com","ACC"),
                new FriendsItems("John","john@gmail.com","ACC"),
                new FriendsItems("DOw","dow@gmail.com","REQ"),
                new FriendsItems("DOe","doe@gmail.com","REQ"),
                new FriendsItems("Himansh","himansh@gmail.com","REQ"),

        };

        for(FriendsItems friends :friendsItems){
            friendsItemsArrayList.add(friends);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
        root = inflator.inflate(R.layout.fragment_friends,container,false);
        Log.d("Kinshuk:Friend Fragment","Inflate OnCreateview");

        FloatingActionButton floatingActionButton = root.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAllfriendFragment = new SearchAllFriendsFragment();

               Intent intent = new Intent(getActivity(),SearchAllFriendsFragment.class);
               startActivity(intent);
                // searchAllfriendFragment.onCreateView(inflator,container,savedInstanceState);
               }
        });
        searchView = root.findViewById(R.id.friendsSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                adapter.getFilter().filter(text);
                return false;
            }
        });
        //setHasOptionsMenu(true);

        //setValues();
        return  root;

    }

    @Override
    public void onGetFriendsAchievementSuccess(List<AchievementUnlocked> response) {


        Log.d(TAG, "onGetFriendsAchievementSuccess: "+response);
    }

    public List<FriendsItems> sortingValues(List<FriendsItems> friendsItemList){
        List<FriendsItems>  currentFriendItemList = new ArrayList<>();
        List<FriendsItems> confirmFriendItemList = new ArrayList<>();
        List<FriendsItems> pendingFriendItemList = new ArrayList<>();

        for(FriendsItems friendsItems:friendsItemList){
            if(friendsItems.getFriendsRequestStatus().equals("ACC")){
                currentFriendItemList.add(friendsItems);
            }
            else if(friendsItems.getFriendsRequestStatus().equals("REQ")){
                confirmFriendItemList.add(friendsItems);
            }
            else {
                pendingFriendItemList.add(friendsItems);
            }
        }
        currentFriendItemList.sort(FriendsItems.compareName);
        confirmFriendItemList.sort(FriendsItems.compareName);

        currentFriendItemList.addAll(confirmFriendItemList);
        if(pendingFriendItemList.size()!=0)
            currentFriendItemList.addAll(pendingFriendItemList);
        return currentFriendItemList;

    }
    @Override
    public void onResume() {
        super.onResume();
        new GetCurrentFriendsAsyncTask(this,this.getContext()).execute();

    }

    @Override
    public void onGetCurrentFriendsSuccess(List<FriendsItems> friends) {
        this.friendsItemsList = sortingValues(friends);
        RecyclerView recyclerView =(RecyclerView)root.findViewById(R.id.friendsRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this.getContext());
        adapter= new FriendsAdapter(this.friendsItemsList,this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        fragment = this;
        newContext = this.getContext();
        //onClick(0);
        adapter.setOnConfirmFriendRequest(new FriendsAdapter.onConfirmFriendRequest(){

            @Override
            public void onClickConfirmFriendRequest(String emailId) {
                confirmEmailID = emailId;
                Log.d(TAG, "onClickConfirmFriendRequest: "+emailId);
                new ConfirmFriendRequestAsyncTask(fragment,newContext,confirmEmailID).execute();

            }
        });
        adapter.setOnRejectFriendRequest(new FriendsAdapter.onRejectFriendRequest() {
            @Override
            public void onClickRejectFriendRequest(String emailId) {
                rejectEmailID = emailId;
                new RejectingFriendRequestAsyncTask(fragment,newContext,rejectEmailID).execute();
                Log.d(TAG, "onClickRejectFriendRequest: "+emailId);
            }
        });
        Log.d(TAG, "onGetCurrentFriendsSuccess: "+friends);

    }

    @Override
    public void onRejectFriendSuccess(String emailId) {
        new GetCurrentFriendsAsyncTask(this,this.getContext()
        ).execute();
        Log.d(TAG, "onRejectFriendRequestSuccess: "+emailId);
    }

    @Override
    public void onConfirmFriendRequestSuccess(String emailId) {
        new GetCurrentFriendsAsyncTask(this,this.getContext()
        ).execute();
        Log.d(TAG, "onConfirmFriendRequestSuccess: "+emailId);
    }

    @Override
    public void onClick(int position) {
        Log.d(TAG, "onClick: "+this.friendsItemsList.get(position).getmFriendEmail());
        FriendsAchievementActivity friendsAchievementActivity = new FriendsAchievementActivity();
        Intent intent = new Intent(getActivity(),FriendsAchievementActivity.class);
        intent.putExtra("email",this.friendsItemsList.get(position).getmFriendEmail());
        intent.putExtra("name",this.friendsItemsList.get(position).getmFriendName());
        startActivity(intent);

    }

    public static class ConfirmFriendRequestAsyncTask extends AsyncTask<Integer,Void,Void>{
        FriendsFragment friendsFragment;
        Context context;
        String emailId;

        ConfirmFriendRequestAsyncTask(FriendsFragment friendsFragment,Context context,String emailId){
            this.friendsFragment = friendsFragment;
            this.context = context;
            this.emailId = emailId;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            ConfirmFriendRequest confirmFriendRequest = new ConfirmFriendRequest(friendsFragment,context);
            confirmFriendRequest.confirmingFriendRequest(emailId);
            return null;
        }
    }

    public static class GetCurrentFriendsAsyncTask extends AsyncTask<Integer,Void,Void>{
        FriendsFragment friendsFragment;
        Context context;

        GetCurrentFriendsAsyncTask(FriendsFragment friendsFragment, Context context){
            this.friendsFragment = friendsFragment;
            this.context = context;
        }
        @Override
        protected Void doInBackground(Integer... integers) {
            GetCurrentFriendsRequest getCurrentFriendsRequest = new GetCurrentFriendsRequest(friendsFragment,context);
            getCurrentFriendsRequest.getCurrentFriends();
            return null;
        }
    }

    public static class RejectingFriendRequestAsyncTask extends  AsyncTask<Integer,Void,Void>{
        FriendsFragment friendsFragment;
        Context context;
        String emailId;

        RejectingFriendRequestAsyncTask(FriendsFragment friendsFragment,Context context,String email){
            this.friendsFragment = friendsFragment;
            this.context = context;
            this.emailId = email;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            RejectFriendRequest rejectFriendRequest = new RejectFriendRequest(friendsFragment ,context);
            rejectFriendRequest.rejectingFriendRequest(emailId);
            return null;
        }
    }


}
