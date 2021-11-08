package vn.edu.usth.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static int RC_SIGN_IN = 100;
    EditText mTitleEt, mDescriptionET;
    Button mSaveBtn,mLoadBtn,mLogoutBtn;
    TextView name,mail;

    //progress dialog
    ProgressDialog pd;

    //firestore instance
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Data");

        //init view
        mTitleEt = findViewById(R.id.titleEt);
        mDescriptionET = findViewById(R.id.descriptionEt);
        mSaveBtn = findViewById(R.id.saveBtn);
        mLoadBtn = findViewById(R.id.loadBtn);

        mLogoutBtn = findViewById(R.id.logoutBtn);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.email);



        //Google Sign In
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null) {
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
        }

        //progress dialog
        pd = new ProgressDialog(this);

        //firestore
        db = FirebaseFirestore.getInstance();

        //Button to Upload
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input data
                String title = mTitleEt.getText().toString().trim();
                String description = mDescriptionET.getText().toString().trim();
                //call function
                uploadData(title, description);
            }
        });

        //Button to Load
        mLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoadActivity.class));
                finish();
            }
        });

        //Button to Logout
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),LoginPage.class);
                startActivity(intent);
                finish();
            }
        });


    }


    private void uploadData(String title, String description) {
        pd.setTitle("Adding Data to Firestore");
        pd.show();
        String id = UUID.randomUUID().toString();

        Map<String,Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("title",title);
        doc.put("description",description);

        //function add data
        db.collection("Documents").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //when data added to database
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Uploading Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failure when adding data
                        
                        pd.dismiss();
                        //get back error messege
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




}











/*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    @SuppressLint("RestrictedApi")
    private void handleSignInResponse(int resultCode, @Nullable Intent data) {

        // Get the Authentication response
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == RESULT_OK) {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (response.isNewUser()) {
                // Create a new User in the database
                createNewUser();
            }
        }
    }
    *//*
 * Create a new User in the users collection in the database, using the FirebaseUser id as the uID
 *//*
    private void createNewUser() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        User newUser = new User(firebaseUser.getDisplayName(), firebaseUser.getUid());

        FirebaseFirestore.collection("users").document(firebaseUser.getUid()).set(newUser);
    }
    *//*
    Get the user ID of the signed in FirebaseUser
     *//*
    public String getUid() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String userID = firebaseUser.getUid();

        return userID;

    }

    *//*
      Get the corresponding user document for the signed in user
     *//*
    public DocumentReference getUserDocument(String uID) {

        DocumentReference userDocument = FirebaseFirestore.collection("users").document(uID);

        return userDocument;

    }*/
