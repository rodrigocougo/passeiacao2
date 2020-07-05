package br.fatec.tcc.passeiacao.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.io.Console;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.fatec.tcc.passeiacao.model.UserModel;

public class UserViewModel extends AndroidViewModel {

    private LiveData<UserModel> userAuth;
    private UserModel userEmail;
    private AppDatabase appDatabase;
    public static String VSemail_user = "";

    public UserViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        userAuth = appDatabase.itemAndUserModel().getUserSign();
    }

    public LiveData<UserModel> getUserAuth() {
        return userAuth;
    }

    /****************************************************************************************************/
    //Retorna apenas 1 USUARIO por EMAIL setado...
    public UserModel getUserEmail(String VFemail) {
        VSemail_user = VFemail;
        try {
            return new UserViewModel.getUserIDViewModel(appDatabase).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class getUserIDViewModel extends AsyncTask<UserModel, Void, UserModel> {

        private AppDatabase db;

        getUserIDViewModel(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected UserModel doInBackground(final UserModel... params) {
            UserModel retorno = db.itemAndUserModel().getUserbyEmail(VSemail_user);
            return retorno;
        }
    }

    /**************************************************************************************************/
    public UserModel getItemUser(String VFemail) {
        userEmail = appDatabase.itemAndUserModel().getUserbyEmail(VFemail);
        return userEmail;
    }

    /**************************************************************************************************/
    //Update Auth user...
    public void setAuthUserEmail(String VFemail) {
        this.VSemail_user = VFemail;
        new setAuthUserEmailViewModel(appDatabase).execute();
    }
    private static class setAuthUserEmailViewModel extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;

        setAuthUserEmailViewModel(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.itemAndUserModel().setAuthUserEmail(VSemail_user);
            return null;
        }
    }
    
    /**************************************************************************************************/
    //Add User...
    public void addUser(final UserModel user) {
        if(user == null) return;
        UserModel userModel = getUserEmail(user.getEmail());
        if(userModel != null){
            new UpdateUserViewModel(appDatabase,
                    user.getNome(),
                    user.getEmail(),
                    user.getTelefone(),
                    user.getSexo(),
                    user.getUsuario(),
                    user.getSenha(),
                    user.getCPF(),
                    user.getNasc(),
                    user.getCep(),
                    user.getNumero(),
                    user.getEndereco(),
                    user.getComplemento(),
                    user.getBairro(),
                    user.getCidade(),
                    user.getDono(),
                    user.getPasseador(),
                    user.getCanceled(),
                    user.getConcluded(),
                    user.getNote(),
                    user.getImageAvatar(),
                    user.getImageCover(),
                    user.getLatitude(),
                    user.getLongitude(),
                    user.getId()).execute();
        }else{
            new AddUserViewModel(appDatabase).execute(user);
        }
    }
    private static class AddUserViewModel extends AsyncTask<UserModel, Void, Void> {

        private AppDatabase db;

        AddUserViewModel(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final UserModel... params) {
            db.itemAndUserModel().addUser(params[0]);
            return null;
        }

    }
    private static class UpdateUserViewModel extends AsyncTask<UserModel, Void, Void> {

        private AppDatabase db;
        private String Nome;
        private String Email;
        private String Telefone;
        private String Sexo;
        private String Usuario;
        private String Senha;
        private String Cpf;
        private String Nasc;
        private String Cep;
        private String Numero;
        private String Endereco;
        private String Complemento;
        private String Bairro;
        private String Cidade;
        private int Dono;
        private int Passeador;
        private int Canceleds;
        private int Concludeds;
        private double Note;
        private String Avatar;
        private String Cover;
        private double Latitude;
        private double Longitude;
        private String IdUser;

        UpdateUserViewModel(AppDatabase appDatabase,
                            String nome, String email, String telefone, String sexo, String usuario, String senha, String cpf, String nasc, String cep, String numero, String endereco, String complemento, String bairro, String cidade, Boolean dono, Boolean passeador, Integer canceleds, Integer concludeds, Double note, String avatar, String cover, double latitude, double longitude, String idUser) {
            db = appDatabase;
            Nome = nome;
            Email = email;
            Telefone = telefone;
            Sexo = sexo;
            Usuario = usuario;
            Senha = senha;
            Cpf = cpf;
            Nasc = nasc;
            Cep = cep;
            Numero = numero;
            Endereco = endereco;
            Complemento = complemento;
            Bairro = bairro;
            Cidade = cidade;
            Dono = dono ? 1 : 0;
            Passeador = passeador ? 1 : 0;
            Canceleds = canceleds;
            Concludeds = concludeds;
            Note = note;
            Avatar = avatar;
            Cover = cover;
            Latitude = latitude;
            Longitude = longitude;
            IdUser = idUser;
        }

        @Override
        protected Void doInBackground(final UserModel... params) {
            db.itemAndUserModel().updateUserOne(Nome, Email, Telefone, Sexo, Usuario, Senha, Cpf, Nasc, Cep, Numero, Endereco, Complemento, Bairro, Cidade, Dono, Passeador, Canceleds, Concludeds, Note, Avatar, Cover, Latitude, Longitude, IdUser);
            return null;
        }

    }

    public void removeAuthAllUsers() {
        new UserViewModel.removeAuthAllUsersViewModel(appDatabase).execute();
    }
    private static class removeAuthAllUsersViewModel extends AsyncTask<Void, Void, Void> {

        private AppDatabase db;

        removeAuthAllUsersViewModel(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            db.itemAndUserModel().removeAuthAllUsers();
            return null;
        }

    }

}

