package app.com.example.android.twitteroauth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import timber.log.Timber;

public class TimelineActivity extends AppCompatActivity {
    @BindView(R.id.avatar)
    ImageView avatarView;

    public int statusLength;

    //get array of objects combined with textArr and imgArr

    public ArrayList<String> textArr = new ArrayList<>();
    public ArrayList<String> imgArr = new ArrayList<>();

//    //type in starter, tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        TwitterBody twitterObj = (TwitterBody)getIntent().getSerializableExtra(getString(R.string.passStatus));
        statusLength = twitterObj.getStatusesLenght();
        textArr = twitterObj.getTexts();
        imgArr = twitterObj.getImgUrl();

        ListView listView = (ListView)findViewById(R.id.timelineList);
        if (listView != null) {
            listView.setAdapter(new ListViewAdapter(this, textArr, imgArr));
        }
        else {
            Timber.e("listview is null");
        }
    }


}
