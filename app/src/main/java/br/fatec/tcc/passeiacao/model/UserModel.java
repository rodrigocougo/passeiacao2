package br.fatec.tcc.passeiacao.model;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.fatec.tcc.passeiacao.Util;

/* Está classe sera responsavel pelo cadastro de usuario: dono/passeador
 * Com a definição dos campos "Dono: Boolean", "Passeador: Boolean" para marcar o tipoUser*/
@Entity
public class UserModel {
    @PrimaryKey(autoGenerate = true)
    private long id_auto;
    private String id;
    private String Nome;
    private String Email;
    private String Telefone;
    private String Sexo;
    private String Usuario;
    private String Senha;
    private String CPF;
    private String Nasc;
    private String Cep;
    private String Numero;
    private String Endereco;
    private String Complemento;
    private String Bairro;
    private String Cidade;
    private double Note = 0.0;
    private Integer Canceled = 0;
    private Integer Concluded = 0;
    private String ImageCover = "";
    private String ImageAvatar = "";
    /* Campos chaves da definição do tipoUser (Dono/Passeador) */
    private Boolean Dono = false;
    private Boolean Passeador = false;
    private Boolean Auth;   //Confirma se usuario esta auth no firebase;
    private String CreateAt;
    private String RegisterCreateAt;
    private String UpdateAt;
    private String RegisterUpdateAt;
    private Boolean Active;
    private double Latitude = 0;
    private double Longitude = 0;

    public UserModel() {
    }

    public UserModel(String id, String nome, String email, String telefone, String sexo, String usuario, String senha, String CPF, String nasc, String cep, String numero, String endereco, String complemento, String bairro, String cidade, double note, Integer canceled, Integer concluded, String imageCover, String imageAvatar, Boolean dono, Boolean passeador, Boolean auth, String createAt, String registerCreateAt, String updateAt, String registerUpdateAt, double latitude, double longitude) {
        this.id = id;
        Nome = nome;
        Email = email;
        Telefone = telefone;
        Sexo = sexo;
        Usuario = usuario;
        Senha = senha;
        this.CPF = CPF;
        Nasc = nasc;
        Cep = cep;
        Numero = numero;
        Endereco = endereco;
        Complemento = complemento;
        Bairro = bairro;
        Cidade = cidade;
        Note = note;
        Canceled = canceled;
        Concluded = concluded;
        ImageCover = imageCover;
        ImageAvatar = imageAvatar;
        Dono = dono;
        Passeador = passeador;
        Auth = auth;
        CreateAt = createAt;
        RegisterCreateAt = registerCreateAt;
        UpdateAt = updateAt;
        RegisterUpdateAt = registerUpdateAt;
        Latitude = latitude;
        Longitude = longitude;
    }

    public UserModel(String id, String nome, String email, String telefone, String sexo, String usuario, String senha, String CPF, String nasc, String cep, String numero, String endereco, String complemento, String bairro, String cidade, String imageCover, String imageAvatar, Boolean dono, Boolean passeador, Boolean auth, String updateAt, String registerUpdateAt) {
        this.id = id;
        Nome = nome;
        Email = email;
        Telefone = telefone;
        Sexo = sexo;
        Usuario = usuario;
        Senha = senha;
        this.CPF = CPF;
        Nasc = nasc;
        Cep = cep;
        Numero = numero;
        Endereco = endereco;
        Complemento = complemento;
        Bairro = bairro;
        Cidade = cidade;
        ImageCover = imageCover;
        ImageAvatar = imageAvatar;
        Dono = dono;
        Passeador = passeador;
        Auth = auth;
        UpdateAt = updateAt;
        RegisterUpdateAt = registerUpdateAt;
    }

    public long getId_auto() {
        return id_auto;
    }

    public void setId_auto(long id_auto) {
        this.id_auto = id_auto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
    }

    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getNasc() {
        return Nasc;
    }

    public void setNasc(String nasc) {
        Nasc = nasc;
    }

    public String getCep() {
        return Cep;
    }

    public void setCep(String cep) {
        Cep = cep;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getEndereco() {
        return Endereco;
    }

    public void setEndereco(String endereco) {
        Endereco = endereco;
    }

    public String getComplemento() {
        return Complemento;
    }

    public void setComplemento(String complemento) {
        Complemento = complemento;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public double getNote() {
        BigDecimal bd = new BigDecimal(Note).setScale(1, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    public void setNote(double note) {
        Note = note;
    }

    public Integer getCanceled() {
        return Canceled;
    }

    public void setCanceled(Integer canceled) {
        Canceled = canceled;
    }

    public Integer getConcluded() {
        return Concluded;
    }

    public void setConcluded(Integer concluded) {
        Concluded = concluded;
    }

    public Boolean getDono() {
        return Dono;
    }

    public void setDono(Boolean dono) {
        Dono = dono;
    }

    public Boolean getPasseador() {
        return Passeador;
    }

    public void setPasseador(Boolean passeador) {
        Passeador = passeador;
    }

    public Boolean getAuth() {
        return Auth;
    }

    public void setAuth(Boolean auth) {
        Auth = auth;
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

    public String getImageCover() {
        return ImageCover;
    }

    public void setImageCover(String imageCover) {
        ImageCover = imageCover;
    }

    public String getImageAvatar() {
        return ImageAvatar;
    }

    public void setImageAvatar(String imageAvatar) {
        ImageAvatar = imageAvatar;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    /*Conversões de dados*/
    public Integer getNoteUserConverter() {
        int result = (int) getNote();
        return result;
    }

    public String getNascConvertAge() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNasc = new Date();
        try {
            if (Nasc != "") {
                dateNasc = formato.parse(Nasc);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Integer age = Util.getIdade(dateNasc);
        return String.valueOf(age);
    }

    public UserModel modelGet() {
        return new UserModel(
                getId(),
                getNome(),
                getEmail(),
                getTelefone(),
                getSexo(),
                getUsuario(),
                getSenha(),
                getCPF(),
                getNasc(),
                getCep(),
                getNumero(),
                getEndereco(),
                getComplemento(),
                getBairro(),
                getCidade(),
                getNote(),
                getCanceled(),
                getConcluded(),
                getImageCover(),
                getImageAvatar(),
                getDono(),
                getPasseador(),
                getAuth(),
                getCreateAt(),
                getRegisterCreateAt(),
                getUpdateAt(),
                getRegisterUpdateAt(),
                getLatitude(),
                getLongitude()
        );
    }

    /*public void updateModel(UserModel userModel) {
        this.setId(userModel.getId());
        this.setId(userModel.getNome());
        this.setId(userModel.getEmail());
        this.setId(userModel.getTelefone());
        this.setId(userModel.getSexo());
        this.setId(userModel.getUsuario());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
        this.setId(userModel.getId());
    }*/
}
