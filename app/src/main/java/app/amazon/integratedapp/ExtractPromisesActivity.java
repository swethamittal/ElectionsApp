package app.amazon.integratedapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ExtractPromisesActivity extends BaseAdapter {
    String promises[];
    Context mContext;
    ExtractPromisesActivity(Context c,String promises[]){
        this.promises=promises;
        mContext=c;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return promises.length;
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
            grid = inflater.inflate(R.layout.activity_extract_promises, null);
            TextView textView = (TextView) grid.findViewById(R.id.promise);
            textView.setText(promises[position]);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
