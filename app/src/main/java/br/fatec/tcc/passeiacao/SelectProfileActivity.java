package br.fatec.tcc.passeiacao;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

public class SelectProfileActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private firebaseService firebaseService = new firebaseService();

    private ImageButton donoButton;
    private ImageButton passeadorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_perfil);

        /*Toolbar Layout*/
        Toolbar toolbarChooseProfile = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarChooseProfile);
        setSupportActionBar(toolbarChooseProfile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //Mostrar o botão
            getSupportActionBar ().setHomeButtonEnabled (true);      //Ativar o botão
            getSupportActionBar ().setTitle ("Selecione o modelo de negócio");     //Titulo para ser exibido na sua Action Bar em frente à seta
            //getSupportActionBar ().setTitle (getResources().getString(R.string.action_settings_title));     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        donoButton = (ImageButton) findViewById(R.id.escolherDonoButton);
        donoButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

                // tipo 0 = dono
                //tipo = 0;

                // criando um bundle para informar à nova Activity que se trata de um dono de cão
                //Bundle b = new Bundle ();
                //b.putInt("tipo", tipo);

                // chamando a nova Activity
                Intent cadastrarIntent = new Intent(SelectProfileActivity.this, ScreenOwnerActivity.class);
                //cadastrarIntent.putExtras(b);
                startActivity(cadastrarIntent);
                //Toast.makeText(SelectProfileActivity.this, "SOU DONO!", Toast.LENGTH_LONG).show();
            }
        });

        passeadorButton = (ImageButton) findViewById(R.id.escolherPasseadorButton);
        passeadorButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

                // tipo 1 = passeador
                //tipo = 1;

                // criando um bundle para informar à nova Activity que se trata de um passeador
                //Bundle b = new Bundle ();
                //b.putInt("tipo", tipo);

                // chamando a nova Activity
                Intent cadastrarIntent = new Intent(SelectProfileActivity.this, ScreenWalkerActivity.class);
                //cadastrarIntent.putExtras(b);
                startActivity(cadastrarIntent);
                //Toast.makeText(SelectProfileActivity.this, "SOU DONO!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(this)
                        .setTitle("Aviso")
                        .setMessage("Deseja realmente deslogar?")
                        .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Desloga usuario e fecha esta janela
                            }
                        })
                        .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //não exclui, apenas fecha a mensagem
                                deslogarFireBase();
                                finish();
                            }
                        })
                        .show();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deslogarFireBase(){

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(this);

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) finish();

        databaseReference.child("Usuarios").orderByChild("email").equalTo(user.getEmail().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    final UserModel userModel = dataSnapshot.getChildren().iterator().next()
                            .getValue(UserModel.class);

                    //Segundo, valida senha do servidor decodificada;
                    mAuth.signInWithEmailAndPassword(userModel.getEmail().toString(), Util.DecodificarBase64(userModel.getSenha()))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Re-autentica o userModel para poder excluir;
                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential(userModel.getEmail(), userModel.getSenha());

                                        user.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d("TAG", "User re-authenticated.");
                                                        //Deleta o userModel no FB Authenticação
                                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d("TAG", "UserModel deletado com sucesso do FB (Apenas Auth)");
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                    } else {
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("EXIT", true);
                                    startActivity(intent);
                                    // ...
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
