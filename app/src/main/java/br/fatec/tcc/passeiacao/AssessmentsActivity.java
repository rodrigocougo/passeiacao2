package br.fatec.tcc.passeiacao;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.adapters.AssessmentsADP;
import br.fatec.tcc.passeiacao.model.AssessmentsModel;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

import static aplicacao.passeiacao.IS_OWNER;
import static aplicacao.passeiacao.IS_WALKER;

public class AssessmentsActivity extends AppCompatActivity {

    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private DatabaseReference databaseReference;
    private Query fbReferenceScheduleds;

    private ProgressBar mProgressBar;
    private UserModel userModelAuth = null;

    private Toolbar myToolbar;
    private UserViewModel userViewModel;

    private RecyclerView mRecyclerViewListAssessments;
    private AssessmentsADP mAssessmentsADP;

    private String id_user_firebase = "";
    private int tipo = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments);

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarComments);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            getSupportActionBar().setTitle("Minhas avaliações");     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        //Faz o carregamento dos dados do usuario selecionado
        Intent origemIntent = getIntent();
        Bundle bundle = origemIntent.getExtras();
        id_user_firebase = bundle.getString("id_user_auth", "");
        tipo = bundle.getInt("tipo", -1);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getApplicationContext());

        //FrameLayout nav_host_fragment = findViewById(R.id.nav_host_fragment);
        mProgressBar = findViewById(R.id.progress_bar);

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarComments);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //Mostrar o botão
            getSupportActionBar ().setHomeButtonEnabled (true);      //Ativar o botão
            getSupportActionBar ().setTitle ("");     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        mRecyclerViewListAssessments = findViewById(R.id.rcvListComments);
        mRecyclerViewListAssessments.setHasFixedSize(true);
        ArrayList<AssessmentsModel> mAssessmentsModel = new ArrayList<AssessmentsModel> ();
        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerViewListAssessments.setLayoutManager(llmCat);
        mAssessmentsADP = new AssessmentsADP((ArrayList<Object>)(List<?>) mAssessmentsModel, 1);
        mRecyclerViewListAssessments.setAdapter(mAssessmentsADP);

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
                if(tipo == IS_OWNER){
                    fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_owner").equalTo(id_user_firebase);
                }else if(tipo == IS_WALKER){
                    fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_walker").equalTo(id_user_firebase);
                }
                getScheduledsAssessments(userModelAuth.getId());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
            switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getScheduledsAssessments(String idUserLogin) {
        mProgressBar.setVisibility(View.VISIBLE);
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer x = 1;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        if(scheduled.getAssessment_date_owner() != null){
                            AssessmentsModel assessmentsModel = null;
                            if(tipo == IS_OWNER){
                                assessmentsModel = new AssessmentsModel(
                                        scheduled.getId(),
                                        scheduled.getId_walker(),
                                        "",
                                        scheduled.getTitle_walker(),
                                        scheduled.getAssessment_note_walker(),
                                        scheduled.getAssessment_message_walker(),
                                        scheduled.getAssessment_date_walker()
                                );
                            }else if(tipo == IS_WALKER){
                                assessmentsModel = new AssessmentsModel(
                                        scheduled.getId(),
                                        scheduled.getId_owner(),
                                        "",
                                        scheduled.getTitle_owner(),
                                        scheduled.getAssessment_note_owner(),
                                        scheduled.getAssessment_message_owner(),
                                        scheduled.getAssessment_date_owner()
                                );
                            }
                            mAssessmentsADP.addRegisterAssessment(assessmentsModel);
                        }
                        x++;
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                //openFragment(OwnersScheduledFRG.newInstance(new ArrayList<ScheduledModel>()));
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
//    #############################################################################################
}
