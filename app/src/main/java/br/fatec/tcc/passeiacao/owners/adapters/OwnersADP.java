package br.fatec.tcc.passeiacao.owners.adapters;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickDogsFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickHistoricalFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickIFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickScheduledFA;
import br.fatec.tcc.passeiacao.model.DogModel;
import br.fatec.tcc.passeiacao.model.HistoricalModel;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;

import static aplicacao.passeiacao.IS_DOGS;
import static aplicacao.passeiacao.IS_DOG_PROFILE;
import static aplicacao.passeiacao.IS_HISTORICAL;
import static aplicacao.passeiacao.IS_INSERT;
import static aplicacao.passeiacao.IS_REMOVE;
import static aplicacao.passeiacao.IS_SCHEDULED;
import static aplicacao.passeiacao.IS_SEARCH;

public class OwnersADP extends RecyclerView.Adapter<OwnersADP.ViewHolder> {

    private ArrayList<Object> mDataSet;
    private Integer vTipoInterface;
    private InterfaceClickIFA mInterfaceClickIFA;
    private InterfaceClickDogsFA mInterfaceClickDogsFA;
    private InterfaceClickScheduledFA mInterfaceClickScheduledFA;
    private InterfaceClickHistoricalFA mInterfaceClickHistoricalFA;
    public static Integer typeFragmen = -1;

    public OwnersADP(ArrayList<Object> mDataSet, Integer vTipoInterface) {
        this.mDataSet = mDataSet;
        this.vTipoInterface = vTipoInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if(vTipoInterface == IS_SEARCH){
            Log.d("OwnersSearchFRG", "CHEGOU 1a");
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_walker, parent, false);
        }else if(vTipoInterface == IS_DOGS){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner_dogs, parent, false);
        }else if(vTipoInterface == IS_SCHEDULED){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner_scheduled, parent, false);
        }else if(vTipoInterface == IS_HISTORICAL){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner_history, parent, false);
        }else if(vTipoInterface == IS_DOG_PROFILE){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner_dogs, parent, false);
        }

        return new OwnersADP.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(vTipoInterface == IS_SEARCH) {           //Busca por donos
            ArrayList<UserModel> mDataSetTemp = (ArrayList<UserModel>)(List<?>) mDataSet;
            holder.lblTitleCardWalker.setText(mDataSetTemp.get(position).getNome());
            holder.lblSubTitleCardWalker.setText(mDataSetTemp.get(position).getBairro());
            holder.lblNoteWalker.setText(String.valueOf(mDataSetTemp.get(position).getNote()));
            if(mDataSetTemp.get(position).getNote() > 0){
                holder.rtbProfileWalker.setRating(1);
            }else{
                holder.rtbProfileWalker.setRating(0);
            }
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImageAvatar())) {
                holder.imgAvatarWalker.setImageURI(Uri.parse(mDataSetTemp.get(position).getImageAvatar()));
            }
        }else if(vTipoInterface == IS_DOGS || vTipoInterface == IS_DOG_PROFILE) {     //Dogs
            ArrayList<DogModel> mDataSetTemp = (ArrayList<DogModel>)(List<?>) mDataSet;
            holder.lblTitleCardOwnerDogs.setText(mDataSetTemp.get(position).getName());
            holder.lblSubTitleCardOwnerDogs.setText(mDataSetTemp.get(position).getBreed());
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImage_dog())) {
                holder.imgAvatarOwnerDogs.setImageURI(Uri.parse(mDataSetTemp.get(position).getImage_dog()));
            }
            /*if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImageCover())) {
                holder.imgAvatarWalker.setImageURI(Uri.parse(mDataSetTemp.get(position).getImageAvatar()));
            }*/
        }else if(vTipoInterface == IS_SCHEDULED) {     //Agendados
            final ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
            holder.lblTitleCardOwnerScheduled.setText(mDataSetTemp.get(position).getTitle_walker());
            holder.lblSubTitleCardOwnerScheduled.setText(mDataSetTemp.get(position).getAddress_walker());
            holder.lblLinkOwnerScheduled.setVisibility(View.GONE);
            holder.lblHighlighterOwnerScheduled.setVisibility(View.VISIBLE);
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImage_walker())) {
                holder.imgAvatarOwnerScheduled.setImageURI(Uri.parse(mDataSetTemp.get(position).getImage_walker()));
            }

            if(mDataSetTemp.get(position).getSend_invitation() == false){               //Foi enviado ?
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Aguardando\nsinal");
                holder.lblLinkOwnerScheduled.setText("CANCELAR");
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledCANCEL(mDataSetTemp.get(position), false);
                        }
                    }
                });

            }if(mDataSetTemp.get(position).getReceived_invitation() == true){           //Recebeu o convite
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Recebido");
                holder.lblLinkOwnerScheduled.setText("CANCELAR");
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledCANCEL(mDataSetTemp.get(position), false);
                        }
                    }
                });

            }if(mDataSetTemp.get(position).getConfirmed_invitation() == true){          //Confirmou o interesse (agendou)
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Aceitou\nconvite");
                holder.lblLinkOwnerScheduled.setText("CANCELAR");
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledCANCEL(mDataSetTemp.get(position), true);
                        }
                    }
                });

            }if(mDataSetTemp.get(position).getInitiated_invitation() == true){          //Sinalizou o inicio do passeio
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Aguardando\nsinal seu");
                holder.lblLinkOwnerScheduled.setText("CONFIRMAR INICIO");
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledBEGIN(mDataSetTemp.get(position));
                        }
                    }
                });

            }if(mDataSetTemp.get(position).getConfirmed_initiated_invitation() == true){          //Confirmou o inicio do passeio
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Passeio\niniciado");
                holder.lblLinkOwnerScheduled.setText("FINALIZAR\nPASSEIO");
                holder.imvIconPlace.setVisibility(View.VISIBLE);
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledDONE(mDataSetTemp.get(position));
                        }
                    }
                });

            }if(mDataSetTemp.get(position).getDone_invitation() == true){               //Sinalizou o fim do passeio
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Aguardando\nsinal");
                holder.lblLinkOwnerScheduled.setText("FINALIZAR\nPASSEIO");
                holder.imvIconPlace.setVisibility(View.GONE);
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledDONE(mDataSetTemp.get(position));
                        }
                    }
                });

            }if(mDataSetTemp.get(position).getConfirmed_done_invitation() == true){     //Confirmou o fim do passeio (Apenas o Dono pode) ETAPA FINAL
                holder.lblHighlighterOwnerScheduled.setText("●  Aguardando\nsinal");
                holder.lblLinkOwnerScheduled.setVisibility(View.GONE);
                holder.imvIconPlace.setVisibility(View.GONE);

            }if(mDataSetTemp.get(position).getConfirmed_done_invitation() == true && mDataSetTemp.get(position).getDone_invitation() == true){     //Confirmou o fim do passeio (Apenas o Dono pode) ETAPA FINAL
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Passeio\nfinalizado");
                holder.lblLinkOwnerScheduled.setText("ENCERRAR");
                holder.imvIconPlace.setVisibility(View.GONE);
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledCLOSED(mDataSetTemp.get(position));
                        }
                    }
                });

            }if(mDataSetTemp.get(position).getCanceled_invitation() == true && mDataSetTemp.get(position).getConfirmed_done_invitation() == false){           //Cancelado
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setText("●  Recusado");
                holder.lblHighlighterOwnerScheduled.setTextColor(Color.RED);
                holder.lblLinkOwnerScheduled.setText("ENCERRAR");
                holder.lblLinkOwnerScheduled.setVisibility(View.VISIBLE);
                holder.imvIconPlace.setVisibility(View.GONE);
                holder.lblLinkOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            mInterfaceClickScheduledFA.onClickListenerScheduledCLOSED(mDataSetTemp.get(position));
                        }
                    }
                });
            }

        }else if(vTipoInterface == IS_HISTORICAL) {
            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
            holder.lblTitleCardOwnerHistory.setText(mDataSetTemp.get(position).getTitle_walker());
            holder.lblSubTitleCardOwnerHistory.setText(mDataSetTemp.get(position).getAddress_walker());
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImage_walker())) {
                holder.imgAvatarOwnerHistory.setImageURI(Uri.parse(mDataSetTemp.get(position).getImage_walker()));
            }

            if(mDataSetTemp.get(position).getCanceled_invitation() == false) {
                holder.lblLink2OwnerHistory.setVisibility(View.GONE);
            }else{
                holder.lblHighlighterOwnerScheduled.setText("Recusado");
                holder.lblHighlighterOwnerScheduled.setTextColor(Color.RED);
                holder.lblHighlighterOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblLinkOwnerHistory.setVisibility(View.INVISIBLE);
                holder.lblLink2OwnerHistory.setVisibility(View.GONE);
                return;
            }
            holder.lblLink2OwnerHistory.setVisibility(View.VISIBLE);  //Contratar novamente
            if(mDataSetTemp.get(position).getConfirmed_done_invitation() == true && mDataSetTemp.get(position).getAssessment_date_walker() == null) {
                holder.lblHighlighterOwnerScheduled.setText("Finalizado\ncom sucesso");
                holder.lblHighlighterOwnerScheduled.setTextColor(Color.GREEN);
                holder.lblHighlighterOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblLink2OwnerHistory.setVisibility(View.GONE);
                holder.lblLinkOwnerHistory.setVisibility(View.VISIBLE);   //Avaliar
            }else{
                holder.lblHighlighterOwnerScheduled.setText("Já avaliado");
                holder.lblHighlighterOwnerScheduled.setTextColor(Color.GREEN);
                holder.lblHighlighterOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblLink2OwnerHistory.setVisibility(View.GONE);
                holder.lblLinkOwnerHistory.setVisibility(View.INVISIBLE);   //Já Avaliado
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //search walkers
        CardView cdvCardWalkerSearch;
        RatingBar rtbProfileWalker;
        TextView lblTitleCardWalker;
        TextView lblSubTitleCardWalker;
        TextView lblNoteWalker;
        SimpleDraweeView imgAvatarWalker;

        //dogs list
        CardView cdvCardOwnerDogs;
        ImageView imgAvatarOwnerDogs;
        TextView lblTitleCardOwnerDogs;
        TextView lblSubTitleCardOwnerDogs;
        ImageView imgIconOwnerDogs;

        //scheduleds list
        CardView cdvCardOwnerScheduled;
        ImageView imgAvatarOwnerScheduled;
        TextView lblTitleCardOwnerScheduled;
        TextView lblSubTitleCardOwnerScheduled;
        TextView lblHighlighterOwnerScheduled;
        TextView lblLinkOwnerScheduled;
        ImageView imvIconPlace;

        //historicals list
        CardView cdvCardOwnerHistory;
        ImageView imgAvatarOwnerHistory;
        TextView lblTitleCardOwnerHistory;
        TextView lblSubTitleCardOwnerHistory;
        TextView lblLinkOwnerHistory;
        TextView lblLink2OwnerHistory;

        public ViewHolder(View v) {
            super(v);
            if(vTipoInterface == IS_SEARCH) {
                Log.d("OwnersSearchFRG", "CHEGOU 2a");
                lblTitleCardWalker = v.findViewById(R.id.lblTitleCardWalker);
                lblSubTitleCardWalker = v.findViewById(R.id.lblSubTitleCardWalker);
                cdvCardWalkerSearch = v.findViewById(R.id.cdvCardWalkerSearch);
                lblNoteWalker = v.findViewById(R.id.lblNoteWalker);
                rtbProfileWalker = v.findViewById(R.id.rtbProfileWalker);
                imgAvatarWalker = v.findViewById(R.id.imgAvatarWalker);
                cdvCardWalkerSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickIFA != null) {
                            ArrayList<UserModel> mDataSetTemp = (ArrayList<UserModel>)(List<?>) mDataSet;
                            mInterfaceClickIFA.onClickListenerUserProfile(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
            }else if(vTipoInterface == IS_DOGS || vTipoInterface == IS_DOG_PROFILE) {
                cdvCardOwnerDogs = v.findViewById(R.id.cdvCardOwnerDogs);
                imgAvatarOwnerDogs = v.findViewById(R.id.imgAvatarOwnerDogs);
                lblTitleCardOwnerDogs = v.findViewById(R.id.lblTitleCardOwnerDogs);
                lblSubTitleCardOwnerDogs = v.findViewById(R.id.lblSubTitleCardOwnerDogs);
                imgIconOwnerDogs = v.findViewById(R.id.imgIconOwnerDogs);
                cdvCardOwnerDogs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickDogsFA != null) {
                            ArrayList<DogModel> mDataSetTemp = (ArrayList<DogModel>)(List<?>) mDataSet;
                            if(vTipoInterface == IS_DOG_PROFILE){
                                int color = cdvCardOwnerDogs.getCardBackgroundColor().getDefaultColor();
                                if(color == Color.GREEN){
                                    cdvCardOwnerDogs.setCardBackgroundColor(Color.WHITE);
                                    mInterfaceClickDogsFA.onClickListenerDogCard(mDataSetTemp.get(getPosition()), vTipoInterface, IS_REMOVE);
                                }else{
                                    cdvCardOwnerDogs.setCardBackgroundColor(Color.GREEN);
                                    mInterfaceClickDogsFA.onClickListenerDogCard(mDataSetTemp.get(getPosition()), vTipoInterface, IS_INSERT);
                                }
                            } else {
                                mInterfaceClickDogsFA.onClickListenerDogCard(mDataSetTemp.get(getPosition()), vTipoInterface, -1);
                            }
                        }
                    }
                });
            }else if(vTipoInterface == IS_SCHEDULED) {
                cdvCardOwnerScheduled = v.findViewById(R.id.cdvCardOwnerScheduled);
                imgAvatarOwnerScheduled = v.findViewById(R.id.imgAvatarOwnerScheduled);
                lblTitleCardOwnerScheduled = v.findViewById(R.id.lblTitleCardOwnerScheduled);
                lblSubTitleCardOwnerScheduled = v.findViewById(R.id.lblSubTitleCardOwnerScheduled);
                lblHighlighterOwnerScheduled = v.findViewById(R.id.lblHighlighterOwnerScheduled);
                lblLinkOwnerScheduled = v.findViewById(R.id.lblLinkOwnerScheduled);
                imvIconPlace = v.findViewById(R.id.imvIconPlace);
                cdvCardOwnerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickIFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
                            mInterfaceClickIFA.onClickListenerUserProfile(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
                imvIconPlace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
                            mInterfaceClickScheduledFA.onClickListenerLocationMap(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
            }else if(vTipoInterface == IS_HISTORICAL) {
                cdvCardOwnerHistory = v.findViewById(R.id.cdvCardOwnerHistory);
                imgAvatarOwnerHistory = v.findViewById(R.id.imgAvatarOwnerHistory);
                lblTitleCardOwnerHistory = v.findViewById(R.id.lblTitleCardOwnerHistory);
                lblSubTitleCardOwnerHistory = v.findViewById(R.id.lblSubTitleCardOwnerHistory);
                lblLinkOwnerHistory = v.findViewById(R.id.lblLinkOwnerHistory);
                lblLink2OwnerHistory = v.findViewById(R.id.lblLink2OwnerHistory);
                lblHighlighterOwnerScheduled = v.findViewById(R.id.lblHighlighterOwnerScheduled);
                cdvCardOwnerHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickIFA != null) {
                            ArrayList<HistoricalModel> mDataSetTemp = (ArrayList<HistoricalModel>)(List<?>) mDataSet;
                            mInterfaceClickIFA.onClickListenerUserProfile(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
                lblLinkOwnerHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickHistoricalFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
                            mInterfaceClickHistoricalFA.onClickListenerAssessment(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    //Interfaces
    public void setInterfaceClickIFA (InterfaceClickIFA r){
        mInterfaceClickIFA = r;
    }
    public void setInterfaceClickDogsFA (InterfaceClickDogsFA r){
        mInterfaceClickDogsFA = r;
    }
    public void setmInterfaceClickScheduledFA (InterfaceClickScheduledFA r){
        mInterfaceClickScheduledFA = r;
    }
    public void setInterfaceClickHistoricalFA (InterfaceClickHistoricalFA r){
        mInterfaceClickHistoricalFA = r;
    }

    //Funções de add/remove/update no adaptador
    public void addUserAll (ArrayList<UserModel> item) {
        this.mDataSet = (ArrayList<Object>)(List<?>) item;
        notifyDataSetChanged();
    }
    public void addRegisterDog (Object item){
        ArrayList<DogModel> mDataSetTemp = (ArrayList<DogModel>)(List<?>) mDataSet;
        DogModel mDataSetTempItem = (DogModel) item;
        for(int x=0; x<mDataSetTemp.size(); x++){
            if(mDataSetTemp.get(x).getId().equals(mDataSetTempItem.getId())){
                mDataSetTemp.get(x).updateModel(mDataSetTempItem.modelGet());
                this.mDataSet.set(x, (DogModel) mDataSetTemp.get(x));
                notifyItemChanged(x);
                return;
            }
        }
        this.mDataSet.add((DogModel) item);
        notifyItemInserted(getItemCount());
    }
    public void addRegisterScheduled (Object item){
        ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
        ScheduledModel mDataSetTempItem = (ScheduledModel) item;
        for(int x=0; x<mDataSetTemp.size(); x++){
            if(mDataSetTemp.get(x).getId().equals(mDataSetTempItem.getId())){
                mDataSetTemp.get(x).updateModel(mDataSetTempItem.modelGet());
                this.mDataSet.set(x, (ScheduledModel) mDataSetTemp.get(x));
                notifyItemChanged(x);
                return;
            }
        }
        this.mDataSet.add((ScheduledModel) item);
        notifyItemInserted(getItemCount());
    }
    public void removeRegisterScheduled(Object item) {
        if (mDataSet == null) return;
        ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
        ScheduledModel mDataSetTempItem = (ScheduledModel) item;
        for (int x = 0; x < mDataSetTemp.size(); x++) {
            if (mDataSetTemp.get(x).getId().equals(mDataSetTempItem.getId())) {
                this.mDataSet.remove(x);
                //mDataSetTemp.remove(x);
                notifyItemChanged(x);
                notifyItemRemoved(x);
                notifyItemRangeChanged(x, this.mDataSet.size());
                notifyItemRangeRemoved(x, this.mDataSet.size());
                if(this.mDataSet.size() == 0){
                    mDataSet = new ArrayList<>();
                }
                getItemCount();
                return;
            }
        }
    }
}
