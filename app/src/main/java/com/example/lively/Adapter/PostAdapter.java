package com.example.lively.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.lively.CommentActivity;
import com.example.lively.FollowersActivity;
import com.example.lively.Model.Post;
import com.example.lively.Model.User;
import com.example.lively.PostDetailFragment;
import com.example.lively.ProfileFragment;
import com.example.lively.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        assert post != null;
        Glide.with(mContext).load(post.getPostimage()).placeholder(R.drawable.lemon).into(holder.postImage);

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.profileImage, holder.username, holder.publisher, post.getPublisher());
        if(post.getPostid()!=null){
            isLiked(post.getPostid(), holder.like);
            nrLikes(holder.likes, post.getPostid());
            getComments(post.getPostid(), holder.comments);
            isSaved(post.getPostid(), holder.save);
        }


        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();

            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();

            }
        });
        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProfileFragment()).commit();

            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PostDetailFragment()).commit();

            }
        });

        holder.save.setOnClickListener(v -> {
            if (holder.like.getTag() != null && holder.like.getTag().equals("save")) {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getPostid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getPostid()).removeValue();
            }
        });

        holder.like.setOnClickListener(v -> {
            if (holder.like.getTag() != null   && holder.like.getTag().equals("like")) {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                        .child(firebaseUser.getUid()).setValue(true);
                addNotifications(post.getPublisher(), post.getPostid());
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                        .child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.comment.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra("postid", post.getPostid());
            intent.putExtra("publisherid", post.getPublisher());
            mContext.startActivity(intent);
        });

        holder.comments.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra("postid", post.getPostid());
            intent.putExtra("publisherid", post.getPublisher());
            mContext.startActivity(intent);
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "likes");
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage, postImage, like, comment, save;
        public TextView username, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.imageProfile);
            postImage = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    private void getComments(String postid, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("View all " + snapshot.getChildrenCount() + " comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isLiked(String postid, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null ) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Likes")
                    .child(postid);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(firebaseUser.getUid()).exists()) {
                        imageView.setImageResource(R.drawable.baseline_thumb_up_alt_24);
                        imageView.setTag("liked");
                    } else {
                        imageView.setImageResource(R.drawable.baseline_thumb_up_off_alt_24);
                        imageView.setTag("like");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void addNotifications(String userid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);

    }


    private void nrLikes(final TextView likes, String postid) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + "likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(ImageView imageProfile, TextView username, TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getImageurl()).into(imageProfile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isSaved(final String postid, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    imageView.setImageResource(R.drawable.baseline_bookmark_24);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.round_bookmark_border_24);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
