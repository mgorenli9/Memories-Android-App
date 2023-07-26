package com.mehmet.memories.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mehmet.memories.R;
import com.mehmet.memories.adapter.PostAdapter;
import com.mehmet.memories.databinding.ActivityFeedBinding;
import com.mehmet.memories.model.Post;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;

    ArrayList<Post> postArrayList;

    private ActivityFeedBinding activityFeedBinding;

    PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFeedBinding= ActivityFeedBinding.inflate(getLayoutInflater());
        View view = activityFeedBinding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance(); // used to sign out

        firebaseFirestore = FirebaseFirestore.getInstance();

        postArrayList = new ArrayList<>();

        getData();

        activityFeedBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter= new PostAdapter(postArrayList);
        activityFeedBinding.recyclerView.setAdapter(postAdapter);
    }


    private void getData(){
                                                        // order by sql deki gibi verileri tarihe göre sıralıyor
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                if(value != null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();

                        String userEmail = (String) data.get("userEmail");
                        String comment = (String) data.get("comment");
                        String title = (String) data.get("title");
                        String downloadURL= (String) data.get("downloadURL");

                        Post post = new Post(userEmail,comment,title,downloadURL);
                        postArrayList.add(post);

                    }
                    postAdapter.notifyDataSetChanged();// veri çekildikten sonraa yeni verinin geldiğini recycler view a bildirir.
                }
            }
        });

    }

    //menülere erisim --> ayrıca bunun için res klasörü içinde menu diye Android Resource file oluşturdum
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // menülerden birisi seçilirse ne yapılacagını belirleyen kısım
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            // eğer kullanıcı add post denen item e tıklarsa -->

        switch (item.getItemId()){
            case R.id.ic_btn_add:{
                Intent intentToUpload = new Intent(FeedActivity.this,UploadActivity.class);
                startActivity(intentToUpload);
                break;
            }
            case R.id.sign_out:{
                // sign out and back to sign in activity
                firebaseAuth.signOut();

                Intent intentToSignIn = new Intent(FeedActivity.this,MainActivity.class);
                startActivity(intentToSignIn);
                break;
            }
            case R.id.settings:{
                Toast.makeText(FeedActivity.this,"Settings",Toast.LENGTH_SHORT).show();
                break;
            }


        }
        return super.onOptionsItemSelected(item);
    }

}