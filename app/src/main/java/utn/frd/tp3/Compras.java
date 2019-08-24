package utn.frd.tp3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Compras extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras);

        findViewById(R.id.compras_btnEnviar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlRealizarTranf = new MainActivity.Datos().getUrlRalito() + "/movimiento/realizar";

                //valores del form
                EditText text = findViewById(R.id.compras_idCuentaOrigen);
                String cuenta_origen = text.getText().toString();

                EditText text2 = findViewById(R.id.compras_idCuentaDestino);
                String cuenta_destino = text2.getText().toString();

                EditText text3 = findViewById(R.id.compras_monto);
                String monto = text3.getText().toString();
                try{
                    JSONObject transferencia = new JSONObject();
                    transferencia.put("cuenta_origen", cuenta_origen);
                    transferencia.put("cuenta_destino", cuenta_destino);
                    transferencia.put("tipo_movimiento", 3);
                    transferencia.put("monto",monto);

                    String jsonString = transferencia.toString();
                    Log.i("myTag", jsonString);
                    new Compras.MiAsyncTask().execute(urlRealizarTranf,"POST",jsonString);
                }catch(JSONException e ){
                    Log.i("myTag", e.getMessage());

                }
            }

        });

    }
    class MiAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings){
            String method = strings[1];
            if(method == "GET"){
                return MainActivity.RESTService.makeGetRequest(strings[0]);
            }else{

                String jsonParam = strings[2];
                return MainActivity.RESTService.callREST(strings[0],strings[1],jsonParam);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            final TextView helloTextView = findViewById(R.id.text_compras);
            try{
                JSONObject jsonResponse = new JSONObject(result);
                if(jsonResponse.has("flag_error")){
                    if(jsonResponse.getInt("flag_error") == 0){
                        result = jsonResponse.getString("mensaje");
                    }else {
                        result = jsonResponse.getString("error");
                    }
                }

            }catch (JSONException e){
                Log.i("myTag", e.getMessage());
            }

            helloTextView.setText(result);
            Log.i("myTag", result);

        }
    }
}
