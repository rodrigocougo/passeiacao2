package br.fatec.tcc.passeiacao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
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

import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;
import br.fatec.tcc.passeiacao.walker.fragments.WalkersScheduledFRG;
import br.fatec.tcc.passeiacao.walker.fragments.WalkersSearchFRG;
import br.fatec.tcc.passeiacao.walker.fragments.WalkersHistoricalFRG;

import static aplicacao.passeiacao.IS_OWNER;
import static aplicacao.passeiacao.IS_WALKER;
import static aplicacao.passeiacao.SET_LAT;
import static aplicacao.passeiacao.SET_LON;

public class ScreenWalkerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Query fbReferenceOwners;
    private Query fbReferenceUserUpdate = null;
    private Query fbReferenceScheduleds;

    private Button deslogarButton;
    private firebaseService firebaseService = new firebaseService();

    private Toolbar myToolbar;
    private BottomNavigationView bottomNavigationView;
    private ViewPager nViewPagerWalkers;
    private int[] etapas = new int[]{0, 1, 2};

    private UserViewModel userViewModel;
    public List<UserModel> userModelWalkersList = new ArrayList<>();
    private UserModel userModel;

    private ProgressBar mProgressBar;

    LocationManager mLocationManager;
    private static final long MIN_DISTANCE_FOR_UPDATE = 5;
    private static final long MIN_TIME_FOR_UPDATE = 1000;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_passeador);

        mProgressBar = findViewById(R.id.progress_bar);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*Toolbar Layout*/
        myToolbar = (Toolbar) findViewById(R.id.toolbarWalker);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(getSupportActionBar () != null) {
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true); //Mostrar o botão
            getSupportActionBar ().setHomeButtonEnabled (true);      //Ativar o botão
            getSupportActionBar ().setTitle ("");     //Titulo para ser exibido na sua Action Bar em frente à seta
            //getSupportActionBar ().setTitle (getResources().getString(R.string.action_settings_title));     //Titulo para ser exibido na sua Action Bar em frente à seta
        }

        /* MenuView */
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_walkers);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.menu.bottom_navigation_menu_walker);
        //badge.setVisible(true);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(this);
        fbReferenceOwners = FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("dono").equalTo(true);

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();
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
                //Instancia primeira vez
                if(userModel != null) {
                    //Verifica se já foi instanciado o Fragment para não quebrar
                    Fragment fragB = getSupportFragmentManager().findFragmentByTag("WalkersSearchFRG");
                    if (fragB == null) {
                        Fragment frg = WalkersSearchFRG.newInstance(userModel.getId());
                        openFragment(frg, "WalkersSearchFRG");
                    }
                    mProgressBar.setVisibility(View.GONE);
                }
                if(fbReferenceUserUpdate == null) {
                    fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_walker").equalTo(userModel.getId());
                    fbReferenceUserUpdate = FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("id").equalTo(userModel.getId());
                    updateUserAuth();
                    getScheduledsCalcAssessments();
                }
                getScheduledsCalcAssessments();
            }
        });
        //getOwners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fbReferenceScheduleds != null) {
            getScheduledsCalcAssessments();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_DISTANCE_FOR_UPDATE,
                    MIN_TIME_FOR_UPDATE, mLocationListener);
        }
    }

    private void getScheduledsCalcAssessments() {
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    List<Integer> notes = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        if (scheduled.getAssessment_date_walker() != "") {
                            //Calcula a nota do usuario atual;
                            int valor = (int)scheduled.getAssessment_note_walker();
                            if(valor > 0){
                                notes.add(valor);
                            }
                        }
                    }
                    final double noteFinal = Util.getCalcNotes(notes);
                    /*#######################################################################*/
                    databaseReference.child("Usuarios").orderByChild("id").equalTo(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    UserModel userModel = dataSnapshot.getChildren().iterator().next()
                                            .getValue(UserModel.class);
                                    userModel.setNote(noteFinal);
                                    snapshot.getRef().setValue(userModel);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    /*#######################################################################*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.navigation_search_owners:
                //getOwners();
                mProgressBar.setVisibility(View.GONE);
                openFragment(WalkersSearchFRG.newInstance(userModel.getId()), "WalkersSearchFRG");
                return true;
            case R.id.navigation_scheduled_2:
                mProgressBar.setVisibility(View.GONE);
                openFragment(WalkersScheduledFRG.newInstance(userModel.getId(), this), "WalkersScheduledFRG");
                return true;
            case R.id.navigation_tours:
                //getScheduleds("");
                mProgressBar.setVisibility(View.GONE);
                openFragment(WalkersHistoricalFRG.newInstance(userModel.getId()), "WalkersHistoricalFRG");
                return true;
      }

        return false;
    }

    public void openFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.replace(R.id.nav_host_fragment_walker, fragment);
        transaction.replace(R.id.nav_host_fragment_walker, fragment, tag);
        transaction.addToBackStack(null);
        //transaction.commit();
        transaction.commitAllowingStateLoss();
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
                if(userModel == null) return false;
                // criando um bundle para informar à nova Activity que se trata de um dono de cão
                b.putInt("tipo", tipo);
                b.putString("id_user_auth", userModel.getId());
                // chamando a nova Activity
                intent = new Intent(this, RegisterUserActivity.class);
                intent.putExtra("id_user_auth", userModel.getId());
                startActivity(intent);
                return false;
            case R.id.action_assessments:
                // criando um bundle para informar à nova Activity que se trata de um dono de cão
                b.putInt("tipo", IS_WALKER);
                // chamando a nova Activity
                intent = new Intent(getApplication(), AssessmentsActivity.class);
                intent.putExtras(b);
                intent.putExtra("id_user_auth", userModel.getId());
                startActivity(intent);
                return true;
            case R.id.action_logoff:
                deslogarFireBase();
                userViewModel.removeAuthAllUsers();
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deslogarFireBase(){

        userViewModel.removeAuthAllUsers();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;

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
                                                                    //finish();
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

    public void loadProfileOwner (UserModel userModel) {
        TextView lblTitleProfile = findViewById(R.id.lblTitleProfile);
        TextView lblSubTitleProfile = findViewById(R.id.lblSubTitleProfile);
        RatingBar rtbProfileOwner = findViewById(R.id.rtbProfileWalker);
        TextView lblObservation = findViewById(R.id.lblObservation);
        TextView lblPerformed = findViewById(R.id.lblPerformed);
        TextView lblCanceled = findViewById(R.id.lblCanceled);
        TextView lblAge = findViewById(R.id.lblAge);
        ImageView imgCover = findViewById(R.id.imageView2);
        SimpleDraweeView imgAVatar = findViewById(R.id.imageView3);
        if(URLUtil.isValidUrl(userModel.getImageAvatar())) {
            imgAVatar.setImageURI(Uri.parse(userModel.getImageAvatar()));
        }
        if(URLUtil.isValidUrl(userModel.getImageCover())) {
            imgCover.setImageURI(Uri.parse(userModel.getImageCover()));
        }

        lblTitleProfile.setText(userModel.getNome());
        lblSubTitleProfile.setText(String.valueOf(userModel.getNote()));
        rtbProfileOwner.setRating((float) userModel.getNoteUserConverter());
        rtbProfileOwner.setIsIndicator(true);
        lblObservation.setText(userModel.getBairro());
        lblPerformed.setText(userModel.getConcluded() + " realizados");
        lblCanceled.setText(userModel.getCanceled() + " cancelados");
        lblAge.setText(userModel.getNascConvertAge() + " Anos");
    }

    private void updateUserAuth() {
        fbReferenceUserUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModelLocal = null;
                if (dataSnapshot.exists()) {
                    userModelLocal = dataSnapshot.getChildren().iterator().next()
                            .getValue(UserModel.class);

                    //Confirma a auth
                    userModelLocal.setAuth(true);

                    //Add os dados do usuário (faz um update do usuario)
                    userViewModel.addUser(userModelLocal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            SET_LON = location.getLongitude();
            SET_LAT = location.getLatitude();
            setUpdateGeoLocation(SET_LAT, SET_LON);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private void setUpdateGeoLocation(final double Lat, final double Lon) {
        userModel.setLongitude(Lon);
        userModel.setLatitude(Lat);
        if(fbReferenceUserUpdate == null) return;
        fbReferenceUserUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel userModelLocal = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        userModel.setLatitude(Lat);
                        userModel.setLongitude(Lon);
                        snapshot.getRef().setValue(userModel);
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

}
