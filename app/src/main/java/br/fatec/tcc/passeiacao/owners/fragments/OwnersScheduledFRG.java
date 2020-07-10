package br.fatec.tcc.passeiacao.owners.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
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
import java.util.zip.Inflater;

import br.fatec.tcc.passeiacao.MapsActivity;
import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickHistoricalFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickScheduledFA;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.owners.adapters.OwnersADP;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

import static aplicacao.passeiacao.IS_SCHEDULED;
import static com.facebook.FacebookSdk.getApplicationContext;

public class OwnersScheduledFRG extends Fragment implements InterfaceClickScheduledFA {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private Query fbReferenceScheduleds;

    private RecyclerView mRecyclerViewListScheduleds;
    private OwnersADP mOwnersADP;
    private List<ScheduledModel> scheduledModelList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView txvNotData;
    private UserViewModel userViewModel;

    private static String idUserFirebase = "";

    public OwnersScheduledFRG() {
    }

    // TODO: Rename and change types and number of parameters
    public static OwnersScheduledFRG newInstance(String midUserFirebase) {
        OwnersScheduledFRG fragment = new OwnersScheduledFRG();
        idUserFirebase = midUserFirebase;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_search, container, false);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getContext());
        fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_owner").equalTo(idUserFirebase);

        mRecyclerViewListScheduleds = v.findViewById(R.id.rcvListSearch);
        mRecyclerViewListScheduleds.setHasFixedSize(true);

        mProgressBar = v.findViewById(R.id.progress_bar2);
        txvNotData = v.findViewById(R.id.txvNotData);

        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerViewListScheduleds.setLayoutManager(llmCat);
        mOwnersADP = new OwnersADP((ArrayList<Object>) (List<?>) scheduledModelList, IS_SCHEDULED);
        mOwnersADP.setmInterfaceClickScheduledFA(this);
        mRecyclerViewListScheduleds.setAdapter(mOwnersADP);

        /***************************************************************************************/
        // Provedor Room UserModel
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        getScheduleds();

        return v;
    }

    private void getScheduleds() {
        mProgressBar.setVisibility(View.VISIBLE);
        txvNotData.setVisibility(View.INVISIBLE);
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        if (!scheduled.getConfirmed_done_closed_owner()) {
                            /*#######################################################################*/
                            databaseReference.child("Usuarios").orderByChild("id").equalTo(scheduled.getId_walker()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserModel userModel = dataSnapshot.getChildren().iterator().next()
                                                .getValue(UserModel.class);
                                        //scheduled.setTitle_walker(userModel.get);
                                        scheduled.setTitle_walker(userModel.getNome());
                                        scheduled.setAddress_walker(userModel.getBairro());
                                    }
                                    if(scheduled.getCanceled_invitation()){
                                        mOwnersADP.removeRegisterScheduled(scheduled);
                                    } else {
                                        mOwnersADP.addRegisterScheduled(scheduled);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    txvNotData.setVisibility(View.VISIBLE);
                                    txvNotData.setText("Ops, falha na conexão!");
                                    mRecyclerViewListScheduleds.setVisibility(View.VISIBLE);
                                }
                            });
                            /*#######################################################################*/
                        }
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mRecyclerViewListScheduleds.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    txvNotData.setVisibility(View.VISIBLE);
                    mRecyclerViewListScheduleds.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                //openFragment(OwnersScheduledFRG.newInstance(new ArrayList<ScheduledModel>()));
                mProgressBar.setVisibility(View.INVISIBLE);
                txvNotData.setVisibility(View.VISIBLE);
            }
        });
    }

    //Processo universal de cancelamento dos agendamentos
    @Override
    public void onClickListenerScheduledCANCEL(final Object selected, final Boolean countMore) {
        new AlertDialog.Builder(getContext())
                .setTitle("Você realmente deseja cancelar este interesse?")
                /*.setMessage("Ao cancelar este passeio, será\n" +
                        "enviado uma notificação ao \n" +
                        "passeador, e será marcado em seu\n" +
                        "perfil um cancelamento de \n" +
                        "serviço.")*/
                .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Desloga usuario e fecha esta janela
                    }
                })
                .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
                        setCanceledInvitation(mDataSetTemp);
                        if(countMore == true){
                            setCanceledUserCountMore();
                        }
                        getScheduleds();
                    }
                })
                .show();
    }

    @Override
    public void onClickListenerScheduledBEGIN(final Object selected) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar inicio deste passeio?")
                .setMessage("Ao confirmar o inicio deste passeio, aparecerá \n" +
                        "como passeio iniciado para o \n" +
                        "passeador, só será finalizado \n" +
                        "após, ambos marcar a finalização.")
                .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Desloga usuario e fecha esta janela
                    }
                })
                .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
                        setConfirmedInitiationInvitation(mDataSetTemp);
                    }
                })
                .show();
    }

    //Processo universal para confirmar inicio do passeio
    @Override
    public void onClickListenerScheduledDONE(final Object selected) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar final deste passeio?")
                .setMessage("Ao confirmar o final deste passeio, aparecerá \n" +
                        "como passeio finalizado para o \n" +
                        "passeador, e estará livre \n" +
                        "para encerrar o agendamento.")
                .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Desloga usuario e fecha esta janela
                    }
                })
                .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
                        setConfirmedDone(mDataSetTemp);
                    }
                })
                .show();
    }

    //Processo universal para encerrar o agendamento e mandar para o historico
    @Override
    public void onClickListenerScheduledCLOSED(final Object selected) {
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

    @Override
    public void onClickListenerLocationMap(Object selected) {
        ScheduledModel scheduledModel = (ScheduledModel) selected;
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("id_walker", scheduledModel.getId_walker());
        startActivity(intent);
    }

    //Operações Firebase
    private void setConfirmedInitiationInvitation(final ScheduledModel mDataSetTemp) {
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString()) && scheduled.getInitiated_invitation()) {
                            scheduled.setConfirmed_initiated_invitation(true);
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

    private void setCanceledInvitation(final ScheduledModel mDataSetTemp) {
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
                            //Toast.makeText(getApplicationContext(), "Passeio cancelado com sucesso!",
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

    private void setCanceledUserCountMore() {
        databaseReference.child("Usuarios").orderByChild("id").equalTo(idUserFirebase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        userModel.setCanceled(userModel.getCanceled() + 1);
                        snapshot.getRef().setValue(userModel);
                        userViewModel.addUser(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setConcludedUserCountMore(final String id_walker) {
        databaseReference.child("Usuarios").orderByChild("id").equalTo(idUserFirebase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        userModel.setConcluded(userModel.getConcluded() + 1);
                        snapshot.getRef().setValue(userModel);
                        userViewModel.addUser(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Usuarios").orderByChild("id").equalTo(id_walker).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        if(userModel.getId().equals(id_walker)){
                            userModel.setConcluded(userModel.getConcluded() + 1);
                            snapshot.getRef().setValue(userModel);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setConfirmedDone(final ScheduledModel mDataSetTemp) {
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString()) && scheduled.getInitiated_invitation()) {
                            scheduled.setDone_invitation(true);
                            scheduled.setConfirmed_done_invitation(true);
                            postSnapshot.getRef().setValue(scheduled);
                            //mWalkersADP.removeRegisterScheduled(scheduled);

                            //não exclui, apenas fecha a mensagem
                            Toast.makeText(getApplicationContext(), "Passeio finalizado com sucesso!",
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

    private void setConfirmedClosed(final ScheduledModel mDataSetTemp) {
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString())) {
                            scheduled.setConfirmed_done_closed_owner(true);
                            postSnapshot.getRef().setValue(scheduled);
                            if(scheduled.getConfirmed_done_closed_walker() && !scheduled.getCanceled_invitation()) {
                                setConcludedUserCountMore(mDataSetTemp.getId_walker());
                            }
                            mOwnersADP.removeRegisterScheduled(scheduled);
                            mOwnersADP.notifyDataSetChanged();

                            //não exclui, apenas fecha a mensagem
                            //Toast.makeText(getApplicationContext(), "Passeio encerrado com sucesso!",
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
}
