package br.fatec.tcc.passeiacao;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;


/** ATENÇÃO = LEIA ESTE CONTEUDO
 * No cadastro (RegisterUserActivity) encontra-se os procedimentos INSERT/UPDATE/DELETE, dentro
 *do bloco "PRINCIPAIS FUNÇÕES CRUD"
 * No login (LoginActivity) encontra-se algumas funções com READ, como na função "LogarUsuario()";
 * Não esqueça de reestruturar as regras de segurança posteriormente, segue referencias:
 *https://firebase.google.com/docs/firestore/security/rules-structure?authuser=0;
 * Foi add o metódo padrão de Authentication do FireBase, precisa ser ativado na guia:
 *Authentication/Método de login (no site do FB);
 * A classe "Util.java" contem as principais funções de formatação de dados basicos;
 * Os dados do usuario são tratados a partir da classe "UserModel.class", inclusive a gravação no FB;
 * Lembrando que no Firebase (que é uma base de dados NoSQL), deve ser evitado fortes esquemas
 *de relacionamento fazendo uso de coleções, e agregados usando documentos/chave-valor e colunas;
 * Pensando na questão a cima, foi criado 2 campos para a definição de DONO/PASSEADOR, desta forma
 *fica facil e leve realizar consultas no FB;
 * Na função "updateUI()" após a validação de authenticação, é realizado a verificação do DONO X PASSEAODR
 * Hierarquia dos dados no FireBase: Base/passeiacao-fatec-tcc/Usuarios/{:id}/UserModel.class;
 * */
public class LoginActivity extends AppCompatActivity {

    // Usando para conseguir ter acesso ao BD e o reference para conseguir voltar à raiz do DB
    private DatabaseReference referenciaDatabase = FirebaseDatabase.getInstance().getReference();

    // Elementos da página
    private TextView lembrarSenhaTextView;
    private TextView cadastrarUsuarioTextView;
    private Button entrarButton;
    private EditText emailEditText;
    private EditText senhaEditText;
    private TextView contUsersTextView;

    // Agora, crie um callbackManager para tratar as respostas de login chamando - fonte Facebook for Developers
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    /* Para responder a um resultado de login, você deve
    registrar um retorno de chamada com LoginManager ou LoginButton.
    Se registrar o retorno de chamada com LoginButton, você não
    precisará registrá-lo no gerenciador de login. - fonte Facebook for Developers*/
    private LoginButton loginButton;

    /* Declaração para instancia com FireBase/FireBaseAuth */
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private firebaseService mfirebaseService;

    private UserViewModel userViewModel;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Boolean returnRegisterUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide(); // Acrescentado para ocultar a barra superior
        setContentView(R.layout.activity_login);

        //Inicializa o FireBase
        mfirebaseService = new firebaseService();
        databaseReference = mfirebaseService.inicializaFireBase(this);

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d("AUTH", "onAuthStateChanged:signed_out");
                }

            }
        };

        contUsersTextView = (TextView) findViewById(R.id.contUsersTextView);
        emailEditText =  findViewById(R.id.emailEditText);
        senhaEditText =  findViewById(R.id.senhaEditText);
        entrarButton = (Button) findViewById(R.id.entrarButton);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions("email");

        /*****************************************************************************************/
        //Login padrão SEM o FaceBook
        entrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /** Valida os campos
                 * Use regras de validação em segurança nesta etapa*/
                String email = emailEditText.getText().toString().trim();
                String senha = senhaEditText.getText().toString().trim();

                /*Usei uma expressão regular para validar email com Pattern,
                 * Fique a vontade para trocar*/
                if (!Util.validarEmail(email.trim().toString())){
                    Toast.makeText(getApplicationContext(), "E-mail invalido!", Toast.LENGTH_LONG).show();
                    return;
                }

                //Valida a senha (para alterar vá na class Util.java)
                if(Util.validaSenha(senhaEditText.getText().toString().trim()) != null){
                    Toast.makeText(getApplicationContext(), Util.validaSenha(senhaEditText.getText().toString()).toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                LogarUsuario(email, senha);
            }
        });
        /*****************************************************************************************/
        // Callback registration (FaceBookLogin)
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        /*****************************************************************************************/
        // Link Cadastro de usuario (RegisterUserActivity)
        cadastrarUsuarioTextView = (TextView) findViewById(R.id.cadastrarTextView);
        cadastrarUsuarioTextView.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                returnRegisterUser = true;
                Intent i = new Intent(LoginActivity.this, RegisterUserActivity.class);
                i.putExtra("id_user_auth", "");
                startActivity(i);
            }
        });
        /*****************************************************************************************/
        // Link Lembrar senha do usuario ()
        lembrarSenhaTextView = (TextView) findViewById(R.id.lembrarSenhaTextView);
        lembrarSenhaTextView.setOnClickListener(new TextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                returnRegisterUser = true;
                Intent i = new Intent(LoginActivity.this, RememberPasswordActivity.class);
                startActivity(i);
            }
        });

        /***************************************************************************************/
        // Provedor Room UserModel
        userViewModel =  new ViewModelProvider(this).get(UserViewModel.class);;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Caso tenha sido clicado no cadastro de usuario, evita o login automatico;
        if(returnRegisterUser){ returnRegisterUser = false; return;}

        mAuth.addAuthStateListener(mAuthListener);
        // Check se usuario está logado no FB (Padrão do FB);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = currentUser.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = currentUser.getUid();
            updateUI(currentUser.getEmail().toString());
        }else   {
            userViewModel.removeAuthAllUsers();
            entrarButton.setVisibility(View.VISIBLE);
            ConstraintLayout cslSpleeshScreen = findViewById(R.id.cslSpleeshScreen);
            cslSpleeshScreen.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Verifica usuario/senha já cadastrados;
    private void LogarUsuario(final String email, final String senha){
        //Realiza consulta campoa a campo para check e validação;
        //Primeiro valida o EMAIL solicitado;
        databaseReference.child("Usuarios").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    UserModel userModel = dataSnapshot.getChildren().iterator().next()
                            .getValue(UserModel.class);

                    //Segundo, valida SENHA do servidor decodificada;
                    if (senha.equals(Util.DecodificarBase64(userModel.getSenha()).toString())) {

                        //Cria userModel no FB Auth de Usuarios;
                        mAuth.createUserWithEmailAndPassword(email, senha)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            //Toast.makeText(getApplicationContext(), "Usuário logado com sucesso!",
                                                    //Toast.LENGTH_SHORT).show();
                                            //updateUI(user.getEmail());
                                        } else {
                                            Log.w("createUserWithEmail", "createUserWithEmail:failure", task.getException());
                                            //Toast.makeText(getApplicationContext(), "Falha na autenticação." + task.getException().toString(),
                                                    //Toast.LENGTH_SHORT).show();
                                            finish();
//                                            updateUI(null);
                                        }

                                        // ...
                                    }
                                });

                        //AGora realiza o Login ativo do FB Auth Usuarios;
                        mAuth.signInWithEmailAndPassword(email, senha)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Log.d("TAG", "FB Auth: signInWithEmail:success");
                                            updateUI(email);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            //                                    updateUI(null);
                                        }

                                        // ...
                                    }
                                });
                    }else{
                        //Avisa que a senha é invalida!
                        Toast.makeText(getApplicationContext(), "Senha invalida!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //Avisa que o e-mail é invalida!
                    Toast.makeText(getApplicationContext(), "E-mail invalido ou não encontrado!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Função da chamada para tela principal, caso usuario já esteja logado em FB Authenticação;
    //Nesta função É FEITA A VERIFICAÇÃO SE O USUARIO É UM DONO OU PASSEADOR no FB;
    private void updateUI(final String email){
        //Faz uma busca no servidor pelo e-mail autenticado;
        databaseReference.child("Usuarios").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       UserModel userModelLocal = null;
                        //Se confirmado o retorno, verifica qual o tipo de userModel (Dono/Passeador)
                       if (dataSnapshot.exists()) {
                           userModelLocal = dataSnapshot.getChildren().iterator().next()
                                   .getValue(UserModel.class);

                           //Salva/Atualiza a persistencia dos dados locais (storage);
                           userViewModel.removeAuthAllUsers();

                           //Confirma a auth
                           userModelLocal.setAuth(true);

                           //Add os dados do usuário (faz um update do usuario)
                           userViewModel.addUser(userModelLocal);
                           //Seta este usuário como Auth no Firebase (Localmente);
                           userViewModel.setAuthUserEmail(email.toString());

                           if(userModelLocal.getDono() && userModelLocal.getPasseador()){
                               Intent i = new Intent(getApplicationContext(), SelectProfileActivity.class);
                               startActivity(i);
                           }else if(userModelLocal.getDono()){
                               Intent i = new Intent(getApplicationContext(), ScreenOwnerActivity.class);
                               startActivity(i);
                           }else if(userModelLocal.getPasseador()){
                               Intent i = new Intent(getApplicationContext(), ScreenWalkerActivity.class);
                               startActivity(i);
                           }
                       }else{
                           //Caso usuário não exista na base (foi removido mas a auth não!)...
                           new AlertDialog.Builder(LoginActivity.this)
                                   .setTitle("Aviso")
                                   .setMessage("Usuário não encontrado, favor \n" +
                                           "entrar em contato com o suporte técnico!")
                                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           //Desloga usuario e fecha esta janela
                                           getApplicationContext().deleteDatabase("passeia_cao_sqlite_db");
                                           FirebaseAuth.getInstance().signOut();
                                           finish();
                                       }
                                   })
                                   .show();
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {
                       Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                               Toast.LENGTH_SHORT).show();
                   }
               });
    }

}
