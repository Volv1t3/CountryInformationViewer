package com.evolvlabs.CountryInformationViewer.Controllers;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.InflateException;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.evolvlabs.CountryInformationViewer.DataModel.ApplicationDataPOJO;
import com.evolvlabs.CountryInformationViewer.DataModel.ContinentRecyclerAdapter;
import com.evolvlabs.CountryInformationViewer.DataModel.CountryInformation;
import com.evolvlabs.CountryInformationViewer.Utilities.DeviceOrientationUtilities;
import com.evolvlabs.CountryInformationViewer.Utilities.MenuItemsDialogBuilder;
import com.evolvlabs.countryinformationviewer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Paulo Cantos, Santiago Arellano
 * @date: 16-Apr-2025
 * @description: EL presente archivo implementa el controlador general del fragmento de seleccion de
 * uno de los modelos. La idea de este controlador es manejar la forma en la que se debe trabajar
 * para controlar solo movimientos, dado que no se maneja mas que la conexion de controladores
 */
public class ChooseAContinentController extends Fragment {

    //>> Parametros internos de la aplicacion
    /**
     * @description : Parametro de conexion al application data AndroidViewModel para la
     * implementacion del patron del MVVM, con el que accedemos a los datos con una instancia
     * manejada por Android internamente y no un simple singleton.
     */
    private ApplicationDataPOJO applicationDataPOJO;
    /**
     * @description : Parametro que nos permite conocer si estamos en un modelo de tablet, esto
     * se modifica dentro del ApplicationDataPOJO mediante metodos que aislan el sistema de 
     * recarga de configuraciones cuando cambia el layout
     */
    private AtomicBoolean areWeInATabletDevice = new AtomicBoolean(false);
    /**
     * @description : Parametro que nos permite notar y actualizar dinamicamente la orientacion 
     * del dispositivo, esto sirve para cargar el layout correspondiente y manejar listeners.
     */
    private AtomicBoolean areWeInPortraitOrientation = new AtomicBoolean(false);
    /**
     * @description : Observer que nos permite manejar la creacion y eliminacion del observer 
     * asignado para la navegacion, dado que tenemos que manejar navegacion SOLO en el caso de 
     * portrait, usamos un objeto para eliminar y asignar este observer dependiendo de la 
     * configuracion del dispositivo
     */
    private Observer<? super String> navigationObserver;


    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo de sobrescritura que inicializa y devuelve la vista del fragmento para la seleccion de
     * continentes. Este metodo es llamado por el sistema Android cuando es momento de dibujar la
     * interfaz del fragmento por primera vez.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Infla el layout del fragmento utilizando el recurso fragment_choose_a_continent_view</li>
     * <li>Asocia el layout inflado con el contenedor proporcionado</li>
     * <li>Retorna la vista creada para ser utilizada por el sistema Android</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param inflater           El LayoutInflater que puede ser usado para inflar cualquier vista
     *                           en el fragmento
     * @param container          Si no es null, esta es la vista padre a la cual el fragmento UI
     *                           deberia ser adjuntado
     * @param savedInstanceState Si no es null, este fragmento esta siendo reconstruido desde un
     *                           estado guardado previamente
     * @return Vista raiz del fragmento o null
     * @throws InflateException Si hay un error durante la inflacion del layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_a_continent_view,
                                container, false);
    }

    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo que se ejecuta despues de que la vista del fragmento ha sido creada. Se encarga de
     * inicializar el modelo de datos de la aplicacion, configurar los observadores para cambios de
     * orientacion y modo del dispositivo, asi como establecer el RecyclerView y el menu flotante.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Inicializa y configura el modelo de datos de la aplicacion (ApplicationDataPOJO)</li>
     * <li>Actualiza el modo del dispositivo y su orientacion</li>
     * <li>Configura los observadores para cambios de orientacion y modo del dispositivo</li>
     * <li>Configura el RecyclerView para mostrar la lista de continentes</li>
     * <li>Establece el menu flotante y sus opciones</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param view               La vista raiz del fragmento que ha sido inflada
     * @param savedInstanceState Bundle que contiene el estado previamente guardado del fragmento
     * @throws IllegalStateException Si se intenta acceder al ViewModel cuando el fragmento no esta
     *                               adjunto a una actividad
     * @throws NullPointerException  Si la vista proporcionada es nula o no se puede acceder al
     *                               contexto de la aplicacion
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //> 1. Aqui tenemos que conectarnos con el modelo de datos general para tener un listener
        // que nos permita manejar los datos de la aplicacion de la manera correcta, dado que
        // usamos el application data internamente solo tenemos que conectar el item a su
        // respectivo listener
        super.onViewCreated(view, savedInstanceState);

        //> 1.1 Conectamos con el data model de la aplicacion en general, y actualizamos la 
        // orientacion y tipo de dispositivo
        applicationDataPOJO =
                new ViewModelProvider(requireActivity()).get(ApplicationDataPOJO.class);
        applicationDataPOJO.updateMutableDeviceMode(requireContext());
        applicationDataPOJO.updateMutableDeviceOrientation(requireContext());
        if (applicationDataPOJO.getNavigationFromContinentToCountryHasAlreadyBeenDone().getValue() == null) {
            applicationDataPOJO.setNavigationFromCountryToInfoHasAlreadyBeenDone(false);
        }
        
        //> 1.2 Anadimos un observable para los cambios de layout
        this.connectOrientationAndDeviceModeListeners();

        applicationDataPOJO.getMutableSelectedContinent().observe(getViewLifecycleOwner(),
                                                                  new Observer<String>() {
                                                                      @Override
                                                                      public void onChanged(String s) {
                                                                          if (s == null || s.isEmpty()) {
                                                                              applicationDataPOJO.setNavigationFromContinentToCountryHasAlreadyBeenDone(false);
                                                                          }
                                                                      }
                                                                  });


        setUpRecyclerViewForContinentView();

        //? 3. Conectamos un mini controlador para un menu flotante que definimos
        this.connectMenuOptionsWithFloatingButton();

    }

    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo encargado de establecer los observadores para monitorear cambios en la orientacion del
     * dispositivo y su modo de operacion (tablet o telefono). Esta configuracion es esencial para
     * adaptar la interfaz y el comportamiento de navegacion segun el contexto del dispositivo.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Configura un observador para detectar si el dispositivo esta en modo tablet</li>
     * <li>Actualiza el estado interno areWeInATabletDevice cuando cambia el modo</li>
     * <li>Configura un observador para detectar si el dispositivo esta en orientacion vertical</li>
     * <li>Actualiza el estado interno areWeInPortraitOrientation cuando cambia la orientacion</li>
     * <li>Reconfigura los listeners de navegacion cuando ocurre cualquier cambio</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si se intenta acceder al LifecycleOwner cuando el fragmento no
     *                               esta adjunto a una actividad
     * @throws NullPointerException  Si applicationDataPOJO no ha sido inicializado correctamente
     */
    private void connectOrientationAndDeviceModeListeners() {
        applicationDataPOJO.getMutableIsDeviceInTabletMode().observe(getViewLifecycleOwner()
                , new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        areWeInATabletDevice.set(aBoolean);
                        setUpNavigationListeners();
                    }
                });
        applicationDataPOJO.getMutableIsDeviceInPortraitMode().observe(getViewLifecycleOwner()
                , new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        areWeInPortraitOrientation.set(aBoolean);
                        setUpNavigationListeners();
                    }
                });
    }
    
    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo encargado de configurar y gestionar la vista del RecyclerView que muestra la lista de 
     * continentes. Establece el adaptador personalizado, el layout manager y configura los 
     * observadores necesarios para mantener la vista actualizada cuando cambian los datos.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Obtiene la referencia del RecyclerView desde el layout</li>
     * <li>Crea y configura el adaptador personalizado ContinentRecyclerAdapter</li>
     * <li>Establece el LinearLayoutManager para organizar los elementos</li>
     * <li>Configura el observador para actualizar la vista cuando cambia la lista de continentes</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si se intenta acceder a la vista cuando el fragmento no esta adjunto
     * @throws NullPointerException Si no se puede encontrar el RecyclerView en el layout
     */
    private void setUpRecyclerViewForContinentView() {
        //> 1. Cargamos la vista del recycler view para modificarla
        RecyclerView viewFromUI =
                getView().findViewById(R.id.inChooseAContinentView_RecyclerViewForItemDisplay);
        if (viewFromUI != null) {
            //? 2. Conectamos el modelo de datos general con el controlador de la lista de
            // items
            ContinentRecyclerAdapter adapter = new ContinentRecyclerAdapter(
                    this.applicationDataPOJO, requireContext());
            viewFromUI.setLayoutManager(new LinearLayoutManager(requireContext()));
            viewFromUI.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            //? 3. Conectamos un listener en el caso de que cambie la lista para reconectar el
            // contenido
            applicationDataPOJO.getConstantContinentList().observe(getViewLifecycleOwner(),
                                                                   new Observer<List<String>>() {
                                                                       @Override
                                                                       public void onChanged(List<String> strings) {
                                                                           adapter.notifyDataSetChanged();
                                                                       }
                                                                   });
        }
    }

    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo responsable de configurar las opciones del menu flotante y sus interacciones.
     * Implementa la funcionalidad del boton flotante que permite al usuario recargar todos los
     * datos de la aplicacion. Este metodo establece un dialogo de confirmacion antes de ejecutar la
     * recarga de datos.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Obtiene la referencia del boton flotante desde el layout</li>
     * <li>Configura el listener de clicks para el boton</li>
     * <li>Implementa la creacion del dialogo de confirmacion con opciones personalizadas</li>
     * <li>Configura la accion de recarga de datos cuando se confirma el dialogo</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si se intenta acceder a la vista cuando el fragmento no esta
     *                               adjunto a una actividad
     * @throws NullPointerException  Si no se puede encontrar el boton flotante en el layout
     */
    private void connectMenuOptionsWithFloatingButton() {
        //> 1. Cargamos el boton de la vista para conectar con un metodo interno para cargar los 
        // datos de nuevo.
        FloatingActionButton button =
                getView().findViewById(R.id.inChooseAContinentView_HelpFloatingActionButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItemsDialogBuilder.createAndShowMenuItemSelection(requireContext(),
                                                                      button,
                                                                      new ArrayList<>() {{
                                                                          add(new Pair<String, String>(
                                                                                  getString(R.string.load_all_content_again_dialog_header),
                                                                                  getString(R.string.load_all_content_again_dialog_message)
                                                                          ));
                                                                      }},
                                                                      new ArrayList<>() {{
                                                                          add(new Pair<String, String>(
                                                                                  getString(R.string.remove_message_on_confirm_button_name),
                                                                                  getString(R.string.remove_message_on_reject_button_name)
                                                                          ));
                                                                      }},
                                                                      new ArrayList<>() {{
                                                                          add(
                                                                                  () -> {
                                                                                      Log.d("MenuDebug", "Runnable executed - about to call dataLoadingSuperMethod");
                                                                                      applicationDataPOJO.reloadDataModel();
                                                                                      if (applicationDataPOJO.getMutableIsDeviceInTabletMode() != null) {
                                                                                          if (Boolean.TRUE.equals(applicationDataPOJO.getMutableIsDeviceInTabletMode().getValue())) {
                                                                                              applicationDataPOJO.reloadPriorSelectedContinent();
                                                                                          }
                                                                                      }
                                                                                      Log.d("MenuDebug", "Runnable completed");
                                                                                  }
                                                                             );
                                                                      }},
                                                                      new String[]{getString(R.string.load_all_content_again_menu_item)});

            }
        });
    }

    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo encargado de configurar los listeners de navegacion entre fragmentos. Gestiona la
     * navegacion entre las vistas de continentes y paises basado en el tipo de dispositivo (tablet
     * o telefono) y su orientacion. Implementa una logica para evitar navegaciones duplicadas y
     * maneja la limpieza de observadores previos.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Elimina cualquier observador de navegacion existente para evitar duplicados</li>
     * <li>Verifica el tipo de dispositivo y su orientacion para determinar si se requiere
     * navegacion</li>
     * <li>Crea y configura un nuevo observador de navegacion si es necesario</li>
     * <li>Conecta el observador al LiveData del continente seleccionado</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si se intenta navegar cuando el fragmento no esta adjunto a una
     *                               actividad o el controlador de navegacion no esta disponible
     * @throws NullPointerException  Si el contexto de la aplicacion o la vista del fragmento no
     *                               estan disponibles durante la navegacion
     */
    private void setUpNavigationListeners() {
        //> 1. Si tenemos un listener conectado, tenemos que eliminarlo para que no exista 
        // problemas de la navegacion de nuevo
        if (navigationObserver != null) {
            applicationDataPOJO.getMutableSelectedContinent().removeObserver(navigationObserver);
            navigationObserver = null;
        }

        //> 2. Si estamos en un dispositivo que no es una tablet, o si tenemos un dispositivo que
        // es una tablet pero en vertical, tenemos qu conectar el listener de navegacion hacia la
        // vista de pais
        if (!this.areWeInATabletDevice.get() || (this.areWeInATabletDevice.get()
                && this.areWeInPortraitOrientation.get())) {
            navigationObserver = new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    //> 2.1 Si tenemos un cambio nulo o vacio, entonces nosotros no conectamos un
                    // listener, esto sucede cuando eliminamos una vista por ejemplo. 
                    if (s == null || s.isEmpty()
                            || Boolean.TRUE.equals(applicationDataPOJO.getIsNavigatingBack().getValue())) {
                        return;
                    }
                    //> 2.2 Si ya hemos realizado un movimiento hacia el pais, entonces tampoco 
                    // debemos hacer el movimiento, esto nos ayuda a evitar doble navegacion y 
                    // loops infinitos
                    if (Boolean.TRUE.equals(applicationDataPOJO.getNavigationFromContinentToCountryHasAlreadyBeenDone().getValue())) {
                        return;
                    }

                    //> 2.3 Intentamos conectar al final, si podemos (try), un listener de 
                    // navegacion que se da cuando se selecciona un continente en la vista.
                    try {
                        View currentView = getView();
                        if (currentView != null) {
                            if (isAdded() && getActivity() != null) {
                                NavController navController = Navigation.findNavController(
                                        getActivity(), R.id.nav_host_fragment);
                                if (navController.getCurrentDestination().getId() == R.id.chooseContinentFragment) {
                                    applicationDataPOJO.setNavigationFromContinentToCountryHasAlreadyBeenDone(true);
                                    navController.navigate(R.id.action_continent_to_country);
                                }
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

                }
            };
            
            //> 3. Conectamos al final el navegador correctamente, esto nos permite asegurarnos 
            // de que solo se conecta un navegador real si no existia antes, y con el final 
            // necesario basado en el anonymous inner class anterior
            applicationDataPOJO.getMutableSelectedContinent().observe(getViewLifecycleOwner(),
                                                                      navigationObserver);
        }
    }


    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo que se ejecuta cuando ocurre un cambio en la configuracion del dispositivo, como una
     * rotacion de pantalla. Se encarga de actualizar el estado del modelo de datos de la aplicacion
     * para reflejar la nueva configuracion del dispositivo, garantizando que la interfaz de usuario
     * se adapte correctamente a los cambios.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Ejecuta el metodo onConfigurationChanged de la clase padre</li>
     * <li>Actualiza el modo del dispositivo en el modelo de datos de la aplicacion</li>
     * <li>Recarga las constantes del dispositivo para adaptarse a la nueva configuracion</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param newConfig La nueva configuracion del dispositivo que contiene los cambios realizados
     * @throws IllegalStateException Posible excepcion si el fragmento no esta adjunto a una
     *                               actividad cuando se intenta acceder al contexto
     */
    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //> 1. Cargamos de nuevo las constantes del dispositivo, esto ayuda en el caso de que 
        // existan rotaciones del dispositivo
        applicationDataPOJO.updateMutableDeviceMode(requireContext());
    }

    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo que se ejecuta cuando el fragmento vuelve a estar visible para el usuario. Maneja el
     * estado de navegacion del fragmento, reiniciando las banderas de navegacion cuando se detecta
     * una navegacion hacia atras. Este comportamiento es crucial para mantener la coherencia en el
     * flujo de navegacion.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Llama al metodo onResume() de la clase padre</li>
     * <li>Verifica si existe una navegacion hacia atras mediante el valor de IsNavigatingBack</li>
     * <li>Si hay navegacion hacia atras, reinicia los estados de navegacion estableciendo las banderas en falso</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Posible excepcion si el fragmento no esta adjunto a una
     *                               actividad
     */
    @Override
    public void onResume() {
        super.onResume();
        //> 1. En el caso de haber realizado una navegacion hacia atras, entonces marcamos que no
        // hemos hecho una navegacion haci atras (reset state) y que no hemos hecho una 
        // navegacion hacia al frente (reset state), esto nos sirve para organizar el movimiento 
        // y evitar que de la vista 1 se pase a la vist 3
        if (Boolean.TRUE.equals(applicationDataPOJO.getIsNavigatingBack().getValue())) {
            applicationDataPOJO.setIsNavigatingBack(false);
            applicationDataPOJO.setNavigationFromContinentToCountryHasAlreadyBeenDone(false);
        }

    }

    /**
     * <body style="color: WHITE;">
     * <description>
     * Metodo que se ejecuta cuando la vista del fragmento esta siendo destruida. Se encarga de
     * realizar la limpieza necesaria de los observadores y listeners asociados al fragmento,
     * previniendo fugas de memoria y comportamientos inesperados en la navegacion.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Ejecuta el metodo onDestroyView() de la clase padre</li>
     * <li>Verifica si existe un observador de navegacion activo</li>
     * <li>Si existe, remueve el observador del MutableLiveData del continente seleccionado</li>
     * <li>Establece la referencia del observador a null para liberar recursos</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Posible excepcion si se intenta acceder al ViewModel cuando el
     *                               fragmento ya no esta activo
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //> 1. Cuando salimos de la vista, tenemos que remover nuestro listener para no tener un 
        // movimiento en falso entre las vistas al regresar.
        if (navigationObserver != null) {
            applicationDataPOJO.getMutableSelectedContinent().removeObserver(navigationObserver);
            navigationObserver = null;
        }
    }
}