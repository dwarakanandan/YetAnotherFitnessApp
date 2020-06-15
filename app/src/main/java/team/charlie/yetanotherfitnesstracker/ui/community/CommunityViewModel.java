package team.charlie.yetanotherfitnesstracker.ui.community;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import team.charlie.yetanotherfitnesstracker.R;

public class CommunityViewModel extends ViewModel {

    private MutableLiveData<String> mText;


    public CommunityViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}