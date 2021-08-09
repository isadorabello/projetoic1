package com.example.oldhelpnewways.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oldhelpnewways.R;
import com.example.oldhelpnewways.helper.ConfiguracaoFirebase;
import com.example.oldhelpnewways.helper.UsuarioFirebase;
import com.example.oldhelpnewways.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Fragment_3 extends Fragment {

    private TextView textData, textNome, textComorbidades, textImportante;
    private CircleImageView imagePerfil;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private Usuario usuarioLogado;

    public Fragment_3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_3, container, false);

        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        inicializarComponentes(view);
        recuperarDados();
        //recupera foto usuario logado
        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if(caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(getActivity()).load(url).into(imagePerfil);
        }
        return view;
    }

    public void inicializarComponentes(View v){
        textData = v.findViewById(R.id.textData);
        textNome = v.findViewById(R.id.textNome);
        textComorbidades = v.findViewById(R.id.textComorbidades);
        textImportante = v.findViewById(R.id.textImportante);
        imagePerfil = v.findViewById(R.id.imageEditarPerfil);
    }

    public void recuperarDados(){
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(autenticacao.getCurrentUser().getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                textNome.setText(usuario.getNome());
                textData.setText(usuario.getDataNascimento());
                textComorbidades.setText(usuario.getComorbidades());
                textImportante.setText(usuario.getaImportantes());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}