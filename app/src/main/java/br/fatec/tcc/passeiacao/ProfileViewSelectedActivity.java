package br.fatec.tcc.passeiacao;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.fatec.tcc.passeiacao.adapters.AssessmentsADP;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickDogsForProfileView;
import br.fatec.tcc.passeiacao.model.AssessmentsModel;
import br.fatec.tcc.passeiacao.model.DogModel;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.owners.fragments.OwnersDogsListFRG;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

import static aplicacao.passeiacao.IS_DOG_PROFILE;
import static aplicacao.passeiacao.IS_INSERT;
import static aplicacao.passeiacao.IS_REMOVE;

public class ProfileViewSelectedActivity extends AppCompatActivity implements InterfaceClickDogsForProfileView {

    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private DatabaseReference databaseReference;
    private Query fbReferenceScheduleds;
    private Query fbReferenceDogs;

    private Toolbar myToolbar;
    private ProgressBar mProgressBar;

    private UserViewModel userViewModel;
    private UserModel userModelAuth = null;

    private AssessmentsADP mAssessmentsADP;
    private ArrayList<AssessmentsModel> mAssessmentsModel = new ArrayList<AssessmentsModel> ();
    private List<DogModel> dogModelList = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    public Button btnInterestedYes;
    public Fragment fragmentSelceted;
    public OwnersDogsListFRG fragmentSelcetedDogs;
    public RecyclerView rcv_list_assessments;
    public Boolean is_invitation = true;

    private Boolean flagActive = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view_selected);

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

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getApplicationContext());

        //FrameLayout nav_host_fragment = findViewById(R.id.nav_host_fragment);
        constraintLayout = findViewById(R.id.constraintLayout);
        mProgressBar = findViewById(R.id.progress_bar);
        rcv_list_assessments = findViewById(R.id.rcv_list_assessments);
        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rcv_list_assessments.setLayoutManager(llmCat);
        mAssessmentsADP = new AssessmentsADP((ArrayList<Object>)(List<?>) mAssessmentsModel, 1);
        rcv_list_assessments.setAdapter(mAssessmentsADP);
        rcv_list_assessments.setVisibility(View.VISIBLE);

        //Faz o carregamento dos dados do usuario selecionado
        Intent origemIntent = getIntent();
        Bundle bundle = origemIntent.getExtras();
        final String id_walker = bundle.getString("id_walker");
        final String name = bundle.getString("name");
        Integer note = bundle.getInt("note");
        Integer rating = bundle.getInt("rating");
        final String address = bundle.getString("address");
        String birth = bundle.getString("birth");
        Integer performed = bundle.getInt("performed");
        Integer canceled = bundle.getInt("canceled");
        final String image = bundle.getString("image");
        is_invitation = bundle.getBoolean("is_invitation", true);

        ImageView imageView3 = findViewById(R.id.imageView3);
        if(URLUtil.isValidUrl(image)) {
            imageView3.setImageURI(Uri.parse(image));
        }
        TextView lblSubTitleProfile = findViewById(R.id.lblSubTitleProfile);
        TextView lblTitleProfile = findViewById(R.id.lblTitleProfile);
        RatingBar rtbProfileOwner = findViewById(R.id.rtbProfileOwner);
        TextView lblObservation = findViewById(R.id.lblObservation);
        TextView lblAge = findViewById(R.id.lblAge);
        TextView lblPerformed = findViewById(R.id.lblPerformed);
        TextView lblCanceled = findViewById(R.id.lblCanceled);
        btnInterestedYes = findViewById(R.id.btnInterestedYes);

        //imageView3.setImageDrawable(null);
        lblSubTitleProfile.setText(rating.toString());
        lblTitleProfile.setText(name);
        rtbProfileOwner.setRating(rating);
        lblObservation.setText(address);
        lblAge.setText(birth + " anos");
        lblPerformed.setText(performed.toString() + " realizados");
        lblCanceled.setText(canceled.toString() + " cancelados");

        if(!is_invitation){
            btnInterestedYes.setVisibility(View.INVISIBLE);
        }

        btnInterestedYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flagActive) {
                    flagActive = true;
                    if (btnInterestedYes.getText().equals("TENHO INTERESSE")) {
                        getDogUser(id_walker, name, address);
                    } else if (btnInterestedYes.getText().equals("CANCELAR INTERESSE")) {
                        deleteScheduled(id_walker);
                        Toast.makeText(getApplicationContext(), "Interesse removido com sucesso!",
                                Toast.LENGTH_SHORT).show();
                    } else if (btnInterestedYes.getText().equals("CANCELAR")) {
                        //getFragmentManager().popBackStack("OwnersDogsList", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.remove(fragmentSelceted).commit();
                        btnInterestedYes.setText("TENHO INTERESSE");
                        flagActive = false;
                        rcv_list_assessments.setVisibility(View.VISIBLE);
                    } else if (btnInterestedYes.getText().equals("ENVIAR INTERESSE")){
                        insertScheduled(id_walker, name, address, image);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.remove(fragmentSelceted).commit();
                        flagActive = false;
                        finish();
                    }
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
                fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_walker").equalTo(id_walker);
                getScheduledsAssessments(userModelAuth.getId());
                fbReferenceDogs = FirebaseDatabase.getInstance().getReference().child("Dogs").orderByChild("id_user").equalTo(userModelAuth.getId());
                //getDogUser();
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

    public void setDogForInvitation(final String id_walker, String name, String address){
        flagActive = false;
        btnInterestedYes.setText("CANCELAR");
        //constraintLayout.setVisibility(View.INVISIBLE);
        fragmentSelcetedDogs = OwnersDogsListFRG.newInstance(userModelAuth.getId(), IS_DOG_PROFILE);
        fragmentSelcetedDogs.setInterfaceClickDogsForProfileView(this);
        openFragment(fragmentSelcetedDogs, "OwnersDogsList");
        //insertScheduled(id_walker, name, address);
    }
    public void openFragment(Fragment fragment, String tag) {
        rcv_list_assessments.setVisibility(View.INVISIBLE);
        fragmentSelceted = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.constraintLayout, fragmentSelceted, tag);
        transaction.addToBackStack(null);
        transaction.commit();
        //transaction.commitAllowingStateLoss();
    }
    //    #############################################################################################
    /*FUNÇÕES BACK-END*/
    private void insertScheduled(final String id_walker, String name, String address, String image) {
        String id = UUID.randomUUID().toString();
        ScheduledModel mScheduledModel = new ScheduledModel();
        mScheduledModel.setId(id);
        mScheduledModel.setId_owner(userModelAuth.getId());
        mScheduledModel.setAddress_owner(userModelAuth.getBairro());
        mScheduledModel.setId_walker(id_walker);
        mScheduledModel.setTitle_owner(userModelAuth.getNome());
        mScheduledModel.setAddress_walker(address);
        mScheduledModel.setTitle_walker(name);
        mScheduledModel.setSend_invitation(true);
        mScheduledModel.setImage_owner(userModelAuth.getImageAvatar());
        mScheduledModel.setImage_walker(image);

        //Realiza o INSERT na base FB;
        databaseReference.child("Scheduleds").child(id).setValue(mScheduledModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ScheduledActive();
                        Toast.makeText(getApplicationContext(), "Interesse enviado com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        flagActive = false;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("INSERT", "failure: ", e.getCause());
                        Toast.makeText(getApplicationContext(), "Falha no INSERT: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        flagActive = false;
                    }
                });

        databaseReference.child("ScheduledsDogs").child(id).setValue(dogModelList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //ScheduledActive();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("INSERT", "failure: ", e.getCause());
                        flagActive = false;
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
                            databaseReference.child("ScheduledsDogs").child(scheduled.getId()).removeValue();
                            ScheduledActive();
                            flagActive = false;
                            //Toast.makeText(getApplicationContext(), "Interesse removido com sucesso!",
                                    //Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                flagActive = false;
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
                        if(scheduled.getId_walker().equals(id_walker) && !scheduled.getCanceled_invitation() && !scheduled.getDone_invitation()){
                            verify = true;
                        }
                    }
                    if(verify && is_invitation) {
                        btnInterestedYes.setVisibility(View.VISIBLE);
                        btnInterestedYes.setText("CANCELAR INTERESSE");
                    }else if(!is_invitation) {
                        btnInterestedYes.setVisibility(View.INVISIBLE);
                    }else{
                        btnInterestedYes.setVisibility(View.VISIBLE);
                        btnInterestedYes.setText("TENHO INTERESSE");
                    }
                }else{
                    if(!is_invitation) {
                        btnInterestedYes.setVisibility(View.INVISIBLE);
                    } else {
                        btnInterestedYes.setVisibility(View.VISIBLE);
                        btnInterestedYes.setText("TENHO INTERESSE");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
            }
        });
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
                            AssessmentsModel assessmentsModel = new AssessmentsModel(
                                    scheduled.getId(),
                                    scheduled.getId_owner(),
                                    "",
                                    scheduled.getTitle_owner(),
                                    scheduled.getAssessment_note_owner(),
                                    scheduled.getAssessment_message_owner(),
                                    scheduled.getAssessment_date_owner()
                            );
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

    private void getDogUser(final String id_walker, final String name, final String address){
        fbReferenceDogs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dogModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        setDogForInvitation(id_walker, name, address);
                    //}
                } else {
                    Toast.makeText(getApplicationContext(), "Cadastre um Dog para prosseguir! ",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /*Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    public void onClickListenerDogCheckForProfile(Object selected, Integer method) {
        if(method == IS_INSERT){
            btnInterestedYes.setText("ENVIAR INTERESSE");
            dogModelList.add((DogModel) selected);
        }else if(method == IS_REMOVE){
            dogModelList.remove((DogModel) selected);
            if(dogModelList.size() > 0){
                btnInterestedYes.setText("ENVIAR INTERESSE");
            }else{
                btnInterestedYes.setText("CANCELAR");
            }
        }
    }
    //    #############################################################################################
}
