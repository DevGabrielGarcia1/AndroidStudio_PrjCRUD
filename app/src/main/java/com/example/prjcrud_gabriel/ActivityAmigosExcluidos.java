package com.example.prjcrud_gabriel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prjcrud_gabriel.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class ActivityAmigosExcluidos extends AppCompatActivity{

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    RecyclerView recyclerView;
    DbAmigosAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setVisibility(View.INVISIBLE);



        binding.toolbar.setTitle("Amigos excluídos");
        //binding.toolbar.setNavigationIcon(R.mipmap.arrow);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //setTitle("Amigos Excluídos");



        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {



                //Botão aomigos excluidos -> Restaurar tudo
                if(item.getItemId() == R.id.action_cleared){
                    DbAmigosDAO dao = new DbAmigosDAO(getBaseContext());
                    if(dao.listarAmigos("status == 30").size() == 0){
                        Snackbar.make(binding.getRoot(),"Não há amigos a serem restaurados!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return true;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(binding.getRoot().getContext());
                    builder.setTitle("Confirmação")
                            .setMessage("Tem certeza que deseja restaurar todos os amigos?")
                            .setPositiveButton("Restaurar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean sucesso = dao.restaurarAll();
                                    if(sucesso) {
                                        Snackbar.make(binding.getRoot(),"Todos os amigos foram restaurado!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        configurarRecycler();
                                    }else{
                                        Snackbar.make(binding.getRoot(), "Erro ao restaurar os amigos!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .create()
                            .show();
                    return true;
                }

                //Botão apagar tudo
                if(item.getItemId() == R.id.action_clearAll){
                    DbAmigosDAO dao = new DbAmigosDAO(getBaseContext());
                    if(dao.listarAmigos("status == 30").size() == 0){
                        Snackbar.make(binding.getRoot(),"Não há amigos a serem excluidos!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return true;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(binding.getRoot().getContext());
                    builder.setTitle("Confirmação")
                            .setMessage("Tem certeza que deseja excluir definitivamente todos os amigos?\n\nEssa operação não pode ser desfeita!")
                            .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean sucesso = dao.excluirAllInDB();
                                    if(sucesso) {
                                        Snackbar.make(binding.getRoot(),"Todos os amigos foram excluidos definitivamene do dispositivo!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        configurarRecycler();
                                    }else{
                                        Snackbar.make(binding.getRoot(), "Erro ao excluir os amigos!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .create()
                            .show();
                    return true;
                }

                return false;
            }
        });

        this.configurarRecycler();
        this.updateTotalList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_cleared).setTitle("Restaurar tudo");
        return true;
    }

    private void configurarRecycler() {
        // Ativando o layou para uma lista tipo RecyclerView e configurando-a
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Preparando o adapter para associar os objetos à lista.
        DbAmigosDAO dao = new DbAmigosDAO(this);

        ActivityAmigosExcluidos _this = this;
        //Modificando adapter
        adapter = new DbAmigosExcluidosAdapter(dao.listarAmigos("status == 30"), this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.updateTotalList();
    }

    private int getTotalList() {
        DbAmigosDAO dao = new DbAmigosDAO(this);
        return dao.listarAmigos("status == 30").size();
    }

    private void updateTotalList() {
        //Mostra quantidadde de registros na lista
        TextView lbl_totalLista = (TextView) findViewById(R.id.lbl_totalLista);
        lbl_totalLista.setText("Você tem " + String.valueOf(this.getTotalList()) + " amigos excluídos.");
    }

}
