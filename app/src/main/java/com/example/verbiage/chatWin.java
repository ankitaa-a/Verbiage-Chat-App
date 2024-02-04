package com.example.verbiage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatWin extends AppCompatActivity {

    String receiverimg, receivername, receiveruid, senderuid;
    CircleImageView profile;
    TextView receiverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    public static String senderImg;
    public static String receiverImg;
    String senderRoom, receiverRoom;
    RecyclerView messagesadapter;
    ArrayList<msgModelclass> messagesarraylist;

    messagesAdapter mmessagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        receivername=getIntent().getStringExtra("nameee");
        receiverimg=getIntent().getStringExtra("receiverImg");
        receiveruid=getIntent().getStringExtra("uid");

        messagesarraylist = new ArrayList<>();

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);

        profile = findViewById(R.id.profileimg);
        receiverNName = findViewById(R.id.receivername);

        messagesadapter = findViewById(R.id.msgadapter);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagesadapter.setLayoutManager(linearLayoutManager);
        mmessagesAdapter = new messagesAdapter(chatWin.this,messagesarraylist);
        messagesadapter.setAdapter(mmessagesAdapter);

        Picasso.get().load(receiverimg).into(profile);
        receiverNName.setText(""+receivername);

        senderuid = firebaseAuth.getUid();

        senderRoom = senderuid + receiveruid;
        receiverRoom = receiveruid + senderuid;

        DatabaseReference reference = database.getReference().child("users").child(firebaseAuth.getUid());
        DatabaseReference chatrefernce = database.getReference().child("chats").child(senderRoom).child("messages");


        chatrefernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesarraylist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    messagesarraylist.add(messages);
                }
                mmessagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue().toString();
                receiverImg = receiverimg;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textmsg.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(chatWin.this, "Empty Field!!", Toast.LENGTH_SHORT).show();
                }
                textmsg.setText("");
                Date date = new Date();
                msgModelclass messagess = new msgModelclass(message,senderuid,date.getTime());

                //copies

                DatabaseReference senderRef = database.getReference().child("chats").child(senderRoom).child("messages");
                DatabaseReference receiverRef = database.getReference().child("chats").child(receiverRoom).child("messages");

                senderRef.push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        receiverRef.push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Handle completion if needed
                            }
                        });
                    }



                //mine
                    /*
                database= FirebaseDatabase.getInstance();
                database.getReference().child("chats").child("senderRoom").child("messages")
                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats").child("receiverRoom").child("messages")
                                        .push().setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                            }*/
                        });
            }
        });
    }


}
