package com.example.lively;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lively.Adapter.MyPhotoAdapter;
import com.example.lively.Model.Post;
import com.example.lively.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {
    ImageView imageProfile, profileMenu;
    TextView posts, followers, following, fullname, bio, username;
    Button editProfile;
    private List<String> mySaves;
    RecyclerView recyclerViewSaves;
    MyPhotoAdapter myPhotoAdapterSaves;
    List<Post> postListSaves;
    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;
    FirebaseUser firebaseUser;
    String profileId;
    ImageButton myPhotos, savedPhotos;
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = requireContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid","none");

        imageProfile = view.findViewById(R.id.imageProfile);
        posts = view.findViewById(R.id.posts);
        following = view.findViewById(R.id.following);
        followers = view.findViewById(R.id.followers);
        fullname = view.findViewById(R.id.fullName);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        editProfile = view.findViewById(R.id.editProfile);
        myPhotos = view.findViewById(R.id.myPhotos);
        savedPhotos = view.findViewById(R.id.savedPhotos);
        profileMenu = view.findViewById(R.id.profileMenu);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myPhotoAdapter);

        recyclerViewSaves = view.findViewById(R.id.recyclerViewSaved);
        recyclerViewSaves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerSaves = new GridLayoutManager(getContext(), 3);
        recyclerViewSaves.setLayoutManager(linearLayoutManagerSaves);
        postListSaves = new ArrayList<>();
        myPhotoAdapterSaves = new MyPhotoAdapter(getContext(), postListSaves);
        recyclerViewSaves.setAdapter(myPhotoAdapterSaves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getNrPosts();
        myPhotos();
        mysaves();

        if (profileId.equals(firebaseUser.getUid())) {
            editProfile.setText("Edit Profile");
        } else {
            checkFollow();
            savedPhotos.setVisibility(View.GONE);
        }

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = editProfile.getText().toString();
                
                if (btn.equals("Edit Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (btn.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications();
                } else if (btn.equals("following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        myPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        savedPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followers");
                startActivity(intent);

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "following");
                startActivity(intent);

            }
        });

        return view;
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout settingsLayout = dialog.findViewById(R.id.layoutSettings);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout logoutLayout = dialog.findViewById(R.id.layoutLogout);

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Settings selected", Toast.LENGTH_SHORT).show();
            }
        });
        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LogInScreen.class));
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);

    }

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = snapshot.getValue(User.class);

                if (user != null) {
                    Glide.with(getContext()).load(user.getImageurl()).into(imageProfile);
                    username.setText(user.getUsername());
                    fullname.setText(user.getFullname());
                    bio.setText(user.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    editProfile.setText("following");
                } else {
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNrPosts () {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        i++;
                    }
                }

                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(profileId)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mysaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mySaves.add(snapshot.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postListSaves.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postListSaves.add(post);
                        }
                    }
                }
                myPhotoAdapterSaves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}