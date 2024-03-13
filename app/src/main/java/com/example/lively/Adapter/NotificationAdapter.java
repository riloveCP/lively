package com.example.lively.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lively.Model.Notification;
import com.example.lively.Model.Post;
import com.example.lively.Model.User;
import com.example.lively.PostDetailFragment;
import com.example.lively.ProfileFragment;
import com.example.lively.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private final Context mContext;
    private final List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = mNotification.get(position);
        holder.text.setText(notification.getText());
        getUserInfo(holder.imageProfile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostid());


        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                if(notification.isIspost()) {
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                            new PostDetailFragment()).commit();

                } else {
                    editor.putString("postid", notification.getUserid());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                            new ProfileFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile, postImage;
        public TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            postImage = itemView.findViewById(R.id.postImage);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo (final ImageView imageView, final TextView username, String publisherId) {
        if (publisherId != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherId);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    Glide.with(mContext).load(user.getImageurl()).into(imageView);
                    username.setText(user.getUsername());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

    private void getPostImage (ImageView imageView, final String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                assert post != null;
                Glide.with(mContext).load(post.getPostimage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
