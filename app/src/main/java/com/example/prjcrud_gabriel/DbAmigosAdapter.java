package com.example.prjcrud_gabriel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.List;

public class DbAmigosAdapter extends RecyclerView.Adapter<DbAmigosHolder> {

    protected final List<DbAmigo> amigos;

    protected final Context mainView;

    public DbAmigosAdapter(List<DbAmigo> amigos, Context mainContext) {
        this.amigos = amigos;
        this.mainView = mainContext;
    }

    // Este método retorna o layout criado pela ViewHolder, inflado numa view
    @Override
    public DbAmigosHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DbAmigosHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_dados_amigo, parent, false));
    }

    // Recebe a ViewHolder e a posição da lista, de forma que um objeto da lista é recuperado pela posição e associado a ela - é o foco da ação para acontecer o processo
    @Override
    public void onBindViewHolder(DbAmigosHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nmAmigo.setText(amigos.get(position).getNome());
        holder.vlCelular.setText(amigos.get(position).getCelular());
        holder.btnEditar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity(v);
                Intent intent = activity.getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("amigo", amigos.get(position));
                activity.finish();
                activity.startActivity(intent);
            }
        });
        final DbAmigo amigo = amigos.get(position);
        holder.btnExcluir.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir o amigo ["+amigo.getNome().toString()+"]?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DbAmigo amigo = amigos.get(position);
                                DbAmigosDAO dao = new DbAmigosDAO(view.getContext());
                                boolean sucesso = dao.excluir(amigo.getId());
                                if(sucesso) {
                                    Snackbar.make(view, "Excluindo o amigo ["+amigo.getNome().toString()+"]!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    excluirAmigo(amigo);
                                    //Mostra quantidadde de registros na lista
                                    TextView lbl_totalLista = (TextView)((Activity)mainView).findViewById(R.id.lbl_totalLista);
                                    lbl_totalLista.setText("Você tem " + String.valueOf(amigos.size()) + " amigos cadastrados.");
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
        holder.txtNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!abrirWhatsapp(amigos.get(position).getCelular())){
                    Snackbar.make(v, "Aplicativo Whatsapp não está instalado no seu celular!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        holder.txtCelular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!abrirWhatsapp(amigos.get(position).getCelular())){
                    Snackbar.make(v, "Aplicativo Whatsapp não está instalado no seu celular!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    // Esta função retorna a quantidade de itens que há na lista. É importante verificar se a lista possui elementos, para não causar um erro de exceção.
    @Override
    public int getItemCount() {
        return amigos != null ? amigos.size() : 0;
    }

    public void inserirAmigo(DbAmigo amigo){
        amigos.add(amigo);
        notifyItemInserted(getItemCount());
    }

    public void atualizarAmigo(DbAmigo amigo){
        amigos.set(amigos.indexOf(amigo), amigo);
        notifyItemChanged(amigos.indexOf(amigo));
    }

    public void excluirAmigo(DbAmigo amigo)
    {
        int position = amigos.indexOf(amigo);
        amigos.remove(position);
        notifyItemRemoved(position);
    }


    private Activity getActivity(View view) {
        Context context = view.getContext();

        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private boolean abrirWhatsapp(String num){
        num = num.replace(" ","").replace("-","");
        num = num.replace("(","").replace(")","");
        num = num.replace("+","");
        num = PhoneNumberUtils.stripSeparators(num);
        try{
            //Abrir o whatsapp no contato expecifico
            Intent intent = new Intent(Intent.ACTION_SEND,Uri.parse("smsto:" + "" + num + "?body=" + ""));
            intent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
            intent.putExtra(intent.EXTRA_TEXT, "teste");
            mainView.startActivity(intent);

            //Abri dentro da activity do app
            /*Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + num + "?body=" + ""));
            intent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
            //intent.setPackage("com.whatsapp");
            mainView.startActivity(intent);*/
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}