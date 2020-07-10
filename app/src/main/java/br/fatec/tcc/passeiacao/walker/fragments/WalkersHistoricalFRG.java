package br.fatec.tcc.passeiacao.walker.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import java.util.Date;
import java.util.List;

import br.fatec.tcc.passeiacao.ProfileViewSelectedActivity;
import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickHistoricalFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickIFA;
import br.fatec.tcc.passeiacao.interfaces.UpdateFragmentSearchOwnersIFA;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.service.firebaseService;
import br.fatec.tcc.passeiacao.walker.adapters.WalkersADP;

import static aplicacao.passeiacao.IS_HISTORICAL;
import static com.facebook.FacebookSdk.getApplicationContext;

public class WalkersHistoricalFRG extends Fragment implements InterfaceClickIFA, UpdateFragmentSearchOwnersIFA, InterfaceClickHistoricalFA {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private Query fbReferenceScheduleds;

    private RecyclerView mRecyclerViewListWalkers;
    private WalkersADP mWalkersADP;
    public static List<ScheduledModel> userModelsTours = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView txvNotData;

    private static String idUserFirebase = "";

    public WalkersHistoricalFRG() {}

    // TODO: Rename and change types and number of parameters
    public static WalkersHistoricalFRG newInstance(String midUserFirebase) {
        WalkersHistoricalFRG fragment = new WalkersHistoricalFRG();
        idUserFirebase = midUserFirebase;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_search, container, false);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getContext());
        fbReferenceScheduleds = FirebaseDatabase.getInstance().getReference().child("Scheduleds").orderByChild("id_walker").equalTo(idUserFirebase);

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
        mWalkersADP = new WalkersADP((ArrayList<Object>)(List<?>) userModelsTours, IS_HISTORICAL);
        mWalkersADP.setInterfaceClickHistoricalFA(this);
        mRecyclerViewListWalkers.setAdapter(mWalkersADP);
        //mWalkersADP.setInterfaceClickScheduledWalkersFA(this);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerViewListWalkers.setVisibility(View.VISIBLE);

        getScheduleds();

        return v;
    }

    @Override
    public void onClickListenerUserProfile(Object userSelected) {
        Log.d("ToursSearchFRG", "CHEGOU");
        /*Intent intent = new Intent(getActivity(), ScreenWalkerActivity.class);
        startActivity(intent);*/
        getWalkerEmail ((UserModel) userSelected);
    }

    public void getWalkerEmail (final UserModel userSelected) {
        //Primeiro valida o EMAIL solicitado;
        if(userSelected != null) {
            databaseReference.child("Usuarios").orderByChild("email").equalTo(userSelected.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        UserModel userModel = dataSnapshot.getChildren().iterator().next()
                                .getValue(UserModel.class);
                        Intent intent = new Intent(getActivity(), ProfileViewSelectedActivity.class);
                        intent.putExtra("id_walker", userSelected.getId());
                        intent.putExtra("name", userSelected.getNome());
                        intent.putExtra("rating", userSelected.getNote());
                        intent.putExtra("address", userSelected.getBairro());
                        intent.putExtra("birth", userSelected.getNasc());
                        intent.putExtra("performed", userSelected.getConcluded());
                        intent.putExtra("canceled", userSelected.getCanceled());
                        intent.putExtra("image", userSelected.getImageAvatar());
                        intent.putExtra("contact", userSelected.getTelefone());
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

    private void getScheduleds() {
        mProgressBar.setVisibility(View.VISIBLE);
        txvNotData.setVisibility(View.GONE);
        mWalkersADP.removeAllScheduled();
        fbReferenceScheduleds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        /* Verifica se este agendamento já foi concluido */
                        if(scheduled.getConfirmed_done_closed_walker()) {
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
                                    //scheduledModelList.add(scheduled);
                                    mWalkersADP.addRegisterScheduled(scheduled);
                                    //mOwnersADP.notifyDataSetChanged();
                                    //openFragment(OwnersScheduledFRG.newInstance(scheduledModelList));
                                    mProgressBar.setVisibility(View.GONE);
                                    txvNotData.setVisibility(View.GONE);
                                    mRecyclerViewListWalkers.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressBar.setVisibility(View.GONE);
                                    txvNotData.setVisibility(View.VISIBLE);
                                }
                            });
                            /*#######################################################################*/
                        }else{
                            mProgressBar.setVisibility(View.GONE);
                            txvNotData.setVisibility(View.VISIBLE);
                        }
                    }
                    /*mProgressBar.setVisibility(View.GONE);
                    txvNotData.setVisibility(View.VISIBLE);*/
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
            }
        });
    }

    @Override
    public void updateUsersWalkersIFA(List<UserModel> userOwners) {
        //userModelsOwners = userOwners;
    }

    @Override
    public void onClickListenerHistoricalCard(Object selected) {

    }

    @Override
    public void onClickListenerAssessment(final Object selected) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Avaliação");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.activity_assessment_send, null);
        builder.setView(customLayout);

        //add a button cancel
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Desloga usuario e fecha esta janela
            }
        });

        // add a button
        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ScheduledModel mDataSetTemp = (ScheduledModel) selected;
                // send data from the AlertDialog to the Activity
                RatingBar rtbAssessmentUserSend = customLayout.findViewById(R.id.rtbAssessmentUserSend);
                EditText edtMessageAssessmentSend = customLayout.findViewById(R.id.edtMessageAssessmentSend);
                setHistorical(mDataSetTemp, Double.valueOf(rtbAssessmentUserSend.getRating()), edtMessageAssessmentSend.getText().toString());
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void setHistorical(final ScheduledModel mDataSetTemp, final Double avaliation, final String message){
        fbReferenceScheduleds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //scheduledModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ScheduledModel scheduled = postSnapshot.getValue(ScheduledModel.class);
                        //Salva o status de recebimento do convite
                        if (scheduled.getId().equals(mDataSetTemp.getId().toString()) && scheduled.getInitiated_invitation()) {
                            scheduled.setAssessment_note_owner(avaliation);
                            scheduled.setAssessment_message_owner(message);
                            scheduled.setAssessment_date_owner(String.valueOf(new Date()));
                            postSnapshot.getRef().setValue(scheduled);
                            setUserRatingMore(avaliation, scheduled.getId_owner());
                            //mWalkersADP.removeRegisterScheduled(scheduled);

                            //não exclui, apenas fecha a mensagem
                            Toast.makeText(getApplicationContext(), "Avaliação enviada com sucesso!",
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
    public void setUserRatingMore (final Double avaliation, String id_owner){
        databaseReference.child("Usuarios").orderByChild("id").equalTo(id_owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        userModel.setNote(userModel.getNote() + avaliation);
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
}
