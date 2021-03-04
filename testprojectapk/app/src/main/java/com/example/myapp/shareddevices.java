package com.example.myapp;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class shareddevices extends Fragment {

    private View shareview;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    private DatabaseReference database;
    String newuid;
    public shareddevices() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        shareview= inflater.inflate(R.layout.fragment_shareddevices,null);
        recyclerView = (RecyclerView)shareview.findViewById(R.id.sharedrecycle);
        mAuth =FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database= FirebaseDatabase.getInstance().getReference();


        return shareview;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Query savedcode = database.child("sharedcode").child(mAuth.getCurrentUser().getUid()).orderByChild("code").equalTo("code");
        savedcode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String savedcodes = snapshot.getKey();        //here this is getting only one key mean (only one share code)
                    Query uidquery = database.child("sharing").orderByChild("code").equalTo(savedcodes);
                    uidquery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot childsnapshot: dataSnapshot.getChildren()){
                                newuid = childsnapshot.getKey();     //this is right.... onDatachange is perfect for getting only one key and here i need only one key
                                FirebaseRecyclerOptions options =new FirebaseRecyclerOptions.Builder<serialinfo>().setQuery(database.child("Users").child(newuid),serialinfo.class).build();

                                FirebaseRecyclerAdapter<serialinfo,serialviewholder> adapter = new FirebaseRecyclerAdapter<serialinfo, serialviewholder>(options) {
                                    @Override
                                    protected void onBindViewHolder(@NonNull final serialviewholder serialviewholder, int i, @NonNull final serialinfo serialinfo) {
                                        serialviewholder.devicename.setText(serialinfo.getDevicename());
                                        serialviewholder.deviceserial.setText(serialinfo.getDeviceserial());

                                        serialviewholder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent lockactivityintent =new Intent(getContext(),LockActivity.class);
                                                lockactivityintent.putExtra("device",serialinfo.getDeviceserial());
                                                startActivity(lockactivityintent);
                                            }
                                        });
                                    }

                                    @NonNull
                                    @Override
                                    public serialviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.serialformat,parent,false);
                                        serialviewholder viewholder = new serialviewholder(view);
                                        return viewholder;
                                    }
                                };

                                recyclerView.setAdapter(adapter);
                                adapter.startListening();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class serialviewholder extends RecyclerView.ViewHolder{
        TextView devicename,deviceserial;
        public serialviewholder(@NonNull View itemView) {
            super(itemView);
            devicename = (TextView)itemView.findViewById(R.id.devicename);
            deviceserial = (TextView)itemView.findViewById(R.id.deviceserial);
        }
    }

}
