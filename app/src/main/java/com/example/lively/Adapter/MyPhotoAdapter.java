package com.example.lively.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lively.Model.Post;
import com.example.lively.PostDetailFragment;
import com.example.lively.R;

import java.util.List;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder>{

    private Context context;
    private List<Post> mPosts;

    public MyPhotoAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photos_item, parent, false);

        return new MyPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);

        Glide.with(context).load(post.getPostImage()).into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PEEFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostId());

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PostDetailFragment()).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImage);
        }
    }
}
