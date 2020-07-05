package br.fatec.tcc.passeiacao.owners.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.fatec.tcc.passeiacao.RegisterDogActivity;
import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.Util;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickHistoricalFA;
import br.fatec.tcc.passeiacao.model.HistoricalModel;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.owners.adapters.OwnersADP;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

import static aplicacao.passeiacao.IS_HISTORICAL;
import static com.facebook.FacebookSdk.getApplicationContext;

public class OwnersHistoricalFRG extends Fragment implements InterfaceClickHistoricalFA {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query fbReferenceHistoricals;
    private Query fbReferenceScheduleds;

    private RecyclerView mRecyclerViewListHistoryHistorical;
    private OwnersADP mOwnersADP;
    private List<HistoricalModel> historicalModelList = new ArrayList<>();

    private ProgressBar mProgressBar;
    private TextView txvNotData;
    private static String idUserFirebase = "";

    public OwnersHistoricalFRG() {
    }

    // TODO: Rename and change types and number of parameters
    public static OwnersHistoricalFRG newInstance(String midUserFirebase) {
        OwnersHistoricalFRG fragment = new OwnersHistoricalFRG();
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
        fbReferenceHistoricals = FirebaseDatabase.getInstance().getReference().child("Historicals").orderByChild("id_owner").equalTo(idUserFirebase);

        mRecyclerViewListHistoryHistorical = v.findViewById(R.id.rcvListSearch);
        mRecyclerViewListHistoryHistorical.setHasFixedSize(true);

        mProgressBar = v.findViewById(R.id.progress_bar2);
        txvNotData = v.findViewById(R.id.txvNotData);

        ArrayList<ScheduledModel> mScheduledModel = new ArrayList<ScheduledModel> ();
        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerViewListHistoryHistorical.setLayoutManager(llmCat);
        mOwnersADP = new OwnersADP((ArrayList<Object>)(List<?>) mScheduledModel, IS_HISTORICAL);
        mOwnersADP.setInterfaceClickHistoricalFA(this);
        mRecyclerViewListHistoryHistorical.setAdapter(mOwnersADP);

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
                        /* Verifica se este agendamento já foi concluido */
                        if(scheduled.getConfirmed_done_closed_owner()) {
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
                                    mOwnersADP.addRegisterScheduled(scheduled);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    txvNotData.setVisibility(View.VISIBLE);
                                    txvNotData.setText("Ops, falha na conexão!");
                                }
                            });
                            /*#######################################################################*/
                        }
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mRecyclerViewListHistoryHistorical.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    txvNotData.setVisibility(View.VISIBLE);
                    mRecyclerViewListHistoryHistorical.setVisibility(View.VISIBLE);
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

    private void getHistoricals() {
        fbReferenceHistoricals.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historicalModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        HistoricalModel historical = postSnapshot.getValue(HistoricalModel.class);
                        //mOwnersADP.addRegisterHistorical(historical);
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mRecyclerViewListHistoryHistorical.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClickListenerHistoricalCard(Object selected) {
        getHistoricalId((HistoricalModel) selected);
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
                            scheduled.setAssessment_note_walker(avaliation);
                            scheduled.setAssessment_message_walker(message);
                            scheduled.setAssessment_date_walker(String.valueOf(new Date()));
                            postSnapshot.getRef().setValue(scheduled);
                            setUserRatingMore(avaliation, scheduled.getId_walker());
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
    public void setUserRatingMore (final Double avaliation, String id_walker){
        databaseReference.child("Usuarios").orderByChild("id").equalTo(id_walker).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final UserModel userModel = snapshot.getValue(UserModel.class);
                        //userModel.setNote(userModel.getNote() + avaliation);
                        //snapshot.getRef().setValue(userModel);
                        //userViewModel.addUser(userModel);
                        Util.getScheduledsCalcAssessments(fbReferenceScheduleds, databaseReference, userModel.getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getHistoricalId(final HistoricalModel historicalSelected) {
        /*Bundle weight = new Bundle();
        weight.putDouble("weight", historicalSelected.getWeight());*/

        if (historicalSelected != null) {
            Intent intent = new Intent(getActivity(), RegisterDogActivity.class);
            intent.putExtra("id_user_post", historicalSelected.getId_user_post());
            intent.putExtra("Comment", historicalSelected.getComment());
            intent.putExtra("Note", historicalSelected.getNote());
            intent.putExtra("CreateAt", historicalSelected.getCreateAt());
            intent.putExtra("RegisterCreateAt", historicalSelected.getRegisterCreateAt());
            intent.putExtra("UpdateAt", historicalSelected.getUpdateAt());
            intent.putExtra("RegisterUpdateAt", historicalSelected.getRegisterUpdateAt());
            //intent.putExtra("image", dogSelected.getImage_dog());
            intent.putExtra("Active", historicalSelected.getActive());
            startActivity(intent);
        }
    }
}