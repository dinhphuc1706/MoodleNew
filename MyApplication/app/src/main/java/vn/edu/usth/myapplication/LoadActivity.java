package vn.edu.usth.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecycleView;
    //layout manager for recyclerview
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton mAddBtn;

    //firestore
    FirebaseFirestore db;
    
    CustomAdapter adapter;
    
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //init firestore
        db = FirebaseFirestore.getInstance();
        
        //init
        mRecycleView = findViewById(R.id.recycler_view);
        mAddBtn = findViewById(R.id.addBtn);
        
        //set properties
        mRecycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(layoutManager);

        //Progress Bar
        pd = new ProgressDialog(this);
        
        //show data
        showData();

        //Floating Action to MainActivity
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoadActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void showData() {
        //set progress dialog
        pd.setTitle("Loading Data...");
        pd.show();


        db.collection("Documents")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //call when retrieved
                        pd.dismiss();
                        //show data
                        for (DocumentSnapshot doc: task.getResult()) {
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("description"));
                            modelList.add(model);
                        }
                        //adapter
                        adapter = new CustomAdapter(LoadActivity.this, modelList);
                        //setup recyclerview
                        mRecycleView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //call when error
                        pd.dismiss();
                        Toast.makeText(LoadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}