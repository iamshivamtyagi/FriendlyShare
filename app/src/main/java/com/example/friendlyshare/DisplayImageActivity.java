package com.example.friendlyshare;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayImageActivity extends AppCompatActivity {

    String userId, currentUid, chatOrStory;

    protected ImageView mImage;

    private boolean started = false;

    protected  String imageUrl = "";
    protected long timestampBeg = 0;
    protected long timestampEnd = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        currentUid = FirebaseAuth.getInstance().getUid();

        Bundle b = getIntent().getExtras();
        userId = b.getString("userId");
        chatOrStory = b.getString("chatOrStory");

        mImage = findViewById(R.id.image);

        switch (chatOrStory) {
            case "chat":
                listenForChat();
                break;
            case "story":
                listenForStory();
                break;
        }

    }

    ArrayList<String> imageUrlList = new ArrayList<>();

    private void listenForChat() {
        final DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid).child("received").child(userId);
        chatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = "";
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                    if (chatSnapshot.child("imageUrl").getValue() != null) {
                        imageUrl = chatSnapshot.child("imageUrl").getValue().toString();
                    }
                    imageUrlList.add(imageUrl);
                    if (!started) {
                        started = true;
                        initializeDisplay();
                    }
                    chatDb.child(chatSnapshot.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void listenForStory() {
        DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        followingStoryDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot storySnapshot : dataSnapshot.child("story").getChildren()) {
                    if (storySnapshot.child("timestampBeg").getValue() != null) {
                        timestampBeg = Long.parseLong(storySnapshot.child("timestampBeg").getValue().toString());
                    }
                    if (storySnapshot.child("timestampEnd").getValue() != null) {
                        timestampEnd = Long.parseLong(storySnapshot.child("timestampEnd").getValue().toString());
                    }
                    if (storySnapshot.child("imageUrl").getValue() != null) {
                        imageUrl = storySnapshot.child("imageUrl").getValue().toString();
                    }
                    long timestampCurrent = System.currentTimeMillis();
                    if (timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd) {
                        imageUrlList.add(imageUrl);
                        if (!started) {
                            started = true;
                            initializeDisplay();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private int imageIterator = 0;

    private void initializeDisplay() {

        Glide.with(DisplayImageActivity.this).load(imageUrl).into(mImage);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });
        final Handler handler = new Handler();
        final int delay = 5000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeImage();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void changeImage() {
        if (imageIterator == imageUrlList.size() - 1) {
            finish();
            return;
        }
        imageIterator++;
        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);
    }
}
