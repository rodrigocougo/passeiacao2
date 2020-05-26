package br.fatec.tcc.passeiacao;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

import br.fatec.tcc.passeiacao.model.HistoricalModel;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.owners.fragments.OwnersHistoricalFRG;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

public class ProfileViewSelectedActivity extends AppCompatActivity {

    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private DatabaseReference databaseReference;
    private Query fbReferenceHistoricals;

    private Toolbar myToolbar;
    private ProgressBar mProgressBar;

    private UserViewModel userViewModel;
    private UserModel userModelAuth = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_selected);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(this);
        fbReferenceHistoricals = FirebaseDatabase.getInstance().getReference().child("Historicals");

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarOwner);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            getSupportActionBar().setHomeButtonEnabled(false);      //Ativar o botão
            //getSupportActionBar().setTitle("Perfil do passeador");     //Titulo para ser exibido na sua Action Bar em frente à seta
            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.clpOwner);
            //collapsingToolbarLayout.setTitle("");
            collapsingToolbarLayout.setTitleEnabled(false);
            //getSupportActionBar ().setTitle (getResources().getString(R.string.action_settings_title));     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        mProgressBar = findViewById(R.id.progress_bar);

        //Faz o carregamento dos dados do usuario selecionado
        Intent origemIntent = getIntent();
        Bundle bundle = origemIntent.getExtras();
        final String id_walker = bundle.getString("id_walker");
        String name = bundle.getString("name");
        Integer note = bundle.getInt("note");
        Integer rating = bundle.getInt("rating");
        String address = bundle.getString("address");
        String birth = bundle.getString("birth");
        Integer performed = bundle.getInt("performed");
        Integer canceled = bundle.getInt("canceled");
        String image = bundle.getString("image");

        ImageView imageView3 = findViewById(R.id.imageView3);
        TextView lblSubTitleProfile = findViewById(R.id.lblSubTitleProfile);
        TextView lblTitleProfile = findViewById(R.id.lblTitleProfile);
        RatingBar rtbProfileOwner = findViewById(R.id.rtbProfileOwner);
        TextView lblObservation = findViewById(R.id.lblObservation);
        TextView lblAge = findViewById(R.id.lblAge);
        TextView lblPerformed = findViewById(R.id.lblPerformed);
        TextView lblCanceled = findViewById(R.id.lblCanceled);
        final Button btnInterestedYes = findViewById(R.id.btnInterestedYes);

        imageView3.setImageDrawable(null);
        lblSubTitleProfile.setText(note.toString());
        lblTitleProfile.setText(name);
        rtbProfileOwner.setRating(rating);
        lblObservation.setText(address);
        lblAge.setText(birth + " anos");
        lblPerformed.setText(performed.toString() + " realizados");
        lblCanceled.setText(canceled.toString() + " cancelados");

        btnInterestedYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnInterestedYes.getText().equals("TENHO INTERESSE")){
                    insertScheduled(id_walker);
                }else if (btnInterestedYes.getText().equals("CANCELAR INTERESSE")){
                    deleteScheduled(id_walker);
                    Toast.makeText(getApplicationContext(), "Interesse removido com sucesso!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        /***************************************************************************************/
        // Provedor Room UserModel
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        /*****************************************************************************************/
        //Observatorio USUARIO AUTH...
        userViewModel.getUserAuth().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(@Nullable UserModel muserModel) {
                userModelAuth = muserModel;
                if(userModelAuth == null) return;
                ScheduledActive();
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

    /*FUNÇÕES BACK-END*/
    private void insertScheduled(final String id_walker) {
        String id = UUID.randomUUID().toString();
        ScheduledModel mScheduledModel = new ScheduledModel();
        mScheduledModel.setId(id);
        mScheduledModel.setId_owner(userModelAuth.getId());
        mScheduledModel.setAddress_owner(userModelAuth.getBairro());
        mScheduledModel.setId_walker(id_walker);
        mScheduledModel.setSend_invitation(true);

        //Realiza o INSERT na base FB;
        databaseReference.child("Scheduleds").child(id).setValue(mScheduledModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ScheduledActive();
                        Toast.makeText(getApplicationContext(), "Interesse enviado com sucesso!",
                                Toast.LENGTH_SHORT).show();
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

    private void deleteScheduled(final String id_walker) {

        //Primeiro valida o EMAIL solicitado;
        databaseReference.child("Scheduleds").orderByChild("id_owner").equalTo(userModelAuth.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        if(scheduled.getId_walker().equals(id_walker)){
                            String childKey = dataSnapshot.getKey();
                            databaseReference.child("Scheduleds").child(scheduled.getId()).removeValue();
                            ScheduledActive();
                            //Toast.makeText(getApplicationContext(), "Interesse removido com sucesso!",
                                    //Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ScheduledActive() {
        Intent origemIntent = getIntent();
        Bundle bundle = origemIntent.getExtras();
        final String id_walker = bundle.getString("id_walker");

        final Button btnInterestedYes = findViewById(R.id.btnInterestedYes);
        databaseReference.child("Scheduleds").orderByChild("id_owner").equalTo(userModelAuth.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean verify = false;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        if(scheduled.getId_walker().equals(id_walker) && !scheduled.getCanceled_invitation() || scheduled.getDone_invitation()){
                            verify = true;
                        }
                    }
                    if(verify){
                        btnInterestedYes.setVisibility(View.VISIBLE);
                        btnInterestedYes.setText("CANCELAR INTERESSE");
                    }else{
                        btnInterestedYes.setVisibility(View.VISIBLE);
                        btnInterestedYes.setText("TENHO INTERESSE");
                    }
                }else{
                    btnInterestedYes.setVisibility(View.VISIBLE);
                    btnInterestedYes.setText("TENHO INTERESSE");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void getHistorical(String email) {
        fbReferenceHistoricals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historicalModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        HistoricalModel historical = postSnapshot.getValue(HistoricalModel.class);
                        historicalModelList.add(historical);
                    }
                }
                openFragment(OwnersHistoricalFRG.newInstance(historicalModelList));
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                openFragment(OwnersHistoricalFRG.newInstance(new ArrayList<HistoricalModel>()));
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }*/
//    #############################################################################################
}
