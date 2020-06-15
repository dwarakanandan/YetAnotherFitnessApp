package team.charlie.yetanotherfitnesstracker.ui.community.friends;

import java.util.Comparator;

public class FriendsItems {
    private String mFriendName;
    private String mFriendEmail;
    private String friendsRequestStatus;

    public FriendsItems(String mFriendName) {
        this.mFriendName = mFriendName;
    }

    public String getmFriendEmail() {
        return mFriendEmail;
    }

    public FriendsItems(String mFriendName, String mFriendEmail) {
        this.mFriendName = mFriendName;
        this.mFriendEmail = mFriendEmail;
    }

    public FriendsItems(String mFriendName, String mFriendEmail,String friendsRequestStatus) {
        this.mFriendName = mFriendName;
        this.mFriendEmail = mFriendEmail;
        this.friendsRequestStatus = friendsRequestStatus;
    }

    public String getFriendsRequestStatus() {
        return friendsRequestStatus;
    }

    public void setmFriendEmail(String mFriendEmail) {
        this.mFriendEmail = mFriendEmail;
    }

    public String getmFriendName() {
        return mFriendName;
    }

    public void setmFriendName(String mFriendName) {
        this.mFriendName = mFriendName;
    }

    public void setFriendsRequestStatus(String friendsRequestStatus) {
        this.friendsRequestStatus = friendsRequestStatus;
    }

    public static Comparator<FriendsItems> compareName = new Comparator<FriendsItems>() {
        @Override
        public int compare(FriendsItems o1, FriendsItems o2) {
            return o1.getmFriendName().compareTo(o2.getmFriendName());
        }
    };

}
