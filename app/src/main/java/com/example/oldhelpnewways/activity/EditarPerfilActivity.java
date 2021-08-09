package com.example.oldhelpnewways.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.oldhelpnewways.R;
import com.example.oldhelpnewways.helper.ConfiguracaoFirebase;
import com.example.oldhelpnewways.helper.UsuarioFirebase;
import com.example.oldhelpnewways.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imagemEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editDataPerfil, editComorbidadePerfil, editImportantePerfil;
    private Button buttonSalvar;
    private Usuario usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String identificadorUsuario;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        inicializarComponentes();

        //recuperar os dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText(usuarioPerfil.getDisplayName());
        recuperaDados();

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(EditarPerfilActivity.this).load(url).into(imagemEditarPerfil);
        }else{
            imagemEditarPerfil.setImageResource(R.drawable.avatar);
        }

        //salvar alterações do nome
        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualaziado = editNomePerfil.getText().toString();
                String dataAtualizada = editDataPerfil.getText().toString();
                String comorbidadeAtualizada = editComorbidadePerfil.getText().toString();
                String importanteAtualizada = editImportantePerfil.getText().toString();
                //atualizar no perfil
                UsuarioFirebase.atulizarNomeUsuario(nomeAtualaziado);

                //atualizar no banco de dados
                usuarioLogado.setNome(nomeAtualaziado);
                if(!dataAtualizada.isEmpty()){
                    usuarioLogado.setDataNascimento(dataAtualizada);
                }else{
                    usuarioLogado.setDataNascimento("Dado não informado");
                }
                if(!comorbidadeAtualizada.isEmpty()){
                    usuarioLogado.setComorbidades(comorbidadeAtualizada);
                }else{
                    usuarioLogado.setComorbidades("Dado não informado");
                }
                if(!importanteAtualizada.isEmpty()){
                    usuarioLogado.setaImportantes(importanteAtualizada);
                }else{
                    usuarioLogado.setaImportantes("Dado não informado");
                }
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //alterar foto do usuario
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    public void recuperaDados(){
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(autenticacao.getCurrentUser().getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                editDataPerfil.setText(usuario.getDataNascimento());
                editComorbidadePerfil.setText(usuario.getComorbidades());
                editImportantePerfil.setText(usuario.getaImportantes());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            Bitmap imagem = null;

            try {
                //seleção apenas da galeria de fotos
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                //caso tenha sido esclhida uma imagem
                if(imagem!=null){
                    //configura imagem na tela do usuario
                    imagemEditarPerfil.setImageBitmap(imagem);

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    final StorageReference imagemRef = storageRef.child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario +".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Recuperar o local da foto
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);
                                }
                            });

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizarFotoUsuario( Uri url){
        //atualizar a foto no perfil
        UsuarioFirebase.atulizarFotoUsuario(url);

        //atualizar a foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(EditarPerfilActivity.this,
                "Sua foto foi alterada",Toast.LENGTH_SHORT).show();
    }



    public void inicializarComponentes(){
        imagemEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editNomePerfil);
        editDataPerfil = findViewById(R.id.editDataPerfil);
        editComorbidadePerfil = findViewById(R.id.editComorbidadePerfil);
        editImportantePerfil = findViewById(R.id.editImportantePerfil);
        buttonSalvar = findViewById(R.id.buttonSalvar);
    }

}