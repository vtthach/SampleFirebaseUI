/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firebase.uidemo.database;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.uidemo.R;
import com.firebase.uidemo.util.SignInResultNotifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, View.OnClickListener {
    private static final String TAG = "RecyclerViewDemo";

    private FirebaseAuth mAuth;
    protected DatabaseReference mChatRef;
    private Button mSendButton;
    protected EditText mMessageEdit;

    private RecyclerView mMessages;
    private LinearLayoutManager mManager;
    protected FirebaseRecyclerAdapter<FireBaseMessage, ChatHolder> mAdapter;
    protected TextView mEmptyListMessage;
    protected TextView tvError;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String s) {
            Log.i("vtt", "onChildAdded: " + s);
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String s) {
            Log.i("vtt", "onChildChanged: " + s);
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            Log.i("vtt", "onChildChanged - getChildrenCount: " + snapshot.getChildrenCount());

        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String s) {
            Log.i("vtt", "onChildMoved: " + s);

        }

        @Override
        public void onCancelled(DatabaseError error) {
            Log.i("vtt", "onCancelled: " + error.getMessage());

        }
    };
    private String threadId = "-Kr7P0jTKhld6taLcS89";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mMessageEdit = (EditText) findViewById(R.id.messageEdit);
        mEmptyListMessage = (TextView) findViewById(R.id.emptyTextView);
        tvError = (TextView) findViewById(R.id.tvError);

        mChatRef = FirebaseDatabase.getInstance().getReference().child(getChatRoomName());
        mChatRef.addChildEventListener(childEventListener);

        mSendButton.setOnClickListener(this);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mMessages = (RecyclerView) findViewById(R.id.messagesList);
        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(mManager);
    }

    private String getChatRoomName() {
//        String roomName = getIntent().getStringExtra("room_name");
//        return roomName != null && roomName.length() > 0 ? roomName : "public_room";
        return "Threads/" + threadId + "/messages";
    }

    @Override
    public void onStart() {
        super.onStart();

        // Default Database rules do not allow unauthenticated reads, so we need to
        // sign in before attaching the RecyclerView adapter otherwise the Adapter will
        // not be able to read any data from the Database.
//        if (isSignedIn()) {
//            attachRecyclerViewAdapter();
//        } else {
        signInAnonymously();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        tvError.setText("");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String name = "Name hack";

            FireBaseMessage chat = new FireBaseMessage(name,
                    mMessageEdit.getText().toString(),
                    mAuth.getCurrentUser().getUid());
            mChatRef.push().setValue(chat, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference reference) {
                    if (error != null) {
                        log("Failed to write message" + error.toException());
                    }
                }
            });

            mMessageEdit.setText("");
        }
    }

    private void log(String s) {
        tvError.setText(s);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUI();
    }

    private void attachRecyclerViewAdapter() {
        mAdapter = getAdapter();

        // Scroll to bottom on new messages
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mMessages, null, mAdapter.getItemCount());
            }
        });

        mMessages.setAdapter(mAdapter);
    }

    protected FirebaseRecyclerAdapter<FireBaseMessage, ChatHolder> getAdapter() {
        Query lastFifty = mChatRef.limitToLast(50);
        return new FirebaseRecyclerAdapter<FireBaseMessage, ChatHolder>(
                FireBaseMessage.class,
                R.layout.message,
                ChatHolder.class,
                lastFifty) {
            @Override
            public void populateViewHolder(ChatHolder holder, FireBaseMessage chat, int position) {
                holder.bind(chat);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    private void signInAnonymously() {
        Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
//        mAuth.signInWithEmailAndPassword(getUserName(), getPassword())
        mAuth.signInAnonymously()
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        attachRecyclerViewAdapter();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        log("signInAnonymously onFailure:" + e.getMessage());
                    }
                })
                .addOnCompleteListener(new SignInResultNotifier(this));
    }

    private String getPassword() {
        return getIntent().getStringExtra("password");
    }

    private String getUserName() {
        return getIntent().getStringExtra("username");
    }

    private boolean isSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void updateUI() {
        // Sending only allowed when signed in
        mSendButton.setEnabled(isSignedIn());
        mMessageEdit.setEnabled(isSignedIn());
    }
}
