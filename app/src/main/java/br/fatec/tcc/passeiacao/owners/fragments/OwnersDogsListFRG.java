package br.fatec.tcc.passeiacao.owners.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.RegisterDogActivity;
import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickDogsFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickDogsForProfileView;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.owners.adapters.OwnersADP;
import br.fatec.tcc.passeiacao.model.DogModel;
import br.fatec.tcc.passeiacao.service.firebaseService;

import static aplicacao.passeiacao.IS_DOGS;
import static com.facebook.FacebookSdk.getApplicationContext;

public class OwnersDogsListFRG extends Fragment implements InterfaceClickDogsFA {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private br.fatec.tcc.passeiacao.service.firebaseService firebaseService = new firebaseService();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query fbReferenceDogs;

    private RecyclerView mRecyclerViewListDogs;
    private OwnersADP mOwnersADP;
    private List<DogModel> dogModelList = new ArrayList<>();

    private ProgressBar mProgressBar;
    private TextView txvNotData;
    private static String idUserFirebase = "";

    public static Integer typeFragmen = -1;

    private InterfaceClickDogsForProfileView mInterfaceClickDogsForProfileView;

    // TODO: Rename and change types and number of parameters
    public static OwnersDogsListFRG newInstance(String midUserFirebase, Integer typ) {
        OwnersDogsListFRG fragment = new OwnersDogsListFRG();
        idUserFirebase = midUserFirebase;
        typeFragmen = typ;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_search, container, false);

        //Inicializa o FireBase
        databaseReference = firebaseService.inicializaFireBase(getContext());
        fbReferenceDogs = FirebaseDatabase.getInstance().getReference().child("Dogs").orderByChild("id_user").equalTo(idUserFirebase);

        mRecyclerViewListDogs = v.findViewById(R.id.rcvListSearch);

        mProgressBar = v.findViewById(R.id.progress_bar2);
        txvNotData = v.findViewById(R.id.txvNotData);

        mRecyclerViewListDogs.setHasFixedSize(true);
        FloatingActionButton floatingActionButtomMore = v.findViewById(R.id.floatingActionButtomMore);
        floatingActionButtomMore.setVisibility(View.VISIBLE);
        floatingActionButtomMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterDogActivity.class);
                intent.putExtra("id_user_auth", idUserFirebase);
                startActivity(intent);
            }
        });

        StaggeredGridLayoutManager llmCat = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        llmCat.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerViewListDogs.setLayoutManager(llmCat);
        mOwnersADP = new OwnersADP((ArrayList<Object>) (List<?>) dogModelList, typeFragmen);
        mOwnersADP.setInterfaceClickDogsFA(this);
        mRecyclerViewListDogs.setAdapter(mOwnersADP);
        mProgressBar.setVisibility(View.GONE);

        getDogs();

        return v;
    }

    private void getDogs() {
        mProgressBar.setVisibility(View.VISIBLE);
        txvNotData.setVisibility(View.INVISIBLE);
        fbReferenceDogs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dogModelList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        DogModel dog = postSnapshot.getValue(DogModel.class);
                        mOwnersADP.addRegisterDog(dog);
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mRecyclerViewListDogs.setVisibility(View.VISIBLE);
                }else{
                    mProgressBar.setVisibility(View.INVISIBLE);
                    txvNotData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erro na conexão! " + databaseError,
                        Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                txvNotData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClickListenerDogCard(Object selected, Integer typeFragment, Integer method) {
        if(typeFragment == IS_DOGS){
            getDogId((DogModel) selected);
        }else{
            onClickListenerDogCheck(selected, method);
        }
    }

    @Override
    public void onClickListenerDogCheck(Object selected, Integer method) {
        if(mInterfaceClickDogsForProfileView != null) {
            mInterfaceClickDogsForProfileView.onClickListenerDogCheckForProfile(selected, method);
        }
    }

    public void setInterfaceClickDogsForProfileView (InterfaceClickDogsForProfileView r){
        mInterfaceClickDogsForProfileView = r;
    }

    public void getDogId(final DogModel dogSelected) {
        Bundle weight = new Bundle();
        weight.putDouble("weight", dogSelected.getWeight());

        if (dogSelected != null) {
            Intent intent = new Intent(getActivity(), RegisterDogActivity.class);
            intent.putExtra("id_dog", dogSelected.getId());
            intent.putExtra("name", dogSelected.getName());
            intent.putExtra("age", dogSelected.getAge());
            intent.putExtra("breed", dogSelected.getBreed());
            intent.putExtra("castrated", dogSelected.getCastrated());
            intent.putExtra("comments", dogSelected.getComments());
            intent.putExtra("genre", dogSelected.getGenre());
            intent.putExtra("weight", dogSelected.getWeight());
            intent.putExtra("image", dogSelected.getImage_dog());
            intent.putExtra("updateUI", true);
            intent.putExtra("id_user_auth", idUserFirebase);
            startActivity(intent);
        }
    }
}
