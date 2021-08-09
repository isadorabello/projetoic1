package com.example.oldhelpnewways.model;

import com.example.oldhelpnewways.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Parametros {

    private String idUsuario;
    private String batimentos;
    private String temperatura;
    private String oxigenacao;
    private String data;

    public Parametros() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ususariosRef = firebaseRef.child("parametros").child(getIdUsuario()).child("21062021");
        ususariosRef.setValue( this );
    }


    public String getIdUsuario() {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuario=autenticacao.getCurrentUser().getUid();
        return idUsuario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getBatimentos() {
        return batimentos;
    }

    public void setBatimentos(String batimentos) {
        this.batimentos = batimentos;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getOxigenacao() {
        return oxigenacao;
    }

    public void setOxigenacao(String oxigenacao) {
        this.oxigenacao = oxigenacao;
    }
}
