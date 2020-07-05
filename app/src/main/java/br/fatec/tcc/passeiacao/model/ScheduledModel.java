package br.fatec.tcc.passeiacao.model;

public class ScheduledModel {
    private String id;
    private String id_owner;
    private String id_walker;
    private Boolean send_invitation = false;        //Enviou o interesse
    private Boolean received_invitation = false;    //Recebeu o interesse
    private Boolean confirmed_invitation = false;   //Confirmou o interesse (agendou)
    private Boolean initiated_invitation = false;   //Sinalizou o inicio do passeio
    private Boolean confirmed_initiated_invitation = false;   //Dono confirmou o inicio do passeio
    private Boolean done_invitation = false;        //Sinalizou o fim do passeio
    private Boolean confirmed_done_invitation = false;  //Confirmou o fim do passeio (Apenas o Dono pode) ETAPA FINAL
    private Boolean confirmed_done_closed_walker = false;  //Confirmou o fim do passeio e a remoção dele da lista de scheduled
    private Boolean confirmed_done_closed_owner = false;  //Confirmou o fim do passeio e a remoção dele da lista de scheduled
    // ### COMPLETA O AGENDAMENTO NESTE PONTO ACIMA ###
    private Boolean canceled_invitation = false;

    /*Campos extras para atualização do card*/
    private String image_walker;
    private String title_walker;
    private String address_walker;

    private String image_owner;
    private String title_owner;
    private String address_owner;

    /* Avaliação do usuario */
    private double assessment_note_walker = 0;
    private String assessment_message_walker;
    private String assessment_date_walker;

    private double assessment_note_owner = 0;
    private String assessment_message_owner;
    private String assessment_date_owner;

    public ScheduledModel() {
    }

    /*public ScheduledModel(String id, String id_owner, String id_walker, Boolean send_invitation, Boolean received_invitation, Boolean confirmed_invitation, Boolean initiated_invitation, Boolean confirmed_initiated_invitation, Boolean done_invitation, Boolean confirmed_done_invitation, Boolean canceled_invitation) {
        this.id = id;
        this.id_owner = id_owner;
        this.id_walker = id_walker;
        this.send_invitation = send_invitation;
        this.received_invitation = received_invitation;
        this.confirmed_invitation = confirmed_invitation;
        this.initiated_invitation = initiated_invitation;
        this.confirmed_initiated_invitation = confirmed_initiated_invitation;
        this.done_invitation = done_invitation;
        this.confirmed_done_invitation = confirmed_done_invitation;
        this.canceled_invitation = canceled_invitation;
    }
*/

    /*public ScheduledModel(String id, String id_owner, String id_walker, Boolean send_invitation, Boolean received_invitation, Boolean confirmed_invitation, Boolean initiated_invitation, Boolean confirmed_initiated_invitation, Boolean done_invitation, Boolean confirmed_done_invitation, Boolean confirmed_done_closed_walker, Boolean confirmed_done_closed_owner, Boolean canceled_invitation) {
        this.id = id;
        this.id_owner = id_owner;
        this.id_walker = id_walker;
        this.send_invitation = send_invitation;
        this.received_invitation = received_invitation;
        this.confirmed_invitation = confirmed_invitation;
        this.initiated_invitation = initiated_invitation;
        this.confirmed_initiated_invitation = confirmed_initiated_invitation;
        this.done_invitation = done_invitation;
        this.confirmed_done_invitation = confirmed_done_invitation;
        this.confirmed_done_closed_walker = confirmed_done_closed_walker;
        this.confirmed_done_closed_owner = confirmed_done_closed_owner;
        this.canceled_invitation = canceled_invitation;
    }*/

    public ScheduledModel(String id, String id_owner, String id_walker, Boolean send_invitation, Boolean received_invitation, Boolean confirmed_invitation, Boolean initiated_invitation, Boolean confirmed_initiated_invitation, Boolean done_invitation, Boolean confirmed_done_invitation, Boolean confirmed_done_closed_walker, Boolean confirmed_done_closed_owner, Boolean canceled_invitation, double assessment_note_walker, String assessment_message_walker, String assessment_date_walker, double assessment_note_owner, String assessment_message_owner, String assessment_date_owner) {
        this.id = id;
        this.id_owner = id_owner;
        this.id_walker = id_walker;
        this.send_invitation = send_invitation;
        this.received_invitation = received_invitation;
        this.confirmed_invitation = confirmed_invitation;
        this.initiated_invitation = initiated_invitation;
        this.confirmed_initiated_invitation = confirmed_initiated_invitation;
        this.done_invitation = done_invitation;
        this.confirmed_done_invitation = confirmed_done_invitation;
        this.confirmed_done_closed_walker = confirmed_done_closed_walker;
        this.confirmed_done_closed_owner = confirmed_done_closed_owner;
        this.canceled_invitation = canceled_invitation;
        this.assessment_note_walker = assessment_note_walker;
        this.assessment_message_walker = assessment_message_walker;
        this.assessment_date_walker = assessment_date_walker;
        this.assessment_note_owner = assessment_note_owner;
        this.assessment_message_owner = assessment_message_owner;
        this.assessment_date_owner = assessment_date_owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_owner() {
        return id_owner;
    }

    public void setId_owner(String id_owner) {
        this.id_owner = id_owner;
    }

    public String getId_walker() {
        return id_walker;
    }

    public void setId_walker(String id_walker) {
        this.id_walker = id_walker;
    }

    public Boolean getSend_invitation() {
        return send_invitation;
    }

    public void setSend_invitation(Boolean send_invitation) {
        this.send_invitation = send_invitation;
    }

    public Boolean getReceived_invitation() {
        return received_invitation;
    }

    public void setReceived_invitation(Boolean received_invitation) {
        this.received_invitation = received_invitation;
    }

    public Boolean getConfirmed_invitation() {
        return confirmed_invitation;
    }

    public void setConfirmed_invitation(Boolean confirmed_invitation) {
        this.confirmed_invitation = confirmed_invitation;
    }

    public Boolean getInitiated_invitation() {
        return initiated_invitation;
    }

    public void setInitiated_invitation(Boolean initiated_invitation) {
        this.initiated_invitation = initiated_invitation;
    }

    public Boolean getDone_invitation() {
        return done_invitation;
    }

    public void setDone_invitation(Boolean done_invitation) {
        this.done_invitation = done_invitation;
    }

    public Boolean getConfirmed_done_invitation() {
        return confirmed_done_invitation;
    }

    public void setConfirmed_done_invitation(Boolean confirmed_done_invitation) {
        this.confirmed_done_invitation = confirmed_done_invitation;
    }

    public Boolean getCanceled_invitation() {
        return canceled_invitation;
    }

    public void setCanceled_invitation(Boolean canceled_invitation) {
        this.canceled_invitation = canceled_invitation;
    }

    public String getImage_walker() {
        return image_walker;
    }

    public void setImage_walker(String image_walker) {
        this.image_walker = image_walker;
    }

    public String getTitle_walker() {
        return title_walker;
    }

    public void setTitle_walker(String title_walker) {
        this.title_walker = title_walker;
    }

    public String getAddress_walker() {
        return address_walker;
    }

    public void setAddress_walker(String address_walker) {
        this.address_walker = address_walker;
    }

    public String getImage_owner() {
        return image_owner;
    }

    public void setImage_owner(String image_owner) {
        this.image_owner = image_owner;
    }

    public String getTitle_owner() {
        return title_owner;
    }

    public void setTitle_owner(String title_owner) {
        this.title_owner = title_owner;
    }

    public String getAddress_owner() {
        return address_owner;
    }

    public void setAddress_owner(String address_owner) {
        this.address_owner = address_owner;
    }

    public Boolean getConfirmed_initiated_invitation() {
        return confirmed_initiated_invitation;
    }

    public void setConfirmed_initiated_invitation(Boolean confirmed_initiated_invitation) {
        this.confirmed_initiated_invitation = confirmed_initiated_invitation;
    }

    public Boolean getConfirmed_done_closed_walker() {
        return confirmed_done_closed_walker;
    }

    public void setConfirmed_done_closed_walker(Boolean confirmed_done_closed_walker) {
        this.confirmed_done_closed_walker = confirmed_done_closed_walker;
    }

    public Boolean getConfirmed_done_closed_owner() {
        return confirmed_done_closed_owner;
    }

    public void setConfirmed_done_closed_owner(Boolean confirmed_done_closed_owner) {
        this.confirmed_done_closed_owner = confirmed_done_closed_owner;
    }

    public double getAssessment_note_walker() {
        return assessment_note_walker;
    }

    public void setAssessment_note_walker(double assessment_note_walker) {
        this.assessment_note_walker = assessment_note_walker;
    }

    public String getAssessment_message_walker() {
        return assessment_message_walker;
    }

    public void setAssessment_message_walker(String assessment_message_walker) {
        this.assessment_message_walker = assessment_message_walker;
    }

    public String getAssessment_date_walker() {
        return assessment_date_walker;
    }

    public void setAssessment_date_walker(String assessment_date_walker) {
        this.assessment_date_walker = assessment_date_walker;
    }

    public double getAssessment_note_owner() {
        return assessment_note_owner;
    }

    public void setAssessment_note_owner(double assessment_note_owner) {
        this.assessment_note_owner = assessment_note_owner;
    }

    public String getAssessment_message_owner() {
        return assessment_message_owner;
    }

    public void setAssessment_message_owner(String assessment_message_owner) {
        this.assessment_message_owner = assessment_message_owner;
    }

    public String getAssessment_date_owner() {
        return assessment_date_owner;
    }

    public void setAssessment_date_owner(String assessment_date_owner) {
        this.assessment_date_owner = assessment_date_owner;
    }

    public ScheduledModel modelGet (){
        return new ScheduledModel(
                getId(),
                getId_owner(),
                getId_walker(),
                getSend_invitation(),
                getReceived_invitation(),
                getConfirmed_invitation(),
                getInitiated_invitation(),
                getConfirmed_initiated_invitation(),
                getDone_invitation(),
                getConfirmed_done_invitation(),
                getConfirmed_done_closed_walker(),
                getConfirmed_done_closed_owner(),
                getCanceled_invitation(),
                getAssessment_note_walker(),
                getAssessment_message_walker(),
                getAssessment_date_walker(),
                getAssessment_note_owner(),
                getAssessment_message_owner(),
                getAssessment_date_owner()
        );
    }
    public void updateModel (ScheduledModel scheduledModel){
        this.setId(scheduledModel.getId());
        this.setId_owner(scheduledModel.getId());
        this.setId_walker(scheduledModel.getId_walker());
        this.setSend_invitation(scheduledModel.getSend_invitation());
        this.setReceived_invitation(scheduledModel.getReceived_invitation());
        this.setConfirmed_invitation(scheduledModel.getConfirmed_invitation());
        this.setConfirmed_initiated_invitation(scheduledModel.getConfirmed_initiated_invitation());
        this.setInitiated_invitation(scheduledModel.getInitiated_invitation());
        this.setDone_invitation(scheduledModel.getDone_invitation());
        this.setConfirmed_done_invitation(scheduledModel.getConfirmed_done_invitation());
        this.setConfirmed_done_closed_walker(scheduledModel.getConfirmed_done_closed_walker());
        this.setConfirmed_done_closed_owner(scheduledModel.getConfirmed_done_closed_owner());
        this.setCanceled_invitation(scheduledModel.getCanceled_invitation());
        this.setAssessment_note_walker(scheduledModel.getAssessment_note_walker());
        this.setAssessment_message_walker(scheduledModel.getAssessment_message_walker());
        this.setAssessment_date_walker(scheduledModel.getAssessment_date_walker());
        this.setAssessment_note_owner(scheduledModel.getAssessment_note_owner());
        this.setAssessment_message_owner(scheduledModel.getAssessment_message_owner());
        this.setAssessment_date_owner(scheduledModel.getAssessment_date_owner());
    }
}
