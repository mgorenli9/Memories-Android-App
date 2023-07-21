package com.mehmet.memories.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mehmet.memories.R;
import com.mehmet.memories.databinding.ActivityFeedBinding;
import com.mehmet.memories.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    Button btnShare;
    ImageView imageView;

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher; // galeriye gitme intenti
    ActivityResultLauncher<String> permissionLauncher;  // galeriye erişim izni

    private ActivityUploadBinding activityUploadBinding;

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUploadBinding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = activityUploadBinding.getRoot();
        setContentView(view);

        btnShare = (Button) findViewById(R.id.btnShare);
        imageView = (ImageView) findViewById(R.id.imageView);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        storageReference = firebaseStorage.getReference();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // seçilen resmi firebase yüklemenın 2 adımı var
                if(imageData!=null){
                    // storageReference(ana sınıf) .child(altt sınıf)("/images") sotaraage içinde imaages isminde bir klasor oluşturur
                    // image datanın url'daan resmi alıp firestore a yükler

                    // universal unique id / image leri database de kaydetmek için
                    UUID uuid = UUID.randomUUID();
                    String imageName = "images/"+ uuid +".jpg"; // images klasorune koy, uydurma bir isim ver ve jpg ekle
                    storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // download url
                            StorageReference newReference = firebaseStorage.getReference(imageName);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    String comment = activityUploadBinding.editTxtComment.getText().toString();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String email = user.getEmail();

                                    HashMap<String, Object> postData = new HashMap<>();
                                    postData.put("userEmail",email);
                                    postData.put("comment",comment);
                                    postData.put("downloadURL",downloadUrl);
                                    postData.put("date", FieldValue.serverTimestamp()); // güncel tarihi verir

                                    // yukarıdaki bilgileri hasmap olarak firestore a kaydeder
                                    firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Intent intent = new Intent(UploadActivity.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UploadActivity.this,"Account informations couldn't saved!",Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this,"The image couldn't uploaded!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        // resme tıklandıgında galeri açılır ve resimm yüklenir
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // tehlikeli bir izin, manifest e eklemek yetmiyor

                // 1. izin var mı kontrol edilir,
                if(ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    // 3. izin yoksa bir açıklaama göstteriyoruz
                    if(ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        Snackbar.make(view,"Permission needed for Gallary",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // ask permission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }).show();
                    }
                    // 4. izin yoksa izni istiyoruz
                    else{
                        // ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }
                // 2. eğer varsa direk intent  // ayrıca izin istemek veya intent için AcivityResult Launcher kullanıyoruz. dogrudan start
                // activity ile başlatmıyoruz
                else{
                    Intent intentToGallary= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallary);
                }

            }
        });

        registerLaunch();


    }

    public void registerLaunch()
    {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                    Intent intentFromResult= result.getData();
                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        activityUploadBinding.imageView.setImageURI(imageData);
/*
                            try{

                                if(Build.VERSION.SDK_INT >= 28){

                                }else{

                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
 */
                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result == true){
                    Intent intentToGallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallary);
                }
                else{
                    Toast.makeText(UploadActivity.this,"Permission needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}