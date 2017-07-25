package com.firebase.uidemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.firebase.uidemo.database.ChatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatRoomSelectActivity extends AppCompatActivity {

    @BindView(R.id.edUserName) EditText edUserName;
    @BindView(R.id.edRoomName) EditText edRoomName;
    @BindView(R.id.edPassword) EditText edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_select);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnStart)
    public void start() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("room_name", edRoomName.getText().toString());
        intent.putExtra("username", edUserName.getText().toString());
        intent.putExtra("password", edPassword.getText().toString());
        startActivity(intent);
    }
}
