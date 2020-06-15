package team.charlie.yetanotherfitnesstracker.ui.community.friends;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import team.charlie.yetanotherfitnesstracker.R;
import team.charlie.yetanotherfitnesstracker.api.ConfirmFriendRequest;

public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private FriendsItems[] listFriendItems;
    private static final String TAG = "FriendsAdapter";
    private List<FriendsItems> friendsItemList;
    private List<FriendsItems> fullFriendsItemList;
    private int FRIENDACCEPTED = 0;
    private int FRIENDREQUEST = 1;
    private int FRIENDPENDING = 2;
    onConfirmFriendRequest confirmFriendRequest;
    onRejectFriendRequest rejectFriendRequest;
    FriendsItemClickListener friendsItemClickListener;

    public FriendsAdapter(List<FriendsItems> friendsItemList,FriendsItemClickListener friendsItemClickListener) {
        this.friendsItemList = friendsItemList;
        this.fullFriendsItemList = new ArrayList<>(friendsItemList);
        this.friendsItemClickListener = friendsItemClickListener;
    }

    public void setOnConfirmFriendRequest(onConfirmFriendRequest confirmFriendRequest){
        this.confirmFriendRequest = confirmFriendRequest;
    }

    public interface  onConfirmFriendRequest{
        void onClickConfirmFriendRequest(String emailId);
    }

    public interface onRejectFriendRequest{
        void onClickRejectFriendRequest(String emailId);
    }

    public void setOnRejectFriendRequest(onRejectFriendRequest rejectFriendRequest){
        this.rejectFriendRequest = rejectFriendRequest;
    }
    public static class CurrentFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textView;
        public TextView textImageView;
        FriendsItemClickListener activity;
        public CurrentFriendViewHolder(@NonNull View itemView,FriendsItemClickListener friendsItemClickListener) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.textViewFriends);
            this.textImageView = itemView.findViewById(R.id.imageFriend);
            this.activity = friendsItemClickListener;
            Log.d(TAG, "FriendClickedConstructor: ");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "FriendClicked: ");
            this.activity.onClick(getAdapterPosition());
        }
    }

    public static class PendingFriendViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private TextView textImageView;
        public PendingFriendViewHolder(@NonNull View itemView) {

            super(itemView);

            this.textView = itemView.findViewById(R.id.textViewReqFriends);
            this.textImageView = itemView.findViewById(R.id.imageReqFriend);


        }
    }

    public static class ConfirmFriendsViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewFriendName;
        public TextView textFriendImage;
        public TextView textEmailId;
        public ImageButton acceptFriendButton;
        public ImageButton declineFriendButton;
        public ConfirmFriendsViewHolder(@NonNull View itemView) {

            super(itemView);
            this.declineFriendButton = itemView.findViewById(R.id.friendCancelButton);
            this.acceptFriendButton = itemView.findViewById(R.id.imageConfirmFriendButton);
            this.textEmailId = itemView.findViewById(R.id.emailIdConfirmFriend);
            this.textViewFriendName = itemView.findViewById(R.id.confirmFriendName);
            this.textFriendImage = itemView.findViewById(R.id.confirmFriendImage);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == FRIENDACCEPTED) {
            View listItem = layoutInflater.inflate(R.layout.friends_fragment_list_item,parent,false);
            CurrentFriendViewHolder viewHolder = new CurrentFriendViewHolder(listItem,this.friendsItemClickListener);

            return viewHolder;

        }
        else if(viewType == FRIENDREQUEST){
            View listItem = layoutInflater.inflate(R.layout.friends_fragment_confirmfriendrequest_list_item,parent,false);
            ConfirmFriendsViewHolder viewHolder = new ConfirmFriendsViewHolder(listItem);

            return viewHolder;
        }
        else{
            View listItem = layoutInflater.inflate(R.layout.fragment_friends_sent_request_list_item,parent,false);
            PendingFriendViewHolder viewHolder = new PendingFriendViewHolder(listItem);

            return viewHolder;
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FriendsItems friendsItem = friendsItemList.get(position);
        if(friendsItem.getFriendsRequestStatus().equals("ACC")){
            ((CurrentFriendViewHolder)holder).textView.setText(friendsItem.getmFriendName());
            ((CurrentFriendViewHolder)holder).textImageView.setText(friendsItem.getmFriendName().substring(0,1));
            ((CurrentFriendViewHolder)holder).setIsRecyclable(false);
        }
        else if(friendsItem.getFriendsRequestStatus().equals("REQ")){
            ((ConfirmFriendsViewHolder)holder).textViewFriendName.setText(friendsItem.getmFriendName());
            ((ConfirmFriendsViewHolder)holder).textFriendImage.setText(friendsItem.getmFriendName().substring(0,1));
            ((ConfirmFriendsViewHolder)holder).textEmailId.setText(friendsItem.getmFriendEmail());
            ((ConfirmFriendsViewHolder)holder).acceptFriendButton.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            confirmFriendRequest.onClickConfirmFriendRequest(friendsItem.getmFriendEmail());
                        }
                    }
            );
            ((ConfirmFriendsViewHolder)holder).declineFriendButton.setOnClickListener(
                    new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            rejectFriendRequest.onClickRejectFriendRequest(friendsItem.getmFriendEmail());
                        }
                    }
            );

            ((ConfirmFriendsViewHolder)holder).setIsRecyclable(false);
        }
        else{
            ((PendingFriendViewHolder)holder).textView.setText(friendsItem.getmFriendName());
            ((PendingFriendViewHolder)holder).textImageView.setText(friendsItem.getmFriendName().substring(0,1));
        }
    }

    @Override
    public int getItemViewType(int position) {
        FriendsItems item = friendsItemList.get(position);
        if(item.getFriendsRequestStatus().toString().equals("ACC")){
            return FRIENDACCEPTED;
        }
        else if(item.getFriendsRequestStatus().toString().equals("REQ")){
            return FRIENDREQUEST;
        }
        else{
            return FRIENDPENDING;
        }
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


}
