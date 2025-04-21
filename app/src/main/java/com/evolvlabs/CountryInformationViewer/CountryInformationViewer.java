package com.evolvlabs.CountryInformationViewer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.evolvlabs.CountryInformationViewer.DataModel.ApplicationDataPOJO;
import com.evolvlabs.CountryInformationViewer.DataModel.CountryInformation;
import com.evolvlabs.countryinformationviewer.R;

import java.util.function.Consumer;

/**
 * @author : Paulo Cantos, Santiago Arellano
 * @date : 16-Apr-2025
 * @description : El presente archivo implementa el controlador base para toda la aplicacion, en
 * esta seccion se determina el layout del dispositivo y el manejo de la estructuracion de
 * fragmentos y orientacion de estos para una mejor experiencia visual. Internamente, se trabaja con
 * {@link com.evolvlabs.CountryInformationViewer.DataModel.ApplicationDataPOJO} para manejar un
 * pseudo-singleton definido por Android en base a ViewModel, lo que nos permite trabajar para tener
 * nuestros datos, siempre activos y vivos durante la ejecucion entera de la app.
 */
public class CountryInformationViewer extends AppCompatActivity {

    private ApplicationDataPOJO dataModelForApp;
    private boolean isCurrentDeviceATable = false;
    private boolean isCurrentDeviceInLandscapeMode = false;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable
                            Bundle savedInstanceState) {
        //? 1. Llamada a super basica para manejar la inicializacion interna de la Activity
        super.onCreate(savedInstanceState);

        //? 2. Llamada a un metodo interno para inicializar la informacion de la aplicacion
        this.initializeApplicationDataPOJOForInternalUse();
        //? 3.  Detectamos la configuracion de la pantalla e inflamos el layout principal

        //? 4. Cargamos el layout file
        this.basedOnConfigurationInflateAppriateDeviceLayout();

        dataModelForApp.updateMutableDeviceMode(getApplicationContext());
        dataModelForApp.updateMutableDeviceOrientation(getApplicationContext());
    }



    private void initializeApplicationDataPOJOForInternalUse() {
        //? 1. Para iniciar este metodo debemos inicializar la clase creandola, esta linea
        // utiliza la architectura determinada por andorid para manejar un ViewModel, este tipo
        // de declaracion crea un view model especificamente establecido para que todos los
        // fragmentos tengan accesso al mismo dato.
        this.dataModelForApp = new ViewModelProvider(this).get(ApplicationDataPOJO.class);
    }


    private void basedOnConfigurationInflateAppriateDeviceLayout(){
        //? 1. Dependiendo de la configuracion del dispositivo, inflamos el layout adecuado
        setContentView(R.layout.main_activity_fragment_holder);
    }

    @Override
    public void onConfigurationChanged(@Nullable @org.jetbrains.annotations.Nullable
                                       Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.dataModelForApp.updateMutableDeviceMode(getApplicationContext());
        this.dataModelForApp.updateMutableDeviceOrientation(getApplicationContext());
    }



}
