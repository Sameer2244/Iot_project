package com.example.archimax;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class serialfragment extends Fragment {

    private View serialview;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    String currentuserid;
    RecyclerView recyclerView;
    private DatabaseReference database;
    static String serialid;

    private static final String TAG = serialfragment.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        serialview= inflater.inflate(R.layout.serialfragment,null);
        recyclerView = (RecyclerView)serialview.findViewById(R.id.serialrecycle);
        mAuth =FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        currentuserid = currentuser.getUid();

        database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserid);
        return serialview;

    }



    public serialfragment(){

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseRecyclerOptions options =new FirebaseRecyclerOptions.Builder<serialinfo>().setQuery(database,serialinfo.class).build();

        FirebaseRecyclerAdapter<serialinfo,serialviewholder>adapter = new FirebaseRecyclerAdapter<serialinfo, serialviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final serialviewholder serialviewholder, int i, @NonNull final serialinfo serialinfo) {
                serialviewholder.devicename.setText(serialinfo.getDevicename());
                serialviewholder.deviceserial.setText(serialinfo.getDeviceserial());

                serialviewholder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent lockactivityintent =new Intent(getContext(),LockActivity.class);
                        serialid = serialinfo.getDeviceserial();
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

    public static class serialviewholder extends RecyclerView.ViewHolder{
        TextView devicename,deviceserial;
        public serialviewholder(@NonNull View itemView) {
            super(itemView);
            devicename = (TextView)itemView.findViewById(R.id.devicename);
            deviceserial = (TextView)itemView.findViewById(R.id.deviceserial);
        }
    }
}
