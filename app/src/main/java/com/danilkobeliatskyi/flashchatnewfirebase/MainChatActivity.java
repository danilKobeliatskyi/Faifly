package com.danilkobeliatskyi.flashchatnewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainChatActivity extends AppCompatActivity {

    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRefernce;
    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setupDisplayName();
        database = FirebaseDatabase.getInstance();
        mDatabaseRefernce = database.getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setupDisplayName(){
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);

        if (mDisplayName == null) mDisplayName = "Anonymous";
    }


    private void sendMessage() {
        Log.d("FlashChat", "I send something");
        String input = mInputText.getText().toString();
        if (!input.equals("")){
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            mDatabaseRefernce.child("messages").push().setValue(chat);
            mInputText.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new ChatListAdapter(this, mDatabaseRefernce, mDisplayName);
        mChatListView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.cleanup();
    }
}
