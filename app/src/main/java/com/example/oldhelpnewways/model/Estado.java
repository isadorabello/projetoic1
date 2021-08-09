package com.example.oldhelpnewways.model;

import com.example.oldhelpnewways.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Estado {
    private String id;
    private String estadoAtual;
    private String alteracao;

    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference ususariosRef = firebaseRef.child("alertas").child(getId());
        ususariosRef.setValue( this );
    }

    public Estado() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstadoAtual() {
        return estadoAtual;
    }

    public void setEstadoAtual(String estadoAtual) {
        this.estadoAtual = estadoAtual;
    }

    public String getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(String alteracao) {
        this.alteracao = alteracao;
    }
}
