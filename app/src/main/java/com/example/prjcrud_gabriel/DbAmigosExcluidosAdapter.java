package com.example.prjcrud_gabriel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class DbAmigosExcluidosAdapter extends DbAmigosAdapter {

    private ActivityAmigosExcluidos mainActivity;

    public DbAmigosExcluidosAdapter(List<DbAmigo> amigos, Context mainContext, ActivityAmigosExcluidos main) {
        super(amigos, mainContext);
        this.mainActivity = main;
    }

    @Override
    public void onBindViewHolder(DbAmigosHolder holder, @SuppressLint("RecyclerView") int position) {
        super.onBindViewHolder(holder, position);
        holder.btnEditar.setImageResource(R.mipmap.restore);
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja restaurar o amigo ["+amigos.get(position).getNome().toString()+"]?")
                        .setPositiveButton("Restaurar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DbAmigo amigo = amigos.get(position);
                                DbAmigosDAO dao = new DbAmigosDAO(v.getContext());
                                boolean sucesso = dao.restaurar(amigo.getId());
                                if(sucesso) {
                                    Snackbar.make(v, "Restaurando o amigo ["+amigo.getNome().toString()+"]!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    excluirAmigo(amigo);
                                    //Mostra quantidadde de registros na lista
                                    TextView lbl_totalLista = (TextView)((Activity)mainView).findViewById(R.id.lbl_totalLista);
                                    lbl_totalLista.setText("Você tem " + String.valueOf(dao.listarAmigos("status == 30").size()) + " amigos excluídos.");
                                    //mainActivity.configurarRecycler();
                                }else{
                                    Snackbar.make(v, "Erro ao restaurar o amigo ["+amigo.getNome().toString()+"]!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });
        holder.btnExcluir.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir definitivamente o amigo ["+amigos.get(position).getNome().toString()+"]?\n\nEssa operação não pode ser desfeita!")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DbAmigo amigo = amigos.get(position);
                                DbAmigosDAO dao = new DbAmigosDAO(view.getContext());
                                boolean sucesso = dao.excluirInDB(amigo.getId());
                                if(sucesso) {
                                    Snackbar.make(view, "Excluindo o amigo ["+amigo.getNome().toString()+"] do dispositivo!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    excluirAmigo(amigo);
                                    //Mostra quantidadde de registros na lista
                                    TextView lbl_totalLista = (TextView)((Activity)mainView).findViewById(R.id.lbl_totalLista);
                                    lbl_totalLista.setText("Você tem " + String.valueOf(amigos.size()) + " amigos excluídos.");
                                }else{
                                    Snackbar.make(view, "Erro ao excluir o amigo ["+amigo.getNome().toString()+"]!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });
    }

}