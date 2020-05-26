package br.fatec.tcc.passeiacao.model;

public class DogModel {
    private String id;
    private String name;
    private Integer age;    //idade
    private String breed;   //raça
    private Double weight;  //peso
    private Double height;  //altura
    private Boolean castrated;  //castrado
    private String comments;    //comentarios
    private String id_user;

    public DogModel() {
    }

    public DogModel(String id, String name, Integer age, String breed, Double weight, Double height, Boolean castrated, String comments) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.height = height;
        this.castrated = castrated;
        this.comments = comments;
    }

    public DogModel(String name, Integer age, String breed, Double weight, Double height, Boolean castrated, String comments) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.height = height;
        this.castrated = castrated;
        this.comments = comments;
    }

    public DogModel(String id, String name, Integer age, String breed, Double weight, Double height, Boolean castrated, String comments, String id_user) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.height = height;
        this.castrated = castrated;
        this.comments = comments;
        this.id_user = id_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Boolean getCastrated() {
        return castrated;
    }

    public void setCastrated(Boolean castrated) {
        this.castrated = castrated;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public DogModel modelGet (){
        return new DogModel(
                getId(),
                getName(),
                getAge(),
                getBreed(),
                getWeight(),
                getHeight(),
                getCastrated(),
                getComments(),
                getId_user()
        );
    }
    public void updateModel (DogModel dogModel){
        this.setId(dogModel.getId());
        this.setName(dogModel.getName());
        this.setAge(dogModel.getAge());
        this.setBreed(dogModel.getBreed());
        this.setWeight(dogModel.getWeight());
        this.setHeight(dogModel.getHeight());
        this.setCastrated(dogModel.getCastrated());
        this.setComments(dogModel.getComments());
        this.setId_user(dogModel.getId_user());
    }
}
