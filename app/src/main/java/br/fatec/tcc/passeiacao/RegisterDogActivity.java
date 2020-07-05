package br.fatec.tcc.passeiacao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import br.fatec.tcc.passeiacao.model.DogModel;

public class RegisterDogActivity extends AppCompatActivity {

    // request code
    private final int PICK_IMAGE_REQUEST_DOG = 1;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Toolbar myToolbar;

    ImageView imgCoverDog;
    EditText edtNameDog;
    EditText edtAgeDog;
    Spinner spnBreedDog;
    EditText edtWeightDog;
    Spinner spnGenreDog;
    Spinner spnCastrated;
    EditText edtObservationDog;
    Button btnRegisterDog;
    String downloadUrlDog = "";

    private AlertDialog.Builder confirmacao;

    private String Breed = "Raça --";
    private String Castred = "Castrado --";
    private String Genre = "Sexo --";
    private TextView idTextView;
    private String id = "";
    private String id_user_firebase = "";
    String id_dog = null;

    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private final String mMsqInfo = "";
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_dog);

        //Inicializa o FireBase
        inicializaFireBase();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Faz o carregamento dos dados do usuario selecionado
        Intent origemIntent = getIntent();
        Bundle bundle = origemIntent.getExtras();
        id_user_firebase = bundle.getString("id_user_auth");

        carregaCampos();

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarChooseRegisterDog);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //Mostrar o botão
            getSupportActionBar ().setHomeButtonEnabled (true);      //Ativar o botão
            getSupportActionBar ().setTitle ("Cadastro de cãezinhos");     //Titulo para ser exibido na sua Action Bar em frente à seta
            //getSupportActionBar ().setTitle (getResources().getString(R.string.action_settings_title));     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        btnRegisterDog = findViewById(R.id.btnRegisterDog);
        btnRegisterDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DogModel dogModel = carregaDog();
                if(dogModel == null) {return;}
                if(id_dog != null){
                    updateDogFireBase(dogModel);
                }else{
                    insertDogFireBase(dogModel);
                }
            }
        });

        imgCoverDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(PICK_IMAGE_REQUEST_DOG);
            }
        });

        if(bundle.getBoolean("updateUI")){
            loadingDatasUpdate(bundle);
            btnRegisterDog.setText("ATUALIZAR DADOS");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Carrega os campos do cadastro (activity_cadastrar_usuario); */
    private void carregaCampos(){
        imgCoverDog = findViewById(R.id.imgCoverDog);
        edtNameDog = findViewById(R.id.edtNameDog);
        edtAgeDog = findViewById(R.id.edtAgeDog);
        spnBreedDog = findViewById(R.id.spnBreedDog);
        edtWeightDog = findViewById(R.id.edtWeightDog);
        spnGenreDog = findViewById(R.id.spnGenreDog);
        spnCastrated = findViewById(R.id.spnCastrated);
        edtObservationDog = findViewById(R.id.edtObservationDog);

        //Formatação dos campos
        ArrayAdapter<CharSequence> adapterBreedDog = ArrayAdapter.createFromResource(this,
                R.array.arr_breed, android.R.layout.simple_spinner_item);
        adapterBreedDog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBreedDog.setAdapter(adapterBreedDog);
        spnBreedDog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    ((TextView) parent.getChildAt(position)).setTextColor(Color.GRAY);
                }
                Breed = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), Breed, Toast.LENGTH_SHORT).show();
                /*parent.getSelectedView();
                TextView errorText = (TextView) parent.getSelectedView();
                errorText.setError("Preenchimento obrigatório");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("--");//changes the selected item text to this*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Breed = "Raça --";
            }
        });

        ArrayAdapter<CharSequence> adapterGenreDog = ArrayAdapter.createFromResource(this,
                R.array.arr_genre_dog, android.R.layout.simple_spinner_item);
        adapterGenreDog.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenreDog.setAdapter(adapterGenreDog);
        spnGenreDog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    ((TextView) parent.getChildAt(position)).setTextColor(Color.GRAY);
                }
                Genre = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), Breed, Toast.LENGTH_SHORT).show();
                /*parent.getSelectedView();
                TextView errorText = (TextView) parent.getSelectedView();
                errorText.setError("Preenchimento obrigatório");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText("--");//changes the selected item text to this*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Breed = "Raça --";
            }
        });

        ArrayAdapter<CharSequence> adapterCastrated = ArrayAdapter.createFromResource(this,
                R.array.arr_castrated, android.R.layout.simple_spinner_item);
        adapterCastrated.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCastrated.setAdapter(adapterCastrated);
        spnCastrated.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    ((TextView) parent.getChildAt(position)).setTextColor(Color.GRAY);
                }
                Castred = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), TypeDog, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Castred = "Castrado --";
            }
        });

//        telefoneEditText = (EditText) findViewById(R.id.telefoneEditText);
//        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
//        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefoneEditText, simpleMaskTelefone);
//        telefoneEditText.addTextChangedListener(maskTelefone);
    }

    /* Realiza a validação dos campos já preenchidos e retorna o error;
     * Use esta função para validar os campos; */
    private boolean validaCampos(){

        //Executa a validação dos campos
        if (edtNameDog.getText().toString().trim().equals("")) {
            edtNameDog.setError("Preenchimento obrigatório");
            edtNameDog.requestFocus();
            return true;
        }else if (Breed.toString().trim().equals("Raça --")){
            ((TextView)spnBreedDog.getChildAt(0)).setError("Message");
//            spnBreedDog.setError("Preenchimento obrigatório");
            spnBreedDog.requestFocus();
            return true;
        }else{
            return false;
        }

    }

    /* Cria a classe de usuario que será uasda no cadastro;
     * Use esta função para formatar os campos como desejado; */
    private DogModel carregaDog(){

        //String imgCoverDog = findViewById(R.id.imgCoverDog);
        String medtNameDog = edtNameDog.getText().toString();
        Integer medtAgeDog = Integer.parseInt(edtAgeDog.getText().toString());
        Double medtWeightDog = Double.valueOf(edtWeightDog.getText().toString());
        //Double mspnGenreDog = Double.valueOf(spnGenreDog.getText().toString());
        String medtObservationDog = edtObservationDog.getText().toString();

        //Faz a formatação dos dados
        DogModel dogModel = new DogModel(
                medtNameDog,
                medtAgeDog,
                !Breed.equals("Raça --") ? Breed : "",
                medtWeightDog,
                !Genre.equals("Sexo --") ? Genre : "",
                Castred.equals("Sim") ? true : false,
                medtObservationDog,
                downloadUrlDog
        );
        dogModel.setId(id);

        return dogModel;
    }

    /* Função de inicialização do servidor Firebase (Não esqueça de criar a base
     * em seu dominio no console do Firebase e mudar o arquivo: )*/
    private void inicializaFireBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /* Função para carregamento da interface, apenas transportei o código existente do onCreate pra cá */
    private void updateUI(String email){

        // dialog = new AlertDialog.Builder(MainActivity.this);
        // Criando AlertDialog para perguntar ao usuário se já deseja cadastrar Cão (caso dono) ou Agenda (caso passeador)
        confirmacao = new AlertDialog.Builder(RegisterDogActivity.this);

        confirmacao.setTitle("Confirmação");

            confirmacao.setMessage("Cadastrar novo cãozinho?");
            confirmacao.setNegativeButton("Voltar",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            confirmacao.setPositiveButton("Sim",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            confirmacao.create();
            confirmacao.show();
    }

    /* Função para atualização do cadastro */
    private void loadingDatasUpdate (Bundle bundle){
        id_dog = bundle.getString("id_dog");
        String name = bundle.getString("name");
        Integer age = bundle.getInt("age", 0);
        String breed = bundle.getString("breed", "Raça --");
        Boolean castrated = bundle.getBoolean("castrated");
        String comments = bundle.getString("comments");
        String genre = bundle.getString("genre", "Sexo --");
        Double weight = bundle.getDouble("weight", 0.0);
        String image = bundle.getString("image", "");

        ArrayAdapter<CharSequence> adapterBreedDog = ArrayAdapter.createFromResource(this,
                R.array.arr_breed, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> adapterGenre = ArrayAdapter.createFromResource(this,
                R.array.arr_castrated, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> adapterCastrated = ArrayAdapter.createFromResource(this,
                R.array.arr_castrated, android.R.layout.simple_spinner_item);

        edtNameDog.setText(name);
        edtAgeDog.setText(age.toString());
        spnBreedDog.setSelection(((ArrayAdapter)spnBreedDog.getAdapter()).getPosition(breed.toString()));
        edtWeightDog.setText(weight.toString());
        //spnGenreDog.setText(genre);
        spnGenreDog.setSelection(((ArrayAdapter)spnGenreDog.getAdapter()).getPosition(genre.toString()));
        spnCastrated.setSelection(((ArrayAdapter)spnCastrated.getAdapter()).getPosition(castrated ? "Sim" : "Não"));
        edtObservationDog.setText(comments);
        if(URLUtil.isValidUrl(image)) {
            downloadUrlDog = image;
            imgCoverDog.setImageURI(Uri.parse(image));
        }
    }
    //    #############################################################################################
    /* PRINCIPAIS FUNÇÕES CRUD */
    private void insertDogFireBase(final DogModel dogModel){

        if(id_user_firebase == null) {return;}
        String id = UUID.randomUUID().toString();
        dogModel.setId(id);
        dogModel.setId_user(id_user_firebase);

        //Realiza o INSERT na base FB;
        databaseReference.child("Dogs").child(id).setValue(dogModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Sucesso
                        Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("INSERT", "failure: ", e.getCause());
                        Toast.makeText(getApplicationContext(), "Falha no INSERT: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateDogFireBase(final DogModel dogModel){
        dogModel.setId(id_dog);
        dogModel.setId_user(id_user_firebase);

        databaseReference.child("Dogs").orderByChild("id").equalTo(id_dog).addListenerForSingleValueEvent(new ValueEventListener() {
        //databaseReference.child("Dogs").child(id_dog).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(dogModel);
                    Toast.makeText(getApplicationContext(), "Atualização realizada com sucesso",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDogFireBase(DogModel dogModel){

        //Primeiro valida o EMAIL solicitado;
        databaseReference.child("Dogs").child(dogModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String childKey = dataSnapshot.getKey();
                databaseReference.child("Dogs").child(childKey).removeValue();
                Toast.makeText(getApplicationContext(), "DELETE realizado com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    #############################################################################################
//    android:digits="0123456789.-"
//    #############################################################################################
    private void openFileChooser(Integer PICK_IMAGE_REQUEST) {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, PICK_IMAGE_REQUEST);
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Aguarde...");
        mDialog.setMessage(mMsqInfo);
        mDialog.setCancelable(true);
        mDialog.show();

        if (requestCode == PICK_IMAGE_REQUEST_DOG && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            //Picasso.get().load(mImageUri).into(imgAvatarRegisterUser);
            imgCoverDog.setImageURI(mImageUri);
            if (mImageUri != null) {
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));
                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //mProgressBar.setProgress(0);
                                        //Fecha o load
                                        if ((mDialog != null) && ( mDialog.isShowing())){
                                            mDialog.dismiss();
                                            mDialog = null;
                                        }
                                    }
                                }, 500);
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful());
                                Uri downloadUrl = urlTask.getResult();
                                downloadUrlDog = downloadUrl.toString();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Fecha o load
                                if ((mDialog != null) && ( mDialog.isShowing())){
                                    mDialog.dismiss();
                                    mDialog = null;
                                }
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mDialog.setMessage(
                                        "Carregando "
                                                + (int)progress + "%");
                                //mProgressBar.setProgress((int) progress);
                            }
                        });
            }
        } else {
            //Fecha o load
            if ((mDialog != null) && ( mDialog.isShowing())){
                mDialog.dismiss();
                mDialog = null;
            }
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
