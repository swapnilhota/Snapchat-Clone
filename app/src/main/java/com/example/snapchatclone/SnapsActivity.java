package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapsActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ListView snapListView;
    static ArrayList<String> emails = new ArrayList<>();
    static ArrayList<DataSnapshot> snaps = new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);

        snapListView = findViewById(R.id.snapsListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, emails);
        snapListView.setAdapter(arrayAdapter);
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                emails.add(snapshot.child("from").getValue().toString());
                snaps.add(snapshot);
                arrayAdapter.notifyDataSetChanged();

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                int index = 0;
                for(DataSnapshot snap : snaps)
                {
                    if(snap.getKey() == snapshot.getKey())
                    {
                        snaps.remove(index);
                        emails.remove(index);
                    }
                    index++;
                }

                arrayAdapter.notifyDataSetChanged();

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        snapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DataSnapshot snapshot = snaps.get(i);

                Intent intent = new Intent(getApplicationContext(), ViewSnapActivity.class);
                intent.putExtra("imageName", snapshot.child("imageName").getValue().toString());
                intent.putExtra("caption", snapshot.child("caption").getValue().toString());
                intent.putExtra("snapKey", snapshot.getKey());
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.createSnap)
        {
            Intent intent = new Intent(getApplicationContext(), CreateSnapActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.logout)
        {
            mAuth.signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mAuth.signOut();
        finish();
    }
}