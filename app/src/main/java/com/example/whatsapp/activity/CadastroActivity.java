package com.example.whatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.UsuarioFirebase;
import com.example.whatsapp.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Base64;

public class CadastroActivity extends AppCompatActivity {
    
    private EditText editNome, editEmail, editSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        editEmail = findViewById(R.id.editEmailLogin);
        editNome = findViewById(R.id.editNome);
        editSenha = findViewById(R.id.editSenhaLogin);
    }
    
    public void cadastrarUsuario(Usuario usuario) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Toast.makeText(CadastroActivity.this, "Cadastro efetuado com sucesso", Toast.LENGTH_SHORT).show();
                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                finish();

                try {
                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(identificadorUsuario);
                    usuario.salvar();
                } catch (Exception e){
                    e.printStackTrace();
                }

            } else{
                String excecao = "";
                try{
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte";
                } catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Digite um email valido";
                } catch (FirebaseAuthUserCollisionException e){
                    excecao = "Ja existe uma conta com esse email";
                } catch (Exception e){
                    excecao = "Erro ao cadastrar usuario " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void validarCadastroUsuario(View view){
        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if (!nome.isEmpty()){
            if (!email.isEmpty()){
                if (!senha.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    cadastrarUsuario(usuario);
                } else{
                    Toast.makeText(this, "Preencha o campo senha", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Preencha o campo email", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(this, "Preencha o campo nome", Toast.LENGTH_SHORT).show();
        }
    }
}