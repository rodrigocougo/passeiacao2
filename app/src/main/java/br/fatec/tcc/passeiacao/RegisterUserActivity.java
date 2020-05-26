package br.fatec.tcc.passeiacao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.room.UserViewModel;

import static aplicacao.passeiacao.DONO;
import static aplicacao.passeiacao.PASSEADOR;

public class RegisterUserActivity extends AppCompatActivity {

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    private int IMAGE_TYPE = 1; //1=COVER / 2=AVATAR

    /*Campo do cadastro de usuarios/dono/passeador (activity_cadastrar_usuario) */
    private ImageButton imageButtonLoadCover;
    private SimpleDraweeView imgAvatarRegisterUser;
    private ImageView imgCoverUser;
    private EditText nomeEditText;
    private EditText emailEditText;
    private EditText telefoneEditText;
    private Spinner sexoSpinner;
    private EditText usuarioEditText;
    private EditText senhaEditText;
    private EditText cpfEditText;
    private EditText dataNascimentoEditText;
    private EditText cepEditText;
    private EditText numeroEditText;
    private EditText enderecoEditText;
    private EditText complementoEditText;
    private EditText bairroEditText;
    private EditText cidadeEditText;
    private TextView idTextView;

    private Toolbar myToolbar;

    private Button cadastrarUsuarioButton;
    private Button atualizarUsuarioButton;
    private Button deleteUsuarioButton;
    private AlertDialog.Builder confirmacao;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    private String Genero = "--";
    private String TypeUser = "--";
    private String id = "";
    private String userType = "";
    private int tipoUsuario = -1;
    private String id_user_firebase = "";

    private UserModel userModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        //Faz o carregamento dos dados do usuario selecionado
        Intent origemIntent = getIntent();
        Bundle bundle = origemIntent.getExtras();
        id_user_firebase = bundle.getString("id_user_auth", "");

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarChooseRegisterUser);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            if (!id_user_firebase.equals("")) {
                getSupportActionBar().setTitle("Atualizar usuário");     //Titulo para ser exibido na sua Action Bar em frente à seta
            } else {
                getSupportActionBar().setTitle("Novo usuário");     //Titulo para ser exibido na sua Action Bar em frente à seta
            }
            //getSupportActionBar ().setTitle (getResources().getString(R.string.action_settings_title));     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        /*Gera o ID (randomico) para gravação no Firebase (Procedimento padrão);
         * Este ID foi gerado neste ponto, para poder realizar o UPDATE nesta tela,
         * posteriormente deve ser trocado!*/
        id = UUID.randomUUID().toString();

        //Inicializa o FireBase
        inicializaFireBase();

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Carrega os campos do formulario de cadastro para coletar informações;
        carregaCampos();

        /* Botão de CADASTRO do usuario (PASSEADOR/DONO)*/
        cadastrarUsuarioButton = (Button) findViewById(R.id.cadastrarUsuarioButton);
        atualizarUsuarioButton = (Button) findViewById(R.id.atualizarUsuario);
        deleteUsuarioButton = (Button) findViewById(R.id.deleteUsuario);
        imageButtonLoadCover = (ImageButton) findViewById(R.id.imageButtonLoadCover);
        deleteUsuarioButton.setVisibility(View.GONE);
        if (!id_user_firebase.equals("")) {
            atualizarUsuarioButton.setVisibility(View.VISIBLE);
            cadastrarUsuarioButton.setVisibility(View.GONE);
        } else {
            atualizarUsuarioButton.setVisibility(View.GONE);
            cadastrarUsuarioButton.setVisibility(View.VISIBLE);
        }

        atualizarUsuarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verifica/valida todos os campos conforme regras de validação desta função;
                if (validaCampos() == true) return;

                //Cria uma nova instancia userModel, e traz as informações dos campos;
                UserModel userModel = carregaUsuario();

                updateUsuarioFireBase(userModel);
            }
        });

        cadastrarUsuarioButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verifica/valida todos os campos conforme regras de validação desta função;
                if (validaCampos() == true) return;

                //Cria uma nova instancia userModel, e traz as informações dos campos;
                UserModel userModel = carregaUsuario();

                //Realiza o INSERT no FIREBASE;
                insertUsuarioFireBase(userModel);


            }
        });

        deleteUsuarioButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Verifica/valida todos os campos conforme regras de validação desta função;
                if (validaCampos() == true) return;

                //Cria uma nova instancia userModel, e traz as informações dos campos;
                UserModel userModel = carregaUsuario();

                //Realiza o INSERT no FIREBASE;
                deleteUsuarioFireBase(userModel);


            }
        });

        imageButtonLoadCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 1;
                SelectImage();
            }
        });

        /***************************************************************************************/
        // Provedor Room UserModel
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        /*****************************************************************************************/
        //Observatorio das PRODUTOS DO PEDIDO...
        userViewModel.getUserAuth().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(@Nullable UserModel muserModel) {
                userModel = muserModel;
                if (userModel == null) return;
                /***************************************************************************************/
                //Carregamento dos campos para update
                if (!id_user_firebase.equals("")) {
                    // Carrega os dados do usuario logado
                    loadingDatasUpdate();
                }
            }
        });
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

    //    #############################################################################################

    /* Carrega os campos do cadastro (activity_cadastrar_usuario); */
    private void carregaCampos() {
        imgAvatarRegisterUser = (SimpleDraweeView) findViewById(R.id.imgAvatarRegisterUser);
        imgCoverUser = (ImageView) findViewById(R.id.imgCoverUser);
        nomeEditText = (EditText) findViewById(R.id.nomeEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        telefoneEditText = (EditText) findViewById(R.id.telefoneEditText);
        sexoSpinner = (Spinner) findViewById(R.id.sexoSpinner);
        usuarioEditText = (EditText) findViewById(R.id.usuarioEditText);
        senhaEditText = (EditText) findViewById(R.id.senhaEditText);
        cpfEditText = (EditText) findViewById(R.id.cpfEditText);
        dataNascimentoEditText = (EditText) findViewById(R.id.dataNascimentoEditText);
        cepEditText = (EditText) findViewById(R.id.cepEditText);
        numeroEditText = (EditText) findViewById(R.id.numeroEditText);
        enderecoEditText = (EditText) findViewById(R.id.enderecoEditText);
        complementoEditText = (EditText) findViewById(R.id.complementoEditText);
        bairroEditText = (EditText) findViewById(R.id.bairroEditText);
        cidadeEditText = (EditText) findViewById(R.id.cidadeEditText);
        idTextView = (TextView) findViewById(R.id.idTextView);

        //Formatação dos campos
        telefoneEditText.addTextChangedListener(Util.insert(telefoneEditText));
        cpfEditText.addTextChangedListener(Util.insert2(cpfEditText));
        dataNascimentoEditText.addTextChangedListener(Util.insert3(dataNascimentoEditText));

        //Carrega o spinner de genero
        final Spinner spinner = (Spinner) findViewById(R.id.sexoSpinner);
        final Spinner spnSelectedType = (Spinner) findViewById(R.id.spnSelectedType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.arr_sexo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Genero = parent.getItemAtPosition(position).toString();
                Toast.makeText(getBaseContext(), Genero, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.arr_type, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectedType.setAdapter(adapter2);
        spnSelectedType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TypeUser = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), TypeUser, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgAvatarRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 2;
                SelectImage();
            }
        });

        imgCoverUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 1;
                SelectImage();
            }
        });

//        telefoneEditText = (EditText) findViewById(R.id.telefoneEditText);
//        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
//        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefoneEditText, simpleMaskTelefone);
//        telefoneEditText.addTextChangedListener(maskTelefone);
    }

    /* Realiza a validação dos campos já preenchidos e retorna o error;
     * Use esta função para validar os campos; */
    private boolean validaCampos() {

        //Executa a validação dos campos
        if (nomeEditText.getText().toString().trim().equals("")) {
            nomeEditText.setError("Preenchimento obrigatório");
            nomeEditText.requestFocus();
            return true;
        } else if (emailEditText.getText().toString().trim().equals("")) {
            emailEditText.setError("Preenchimento obrigatório");
            emailEditText.requestFocus();
            return true;
        } else if (telefoneEditText.getText().toString().trim().equals("")) {
            telefoneEditText.setError("Preenchimento obrigatório");
            telefoneEditText.requestFocus();
            return true;
        } else if (Genero.toString().trim().equals("--")) {
            telefoneEditText.setError("Preenchimento obrigatório");
            telefoneEditText.requestFocus();
            return true;
        } else if (usuarioEditText.getText().toString().trim().equals("")) {
            usuarioEditText.setError("Preenchimento obrigatório");
            usuarioEditText.requestFocus();
            return true;
        } else if (senhaEditText.getText().toString().trim().equals("")) {
            senhaEditText.setError("Preenchimento obrigatório");
            senhaEditText.requestFocus();
            return true;
        } else if (Util.validaSenha(senhaEditText.getText().toString().trim()) != null) {
            String retorno = Util.validaSenha(senhaEditText.getText().toString().trim());
            senhaEditText.setError(retorno);
            senhaEditText.requestFocus();
            return true;
        } else if (!Util.isCPF(cpfEditText.getText().toString().trim())) {
            cpfEditText.setError("CPF invalido");
            cpfEditText.requestFocus();
            return true;
        } else if (dataNascimentoEditText.getText().toString().trim().equals("")) {
            dataNascimentoEditText.setError("Preenchimento obrigatório");
            dataNascimentoEditText.requestFocus();
            return true;
        } else if (cepEditText.getText().toString().trim().equals("")) {
            cepEditText.setError("Preenchimento obrigatório");
            cepEditText.requestFocus();
            return true;
        } else if (numeroEditText.getText().toString().trim().equals("")) {
            numeroEditText.setError("Preenchimento obrigatório");
            numeroEditText.requestFocus();
            return true;
        } else if (enderecoEditText.getText().toString().trim().equals("")) {
            enderecoEditText.setError("Preenchimento obrigatório");
            enderecoEditText.requestFocus();
            return true;
        } else if (complementoEditText.getText().toString().trim().equals("")) {
            complementoEditText.setError("Preenchimento obrigatório");
            complementoEditText.requestFocus();
            return true;
        } else if (bairroEditText.getText().toString().trim().equals("")) {
            bairroEditText.setError("Preenchimento obrigatório");
            bairroEditText.requestFocus();
            return true;
        } else if (cidadeEditText.getText().toString().trim().equals("")) {
            cidadeEditText.setError("Preenchimento obrigatório");
            cidadeEditText.requestFocus();
            return true;
        } else {
            return false;
        }

    }

    /* Cria a classe de usuario que será uasda no cadastro;
     * Use esta função para formatar os campos como desejado; */
    private UserModel carregaUsuario() {

        String nome = nomeEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String telefone = telefoneEditText.getText().toString();
        String sexo = Genero;
        String login = usuarioEditText.getText().toString();
        String senha = Util.CodificarBase64(senhaEditText.getText().toString());
        String cpf = cpfEditText.getText().toString();
        String dataNasc = dataNascimentoEditText.getText().toString();
        String cep = cepEditText.getText().toString();
        String numero = numeroEditText.getText().toString();
        String endereco = enderecoEditText.getText().toString();
        String compl = complementoEditText.getText().toString();
        String bairro = bairroEditText.getText().toString();
        String cidade = cidadeEditText.getText().toString();

        //Grava o ID EM UM CAMPO INVISIBLE PARA UPDATE (Porteriormente remover este procedimento)
        idTextView.setText(id.toString());

        //Faz a formatação dos dados
        UserModel userModel = new UserModel(
                "",
                nome.trim(),
                email.trim(),
                telefone.trim(),
                sexo.trim(),
                login.trim(),
                senha.trim(),
                cpf.trim(),
                dataNasc.trim(),
                cep.trim(),
                numero.trim(),
                endereco.trim(),
                compl.trim(),
                bairro.trim(),
                cidade.trim(),
                0.0,
                0,
                0,
                "",
                "",
                false,
                false,
                false,
                "",
                "",
                "",
                ""
        );

        userModel.setId(id);
        //Tipo de UserModel
        if (TypeUser.equals("Serei apenas o dono")) {
            userModel.setDono(true);
        } else if (TypeUser.equals("Serei apenas o passeador")) {
            userModel.setPasseador(true);
        } else if (TypeUser.equals("Serei dono e passeador")) {
            userModel.setPasseador(true);
            userModel.setDono(true);
        }

        return userModel;
    }

    /* Função de inicialização do servidor Firebase (Não esqueça de criar a base
     * em seu dominio no console do Firebase e mudar o arquivo: )*/
    private void inicializaFireBase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /* Função para carregamento da interface, apenas transportei o código existente do onCreate pra cá*/
    private void updateUI(String email) {

        // dialog = new AlertDialog.Builder(MainActivity.this);
        // Criando AlertDialog para perguntar ao usuário se já deseja cadastrar Cão (caso dono) ou Agenda (caso passeador)
        confirmacao = new AlertDialog.Builder(RegisterUserActivity.this);

        confirmacao.setTitle("Para onde vamos daqui?");

        if (tipoUsuario == DONO) {
            confirmacao.setMessage("Deseja cadastrar seu cãozinho agora?");
            confirmacao.setNegativeButton("Agora não",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent telaDono = new Intent(RegisterUserActivity.this, ScreenOwnerActivity.class);
                            startActivity(telaDono);
                        }
                    });

            confirmacao.setPositiveButton("Claro!",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent novoCao = new Intent(RegisterUserActivity.this, RegisterDogActivity.class);
                            startActivity(novoCao);
                        }
                    });

            confirmacao.create();
            confirmacao.show();
        } else if (tipoUsuario == PASSEADOR) {
            confirmacao.setMessage("Deseja cadastrar sua agenda agora?");
            confirmacao.setNegativeButton("Depois",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent telaPasseador = new Intent(RegisterUserActivity.this, ScreenWalkerActivity.class);
                            startActivity(telaPasseador);
                        }
                    });
            confirmacao.setPositiveButton("Vamos lá!",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Intent novaAgenda = new Intent(RegisterUserActivity.this, RegisterScheduleActivity.class);
                            //startActivity(novaAgenda);
                        }
                    });
            confirmacao.create();
            confirmacao.show();
        }
    }

    /* Função para atualização do cadastro */
    private void loadingDatasUpdate() {
        //sexoSpinner = (Spinner) findViewById(R.id.sexoSpinner);

        nomeEditText.setText(userModel.getNome().toString());
        emailEditText.setText(userModel.getEmail().toString());
        telefoneEditText.setText(userModel.getTelefone().toString());
        usuarioEditText.setText(userModel.getUsuario().toString());
        //senhaEditText.setText(userModel.getSenha().toString());
        cpfEditText.setText(userModel.getCPF().toString());
        dataNascimentoEditText.setText(userModel.getNasc().toString());
        cepEditText.setText(userModel.getCep().toString());
        numeroEditText.setText(userModel.getNumero().toString());
        enderecoEditText.setText(userModel.getEndereco().toString());
        complementoEditText.setText(userModel.getComplemento().toString());
        bairroEditText.setText(userModel.getBairro().toString());
        cidadeEditText.setText(userModel.getCidade().toString());
        idTextView.setText(userModel.getId().toString());

        //Carrega o spinner de genero
        final Spinner sexoSpinner = (Spinner) findViewById(R.id.sexoSpinner);
        final Spinner spnSelectedType = (Spinner) findViewById(R.id.spnSelectedType);

        sexoSpinner.setSelection(((ArrayAdapter) sexoSpinner.getAdapter()).getPosition(userModel.getSexo()));

        spnSelectedType.setSelection(((ArrayAdapter) spnSelectedType.getAdapter()).getPosition(
                userModel.getDono() && userModel.getPasseador() ? "Serei dono e passeador" :
                        userModel.getDono() ? "Serei apenas o dono" :
                                userModel.getPasseador() ? "Serei apenas o passeador" : "--"));
    }

    //    #############################################################################################
    //load images

    //    #############################################################################################
    /*PRINCIPAIS FUNÇÕES CRUD*/
    private void insertUsuarioFireBase(final UserModel userModel) {

        /*Existem dois processos diferentes nesta função, um para o INSERT no FireBase (Grava userModel)
         * outro para o INSERT no FireBAse Auth de UserModel (Grava só o email/senha)*/

        final String email = userModel.getEmail().toString();
        final String senha = userModel.getSenha().toString();

        //Realiza o INSERT na base FB;
        databaseReference.child("Usuarios").child(userModel.getId()).setValue(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Após o INSERT no FB, faz o INSERT na autenticação do FB Auth;
                        mAuth.createUserWithEmailAndPassword(email, Util.DecodificarBase64(senha))
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(user.getEmail());
                                            finish();
                                        } else {
                                            Log.w("createUserWithEmail", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "Falha na autenticação." + task.getException().toString(),
                                                    Toast.LENGTH_SHORT).show();
                                            //updateUI(email);
//                                            updateUI(null);
                                        }

                                        // ...
                                    }
                                });
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

    private void updateUsuarioFireBase(final UserModel userModel) {
        userModel.setId(id_user_firebase);
        databaseReference.child("Usuarios").orderByChild("id").equalTo(id_user_firebase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(userModel);
                    Toast.makeText(getApplicationContext(), "Atualização realizada com sucesso",
                            Toast.LENGTH_SHORT).show();
                    userViewModel.addUser(userModel);
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

    private void deleteUsuarioFireBase(UserModel userModel) {

        //Primeiro valida o EMAIL solicitado;
        databaseReference.child("Usuarios").child(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String childKey = dataSnapshot.getKey();
                databaseReference.child("Usuarios").child(childKey).removeValue();
                Toast.makeText(getApplicationContext(), "DELETE realizado com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Deleta o userModel no FB Authenticação
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "UserModel deletado com sucesso FB");
                }
            }
        });
    }

    //    #############################################################################################
//    android:digits="0123456789.-"
    //Operações com imagens
// Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Seleicone a imagem..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {
                if (IMAGE_TYPE == 1) {
                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    imgCoverUser.setImageBitmap(bitmap);
                } else if (IMAGE_TYPE == 2) {
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                    BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Paint paint = new Paint();
                    paint.setShader(shader);
                    paint.setAntiAlias(true);
                    Canvas c = new Canvas(circleBitmap);
                    c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
                    //imgAvatarRegisterUser.setImageBitmap(bitmap);
                    imgAvatarRegisterUser.setImageURI(filePath);
                }

            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    //metodo 2 upload firebase image
    private void upLoadImageFirebase() {
        imageButtonLoadCover.setDrawingCacheEnabled(true);
        imageButtonLoadCover.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageButtonLoadCover.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = null;//mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    //progressDialog.dismiss();
                                    Toast
                                            .makeText(getApplicationContext(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            //progressDialog.dismiss();
                            Toast
                                    .makeText(getApplicationContext(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    /*progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");*/
                                }
                            });
        }
    }

}
