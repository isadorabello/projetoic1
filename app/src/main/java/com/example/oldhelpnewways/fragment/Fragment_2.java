package com.example.oldhelpnewways.fragment;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oldhelpnewways.R;
import com.example.oldhelpnewways.activity.MainActivity;
import com.example.oldhelpnewways.helper.ConfiguracaoFirebase;
import com.example.oldhelpnewways.model.Estado;
import com.example.oldhelpnewways.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Fragment_2 extends Fragment {

    private TextView textQueda, textAviso, textNome, textAlteracoes;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private Usuario usuarioLogado;
    private final static String NOTI = "1";
    private ImageView imagemAlerta;

    public Fragment_2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_2, container, false);

        createNotificationChannel();

        inicializarComponentes(view);
        recuperarDadosAlertas();
        recuperarDados();


        textQueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable s) {
                String texto = textQueda.getText().toString();

                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

                if(texto.equals("Perigo")){
                    textAviso.setText("O dispositivo detectou alteração em mais de um parâmetro vital!");
                    imagemAlerta.setImageResource(R.drawable.alertavermelhopng);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), NOTI)
                            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                            .setContentTitle("Alerta")
                            .setContentText("Algo esta errado! Por favor, verifique o estado do usuário!")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(1, builder.build());

                }
                if(texto.equals("Instável")){
                    textAviso.setText("O dispositivo detectou alteração em um parâmetro vital!");
                    imagemAlerta.setImageResource(R.drawable.alertaamarelopng);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), NOTI)
                            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
                            .setContentTitle("Alerta")
                            .setContentText("Algo esta errado! Por favor, verifique o estado do usuário!")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(1, builder.build());
                }
                if(texto.equals("Estável")){
                    textAviso.setText("Tudo está normal! :)");
                    imagemAlerta.setImageResource(R.drawable.alertaverdepng);
                }
            }
        });
        return view;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notificacao_estado);
            String description = getString(R.string.aviso);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTI, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(getContext(), NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("ResourceAsColor")
    public void inicializarComponentes(View v){
        //v.setBackgroundColor(R.color.branco);
        textQueda = v.findViewById(R.id.textQueda);
        textAviso = v.findViewById(R.id.textAviso);
        textAlteracoes = v.findViewById(R.id.textAlteracoes);
        textNome = v.findViewById(R.id.textNome);
        imagemAlerta = v.findViewById(R.id.imageAlerta);
    }

    public void recuperarDadosAlertas(){
        DatabaseReference usuarioRef = firebaseRef.child("alertas").child(autenticacao.getCurrentUser().getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Estado estado = snapshot.getValue(Estado.class);
                textQueda.setText(estado.getEstadoAtual());
                textAlteracoes.setText(estado.getAlteracao());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recuperarDados(){
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(autenticacao.getCurrentUser().getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                textNome.setText(usuario.getNome());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}