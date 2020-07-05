package br.fatec.tcc.passeiacao.model;

public class DogModel {
    private String id;
    private String name;
    private Integer age;        //idade
    private String breed;       //raça
    private Double weight;      //peso
    private String genre;       //Sexo
    //private Double height;      //altura
    private Boolean castrated;  //castrado
    private String comments;    //comentarios
    private String id_user;
    private String image_dog;

    public DogModel() {
    }

    public DogModel(String id, String name, Integer age, String breed, Double weight, String genre, Boolean castrated, String comments) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.genre = genre;
        this.castrated = castrated;
        this.comments = comments;
    }

    public DogModel(String name, Integer age, String breed, Double weight, String genre, Boolean castrated, String comments, String image_dog) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.genre = genre;
        this.castrated = castrated;
        this.comments = comments;
        this.image_dog = image_dog;
    }

    public DogModel(String id, String name, Integer age, String breed, Double weight, String genre, Boolean castrated, String comments, String id_user, String image_dog) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.genre = genre;
        this.castrated = castrated;
        this.comments = comments;
        this.id_user = id_user;
        this.image_dog = image_dog;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public String getImage_dog() {
        return image_dog;
    }

    public void setImage_dog(String image_dog) {
        this.image_dog = image_dog;
    }

    public DogModel modelGet (){
        return new DogModel(
                getId(),
                getName(),
                getAge(),
                getBreed(),
                getWeight(),
                getGenre(),
                getCastrated(),
                getComments(),
                getId_user(),
                getImage_dog()
        );
    }
    public void updateModel (DogModel dogModel){
        this.setId(dogModel.getId());
        this.setName(dogModel.getName());
        this.setAge(dogModel.getAge());
        this.setBreed(dogModel.getBreed());
        this.setWeight(dogModel.getWeight());
        this.setGenre(dogModel.getGenre());
        this.setCastrated(dogModel.getCastrated());
        this.setComments(dogModel.getComments());
        this.setId_user(dogModel.getId_user());
        this.setImage_dog(dogModel.getImage_dog());
    }
}
