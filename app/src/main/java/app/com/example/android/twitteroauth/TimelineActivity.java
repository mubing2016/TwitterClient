package app.com.example.android.twitteroauth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import butterknife.BindView;
import timber.log.Timber;

public class TimelineActivity extends AppCompatActivity {
    @BindView(R.id.avatar)
    ImageView avatarView;

    public int statusLength = 19;
    public String[] timelineArray = new String[statusLength * 2];
    public String[] textArr = new String[statusLength];
    public String[] imgArr = new String[statusLength];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            timelineArray = b.getStringArray("statuses");
        }
        if (timelineArray != null && timelineArray.length != 0) {
            System.arraycopy(timelineArray, 0, textArr, 0, statusLength);
            System.arraycopy(timelineArray, statusLength, imgArr, 0, statusLength);
        }

        Timber.v("TIMELINEACTIVITY URL: " + imgArr[3]);

        ListView listView = (ListView)findViewById(R.id.timelineList);
        if (listView != null) {
            listView.setAdapter(new ListViewAdapter(this, textArr, imgArr));
        }
        else {
            Timber.e("listview is null");
        }
    }


}
