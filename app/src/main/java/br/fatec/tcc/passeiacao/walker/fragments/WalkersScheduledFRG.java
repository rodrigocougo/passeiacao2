package br.fatec.tcc.passeiacao.walker.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.ScreenWalkerActivity;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickScheduledWalkersFA;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.service.firebaseService;
import br.fatec.tcc.passeiacao.walker.adapters.WalkersADP;

import static android.content.Context.LOCATION_SERVICE;
import static aplicacao.passeiacao.IS_SCHEDULED;
import static aplicacao.passeiacao.SET_LAT;
import static aplicacao.passeiacao.SET_LON;
import static com.facebook.FacebookSdk.getApplicationContext;

public class WalkersScheduledFRG extends Fragment implements InterfaceClickScheduledWalkersFA {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private Query fbReferenceScheduleds;
    private Query fbReferenceUserUpdate = null;

    private RecyclerView mRecyclerViewListScheduleds;
    private WalkersADP mWalkersADP;
    private List<ScheduledModel> scheduledModelList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private ProgressDialog mDialog;
    private TextView txvNotData;
    public static String id_user_auth;
    private static Object selected;
    private static Context context;

    LocationManager mLocationManager;
    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public WalkersScheduledFRG() {
    }

    // TODO: Rename and change types and number of parameters
    public static WalkersScheduledFRG newInstance(String id_user, Context c) {
        WalkersScheduledFRG fragment = new WalkersScheduledFRG();
        fragment.id_user_auth = id_user;
        context = c;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_search, container, false);

        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getContext());
        fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_walker").equalTo(id_user_auth);
        fbReferenceUserUpdate = FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("id").equalTo(id_user_auth);

        mRecyclerViewListScheduleds = v.findViewById(R.id.rcvListSearch);
        mRecyclerViewListScheduleds.setHasFixedSize(true);

        mProgressBar = v.findViewById(R.id.progress_bar2);
        txvNotData = v.findViewById(R.id.txvNotData);

        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerViewListScheduleds.setLayoutManager(llmCat);
        mWalkersADP = new WalkersADP((ArrayList<Object>) (List<?>) scheduledModelList, IS_SCHEDULED, getActivity());
        mWalkersADP.setInterfaceClickScheduledWalkersFA(this);
        mRecyclerViewListScheduleds.setAdapter(mWalkersADP);

        getScheduleds();

        return v;
    }

    private void getScheduleds() {
        mProgressBar.setVisibility(View.VISIBLE);
        txvNotData.setVisibility(View.GONE);
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        /*#######################################################################*/
                        //Verifica o status do agendamento
                        if (scheduled.getConfirmed_invitation() == true && !scheduled.getConfirmed_done_closed_walker()) {
                            databaseReference.child("Usuarios").orderByChild("id").equalTo(scheduled.getId_owner()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserModel userModel = dataSnapshot.getChildren().iterator().next()
                                                .getValue(UserModel.class);
                                        //scheduled.setTitle_walker(userModel.get);
                                        scheduled.setTitle_walker(userModel.getNome());
                                        scheduled.setAddress_walker(userModel.getBairro());
                                    }
                                    //scheduledModelList.add(scheduled);
                                    mWalkersADP.addRegisterScheduled(scheduled);
                                    mProgressBar.setVisibility(View.GONE);
                                    txvNotData.setVisibility(View.GONE);
                                    //mWalkersADP.notifyDataSetChanged();
                                    //openFragment(OwnersScheduledFRG.newInstance(scheduledModelList));
                                    mRecyclerViewListScheduleds.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressBar.setVisibility(View.GONE);
                                    txvNotData.setVisibility(View.VISIBLE);
                                }
                            });
                            /*#######################################################################*/
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            txvNotData.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    txvNotData.setVisibility(View.VISIBLE);
                    mRecyclerViewListScheduleds.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                txvNotData.setVisibility(View.VISIBLE);
                //openFragment(OwnersScheduledFRG.newInstance(new ArrayList<ScheduledModel>()));
                //mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClickListenerScheduledDenied(final Object selected) {
        new AlertDialog.Builder(getContext())
                .setTitle("Você realmente deseja cancelar este passeio?")
                .setMessage("Ao cancelar este passeio, será\n" +
                        "enviado uma notificação ao \n" +
                        "dono, e será marcado em seu\n" +
                        "perfil um cancelamento de \n" +
                        "serviço.")
                .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Desloga usuario e fecha esta janela
                    }
                })
                .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
                        setCanceledUserCountMore();
                        setCanceledInvitation(mDataSetTemp);
                    }
                })
                .show();
    }

    @Override
    public void onClickListenerScheduledConfirmed(final Object selected) {
        /*ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);*/
        this.selected = selected;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager service = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

                    // Verifica se o GPS está ativo
                    boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    // Caso não esteja ativo abre um novo diálogo com as configurações para
                    // realizar se ativamento
                    if (!enabled) {
                        /*Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);*/
                        createNoGpsDialog();
                    } else {
                        verificationLocationForInvitation();
                    }
                    /*Toast.makeText(getContext(),
                            "Atenção! Ative o Local...",Toast.LENGTH_LONG).show();*/

                } else {
                    Toast.makeText(getContext(),
                            "Permissão de Localização Negada, ...:(", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void createNoGpsDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog mNoGpsDialog = builder.setMessage("Por favor ative seu GPS para iniciar o passeio e informar sua localização ao dono!")
                .setPositiveButton("Ativar", dialogClickListener)
                .create();
        mNoGpsDialog.show();

    }

    //Faz a verificação se usuario tem lon & lat
    private void verificationLocationForInvitation() {
        databaseReference.child("Usuarios").orderByChild("id").equalTo(id_user_auth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel.getLongitude() != 0 && userModel.getLatitude() != 0) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Iniciar este passeio?")
                                    .setMessage("Ao iniciar este passeio, aparecerá \n" +
                                            "como liberado para iniciar pelo \n" +
                                            "dono do passeio, após ambos \n" +
                                            "iniciar, sua localização ficará \n" +
                                            "disponível para o dono,")
                                    .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Desloga usuario e fecha esta janela
                                        }
                                    })
                                    .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
                                            setInitiationInvitation(mDataSetTemp);
                                        }
                                    })
                                    .show();
                        } else {
                            mDialog = new ProgressDialog(getContext());
                            mDialog.setTitle("Aguarde");
                            mDialog.setMessage("Carregando localização...");
                            mDialog.setCancelable(true);
                            mDialog.show();
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_DISTANCE_FOR_UPDATE,
                                    MIN_TIME_FOR_UPDATE, mLocationListener);
                        }
                        //userViewModel.addUser(userModel);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        if ((mDialog != null) && ( mDialog.isShowing())){
                            mDialog.dismiss();
                            mDialog = null;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                if ((mDialog != null) && ( mDialog.isShowing())){
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        });
    }

    @Override
    public void onClickListenerScheduledCard(Object selected) {

    }

    @Override
    public void onClickListenerScheduledClosed(final Object selected) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar encerramento deste passeio?")
                .setMessage("Ao confirmar o encerramento deste passeio, ele \n" +
                        "passará para o seu histórico de \n" +
                        "passeios.")
                .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Desloga usuario e fecha esta janela
                    }
                })
                .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
                        setConfirmedClosed(mDataSetTemp);
                    }
                })
                .show();
    }

    //Operações Firebase
    private void setInitiationInvitation (final ScheduledModel mDataSetTemp){
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString())) {
                            scheduled.setInitiated_invitation(true);
                            postSnapshot.getRef().setValue(scheduled);
                            mWalkersADP.addRegisterScheduled(scheduled);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setCanceledInvitation (final ScheduledModel mDataSetTemp){
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString())) {
                            scheduled.setCanceled_invitation(true);
                            scheduled.setConfirmed_done_closed_owner(true);
                            scheduled.setConfirmed_done_closed_walker(true);
                            postSnapshot.getRef().setValue(scheduled);
                            //mWalkersADP.removeRegisterScheduled(scheduled);

                            //não exclui, apenas fecha a mensagem
                            /*Toast.makeText(getApplicationContext(), "Passeio liberado para \n" +
                                            "ser iniciado pelo dono do \n" +
                                            "passeio!",
                                    Toast.LENGTH_SHORT).show();*/
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setCanceledUserCountMore (){
        databaseReference.child("Usuarios").orderByChild("id").equalTo(id_user_auth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        userModel.setCanceled(userModel.getCanceled() + 1);
                        snapshot.getRef().setValue(userModel);
                        //userViewModel.addUser(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setConfirmedClosed(final ScheduledModel mDataSetTemp) {
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString()) && scheduled.getInitiated_invitation()) {
                            scheduled.setConfirmed_done_closed_walker(true);
                            postSnapshot.getRef().setValue(scheduled);
                            mWalkersADP.removeRegisterScheduled(scheduled);
                            mWalkersADP.notifyDataSetChanged();
                            //não exclui, apenas fecha a mensagem
                            Toast.makeText(getApplicationContext(), "Agendamento movido para o histórico com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

