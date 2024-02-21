package com.example.lively;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    Button logOut;
    TextView userDetail;
    ImageView menu;
    Toolbar toolbar;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        toolbar = view.findViewById(R.id.profileToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        logOut = view.findViewById(R.id.logOut);
        userDetail = view.findViewById(R.id.userDetail);
        user = auth.getCurrentUser();
        menu = view.findViewById(R.id.profileMenu);
        context = getContext();

        if (user == null) {
            //intent to land on login screen
            Intent intent = new Intent(context, LogInScreen.class);
            startActivity(intent);
        } else {
            userDetail.setText(user.getEmail());
        }

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                //intent to land on login screen
                Intent intent = new Intent(context, LogInScreen.class);
                startActivity(intent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Menu selected", Toast.LENGTH_SHORT).show();
                showDialog();
            }
        });

        return view;
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_lay);

        LinearLayout settingsLayout = dialog.findViewById(R.id.layoutSettings);
        LinearLayout savedLayout = dialog.findViewById(R.id.layoutSaved);
        LinearLayout yourActivityLayout = dialog.findViewById(R.id.layoutYourActivity);
        LinearLayout darkModeLayout = dialog.findViewById(R.id.layoutDarkmode);
        LinearLayout logOutLayout = dialog.findViewById(R.id.layoutLogout);

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Setting Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        savedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Saved Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        yourActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Your activity Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        darkModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Dark mode Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        logOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Log out Clicked", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                //intent to land on login screen
                Intent intent = new Intent(context, LogInScreen.class);
                startActivity(intent);

            }
        });

        dialog.show();
        Window window = dialog.getWindow();

        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogAnimation);
            window.setGravity(Gravity.BOTTOM);
        }

    }
}