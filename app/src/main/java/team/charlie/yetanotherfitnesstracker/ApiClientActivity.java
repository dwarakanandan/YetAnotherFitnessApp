package team.charlie.yetanotherfitnesstracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import team.charlie.yetanotherfitnesstracker.database.entities.FitnessActivity;
import team.charlie.yetanotherfitnesstracker.database.entities.LocationWithStepCount;
import team.charlie.yetanotherfitnesstracker.ui.AchievementUnlocked;
import team.charlie.yetanotherfitnesstracker.ui.community.contests.ContestItem;
import team.charlie.yetanotherfitnesstracker.ui.community.friends.FriendsItems;

public interface ApiClientActivity {

    default void onRegisterSuccess() {

    }

    default void onRegisterFailure(String message) {

    }

    default void onLoginSuccess() {

    }

    default void onLoginFailure() {

    }

    default void onLogoutSuccess() {

    }

    default void onLogoutFailure() {

    }

    default void onUserProfileSuccess(String response) {

    }

    default void onUserProfileFailure() {

    }

    default void onActivityInsertSuccess(int fitnessActivityId, String remoteBackupId) {

    }

    default void onActivityInsertFailure() {

    }

    default void onGetAllFriendsSuccess(Map<String,String> map){

    }

    default void onGetAllFriendsFailure(){

    }

    default void onSendFriendRequestSuccess(String jsonResponse){

    }

    default void onSendFriendRequestFailure(){

    }

    default void onGetCurrentFriendsSuccess(List<FriendsItems> friendsItemsList){

    }

    default void onGetCurrentFriendsFailure(){

    }

    default void onGetActivitiesByTimeSuccess(ArrayList<FitnessActivity> fitnessActivities, ArrayList<String> remoteIds, ArrayList<LocationWithStepCount> locationWithStepCounts) {

    }

    default void onGetActivitiesByTimeFailure() {

    }

    default void onConfirmFriendRequestSuccess(String emailId){

    }

    default  void onConfirmFriendRequestFailure(){

    }

    default void onUpdateUserProfileSuccess() {

    }

    default void onUpdateUserProfileFailure() {

    }

    default void onAddWeightSuccess(int weightEntryId, String remoteId) {

    }

    default void onAddWeightFailure() {

    }

    default void onUpdateWeightSuccess(int weightEntryId, String remoteId) {

    }

    default void onUpdateWeightFailure(int weightEntryId, String remoteId) {

    }

    default void onActivityUpdateSuccess(int fitnessActivityId, String remoteBackupId) {

    }

    default void onActivityUpdateFailure() {

    }

    default void onUpdateUserLocationSuccess() {

    }

    default void onUpdateUserLocationFailure() {

    }

    default void onGetContestsSuccess(ArrayList<ContestItem> contestItems) {

    }

    default void onGetContestsFailure() {

    }

    default void onCreateContestSuccess() {

    }

    default void onCreateContestFailure() {

    }

    default void onContestJoinSuccess() {

    }

    default void onContestJoinFailure() {

    }

    default void onRejectFriendSuccess(String emailId){

    }

    default void onRejectFriendFailure(){

    }

    default void onGetAchievementsSuccess(List<AchievementUnlocked> response){

    }

    default void onGetAchievementsFailure(){

    }

    default void onGetFriendsAchievementSuccess(List<AchievementUnlocked> response){

    }

    default void onGetFriendsAchievementsFailure(){

    }
}
