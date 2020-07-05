package br.fatec.tcc.passeiacao.model;

public class AssessmentsModel {
    private String id_assessment;
    private String id_user;
    private String image;
    private String title;
    private double ratingBar;
    private String comment;
    private String createAt;

    public AssessmentsModel() {
    }

    public AssessmentsModel(String id_assessment, String id_user, String image, String title, double ratingBar, String comment, String createAt) {
        this.id_assessment = id_assessment;
        this.id_user = id_user;
        this.image = image;
        this.title = title;
        this.ratingBar = ratingBar;
        this.comment = comment;
        this.createAt = createAt;
    }

    public String getId_assessment() {
        return id_assessment;
    }

    public void setId_assessment(String id_assessment) {
        this.id_assessment = id_assessment;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
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

    public AssessmentsModel modelGet (){
        return new AssessmentsModel(
            getId_assessment(),
            getId_user(),
            getImage(),
            getTitle(),
            getRatingBar(),
            getComment(),
            getCreateAt()
        );
    }
    public void updateModel (AssessmentsModel assessmentsModel){
        this.setId_assessment(assessmentsModel.getId_assessment());
        this.setId_user(assessmentsModel.getId_user());
        this.setImage(assessmentsModel.getImage());
        this.setTitle(assessmentsModel.getTitle());
        this.setRatingBar(assessmentsModel.getRatingBar());
        this.setComment(assessmentsModel.getComment());
        this.setCreateAt(assessmentsModel.getCreateAt());
    }
}
