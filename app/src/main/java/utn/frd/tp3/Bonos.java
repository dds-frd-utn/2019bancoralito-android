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

public class Bonos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonos);

        findViewById(R.id.bonos_btnEnviar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlRealizarTranf = new MainActivity.Datos().getUrlRalito() + "/movimiento/realizar";

                //valores del form
                EditText text = findViewById(R.id.bonos_idCuentaOrigen);
                String cuenta_origen = text.getText().toString();

                //uso la cuenta destino 0 como la del banco central
                String cuenta_destino = "0";


                String monto = "50";
                try{
                    //Realizo el movimiento en mi db
                    JSONObject movimiento = new JSONObject();
                    movimiento.put("cuenta_origen", cuenta_origen);
                    movimiento.put("cuenta_destino", cuenta_destino);
                    movimiento.put("tipo_movimiento", 4);
                    movimiento.put("monto",monto);

                    String jsonString = movimiento.toString();
                    Log.i("myTag", jsonString);
                    new Bonos.MiAsyncTask().execute(urlRealizarTranf,"POST",jsonString);

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

            try{

                JSONObject jsonResponse = new JSONObject(result);
                //si tiene este campo viene de mi api
                if(jsonResponse.has("flag_error")){
                    int flag_error = jsonResponse.getInt("flag_error");
                    //Si no hubo error, como saldo insuficiente
                    if(flag_error == 0){
                        //Lo compro en el BC
                        String urlCompraBono = new MainActivity.Datos().getUrlEsferopolis() + "/bono";
                        String codigo = "";
                        EditText text2 = findViewById(R.id.bonos_idTipoBono);
                        String idTipoBono = text2.getText().toString();

                        if(idTipoBono == "1"){
                            codigo = "ESF01";
                        }else{
                            codigo = "ESF02";
                        }
                        JSONObject compraBono = new JSONObject();
                        compraBono.put("idInversiones", codigo);
                        compraBono.put("idBanco", 16);
                        compraBono.put("montoInvertido", 50);
                        new Bonos.MiAsyncTask().execute(urlCompraBono,"POST",compraBono.toString());
                        //preparo una respuesta para mostrar
                        result = jsonResponse.getString("mensaje");
                    }else{
                        result = jsonResponse.getString("error");
                    }
                }
                final TextView helloTextView = findViewById(R.id.text_bonos);
                helloTextView.setText(result);
                Log.i("myTag", result);
            }catch (JSONException e ){
                Log.i("myTag", e.getMessage());
            }


        }
    }
}
