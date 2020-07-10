package br.fatec.tcc.passeiacao.owners.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import br.fatec.tcc.passeiacao.ProfileViewSelectedActivity;
import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickIFA;
import br.fatec.tcc.passeiacao.interfaces.UpdateFragmentSearchOwnersIFA;
import br.fatec.tcc.passeiacao.owners.adapters.OwnersADP;
import br.fatec.tcc.passeiacao.model.UserModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

import static aplicacao.passeiacao.IS_SEARCH;
import static com.facebook.FacebookSdk.getApplicationContext;

public class OwnersSearchFRG extends Fragment implements InterfaceClickIFA, UpdateFragmentSearchOwnersIFA {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private Query fbReferenceWalkers;

    private RecyclerView mRecyclerViewListOwners;
    private ProgressBar mProgressBar;
    private TextView txvNotData;
    private OwnersADP mOwnersADP;
    public static List<UserModel> userModelsWalkers;
    public ArrayList<UserModel> userWalkersModelList = new ArrayList<>();

    private static String idUserFirebase = "";

    public OwnersSearchFRG(List<UserModel> userModelsWalkers) {
        this.userModelsWalkers = userModelsWalkers;
    }

    public OwnersSearchFRG() {

    }

    // TODO: Rename and change types and number of parameters
    public static OwnersSearchFRG newInstance(String midUserFirebase) {
        OwnersSearchFRG fragment = new OwnersSearchFRG();
        idUserFirebase = midUserFirebase;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_search, container, false);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getContext());

        // Initialize Firebase Auth (utenticação padrão do Firebase)
        mAuth = FirebaseAuth.getInstance();

        fbReferenceWalkers = FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("passeador").equalTo(true);

        //
        mRecyclerViewListOwners = v.findViewById(R.id.rcvListSearch);

        mProgressBar = v.findViewById(R.id.progress_bar2);
        txvNotData = v.findViewById(R.id.txvNotData);

        mRecyclerViewListOwners.setHasFixedSize(true);
        /*mRecyclerViewListOwners.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewListOwners.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//                GridLayoutManager llm = (GridLayoutManager) mRecyclerView.getLayoutManager();
                StaggeredGridLayoutManager llm = (StaggeredGridLayoutManager) mRecyclerViewListOwners.getLayoutManager();
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
        mRecyclerViewListOwners.setLayoutManager(llmCat);
        mOwnersADP = new OwnersADP((ArrayList<Object>)(List<?>) userModelsWalkers, IS_SEARCH);
        mRecyclerViewListOwners.setAdapter(mOwnersADP);
        mOwnersADP.setInterfaceClickIFA(this);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerViewListOwners.setVisibility(View.VISIBLE);

        /*Observatorios*/
        /*mviewModelcontatoAmigo = ViewModelProviders.of(this).get(contatoListViewModel.class);
        mMenUsuViewModel = ViewModelProviders.of(this).get(MenUsuViewModel.class);
        mMenConViewModel = ViewModelProviders.of(this).get(MenConViewModel.class);*/

        //Observatorio USUARIO ENDEREÇO (Para Envio ao Servidor)...
        /*mviewModelcontatoAmigo.getItemAndPersonListAmigo().observe(this, new Observer<List<dbVINCULO>>() {
            @Override
            public void onChanged(@Nullable List<dbVINCULO> itemAndPeople) {
                mDataSetContato = itemAndPeople;
                mOwnersADP.addContato(mDataSetContato, 1);
            }
        });*/

        getWalkers();

        return v;
    }
    @Override
    public void onClickListenerUserProfile(Object userSelected) {
        //Log.d("OwnersSearchFRG", "CHEGOU");
        /*Intent intent = new Intent(getActivity(), ScreenWalkerActivity.class);
        startActivity(intent);*/
        getWalkerEmail ((UserModel) userSelected);
    }

    @Override
    public void updateUsersWalkersIFA(List<UserModel> usersWalkers) {
        userModelsWalkers = usersWalkers;
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
                        intent.putExtra("note", userSelected.getNote());
                        intent.putExtra("rating", userSelected.getNoteUserConverter());
                        intent.putExtra("address", userSelected.getBairro());
                        intent.putExtra("birth", userSelected.getNascConvertAge());
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

    /* ### Querys persistentes para manipulação dos dados ### */
    private void getWalkers() {
        mProgressBar.setVisibility(View.VISIBLE);
        txvNotData.setVisibility(View.GONE);
        fbReferenceWalkers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txvNotData.setVisibility(View.VISIBLE);
                if (dataSnapshot.exists()) {
                    userWalkersModelList = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        UserModel user = postSnapshot.getValue(UserModel.class);
                        if (!user.getId().equals(idUserFirebase)) {
                            userWalkersModelList.add(user);
                            txvNotData.setVisibility(View.GONE);
                        }
                    }
                    mOwnersADP.addUserAll(userWalkersModelList);
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Avisa que o e-mail é invalida!
                Toast.makeText(getApplicationContext(), "Nenhum passeador encontrado no momento!",
                        Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                txvNotData.setVisibility(View.VISIBLE);
            }
        });
    }
}
