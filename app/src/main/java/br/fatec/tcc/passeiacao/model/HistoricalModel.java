package br.fatec.tcc.passeiacao.model;

public class HistoricalModel {

    private Long id_auto;
    private String id_user_post;
    private String Comment;
    private String Note;
    private String CreateAt;
    private String RegisterCreateAt;
    private String UpdateAt;
    private String RegisterUpdateAt;
    private Boolean Active;

    public HistoricalModel() {
    }

    public HistoricalModel(String id_user_post, String comment, String note, String createAt, String registerCreateAt, String updateAt, String registerUpdateAt, Boolean active) {
        this.id_user_post = id_user_post;
        Comment = comment;
        Note = note;
        CreateAt = createAt;
        RegisterCreateAt = registerCreateAt;
        UpdateAt = updateAt;
        RegisterUpdateAt = registerUpdateAt;
        Active = active;
    }

    public Long getId_auto() {
        return id_auto;
    }

    public void setId_auto(Long id_auto) {
        this.id_auto = id_auto;
    }

    public String getId_user_post() {
        return id_user_post;
    }

    public void setId_user_post(String id_user_post) {
        this.id_user_post = id_user_post;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(String createAt) {
        CreateAt = createAt;
    }

    public String getRegisterCreateAt() {
        return RegisterCreateAt;
    }

    public void setRegisterCreateAt(String registerCreateAt) {
        RegisterCreateAt = registerCreateAt;
    }

    public String getUpdateAt() {
        return UpdateAt;
    }

    public void setUpdateAt(String updateAt) {
        UpdateAt = updateAt;
    }

    public String getRegisterUpdateAt() {
        return RegisterUpdateAt;
    }

    public void setRegisterUpdateAt(String registerUpdateAt) {
        RegisterUpdateAt = registerUpdateAt;
    }

    public Boolean getActive() {
        return Active;
    }

    public void setActive(Boolean active) {
        Active = active;
    }
}
