package com.example.oldhelpnewways.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oldhelpnewways.R;
import com.example.oldhelpnewways.helper.ConfiguracaoFirebase;
import com.example.oldhelpnewways.helper.UsuarioFirebase;
import com.example.oldhelpnewways.model.Parametros;
import com.example.oldhelpnewways.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Fragment_1 extends Fragment {
    private TextView textDataInicio, textBatimentos, textTemperatura, textOxigenacao;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private Usuario usuarioLogado;

    public Fragment_1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        inicializarComponentes(view);
        recuperarDados();

        return view;
    }

    public void inicializarComponentes(View view){
        textDataInicio = view.findViewById(R.id.textDataIncio);
        textBatimentos = view.findViewById(R.id.textBatimentos);
        textTemperatura = view.findViewById(R.id.textTemperatura);
        textOxigenacao = view.findViewById(R.id.textOxigenacao);
    }

    public void recuperarDados(){
        DatabaseReference usuarioRef = firebaseRef.child("parametros").child(autenticacao.getCurrentUser().getUid()).child("21062021");
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Parametros parametros = snapshot.getValue(Parametros.class);
                textBatimentos.setText(parametros.getBatimentos());
                textTemperatura.setText(parametros.getTemperatura());
                textOxigenacao.setText(parametros.getOxigenacao());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}