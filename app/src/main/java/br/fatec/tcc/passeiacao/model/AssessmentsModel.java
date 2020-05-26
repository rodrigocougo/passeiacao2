package br.fatec.tcc.passeiacao.model;

public class AssessmentsModel {
    private long id_assessment;
    private long id_user;
    private String image;
    private String title;
    private double ratingBar;
    private String comment;
    private String createAt;

    public AssessmentsModel() {
    }

    public AssessmentsModel(long id_assessment, long id_user, String image, String title, double ratingBar, String comment, String createAt) {
        this.id_assessment = id_assessment;
        this.id_user = id_user;
        this.image = image;
        this.title = title;
        this.ratingBar = ratingBar;
        this.comment = comment;
        this.createAt = createAt;
    }

    public long getId_assessment() {
        return id_assessment;
    }

    public void setId_assessment(long id_assessment) {
        this.id_assessment = id_assessment;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRatingBar() {
        return ratingBar;
    }

    public void setRatingBar(double ratingBar) {
        this.ratingBar = ratingBar;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
