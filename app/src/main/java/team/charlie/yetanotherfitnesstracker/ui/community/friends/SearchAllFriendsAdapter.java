package team.charlie.yetanotherfitnesstracker.ui.community.friends;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.R;

public class SearchAllFriendsAdapter extends RecyclerView.Adapter<SearchAllFriendsAdapter.ViewHolder> implements Filterable {

    private List<FriendsItems> friendsItemList;
    private List<FriendsItems> fullFriendsItemList;
    private static List<String> friendRequestsSentTo;
    private static final String TAG = "SearchAllFriendsAdapter";
    onFriendRequestSend onClickSendFriendRequest;

    public void setOnClickSendFriendRequest(onFriendRequestSend onClickSendFriendRequest) {
        this.onClickSendFriendRequest = onClickSendFriendRequest;
    }

    public SearchAllFriendsAdapter(List<FriendsItems> friendsItemList) {
        this.friendsItemList = friendsItemList;
        this.fullFriendsItemList = new ArrayList<>(friendsItemList);
        this.friendRequestsSentTo = new ArrayList<>();
    }

    public interface onFriendRequestSend{
        void onClickAddFriend(String emailId);
    }

    @NonNull
    @Override
    public SearchAllFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.friends_activity_searchallfriends_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAllFriendsAdapter.ViewHolder holder, int position) {
        FriendsItems friendsItem = friendsItemList.get(position);
        Log.d("KINSHUK", "onBindViewHolder: " + friendsItem.getmFriendName());
        holder.textView.setText(friendsItem.getmFriendName());
        holder.textImageView.setText(friendsItem.getmFriendName().substring(0,1));
        holder.textEmailId.setText(friendsItem.getmFriendEmail());
        holder.addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addFriendButton.setImageResource(R.drawable.ic_done);
                //friendRequestsSentTo.add(textEmailId.getText().toString());
                //onAddFriendClickListen.onClickingAddFriendButoon(textEmailId.getText().toString());
                onClickSendFriendRequest.onClickAddFriend(holder.textEmailId.getText().toString());

                //onadd.onClickingAddFriendButoon(textEmailId.getText().toString());
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = "Friend Request Sent to : "+holder.textEmailId.getText();

                Toast toast = Toast.makeText(v.getContext(), text, duration);
                toast.show();
                Log.d(TAG, "onClick: Setting "+holder.textEmailId.getText()+  friendRequestsSentTo.size());
            }
        });
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return friendsItemList.size();
    }

    @Override
    public Filter getFilter() {
        return friendsFilter;
    }
    private Filter friendsFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FriendsItems> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredList.addAll(fullFriendsItemList);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(FriendsItems friends:fullFriendsItemList){
                    if(friends.getmFriendName().toLowerCase().contains(filterPattern)){
                        filteredList.add(friends);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values= filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friendsItemList.clear();
            friendsItemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView textImageView;
        public TextView textEmailId;
        public ImageButton addFriendButton;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            this.addFriendButton = itemView.findViewById(R.id.imageAddFriendButton);
            this.textEmailId = itemView.findViewById(R.id.emailIdFriend);
            this.textView = itemView.findViewById(R.id.textViewAllFriends);
            this.textImageView = itemView.findViewById(R.id.allFriendImage);
        }
    }

}
