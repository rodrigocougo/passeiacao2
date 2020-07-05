package br.fatec.tcc.passeiacao.interfaces;

public interface InterfaceClickScheduledFA {
    void onClickListenerScheduledCANCEL(Object selected, Boolean countMore);
    void onClickListenerScheduledBEGIN(Object selected);
    void onClickListenerScheduledDONE(Object selected);
    void onClickListenerScheduledCLOSED(Object selected);
    void onClickListenerLocationMap(Object selected);
}
