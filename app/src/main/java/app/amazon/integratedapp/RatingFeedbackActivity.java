package app.amazon.integratedapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class RatingFeedbackActivity extends BaseAdapter {
    Context mContext;
    ArrayList<String> rating;
    ArrayList<String> feedback;
    ArrayList<String> consti;
    RatingFeedbackActivity(Context c,ArrayList<String> rating,ArrayList<String> feedback,ArrayList<String> consti){
        Log.d("constructor","called");
        this.rating=rating;
        this.feedback=feedback;
        this.consti=consti;
        this.mContext=c;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return rating.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        Log.d("inside","get view");
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.activity_rating_feedback, null);
            TextView fb = (TextView) grid.findViewById(R.id.feedbacks);
            TextView constituency=(TextView)grid.findViewById(R.id.consti);
            RatingBar rb=(RatingBar)grid.findViewById(R.id.ratingScore);
            Log.d("rf class",feedback.get(position)+" "+consti.get(position)+" "+rating.get(position));
            if(feedback.get(position).equals("")) {
                fb.setHeight(0);
                fb.setWidth(0);
            }
            else{
                fb.setText(feedback.get(position));
            }
            constituency.setText(consti.get(position));
            rb.setRating(Float.valueOf(rating.get(position)));
//            rb.setScaleX(0.5f);
//            rb.setScaleY(0.5f);
            rb.setEnabled(false);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }

    public void clearData(){
        rating.clear();
        feedback.clear();
        consti.clear();
        notifyDataSetChanged();
    }

}

