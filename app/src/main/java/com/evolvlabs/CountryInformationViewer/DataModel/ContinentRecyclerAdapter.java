package com.evolvlabs.CountryInformationViewer.DataModel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.ReturnThis;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.evolvlabs.CountryInformationViewer.Utilities.ConfirmationDialogBuilder;
import com.evolvlabs.countryinformationviewer.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;


/**
 * @author: Paulo Cantos, Santiago Arellano
 * @date: 15-Apr-2025
 * @description: El presente archivo implementa un adaptador complejo para el manejo de un Recycler
 * View en lugar de un list View, este es un gusto que teniamos porque habiamos usado siempre
 * ListViews y por tanto trabajar con Recycler view seria nuevo y atractivo dado su metodologia de
 * trabajo
 */
public class ContinentRecyclerAdapter
        extends RecyclerView.Adapter<ContinentRecyclerAdapter.ContinentViewHolder> {

    /*! Parametros Internos*/
    private final ApplicationDataPOJO viewModelPassedFromOutside;
    private final Context contextPassedFromTheOutside;

    /*Constructor de la clase general*/
    public ContinentRecyclerAdapter(ApplicationDataPOJO applicationDataPOJOFromTheOutside,
                                    Context contextPassedFromTheOutside) {
        //? 1. Guardamos la conexion directamente en el adapter, para tener conexion, mientras el
        // adapter siga vivo con los datos de la app
        this.viewModelPassedFromOutside = applicationDataPOJOFromTheOutside;
        this.contextPassedFromTheOutside = contextPassedFromTheOutside;
    }


    public static class ContinentViewHolder extends RecyclerView.ViewHolder {

        private ImageView inHereIsYourInfo_ImageViewForFlag;
        private TextView inHereIsYourInfo_TextViewForCountryName;

        public ContinentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.inHereIsYourInfo_ImageViewForFlag =
                    itemView.findViewById(R.id.inHereIsYourInfo_ImageViewForFlag);
            this.inHereIsYourInfo_TextViewForCountryName =
                    itemView.findViewById(R.id.inHereIsYourInfo_TextViewForCountryName);

        }

        public void bind(String continent,
                         Bitmap continetIconBitmap) {
            this.inHereIsYourInfo_ImageViewForFlag.setImageBitmap(continetIconBitmap);
            this.inHereIsYourInfo_TextViewForCountryName.setText(continent);
        }

        public void bindOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                this.inHereIsYourInfo_TextViewForCountryName.setOnClickListener(listener);
                this.inHereIsYourInfo_ImageViewForFlag.setOnClickListener(listener);
                this.itemView.setOnClickListener(listener);
            }
        }

        public void bindOnLongClickListener(View.OnLongClickListener listener) {
            if (listener != null) {
                this.inHereIsYourInfo_TextViewForCountryName.setOnLongClickListener(listener);
                this.inHereIsYourInfo_ImageViewForFlag.setOnLongClickListener(listener);
                this.itemView.setOnLongClickListener(listener);
            }
        }
    }

    @NonNull
    @Override
    public @NotNull ContinentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        //? 1. Inflamos el layout especifico para esta vista
        View inflatedViewForLayout =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.custom_recycler_view_layout,
                                 viewGroup, false);
        //? 2. Retornamos el ViewHolder interno creado automaticamente
        return new ContinentViewHolder(inflatedViewForLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContinentViewHolder s, int i) {
        //? Para cargar los datos, tenemos que revisar la lista y obtener la posicion de la lista
        // que tenemos internamente
        if (viewModelPassedFromOutside.constantContinentList.getValue() != null) {
            String continentName =
                    viewModelPassedFromOutside.constantContinentList.getValue().get(i);
            Bitmap continentIconBitmap = null;
            if (viewModelPassedFromOutside.constantContinentIconMap.getValue() != null) {
                continentIconBitmap =
                        viewModelPassedFromOutside.constantContinentIconMap.getValue().get(continentName);
            }
            else {
                try (InputStream stream =
                             contextPassedFromTheOutside.getAssets().open("Icons/"+ continentName)){
                    continentIconBitmap =
                            BitmapFactory.decodeStream(stream);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            s.bind(continentName, continentIconBitmap);
            s.bindOnClickListener(new OnContinentClickListener());
            s.bindOnLongClickListener(new OnContinentLongClickListener());
        }
    }

    @Override
    public int getItemCount() {
        return ((viewModelPassedFromOutside.constantContinentList.getValue() != null) ?
                viewModelPassedFromOutside.constantContinentList.getValue().size() : 0);
    }


    private class OnContinentClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //? 1. Aqui tenemos que actualizar los datos internos del ApplicationDataPOJO, para
            // que reflejen la seleccion del continente, este dato se guarda especificamente en
            // un formato especial en la String que se pone en la vista
            if (v.findViewById(R.id.inHereIsYourInfo_TextViewForCountryName) != null) {
                //? 1.1 Tomamos el texto dentro del view
                String continentName =
                        ((TextView) v.findViewById(R.id.inHereIsYourInfo_TextViewForCountryName))
                                .getText().toString();

                // 2. Tomamos el texto y revisamos si existe este continente dentro del
                // Application Data
                if (viewModelPassedFromOutside.constantContinentList.getValue() != null) {
                    if (viewModelPassedFromOutside.constantContinentList
                            .getValue()
                            .contains(continentName)) {
                        // 3. Si existe, entonces actualizamos el valor del continente seleccionado
                        viewModelPassedFromOutside.setSelectedContinentIntoMutableData(continentName);
                    }
                }
            }
        }
    }

    private class OnContinentLongClickListener implements View.OnLongClickListener {


        @Override
        public boolean onLongClick(View v) {
            //? 1. Aqui tenemos que eliminar un continente del listado general, lo que nos
            // permitiria actualizar los datos y las tablas asociadas con estos valores

            if (v.findViewById(R.id.inHereIsYourInfo_TextViewForCountryName) != null) {
                //? 1.1 Tomamos el texto dentro del view
                String continentName =
                        ((TextView) v.findViewById(R.id.inHereIsYourInfo_TextViewForCountryName))
                                .getText().toString();

                // 2. Tomamos el texto y revisamos si existe este continente dentro del
                // Application Data
                if (viewModelPassedFromOutside.constantContinentList.getValue() != null) {
                    if (viewModelPassedFromOutside.constantContinentList
                            .getValue()
                            .contains(continentName)) {

                        // 3. Si llegamos aqui, tenemos que trabajar para cargar un pequeno modal
                        // para confirmar que el usuario quiere eliminar el continente
                        ConfirmationDialogBuilder.showConfirmationDialog(contextPassedFromTheOutside.getString(R.string.removing_contient_dialog_header, continentName),
                                                                         contextPassedFromTheOutside.getString(R.string.removing_continent_confirmation_message),
                                                                         () -> {
                                                                                   viewModelPassedFromOutside.removeConstantContinentFromContinentList(continentName);
                                                                               }
                                , v.getContext(), v.getResources());
                        return true;
                    }
                }
            }
            return false;
        }


    }


}
