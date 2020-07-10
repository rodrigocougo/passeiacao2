package br.fatec.tcc.passeiacao.walker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.fatec.tcc.passeiacao.R;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickHistoricalFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickIFA;
import br.fatec.tcc.passeiacao.interfaces.InterfaceClickScheduledWalkersFA;
import br.fatec.tcc.passeiacao.model.DogModel;
import br.fatec.tcc.passeiacao.model.HistoricalModel;
import br.fatec.tcc.passeiacao.model.ScheduledModel;
import br.fatec.tcc.passeiacao.model.UserModel;

import static aplicacao.passeiacao.IS_HISTORICAL;
import static aplicacao.passeiacao.IS_SCHEDULED;
import static aplicacao.passeiacao.IS_SEARCH;

public class WalkersADP extends RecyclerView.Adapter<br.fatec.tcc.passeiacao.walker.adapters.WalkersADP.ViewHolder> {

    private ArrayList<Object> mDataSet;
    private Integer vTipoInterface;
    private InterfaceClickScheduledWalkersFA mInterfaceClickScheduledWalkersFA;
    private InterfaceClickHistoricalFA mInterfaceClickHistoricalFA;
    private Context context;

    public WalkersADP(ArrayList<Object> mDataSet, Integer vTipoInterface) {
        this.mDataSet = mDataSet;
        this.vTipoInterface = vTipoInterface;
    }

    public WalkersADP(ArrayList<Object> mDataSet, Integer vTipoInterface, Context context) {
        this.mDataSet = mDataSet;
        this.vTipoInterface = vTipoInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public br.fatec.tcc.passeiacao.walker.adapters.WalkersADP.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if (vTipoInterface == IS_SEARCH) {
            Log.d("WalkersSearchFRG", "CHEGOU 1a");
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner, parent, false);
        } else if (vTipoInterface == IS_SCHEDULED) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_walker_scheduled, parent, false);
        } else if (vTipoInterface == IS_HISTORICAL) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_owner_history, parent, false);
        }

        return new br.fatec.tcc.passeiacao.walker.adapters.WalkersADP.ViewHolder(v);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull br.fatec.tcc.passeiacao.walker.adapters.WalkersADP.ViewHolder holder, final int position) {
        if (vTipoInterface == IS_SEARCH) {
            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
            holder.lblTitleCardOwner.setText(mDataSetTemp.get(position).getTitle_walker());
            holder.lblSubTitleCardOwner.setText(mDataSetTemp.get(position).getAddress_owner());
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImage_owner())) {
                holder.imgAvatarOwner.setImageURI(Uri.parse(mDataSetTemp.get(position).getImage_owner()));
            }
        } else if (vTipoInterface == IS_SCHEDULED) {
            final ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
            holder.lblTitleCardWalker.setText(mDataSetTemp.get(position).getTitle_walker());
            holder.lblSubTitleCardWalker.setText(mDataSetTemp.get(position).getAddress_walker());
            holder.imgIconWalkerCancel.setVisibility(View.GONE);
            holder.imgIconWalkerBegin.setVisibility(View.GONE);
            holder.lblHighlighterOwnerScheduled.setVisibility(View.VISIBLE);
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImage_owner())) {
                holder.imgAvatarWalker.setImageURI(Uri.parse(mDataSetTemp.get(position).getImage_owner()));
            }
            if(mDataSetTemp.get(position).getInitiated_invitation()) {
                holder.lblHighlighterOwnerScheduled.setText("Aguardando\nconfirmação");
                if (mDataSetTemp.get(position).getConfirmed_initiated_invitation()) {
                    holder.lblHighlighterOwnerScheduled.setText("Passeio\nem andamento...");
                } else {
                    holder.lblHighlighterOwnerScheduled.setText("Aguardando\ninicio...");
                }
                if (mDataSetTemp.get(position).getDone_invitation()) {
                    holder.lblHighlighterOwnerScheduled.setText("Aguardando\nconfirmação");
                }
                if (mDataSetTemp.get(position).getConfirmed_done_invitation()) {
                    holder.lblHighlighterOwnerScheduled.setText("Passeio\nfinalizado");
                    holder.lblLinkWalkerScheduled.setText("ENCERRAR");
                    holder.lblLinkWalkerScheduled.setVisibility(View.VISIBLE);
                    holder.lblLinkWalkerScheduled.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mInterfaceClickScheduledWalkersFA != null) {
                                mInterfaceClickScheduledWalkersFA.onClickListenerScheduledClosed(mDataSetTemp.get(position));
                            }
                        }
                    });
                }
            }else if(mDataSetTemp.get(position).getCanceled_invitation()){
                holder.lblHighlighterOwnerScheduled.setText("Passeio\ncancelado");
                holder.lblLinkWalkerScheduled.setText("ENCERRAR");
                holder.lblHighlighterOwnerScheduled.setTextColor(Color.RED);
                holder.lblHighlighterOwnerScheduled.setVisibility(View.VISIBLE);
                holder.lblLinkWalkerScheduled.setVisibility(View.VISIBLE);
                holder.lblLinkWalkerScheduled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledWalkersFA != null) {
                            mInterfaceClickScheduledWalkersFA.onClickListenerScheduledClosed(mDataSetTemp.get(position));
                        }
                    }
                });
            }else{
                holder.imgIconWalkerCancel.setVisibility(View.VISIBLE);
                holder.imgIconWalkerBegin.setVisibility(View.VISIBLE);
                holder.lblHighlighterOwnerScheduled.setVisibility(View.GONE);
            }
        } else if (vTipoInterface == IS_HISTORICAL) {
            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
            holder.lblTitleCardOwnerHistory.setText(mDataSetTemp.get(position).getTitle_owner());
            holder.lblSubTitleCardOwnerHistory.setText(mDataSetTemp.get(position).getAddress_owner());
            if(URLUtil.isValidUrl(mDataSetTemp.get(position).getImage_owner())) {
                holder.imgAvatarOwnerHistory.setImageURI(Uri.parse(mDataSetTemp.get(position).getImage_owner()));
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
            holder.lblLink2OwnerHistory.setVisibility(View.INVISIBLE);  //Contratar novamente
            if(mDataSetTemp.get(position).getConfirmed_done_invitation() == true && mDataSetTemp.get(position).getAssessment_date_owner() == null) {
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

        //card_owner
        CardView cdvCardOwnerSearch;
        ImageView imgAvatarOwner;
        TextView lblTitleCardOwner;
        TextView lblSubTitleCardOwner;
        ImageView imgDenied;
        ImageView imgConfirmed;
        //card_walker_scheduled
        CardView cdvCardWalkerSearch;
        ImageView imgAvatarWalker;
        TextView lblTitleCardWalker;
        TextView lblSubTitleCardWalker;
        ImageView imgIconWalkerCancel;
        ImageView imgIconWalkerBegin;
        TextView lblHighlighterOwnerScheduled;
        TextView lblLinkWalkerScheduled;

        //historicals list
        CardView cdvCardOwnerHistory;
        ImageView imgAvatarOwnerHistory;
        TextView lblTitleCardOwnerHistory;
        TextView lblSubTitleCardOwnerHistory;
        TextView lblLinkOwnerHistory;
        TextView lblLink2OwnerHistory;

        public ViewHolder(View v) {
            super(v);
            if (vTipoInterface == IS_SEARCH) {
                imgAvatarOwner = v.findViewById(R.id.imgAvatarOwner);
                imgDenied = v.findViewById(R.id.imgDenied);
                imgConfirmed = v.findViewById(R.id.imgConfirmed);
                lblTitleCardOwner = v.findViewById(R.id.lblTitleCardOwner);
                lblSubTitleCardOwner = v.findViewById(R.id.lblSubTitleCardOwner);
                cdvCardOwnerSearch = v.findViewById(R.id.cdvCardOwnerSearch);
                cdvCardOwnerSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterfaceClickScheduledWalkersFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
                            mInterfaceClickScheduledWalkersFA.onClickListenerScheduledCard(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
                imgConfirmed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterfaceClickScheduledWalkersFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
                            mInterfaceClickScheduledWalkersFA.onClickListenerScheduledConfirmed(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
                imgDenied.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInterfaceClickScheduledWalkersFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
                            mInterfaceClickScheduledWalkersFA.onClickListenerScheduledDenied(mDataSetTemp.get(getPosition()));
                        }
                    }
                });
            } else if (vTipoInterface == IS_SCHEDULED) {
                imgAvatarWalker = v.findViewById(R.id.imgAvatarWalker);
                lblTitleCardWalker = v.findViewById(R.id.lblTitleCardWalker);
                lblSubTitleCardWalker = v.findViewById(R.id.lblSubTitleCardWalker);
                imgIconWalkerCancel = v.findViewById(R.id.imgIconWalkerCancel);
                imgIconWalkerBegin = v.findViewById(R.id.imgIconWalkerBegin);
                lblHighlighterOwnerScheduled = v.findViewById(R.id.lblHighlighterOwnerScheduled);
                lblLinkWalkerScheduled = v.findViewById(R.id.lblLinkWalkerScheduled);
                cdvCardWalkerSearch = v.findViewById(R.id.cdvCardWalkerSearch);
                cdvCardWalkerSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                imgIconWalkerCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledWalkersFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
                            mInterfaceClickScheduledWalkersFA.onClickListenerScheduledDenied(mDataSetTemp.get(getAdapterPosition()));
                        }
                    }
                });
                imgIconWalkerBegin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickScheduledWalkersFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
                            mInterfaceClickScheduledWalkersFA.onClickListenerScheduledConfirmed(mDataSetTemp.get(getAdapterPosition()));
                        }
                    }
                });
            } else if (vTipoInterface == IS_HISTORICAL) {
                cdvCardOwnerHistory = v.findViewById(R.id.cdvCardOwnerHistory);
                imgAvatarOwnerHistory = v.findViewById(R.id.imgAvatarOwnerHistory);
                lblTitleCardOwnerHistory = v.findViewById(R.id.lblTitleCardOwnerHistory);
                lblSubTitleCardOwnerHistory = v.findViewById(R.id.lblSubTitleCardOwnerHistory);
                lblLinkOwnerHistory = v.findViewById(R.id.lblLinkOwnerHistory);
                lblLink2OwnerHistory = v.findViewById(R.id.lblLink2OwnerHistory);
                lblHighlighterOwnerScheduled = v.findViewById(R.id.lblHighlighterOwnerScheduled);

                lblLinkOwnerHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickHistoricalFA != null) {
                            ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>)(List<?>) mDataSet;
                            mInterfaceClickHistoricalFA.onClickListenerAssessment(mDataSetTemp.get(getPosition()));
                        }
                    }
                });

                /*cdvCardOwnerHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mInterfaceClickIFA != null) {
                            ArrayList<HistoricalModel> mDataSetTemp = (ArrayList<HistoricalModel>)(List<?>) mDataSet;
                            mInterfaceClickIFA.onClickListenerUserProfile(mDataSetTemp.get(getPosition()));
                        }
                    }
                });*/
            }
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setInterfaceClickScheduledWalkersFA(InterfaceClickScheduledWalkersFA r) {
        mInterfaceClickScheduledWalkersFA = r;
    }
    public void setInterfaceClickHistoricalFA (InterfaceClickHistoricalFA r){
        mInterfaceClickHistoricalFA = r;
    }

    //Funções de add/remove/update no adaptador
    public void addRegister(Object mDataSet) {
        this.mDataSet.add((ScheduledModel) mDataSet);
        //notifyItemInserted(getItemCount());
    }
    public void addRegisterScheduled(Object item) {
        ArrayList<ScheduledModel> mDataSetTemp = (ArrayList<ScheduledModel>) (List<?>) mDataSet;
        ScheduledModel mDataSetTempItem = (ScheduledModel) item;
        for (int x = 0; x < mDataSetTemp.size(); x++) {
            if (mDataSetTemp.get(x).getId().equals(mDataSetTempItem.getId())) {
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
    public void removeAllScheduled(){
        mDataSet = new ArrayList<>();
    }

}
