package app.amazon.integratedapp;

import android.content.Context;
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

import java.util.ArrayList;

public class CustomGridActivity extends BaseAdapter{
    private Context mContext;
    ArrayList<String> web;
    ArrayList<Integer> ImageId;
    static ArrayList<Button> buttonsobj;
    static ArrayList<ProgressBar> progressBarsobj;

    public CustomGridActivity(Context c, ArrayList<String> web1, ArrayList<Integer> Imageid1) {
        mContext = c;
        web=new ArrayList<>();
        ImageId=new ArrayList<>();
        buttonsobj=new ArrayList<>();
        progressBarsobj=new ArrayList<>();
        for(String str:web1)
            web.add(str);
        for(Integer str:Imageid1)
            ImageId.add(str);
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
            grid = inflater.inflate(R.layout.activity_single_grid, null);

        } else {
            grid = (View) convertView;
        }
        TextView textView = (TextView) grid.findViewById(R.id.text);
        ImageView imageView = (ImageView)grid.findViewById(R.id.image);
        textView.setText(web.get(position));
        imageView.setImageResource(ImageId.get(position));
        Button button = (Button) grid.findViewById(R.id.button);
        ProgressBar progressBar=grid.findViewById(R.id.determinateBar);
        buttonsobj.add(button);
        progressBarsobj.add(progressBar);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    ((GridView) parent).performItemClick(v, position, 0);
                    return true;
                }
                else
                    return false;
            }
        });
        return grid;
    }

}

