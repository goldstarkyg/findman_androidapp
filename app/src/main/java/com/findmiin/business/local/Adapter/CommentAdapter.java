    package com.findmiin.business.local.Adapter;

    import android.annotation.TargetApi;
    import android.content.Context;
    import android.graphics.Color;
    import android.graphics.PorterDuff;
    import android.graphics.drawable.LayerDrawable;
    import android.os.Build;
    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.LinearLayout;
    import android.widget.RatingBar;
    import android.widget.TextView;

    import com.findmiin.business.local.R;
    import com.findmiin.business.local.manager.DataStructure.CommentData;

    import java.util.ArrayList;

    /**
 * Created by JonIC on 2017-06-20.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<CommentData> contactItemArrayList;
    private Context context;

    public CommentAdapter(Context context, ArrayList<CommentData> contactItemArrayList1) {
        this.contactItemArrayList = contactItemArrayList1;
        this.context = context;
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.ViewHolder viewHolder, final int i) {

        String name = contactItemArrayList.get(i).name;
        double rate2 = Math.ceil((Double.parseDouble(contactItemArrayList.get(i).rate) )/ 2);
        int rate = (int)rate2;
        String content = contactItemArrayList.get(i).content;
        viewHolder.username.setText(name);
        viewHolder.ratingBar.setProgress(rate);
        viewHolder.comment.setText(content);

    }

    @Override
    public int getItemCount() {
        return contactItemArrayList.size();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_container;
        RatingBar ratingBar;
        TextView username;
        TextView comment;

        public ViewHolder(View view) {
            super(view);
            item_container= (LinearLayout) view.findViewById(R.id.item_container);
            ratingBar = (RatingBar)view.findViewById(R.id.ratingbar);
//            android:theme="@style/RatingBar"
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.argb(255, 255,188,33) , PorterDuff.Mode.SRC_ATOP);
            username = (TextView) view.findViewById(R.id.txtname);
            comment = (TextView) view.findViewById(R.id.txtcomment);
        }
    }

}