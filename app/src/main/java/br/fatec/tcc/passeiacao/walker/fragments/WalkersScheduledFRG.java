package br.fatec.tcc.passeiacao.walker.fragments;

import android.content.DialogInterface;
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
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickScheduledWalkersFA;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.service.firebaseService;
import br.fatec.tcc.passeiacao.walker.adapters.WalkersADP;

import static aplicacao.passeiacao.IS_SCHEDULED;
import static com.facebook.FacebookSdk.getApplicationContext;

public class WalkersScheduledFRG extends Fragment  implements InterfaceClickScheduledWalkersFA {

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
    private WalkersADP mWalkersADP;
    private List<ScheduledModel> scheduledModelList = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView txvNotData;
    public static String id_user_auth;

    public WalkersScheduledFRG() {
    }

    // TODO: Rename and change types and number of parameters
    public static WalkersScheduledFRG newInstance(String id_user) {
        WalkersScheduledFRG fragment = new WalkersScheduledFRG();
        fragment.id_user_auth = id_user;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_search, container, false);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getContext());
        fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_walker").equalTo(id_user_auth);

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
                                    mWalkersADP.addRegister(scheduled);
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
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
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
                            //mWalkersADP.removeRegisterScheduled(scheduled);

                            //não exclui, apenas fecha a mensagem
                            Toast.makeText(getApplicationContext(), "Passeio liberado para \n" +
                                            "ser iniciado pelo dono do \n" +
                                            "passeio!",
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
    private void setCanceledInvitation (final ScheduledModel mDataSetTemp){
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
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
                            Toast.makeText(getApplicationContext(), "Passeio liberado para \n" +
                                            "ser iniciado pelo dono do \n" +
                                            "passeio!",
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
    private void setCanceledUserCountMore (){
        databaseReference.child("Usuarios").orderByChild("id").equalTo(id_user_auth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final UserModel userModel = dataSnapshot.getChildren().iterator().next()
                            .getValue(UserModel.class);
                    userModel.setCanceled(userModel.getCanceled() + 1);
                    dataSnapshot.getRef().setValue(userModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setConfirmedClosed(final ScheduledModel mDataSetTemp) {
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
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
                            //mWalkersADP.removeRegisterScheduled(scheduled);

                            //não exclui, apenas fecha a mensagem
                            Toast.makeText(getApplicationContext(), "Passeio encerrado com sucesso!",
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

