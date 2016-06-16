package app.com.example.android.twitteroauth;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import timber.log.Timber;

public class ListViewAdapter extends ArrayAdapter<String> {

    private Activity context;
    private String[] tweetText;
    private String[] imageUrl;

    public ListViewAdapter(Activity context, String[] tweetText, String[] imageUrl) {
        super(context, R.layout.activity_listview, tweetText);
        this.context = context;
        this.tweetText = tweetText;
        this.imageUrl = imageUrl;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view==null) {
            Timber.v("VIEW IS EMPTY");
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.activity_listview, parent, false);
            ListViewHolder listViewHolder = new ListViewHolder(view);
            view.setTag(listViewHolder);
        }

        ListViewHolder viewHolder = (ListViewHolder) view.getTag();
        viewHolder.txtTitle.setText(tweetText[position]);
        loadAvatar(imageUrl[position], viewHolder.avatarView);

        return view;
    }

    public class ListViewHolder{
//        @BindView(R.id.avatar)
        ImageView avatarView;
        TextView txtTitle;

        public ListViewHolder(View rowView) {
//            ButterKnife.bind(this,rowView)
            txtTitle = (TextView) rowView.findViewById(R.id.list_item) ;
            avatarView= (ImageView) rowView.findViewById(R.id.avatar);

        }

    }

    private void loadAvatar(String avatarUrl, ImageView avatarView) {
        int avatarSide  = getContext().getResources().getDimensionPixelSize(R.dimen.avatar_side);
            Picasso.with(getContext())
                    .load(avatarUrl)
                    .resize(avatarSide, avatarSide)
                    .centerCrop()
                    .into(avatarView);
            avatarView.setVisibility(View.VISIBLE);
    }

}
