package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmailLogin, editTextSenhaLogin;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmailLogin = findViewById(R.id.editEmailLogin);
        editTextSenhaLogin = findViewById(R.id.editSenhaLogin);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    public void validarAutenticacaoUsuario(View view){
        String email = editTextEmailLogin.getText().toString();
        String senha = editTextSenhaLogin.getText().toString();

        if (!email.isEmpty()){
            if (!senha.isEmpty()){
                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);
                logarUsuario(usuario);
            }else{
                Toast.makeText(this, "Digite sua senha", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
        }
    }

    public void logarUsuario(Usuario usuario){
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                abrirTelaPrincipal();
            }else{
                String excecao = "";
                try {
                    throw task.getException();
                }catch (FirebaseAuthInvalidUserException e){
                    excecao = "Email nao cadastrado";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Email ou senha incorretos";
                }catch (Exception e){
                    excecao = "Erro ao logar" + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, "Erro ao logar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}