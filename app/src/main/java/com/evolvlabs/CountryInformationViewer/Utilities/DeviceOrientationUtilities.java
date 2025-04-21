package com.evolvlabs.CountryInformationViewer.Utilities;

import android.content.Context;
import android.content.res.Configuration;

/**
 * @author : Paulo Cantos, Santiago Arellano
 * @date : 17-Apr-2025
 * @description : El presente archivo abstrae los metodos usadas para la configuracion del
 * dispositivo dentro de la aplicacion, anteriormente todos estos metodos cambiaban en su
 * implementacion y la idea de esta clase es automatizar y segregar estos cambios a metodos
 * especificos
 */
public class DeviceOrientationUtilities {

    public static boolean determineIfDeviceIsTablet(Context contextFromTheOutside){
        //? 1. Obtenemos la mascara de bits del tamano, como se mostraba en el ejemplo de flag quiz
        int screenConfigurationSizeFromDevice =
                contextFromTheOutside.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        //? 2. Revisamos y retornamos un valor dependiendo del tipo de pantalla
        if (screenConfigurationSizeFromDevice == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenConfigurationSizeFromDevice == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return true;
        }
        //? 3. Si no es una tablet retornamos falso directamente.
        return false;
    }

    public static boolean determineIfDeviceOrientationIsPortrait(Context contextFromTheOutside){
        //? 1. Obtenemos la mascara de bits de la orientacion del dispositivo
        int screenOrientationFromDevice = contextFromTheOutside
                .getResources().getConfiguration().orientation;

        //? 2. Revisamos y retornamos un valor dependiendo de la orientacion del dispositivo
        if (screenOrientationFromDevice == Configuration.ORIENTATION_PORTRAIT){
            return true;
        }
        //? 3. Si no es portrait retornamos falso
        return false;
    }
}
