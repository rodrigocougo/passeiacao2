package br.fatec.tcc.passeiacao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RememberPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_password);

        Toolbar toolbarRecoverPassword = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbarRecoverPassword);
        ImageView imvIconeRecoveryPassword = findViewById(R.id.imvIconeRecoveryPassword);
        TextView txvValueRecoveyPassword = findViewById(R.id.txvValueRecoveyPassword);
        Button btnRecoveryPassword = findViewById(R.id.btnRecoveryPassword);

        /*Toolbar Layout*/
        setSupportActionBar(toolbarRecoverPassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //Mostrar o botão
            getSupportActionBar ().setHomeButtonEnabled (true);      //Ativar o botão
            getSupportActionBar ().setTitle ("sair da Recuperação da senha");     //Titulo para ser exibido na sua Action Bar em frente à seta
        }
    }
}
