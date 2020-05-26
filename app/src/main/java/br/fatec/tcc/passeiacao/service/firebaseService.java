package br.fatec.tcc.passeiacao.service;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class firebaseService {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    /* Função de inicialização do servidor Firebase (Não esqueça de criar a base
     * em seu dominio no console do Firebase e mudar o arquivo*/
    public DatabaseReference inicializaFireBase(Context context){
        FirebaseApp.initializeApp(context);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        return databaseReference;
    }
}
