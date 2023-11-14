package com.example.prjcrud_gabriel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import com.example.prjcrud_gabriel.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    RecyclerView recyclerView;
    DbAmigosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Botão amigos excluidos
                if(item.getItemId() == R.id.action_cleared){
                    //Activity activity = (Activity)binding.getRoot().getContext();
                    Intent intent = new Intent(MainActivity.this, ActivityAmigosExcluidos.class);
                    //intent.putExtra("mainActivity", );
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    //activity.finish();
                    startActivity(intent);
                }

                //Botão apagar tudo
                if(item.getItemId() == R.id.action_clearAll){
                    DbAmigosDAO dao = new DbAmigosDAO(getBaseContext());
                    if(dao.listarAmigos().size() == 0){
                        Snackbar.make(binding.getRoot(),"Não há amigos a serem excluidos!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return true;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(binding.getRoot().getContext());
                    builder.setTitle("Confirmação")
                            .setMessage("Tem certeza que deseja excluir todos os amigos?")
                            .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    boolean sucesso = dao.excluirAll();
                                    if(sucesso) {
                                        Snackbar.make(binding.getRoot(),"Todos os amigos foram excluidos!", Snackbar.LENGTH_LONG)
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


        Intent intent = getIntent();
        if (intent.hasExtra("amigo")) {
            findViewById(R.id.include_cadastro).setVisibility(View.VISIBLE);
            findViewById(R.id.include_listagem).setVisibility(View.INVISIBLE);
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.lblCadastrarAmigoTitle)).setText("Editar Amigo");
            amigoAlterado = (DbAmigo) intent.getSerializableExtra("amigo");
            EditText txt_cadastrarNome = (EditText) findViewById(R.id.txt_cadastrarNome);
            EditText txt_cadastrarCelular = (EditText) findViewById(R.id.txt_cadastrarCelular);

            txt_cadastrarNome.setText(amigoAlterado.getNome());
            txt_cadastrarCelular.setText(amigoAlterado.getCelular());
            int status = 2;
        }


        /*
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        */

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.include_listagem).setVisibility(View.INVISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.VISIBLE);
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                findViewById(R.id.toolbar).setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.lblCadastrarAmigoTitle)).setText("Cadastrar Amigo");

                EditText txt_cadastrarNome = (EditText) findViewById(R.id.txt_cadastrarNome);
                EditText txt_cadastrarCelular = (EditText) findViewById(R.id.txt_cadastrarCelular);
                txt_cadastrarNome.setText("");
                txt_cadastrarCelular.setText("");
            }
        });

        Button btnCadastrarCancelar = (Button) findViewById(R.id.btn_CadastrarCancelar);
        btnCadastrarCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Esconder teclado
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                Snackbar.make(view, "Cancelando...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                findViewById(R.id.include_listagem).setVisibility(View.VISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
            }
        });

        EditText txtCadastrarCelular = (EditText) findViewById(R.id.txt_cadastrarCelular);
        txtCadastrarCelular.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    txtCadastrarCelular.setText(filtroCelular(txtCadastrarCelular.getText().toString()));
            }
        });
        txtCadastrarCelular.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == 66){
                    txtCadastrarCelular.setText(filtroCelular(txtCadastrarCelular.getText().toString()));
                    return true;
                }
                return false;
            }
        });

        Button btnCadastarSalvar = (Button) findViewById(R.id.btn_CadastrarSalvar);
        btnCadastarSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Esconder teclado
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                txtCadastrarCelular.setText(filtroCelular(txtCadastrarCelular.getText().toString()));

                // Sincronizando os campos com o contexto
                EditText txt_cadastrarNome = (EditText) findViewById(R.id.txt_cadastrarNome);
                EditText txt_cadastrarCelular = (EditText) findViewById(R.id.txt_cadastrarCelular);

                if (!validarNumero(String.valueOf(txt_cadastrarCelular.getText()))) {
                    Snackbar.make(view, "O telefone deve estár no formato (XX)9XXXX-XXXX ou (XX)XXXX-XXXX.", Snackbar.LENGTH_LONG)
                            .setAction("Ação", null).show();
                    return;
                }
                Snackbar.make(view, "Salvando...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                findViewById(R.id.include_listagem).setVisibility(View.VISIBLE);
                findViewById(R.id.include_cadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                findViewById(R.id.toolbar).setVisibility(View.VISIBLE);

                // Adaptando atributos
                String nome = txt_cadastrarNome.getText().toString();
                String celular = txt_cadastrarCelular.getText().toString();
                int situacao = 1;

                // Gravando no banco de dados
                DbAmigosDAO dao = new DbAmigosDAO(getBaseContext());
                boolean sucesso;
                if (amigoAlterado != null) {
                    sucesso = dao.salvar(amigoAlterado.getId(), nome, celular, 20);
                } else {
                    sucesso = dao.salvar(nome, celular, 10);
                }

                if (sucesso) {
                    DbAmigo amigo = dao.ultimoAmigo();

                    if (amigoAlterado != null) {
                        adapter.atualizarAmigo(amigo);
                        amigoAlterado = null;
                        configurarRecycler();
                    } else {
                        adapter.inserirAmigo(amigo);
                    }
                    Snackbar.make(view, "Dados de [" + nome + "] salvos com sucesso!", Snackbar.LENGTH_LONG)
                            .setAction("Ação", null).show();
                    updateTotalList();
                } else {
                    Snackbar.make(view, "Erro ao salvar, consulte o log!", Snackbar.LENGTH_LONG)
                            .setAction("Ação", null).show();
                }
            }
        });

        configurarRecycler();
        updateTotalList();
        if (getTotalList() == 0) {
            fab.performClick();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus == true){
            configurarRecycler();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    */

    private void configurarRecycler() {

        // Ativando o layou para uma lista tipo RecyclerView e configurando-a
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Preparando o adapter para associar os objetos à lista.
        DbAmigosDAO dao = new DbAmigosDAO(this);
        adapter = new DbAmigosAdapter(dao.listarAmigos(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        updateTotalList();
    }

    DbAmigo amigoAlterado = null;

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; (i < spinner.getCount()) && !(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)); i++)
            ;
        return index;
    }

    private int getTotalList() {
        DbAmigosDAO dao = new DbAmigosDAO(this);
        return dao.listarAmigos().size();
    }

    private void updateTotalList() {
        //Mostra quantidadde de registros na lista
        TextView lbl_totalLista = (TextView) findViewById(R.id.lbl_totalLista);
        lbl_totalLista.setText("Você tem " + String.valueOf(this.getTotalList()) + " amigos cadastrados.");
    }

    private String filtroCelular(String num){
        num = num.replace("+","").replace("(","").replace(")","").replace("-","").replace(" ","");
        if(num.length() == 8) {
            String format = String.format("9%c%c%c%c-%c%c%c%c", num.charAt(0), num.charAt(1), num.charAt(2), num.charAt(3),num.charAt(4), num.charAt(5), num.charAt(6), num.charAt(7));
            return format;
        }
        if(num.length() == 9) {
            String format = String.format("%c%c%c%c%c-%c%c%c%c", num.charAt(0), num.charAt(1), num.charAt(2), num.charAt(3),num.charAt(4), num.charAt(5), num.charAt(6), num.charAt(7), num.charAt(8));
            return format;
        }
        if(num.length() == 10) {
            String format = String.format("(%c%c)9%c%c%c%c-%c%c%c%c", num.charAt(0), num.charAt(1), num.charAt(2), num.charAt(3),num.charAt(4), num.charAt(5), num.charAt(6), num.charAt(7), num.charAt(8), num.charAt(9));
            return format;
        }
        if(num.length() == 11) {
            String format = String.format("(%c%c)%c%c%c%c%c-%c%c%c%c", num.charAt(0), num.charAt(1), num.charAt(2), num.charAt(3),num.charAt(4), num.charAt(5), num.charAt(6), num.charAt(7), num.charAt(8), num.charAt(9), num.charAt(10));
            return format;
        }
        if(num.length() == 12) {
            String format = String.format("+%c%c(%c%c)%c%c%c%c-%c%c%c%c", num.charAt(0), num.charAt(1), num.charAt(2), num.charAt(3),num.charAt(4), num.charAt(5), num.charAt(6), num.charAt(7), num.charAt(8), num.charAt(9), num.charAt(10), num.charAt(11));
            return format;
        }
        if(num.length() == 13) {
            String format = String.format("+%c%c(%c%c)%c%c%c%c%c-%c%c%c%c", num.charAt(0), num.charAt(1), num.charAt(2), num.charAt(3),num.charAt(4), num.charAt(5), num.charAt(6), num.charAt(7), num.charAt(8), num.charAt(9), num.charAt(10), num.charAt(11), num.charAt(12));
            return format;
        }
        return num;
    }

    private boolean validarNumero(String num) {
        return num.matches("^(\\+\\d{1,4})? ?(\\(?\\d{2}\\)?)? ?9?\\d{4}-\\d{4}$");
    }
}