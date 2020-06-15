package team.charlie.yetanotherfitnesstracker.ui.community.friends;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.ApiClientActivity;
import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.GetAllFriendsRequest;
import team.charlie.yetanotherfitnesstracker.api.SendFriendRequest;

public class SearchAllFriendsFragment extends Activity implements ApiClientActivity {
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    ArrayList<FriendsItems> friendsItems;
    public static FriendsItems friendSelected;
    //Context context1 = this;
    private static final String TAG = "SearchAllFriendsFragmen";
    SearchAllFriendsAdapter searchAllFriendsAdapter;
    SearchAllFriendsFragment fragment;
    Context newContext;
    String friendEmailId="";
//    public void setFriendRequestEmailId(String emailId){
//        friendRequestEmailId = emailId;
//    }
//    public String getFriendRequestEmailId(){
//        return friendRequestEmailId;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchallfriends);

    }
    @Override
    public void onResume() {
        super.onResume();
        new GetFriendsAsyncTask(this, new WeakReference<>(this)).execute();

    }
    @Override
    public void onSendFriendRequestSuccess(String message){
        Log.d(TAG, "onSendFriendRequestSuccess: "+message);
    }

    @Override
    public void onGetAllFriendsSuccess(Map<String,String> userInfo) {
        friendsItems = new ArrayList<>();

        //Log.d(TAG, "onGetAllFriendsSuccess: " + name.toString() + email.toString());
        for(String name:userInfo.keySet()){
            friendsItems.add(new FriendsItems(name,userInfo.get(name)));
        }
        friendsItems.sort(FriendsItems.compareName);
        searchView = findViewById(R.id.allFriendsSearchView);
        Log.d("Kinshuk:Inside SearchAllFriendsFragment", "Adapter count: " );
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                searchAllFriendsAdapter.getFilter().filter(text);
                return false;
            }
        });
        RecyclerView recyclerView =(RecyclerView)findViewById(R.id.allFriendsRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        searchAllFriendsAdapter= new SearchAllFriendsAdapter(friendsItems);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(searchAllFriendsAdapter);
        newContext =this;
        fragment = this;
        searchAllFriendsAdapter.setOnClickSendFriendRequest(new SearchAllFriendsAdapter.onFriendRequestSend(){
            @Override
            public void onClickAddFriend(String emailId) {
                Log.d(TAG, "onClickAddFriend: "+emailId);
                friendEmailId = emailId;
                new SendFriendRequestAsyncTask(fragment,new WeakReference<>(newContext),emailId).execute();
            }
        });
        Log.d(TAG, "onGetAllFriendsSuccess: "+friendEmailId);
        //new SendFriendRequestAsyncTask(this,new WeakReference<>(this),friendSelected).execute();

        //Log.d("Kinshuk", "Adapter count: " + searchAllFriendsAdapter.getItemCount());
    }

    public static void setFriendSelected(FriendsItems item){
        friendSelected = item;
    }

    private static class GetFriendsAsyncTask extends AsyncTask<Integer, Void, Void> {

        SearchAllFriendsFragment searchAllFriendsFragment;
        WeakReference<Context> context;
        WeakReference<View> view;

        public GetFriendsAsyncTask(SearchAllFriendsFragment searchAllFriendsFragment, WeakReference<Context> context) {
            this.searchAllFriendsFragment = searchAllFriendsFragment;
            this.context = context;
            //this.view = view;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            GetAllFriendsRequest getAllFriendsRequest= new GetAllFriendsRequest(searchAllFriendsFragment,context.get());
            getAllFriendsRequest.getAllFriends();
            return null;

        }
    }

    private static class SendFriendRequestAsyncTask extends AsyncTask<Integer,Void,Void> {
        SearchAllFriendsFragment searchAllFriendsFragment;
        String emailId;
        WeakReference<Context> context;
        WeakReference<View> view;

        SendFriendRequestAsyncTask(SearchAllFriendsFragment searchAllFriendsFragment,WeakReference<Context> context, String item){
            this.searchAllFriendsFragment = searchAllFriendsFragment;
            this.context = context;
            this.emailId = item;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            SendFriendRequest sendFriendRequest = new SendFriendRequest(searchAllFriendsFragment,context.get());
            Log.d(TAG, "doInBackground: "+emailId);
            sendFriendRequest.sendingFriendRequest(emailId);
            return null;
        }
    }
}
