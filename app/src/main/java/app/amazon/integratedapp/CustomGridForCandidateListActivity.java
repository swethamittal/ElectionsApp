package app.amazon.integratedapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomGridForCandidateListActivity extends BaseAdapter{
    private Context mContext;
    ArrayList<String> web;
    ArrayList<String> ImageId;
    //static ArrayList<Button> buttonsobj;
    static ArrayList<String> parties;
    static ArrayList<TextView> aadhar;

    public CustomGridForCandidateListActivity(Context c, ArrayList<String> web1, ArrayList<String> Imageid1,ArrayList<String> party) {
        mContext = c;
        web=new ArrayList<>();
        ImageId=new ArrayList<>();
        parties=new ArrayList<>();
        aadhar=new ArrayList<>();
        for(String str:web1)
            web.add(str);
        for(String str:Imageid1)
            ImageId.add(str);
        for(String str:party)
            parties.add(str);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.size();
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
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.activity_single_grid_for_candidate_list, null);

        } else {
            grid = (View) convertView;
        }
        TextView textView = (TextView) grid.findViewById(R.id.text);
        TextView pt=(TextView)grid.findViewById(R.id.party);
        ImageView imageView = (ImageView)grid.findViewById(R.id.image);
        textView.setText(web.get(position));
        pt.setText(parties.get(position));
        Glide.with(mContext)
                .load(ImageId.get(position))
                .into(imageView);
        aadhar.add(textView);
        return grid;
    }

}

