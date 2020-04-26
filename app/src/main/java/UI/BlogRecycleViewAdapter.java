package UI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.blog.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import Model.Blog;

public class BlogRecycleViewAdapter extends RecyclerView.Adapter<BlogRecycleViewAdapter.ViewHolder> {
    List<Blog> blogList;
    Context context;

    public BlogRecycleViewAdapter(List<Blog> blogList, Context ctx) {
        this.blogList = blogList;
        this.context = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row,parent,false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull BlogRecycleViewAdapter.ViewHolder holder, int position) {
        Blog blog = blogList.get(position);

        holder.postTitle.setText(blog.getTitle());
        holder.postDesc.setText(blog.getDesc());
        String imageUrl = blog.getImage();
        Log.d("imageUrl",imageUrl );

        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.mipmap.add_image)
                .into(holder.postImage);
        holder.userId = blog.getUserId();

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.parseLong(blog.getDate())));
        holder.postDate.setText(formattedDate);



    }


    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView postTitle;
        public TextView postDesc;
        public TextView postDate;
        public ImageView postImage;
        public String userId;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            postTitle= (TextView) itemView.findViewById(R.id.postTitleList);
            postDesc = (TextView) itemView.findViewById(R.id.postTextList);
            postDate = (TextView) itemView.findViewById(R.id.timeStampList);
            postImage = (ImageView) itemView.findViewById(R.id.postImageListID);
            userId = null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to next activity
                }
            });

        }
    }
}
