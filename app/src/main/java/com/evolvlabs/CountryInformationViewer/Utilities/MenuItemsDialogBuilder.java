package com.evolvlabs.CountryInformationViewer.Utilities;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import androidx.core.content.res.ResourcesCompat;
import com.evolvlabs.countryinformationviewer.R;

import java.util.List;

/**
 * @author : Santiago Arellano
 * @description : El presente archivo implementa un mini menu interactivo para el usuario, requerido
 * por el trabajo que se da la carga y descarga de las imagenes para cargar los datos nuevamente y
 * permitirle al usuario tener todos los datos rapidamente
 */
public class MenuItemsDialogBuilder {

    public static void createAndShowMenuItemSelection(Context contextFromApplication,
                                                      View anchorViewForMenu,
                                                      List<Pair<String, String>> dialogTitleAndBody,
                                                      List<Pair<String, String>> dialogSucceedOrNotOptions,
                                                      List<Runnable> dialogOnSucceedRunnableOption,
                                                      String... menuOptionsForUser) {
        //> 1. En primera instancia revisamos los parametros enviados para ver que todos tengan
        //> valores
        if (contextFromApplication == null) {
            throw new NullPointerException("Error Code 0x0001 - [Raised] El parametro ingresado " +
                                                   "como contexto de la aplicacion fue nulo");
        } else if (anchorViewForMenu == null) {
            throw new NullPointerException(contextFromApplication
                                                   .getResources()
                                                   .getString(R.string.priority_error_code_message,
                                                              "The " +
                                                                      "value registered under the " +
                                                                      "anchorViewForMenu parameter " +
                                                                      "was null"));

        } else if (dialogTitleAndBody == null) {
            throw new NullPointerException(contextFromApplication
                                                   .getResources()
                                                   .getString(R.string.priority_error_code_message,
                                                              "The " +
                                                                      "value registered under the " +
                                                                      "dialogTitleAndBody parameter was null"));
        } else if (dialogSucceedOrNotOptions == null) {
            throw new NullPointerException(contextFromApplication
                                                   .getResources()
                                                   .getString(R.string.priority_error_code_message,
                                                              "The " +
                                                                      "value registered under the " +
                                                                      "dialogSucceedOrNotOptions " +
                                                                      "parameter was null"));
        } else if (dialogOnSucceedRunnableOption == null) {
            throw new NullPointerException(contextFromApplication
                                                   .getResources()
                                                   .getString(R.string.priority_error_code_message,
                                                              "The " +
                                                                      "value registered under the " +
                                                                      "dialogOnSucceedRunnableOption " +
                                                                      "parameter was null"));
        } else if (menuOptionsForUser == null) {
            throw new NullPointerException(contextFromApplication
                                                   .getResources()
                                                   .getString(R.string.priority_error_code_message,
                                                              "The " +
                                                                      "value registered under the " +
                                                                      "menuOptionsForUser parameter " +
                                                                      "was null"));
        }

        //> 2. Generamos el menu basados en la posicion del view ingresado y el contexto
        //> ingresado al metodo
        ListPopupWindow menuWindow = new ListPopupWindow(contextFromApplication);
        menuWindow.setWidth(800);
        menuWindow.setAdapter(new ArrayAdapter<>(contextFromApplication, R.layout.custom_list_menu_item_layout) {{
            addAll(menuOptionsForUser);
        }});
        menuWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(contextFromApplication.getResources(),
                                                                      com.google.android.material.R.drawable.m3_tabs_transparent_background
                , null));
        menuWindow.setAnchorView(anchorViewForMenu);
        ;
        menuWindow.setModal(true);
        menuWindow.setHorizontalOffset(-anchorViewForMenu.getWidth());

        int[] anchorLocation = new int[2];
        anchorViewForMenu.getLocationOnScreen(anchorLocation);

        int horizontalOffset =
                -(menuWindow.getWidth()/2) + (anchorViewForMenu.getWidth() / 2) ;

        int verticalOffset = -anchorViewForMenu.getHeight() - menuWindow.getHeight();

        menuWindow.setHorizontalOffset(horizontalOffset);
        menuWindow.setVerticalOffset(verticalOffset);



        //> 3. Cargamos los datos dependiendo del proceso del usuario
        menuWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //> 3.1 Cargamos un menu de opciones para el usuario definido por el menu
                // exterior, entonces tenemos que realizar un analisis de las posiciones de lo
                // que se tiene en el menu, se espera que el listado de opciones y contenido sea
                // el mismo orden de las opciones
                ConfirmationDialogBuilder.showConfirmationDialog(dialogTitleAndBody.get(position).first,
                                                                 dialogTitleAndBody.get(position).second,
                                                                 dialogSucceedOrNotOptions.get(position).first,
                                                                 dialogSucceedOrNotOptions.get(position).second,
                                                                 dialogOnSucceedRunnableOption.get(position),
                                                                 contextFromApplication,
                                                                 contextFromApplication.getResources());
            }
        });

        //> 4 Cargamos la posicion del modal para que aparezca arriba del modal
        menuWindow.show();
    }
}
