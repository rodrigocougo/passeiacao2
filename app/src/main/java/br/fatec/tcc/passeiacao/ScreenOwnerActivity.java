package br.fatec.tcc.passeiacao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.model.DogModel;
import br.fatec.tcc.passeiacao.model.HistoricalModel;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.owners.fragments.OwnersDogsListFRG;
import br.fatec.tcc.passeiacao.owners.fragments.OwnersHistoricalFRG;
import br.fatec.tcc.passeiacao.owners.fragments.OwnersScheduledFRG;
import br.fatec.tcc.passeiacao.owners.fragments.OwnersSearchFRG;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

public class ScreenOwnerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query fbReferenceWalkers;
    private Query fbReferenceHistoricals;
    private FirebaseAuth mAuth;

    private Button deslogarButton;
    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();

    private Toolbar myToolbar;
    private BottomNavigationView bottomNavigationView;
    private ViewPager nViewPagerOwner;
    private int[] etapas = new int[]{0, 1, 2, 3};

    private UserViewModel userViewModel;

    private UserModel userModel;
    public List<UserModel> userWalkersModelList = new ArrayList<>();
    private List<DogModel> dogModelList;
    private List<ScheduledModel> scheduledModelList;
    private List<HistoricalModel> historicalModelList;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_dono);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(this);
        fbReferenceHistoricals = FirebaseDatabase.getInstance().getReference().child("Historicals");

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarOwner);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //Mostrar o botão
            getSupportActionBar ().setHomeButtonEnabled (true);      //Ativar o botão
            getSupportActionBar ().setTitle ("");     //Titulo para ser exibido na sua Action Bar em frente à seta
            //getSupportActionBar ().setTitle (getResources().getString(R.string.action_settings_title));     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        mProgressBar = findViewById(R.id.progress_bar);

        /* MenuView */
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.navigation_search);
        //BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(item.getItemId());
        //badge.setNumber(3);

        /* ViewPager */
        //nViewPagerOwner = findViewById(R.id.viewPagerOwner);
        //nViewPagerOwner.beginFakeDrag();
        //mOwnersFPA = new OwnersFPA(getSupportFragmentManager (), etapas, new ArrayList<UserModel>());
        //nViewPagerOwner.setAdapter(mOwnersFPA);
        /*nViewPagerOwner.setAdapter(new OwnersFPA(getSupportFragmentManager (), etapas, userWalkersModelList));
        nViewPagerOwner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });*/

        /*if (savedInstanceState == null) {
            onNavigationItemSelected(bottomNavigationView.getMenu().findItem(R.id.navigation_search));
        }*/

        /***************************************************************************************/
        // Provedor Room UserModel
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        /*****************************************************************************************/
        //Observatorio das PRODUTOS DO PEDIDO...
        userViewModel.getUserAuth().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(@Nullable UserModel muserModel) {
                userModel = muserModel;
                if(userModel == null) return;
                /***************************************************************************************/
                // Carrega os dados do usuario logado
                loadProfileOwner(userModel);
                mProgressBar.setVisibility(View.GONE);
                openFragment(OwnersSearchFRG.newInstance(userModel.getId()), "OwnersSearch");
            }
        });
    }

    public void openFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment, tag);
        transaction.addToBackStack(null);
        //transaction.commit();
        transaction.commitAllowingStateLoss();
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_search:
                mProgressBar.setVisibility(View.GONE);
                openFragment(OwnersSearchFRG.newInstance(userModel.getId()), "OwnersSearch");
                return true;
            case R.id.navigation_dog_list:
                mProgressBar.setVisibility(View.GONE);
                openFragment(OwnersDogsListFRG.newInstance(userModel.getId()), "OwnersDogsList");
                return true;
            case R.id.navigation_scheduled:
                //getScheduleds("");
                mProgressBar.setVisibility(View.GONE);
                openFragment(OwnersScheduledFRG.newInstance(userModel.getId()), "OwnersScheduled");
                return true;
            case R.id.navigation_historical:
                //getHistorical("");
                mProgressBar.setVisibility(View.GONE);
                openFragment(OwnersHistoricalFRG.newInstance(userModel.getId()), "OwnersHistorical");
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_walker_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer tipo = 1;
        Bundle b = new Bundle ();
        Intent intent;

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            case R.id.action_settings:
                // criando um bundle para informar à nova Activity que se trata de um dono de cão
                b.putInt("tipo", tipo);
                b.putString("id_user_auth", userModel.getId());
                // chamando a nova Activity
                intent = new Intent(getApplication(), RegisterUserActivity.class);
                intent.putExtra("id_user_auth", userModel.getId());
                startActivity(intent);
                return true;
            case R.id.action_assessments:
                // criando um bundle para informar à nova Activity que se trata de um dono de cão
                b.putInt("tipo", tipo);
                // chamando a nova Activity
                intent = new Intent(getApplication(), AssessmentsActivity.class);
                intent.putExtras(b);
                startActivity(intent);
                return true;
            case R.id.action_logoff:
                deslogarFireBase();
                userViewModel.removeAuthAllUsers();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadProfileOwner (UserModel userModel) {
        TextView lblTitleProfile = findViewById(R.id.lblTitleProfile);
        TextView lblSubTitleProfile = findViewById(R.id.lblSubTitleProfile);
        RatingBar rtbProfileOwner = findViewById(R.id.rtbProfileOwner);
        TextView lblObservation = findViewById(R.id.lblObservation);
        TextView lblPerformed = findViewById(R.id.lblPerformed);
        TextView lblCanceled = findViewById(R.id.lblCanceled);
        TextView lblAge = findViewById(R.id.lblAge);
        //ImageView imageView3 = findViewById(R.id.imageView3);

        lblTitleProfile.setText(userModel.getNome());
        lblSubTitleProfile.setText(String.valueOf(userModel.getNote()));
        rtbProfileOwner.setRating(userModel.getNoteUserConverter());
        rtbProfileOwner.setIsIndicator(true);
        lblObservation.setText(userModel.getBairro());
        lblPerformed.setText(userModel.getConcluded() + " realizados");
        lblCanceled.setText(userModel.getCanceled() + " cancelados");
        lblAge.setText(userModel.getNascConvertAge() + " Anos");
    }

    private void deslogarFireBase(){

        userViewModel.removeAuthAllUsers();

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(this);

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
                                    //finish();
                                    //System.exit(0);
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

    private void getHistorical(String email) {
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
                    openFragment(OwnersHistoricalFRG.newInstance(""), "OwnersHistorical");
                    mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                openFragment(OwnersHistoricalFRG.newInstance(""), "OwnersHistorical");
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
