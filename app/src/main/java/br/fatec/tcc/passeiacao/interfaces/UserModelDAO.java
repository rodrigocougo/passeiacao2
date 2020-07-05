package br.fatec.tcc.passeiacao.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.List;

import br.fatec.tcc.passeiacao.model.UserModel;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserModelDAO {
    @Query("select * from UserModel where Auth = 1 limit 1")
    LiveData<UserModel> getUserSign();

    @Query("select * from UserModel where Email = :VFemail limit 1")
    UserModel getUserbyEmail(String VFemail);

    @Query("update UserModel set Auth = 0 where Auth = 1")
    void removeAuthAllUsers();

    @Query("update UserModel set Auth = 1 where Email = :VFemail")
    void setAuthUserEmail(String VFemail);

    @Insert(onConflict = REPLACE)
    void addUser(UserModel User);

    @Delete
    void deleteUser(UserModel User);

    @Query("select count(*) from UserModel")
    int getCountUser();

    @Query("delete from UserModel")
    void deleteAll();

    @Query("update UserModel set " +
            "id = :IdUser, " +
            "Nome = :Nome, " +
            "Email = :Email," +
            "Telefone = :Telefone," +
            "Sexo = :Sexo," +
            "Usuario = :Usuario," +
            "Senha = :Senha," +
            "CPF = :CPF," +
            "Nasc = :Nasc," +
            "Cep = :Cep," +
            "Numero = :Numero," +
            "Endereco = :Endereco," +
            "Complemento = :Complemento," +
            "Bairro = :Bairro," +
            "Cidade = :Cidade," +
            "Dono = :Dono," +
            "Passeador = :Passeador," +
            "Canceled = :Canceleds," +
            "Concluded = :Concludeds," +
            "Note = :Note, " +
            "ImageCover = :Cover," +
            "ImageAvatar = :Avatar," +
            "Latitude = :Latitude," +
            "Longitude = :Longitude" +
            " where Email = :Email")
    void updateUserOne(
            String Nome,
            String Email,
            String Telefone,
            String Sexo,
            String Usuario,
            String Senha,
            String CPF,
            String Nasc,
            String Cep,
            String Numero,
            String Endereco,
            String Complemento,
            String Bairro,
            String Cidade,
            Integer Dono,
            Integer Passeador,
            Integer Canceleds,
            Integer Concludeds,
            Double Note,
            String Avatar,
            String Cover,
            double Latitude,
            double Longitude,
            String IdUser);


}
