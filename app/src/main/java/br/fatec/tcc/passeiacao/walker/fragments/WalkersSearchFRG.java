package br.fatec.tcc.passeiacao.walker.fragments;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.ProfileViewSelectedActivity;
import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickScheduledWalkersFA;
import br.fatec.tcc.passeiacao.interfaces.UpdateFragmentSearchOwnersIFA;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.room.UserViewModel;
import br.fatec.tcc.passeiacao.service.firebaseService;
import br.fatec.tcc.passeiacao.walker.adapters.WalkersADP;

import static aplicacao.passeiacao.IS_SEARCH;
import static com.facebook.FacebookSdk.getApplicationContext;

public class WalkersSearchFRG extends Fragment implements InterfaceClickScheduledWalkersFA, UpdateFragmentSearchOwnersIFA {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private Query fbReferenceScheduleds;
    private Query fbReferenceScheduledsRemove;

    private RecyclerView mRecyclerViewListWalkers;
    private WalkersADP mWalkersADP;
    public static List<ScheduledModel> userModelsOwners = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView txvNotData;
    private UserViewModel userViewModel;

    private static String idUserFirebase = "";

    public WalkersSearchFRG() {
    }

    // TODO: Rename and change types and number of parameters
    public static WalkersSearchFRG newInstance(String midUserFirebase) {
        WalkersSearchFRG fragment = new WalkersSearchFRG();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        idUserFirebase = midUserFirebase;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_search, container, false);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getContext());
        fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_walker").equalTo(idUserFirebase);
        fbReferenceScheduledsRemove = FirebaseDatabase.getInstance().getReference().child("Scheduleds");

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();

        //
        mRecyclerViewListWalkers = v.findViewById(R.id.rcvListSearch);

        mProgressBar = v.findViewById(R.id.progress_bar2);
        txvNotData = v.findViewById(R.id.txvNotData);

        mRecyclerViewListWalkers.setHasFixedSize(true);
        /*mRecyclerViewListWalkers.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewListWalkers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//                GridLayoutManager llm = (GridLayoutManager) mRecyclerView.getLayoutManager();
                StaggeredGridLayoutManager llm = (StaggeredGridLayoutManager) mRecyclerViewListWalkers.getLayoutManager();
                int[] aux = llm.findLastCompletelyVisibleItemPositions(null);
                int max = -1;
                //Faz o calculo do ultimo item COLOCADO na rcv...
                for(int i=0; i<aux.length; i++){
                    max = aux[i] > max ? aux[i] : max;
                }
//                long vcont = viewModelCategoria.getRegistros();
//                if(vcont == max + 1){
//                    BuscaCategoriaRetrofit(max+1,max + 5);
//                }
            }
        });*/

        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerViewListWalkers.setLayoutManager(llmCat);
        mWalkersADP = new WalkersADP((ArrayList<Object>) (List<?>) userModelsOwners, IS_SEARCH);
        mRecyclerViewListWalkers.setAdapter(mWalkersADP);
        mWalkersADP.setInterfaceClickScheduledWalkersFA(this);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerViewListWalkers.setVisibility(View.VISIBLE);

        //Observatorio USUARIO ENDEREÇO (Para Envio ao Servidor)...
        /***************************************************************************************/
        // Provedor Room UserModel
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        getScheduleds();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        fbReferenceScheduledsRemove.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final ScheduledModel scheduled = dataSnapshot.getValue(ScheduledModel.class);
                    mWalkersADP.removeRegisterScheduled(scheduled);
                    mWalkersADP.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getScheduleds() {

        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        scheduled.setReceived_invitation(true);
                        postSnapshot.getRef().setValue(scheduled);
                        /*#######################################################################*/
                        //Verifica o status do agendamento
                        if (scheduled.getConfirmed_invitation() == false && !scheduled.getCanceled_invitation()) {
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
                                    mWalkersADP.addRegisterScheduled(scheduled);

                                    mProgressBar.setVisibility(View.GONE);
                                    mRecyclerViewListWalkers.setVisibility(View.VISIBLE);
                                    txvNotData.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            /*#######################################################################*/
                        } else {
                            mWalkersADP.removeRegisterScheduled(scheduled);
                            mProgressBar.setVisibility(View.GONE);
                            txvNotData.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    txvNotData.setVisibility(View.VISIBLE);
                    mRecyclerViewListWalkers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                //openFragment(OwnersScheduledFRG.newInstance(new ArrayList<ScheduledModel>()));
                mProgressBar.setVisibility(View.GONE);
                txvNotData.setVisibility(View.VISIBLE);
            }
        });

    }

    public void getWalkerEmail(final UserModel userSelected) {
        //Primeiro valida o EMAIL solicitado;
        if (userSelected != null) {
            databaseReference.child("Usuarios").orderByChild("email").equalTo(userSelected.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        UserModel userModel = dataSnapshot.getChildren().iterator().next()
                                .getValue(UserModel.class);
                        Intent intent = new Intent(getActivity(), ProfileViewSelectedActivity.class);
                        intent.putExtra("id_walker", userSelected.getId());
                        intent.putExtra("name", userSelected.getNome());
                        intent.putExtra("rating", 4);
                        intent.putExtra("address", userSelected.getBairro());
                        intent.putExtra("birth", userSelected.getNasc());
                        intent.putExtra("performed", 0);
                        intent.putExtra("canceled", 0);
                        intent.putExtra("image", userSelected.getNome());
                        startActivity(intent);
                    } else {
                        //Avisa que o e-mail é invalida!
                        Toast.makeText(getApplicationContext(), "E-mail invalido ou não encontrado!",
                                Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClickListenerScheduledConfirmed(final Object selected) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar convite?")
                .setMessage("Ao confirmar o convite, será\n" +
                        "enviado uma notificação ao \n" +
                        "dono, para inicio do passeio.")
                .setPositiveButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Desloga usuario e fecha esta janela
                    }
                })
                .setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setConfirmedInvation((ScheduledModel) selected);
                    }
                })
                .show();
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
                        setCanceledInvitation(mDataSetTemp);
                        setCanceledUserCountMore();
                    }
                })
                .show();
    }

    @Override
    public void onClickListenerScheduledCard(Object selected) {

    }

    @Override
    public void onClickListenerScheduledClosed(Object selected) {

    }

    @Override
    public void updateUsersWalkersIFA(List<UserModel> usersWalkers) {

    }

    //Operações Firebase
    private void setConfirmedInvation (ScheduledModel selected){
        final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString())) {
                            scheduled.setConfirmed_invitation(true);
                            postSnapshot.getRef().setValue(scheduled);
                            mWalkersADP.removeRegisterScheduled(scheduled);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setCanceledInvitation (ScheduledModel selected){
        final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
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
                            postSnapshot.getRef().setValue(scheduled);
                            mWalkersADP.removeRegisterScheduled(scheduled);
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
}
