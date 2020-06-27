package com.example.archimax;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Fragment fragment;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    TextView emailid;
    DatabaseReference databaseReference;
    String devicename,deviceid,sharecode;
    Query serialquery,validdevice
            ,sharecodeduplicate,sharecodevalid;
        AlertDialog alertDialog,sharedialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add_device();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        emailid = (TextView)view.findViewById(R.id.emailid);

        super.onStart();



        if(currentuser==null){
            tologinactivity();
        }else{
            fragment =new serialfragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.replacelayout,fragment);
            fragmentTransaction.commit();

            emailid.setText(currentuser.getEmail());
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_settings) {

           final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
           View view = getLayoutInflater().inflate(R.layout.sharecodeinput,null);

           final EditText inputsharecode = (EditText)view.findViewById(R.id.inputcode);
           Button ok = (Button)view.findViewById(R.id.codeokbtn);

           alert.setView(view);
           sharedialog = alert.create();
           sharedialog.setCanceledOnTouchOutside(true);

           ok.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   sharecode = inputsharecode.getText().toString();
                   if(TextUtils.isEmpty(sharecode)){
                       Toast.makeText(MainActivity.this, "please enter family share code.", Toast.LENGTH_SHORT).show();
                   }else {
                       sharecodevalid=databaseReference.child("sharing").orderByChild("code").equalTo(sharecode);
                       sharecodevalid.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               if(dataSnapshot.exists()){
                                  duplicatesharecode();
                               }else{
                                   Toast.makeText(MainActivity.this, "invalid share code", Toast.LENGTH_SHORT).show();
                                   sharedialog.dismiss();
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });


                   }
               }
           });
           sharedialog.show();
           return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void duplicatesharecode() {

        sharecodeduplicate = databaseReference.child("sharedcode").child(currentuser.getUid()).orderByChild("number").equalTo(sharecode);
        sharecodeduplicate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        Toast.makeText(MainActivity.this, "you have already used this code", Toast.LENGTH_SHORT).show();
                    }else{
                        databaseReference.child("sharedcode").child(currentuser.getUid()).child(sharecode).child("code").setValue("code");
                        databaseReference.child("sharedcode").child(currentuser.getUid()).child(sharecode).child("code").setValue("code");
                        Toast.makeText(MainActivity.this, "shared device will be available in Shared Locks tab", Toast.LENGTH_SHORT).show();
                        sharedialog.dismiss();
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lock) {

            fragment =new serialfragment();
        } else if (id == R.id.nav_shareddevices) {
            fragment = new shareddevices();
        } else if (id == R.id.nav_signout) {
            signout();
        }

        if(fragment!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.replacelayout,fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signout() {
        mAuth.signOut();
        tologinactivity();
    }

    private void Add_device() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.serial_add_dialog,null);

        final EditText inputdevicename = (EditText)view.findViewById(R.id.inputdevicename);
        final EditText inputdeviceserial = (EditText)view.findViewById(R.id.inputdeviceserial);
        Button cancel = (Button)view.findViewById(R.id.cancel);
        Button ok = (Button)view.findViewById(R.id.ok);

        alert.setView(view);
        alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                devicename = inputdevicename.getText().toString();
                deviceid =inputdeviceserial.getText().toString();
                if(TextUtils.isEmpty(deviceid)){
                    Toast.makeText(MainActivity.this, "please enter device ID.", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(devicename)){
                    Toast.makeText(MainActivity.this, "please enter device name.", Toast.LENGTH_SHORT).show();

                }else {
                    validdevice = FirebaseDatabase.getInstance().getReference().child("availabledevices").orderByChild(deviceid).equalTo(deviceid);
                    serialquery = databaseReference.child("Takendevices").orderByChild(deviceid).equalTo(deviceid);
                    validdevice.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                //databaseReference.child(String.valueOf(dataSnapshot.getChildren())).removeValue();
                                duplicatedevice();
                            } else {
                                Toast.makeText(MainActivity.this, "Device id is invalid!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });
        alertDialog.show();
    }


    private  void tologinactivity(){
        Intent intent = new Intent(MainActivity.this,welcomescreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    private void duplicatedevice(){
        serialquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    Toast.makeText(MainActivity.this, "device already taken", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }else{
                    databaseReference.child("Users").child(currentuser.getUid()).child(devicename).setValue("");
                    databaseReference.child("Users").child(currentuser.getUid()).child(devicename).child("devicename").setValue(devicename);
                    databaseReference.child("Users").child(currentuser.getUid()).child(devicename).child("deviceserial").setValue(deviceid);
                    databaseReference.child("Takendevices").child(currentuser.getUid()).child(deviceid).setValue(deviceid);
                    Toast.makeText(MainActivity.this, "Device successfully created.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
