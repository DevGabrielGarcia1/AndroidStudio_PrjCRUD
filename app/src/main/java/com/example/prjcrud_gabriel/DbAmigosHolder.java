package com.example.prjcrud_gabriel;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class DbAmigosHolder extends RecyclerView.ViewHolder {
    public TextView nmAmigo;
    public TextView vlCelular;
    public ImageButton btnEditar;
    public ImageButton btnExcluir;
    public TextView lbl_totalLista;
    public TextView txtNome;
    public TextView txtCelular;

    public DbAmigosHolder(View itemView) {
        super(itemView);
        nmAmigo = (TextView) itemView.findViewById(R.id.txtNomeAmigo);
        vlCelular = (TextView) itemView.findViewById(R.id.txtCelularAmigo);
        btnEditar = (ImageButton) itemView.findViewById(R.id.btnEditAmigo);
        btnExcluir = (ImageButton) itemView.findViewById(R.id.btnRemoveAmigo);
        lbl_totalLista = (TextView) itemView.findViewById(R.id.lbl_totalLista);
        txtNome = (TextView) itemView.findViewById(R.id.txtNomeAmigo);
        txtCelular = (TextView) itemView.findViewById(R.id.txtCelularAmigo);
    }
}

