package om.ratemybathroo.ratemybathroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Deepak on 1/3/2015.
 */
public class RatingListAdapter extends ArrayAdapter<ParseObject> {
    public RatingListAdapter(Context context, List<ParseObject> ratings) {
        super(context, R.layout.rating_element, ratings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.rating_element,parent,false);
        ParseObject individualItem = getItem(position);
        TextView username = (TextView) customView.findViewById(R.id.tv_username_list);
        TextView comment = (TextView) customView.findViewById(R.id.tv_comments_details_list);
        RatingBar rb_rating = (RatingBar) customView.findViewById(R.id.rb_rating_details_list);

        //ParseObject object = individualItem.get("user");

        username.setText(individualItem.get("username").toString());
        comment.setText(individualItem.get("comment").toString());
        rb_rating.setRating(Float.parseFloat(individualItem.get("rating").toString()));

        return customView;


    }
}
