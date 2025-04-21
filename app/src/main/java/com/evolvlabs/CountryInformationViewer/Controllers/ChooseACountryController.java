package com.evolvlabs.CountryInformationViewer.Controllers;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.InflateException;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.evolvlabs.CountryInformationViewer.DataModel.ApplicationDataPOJO;
import com.evolvlabs.CountryInformationViewer.DataModel.CountryInformation;
import com.evolvlabs.CountryInformationViewer.DataModel.CountryRecyclerAdapter;
import com.evolvlabs.countryinformationviewer.R;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass. Use the {@link ChooseACountryController#newInstance} factory
 * method to create an instance of this fragment.
 */
public class ChooseACountryController extends Fragment {

    //> Parametros de la clase
    /**
     * @description : Referencia interna hacia el singleton de Android, AndroidViewModel que permite
     * continuar con el modelo de implementacion MVVM
     */
    private ApplicationDataPOJO applicationDataPOJO;
    /**
     * @description : constante interna que nos permite conocer rapidamente si estamos en un
     * dispositivo de tipo tablet o no
     */
    private AtomicBoolean areWeInATabletDevice = new AtomicBoolean(false);
    /**
     * @description : instancia interna usada para manejar el listener de la navegacion que se tiene
     * que conectar o desconectar dependiendo del layout y movimientos anteriores
     */
    private Observer<? super CountryInformation> navigationObserver;
    /**
     * @description : constante interna que nos permite conocer rapidamente si estamos en un
     * dispositivo que tiene su pantalla en landscape o portrait mode
     */
    private AtomicBoolean areWeInPortraitOrientation = new AtomicBoolean(false);


    /**
     * <body style="color: WHITE">
     * <description>
     * Metodo de ciclo de vida del fragmento que se encarga de inflar y crear la vista inicial del
     * componente. Este metodo es llamado por el sistema operativo Android cuando el fragmento
     * necesita crear su interfaz de usuario por primera vez.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Recibe los parametros necesarios del sistema operativo</li>
     * <li>Infla el layout definido para este fragmento usando el inflater proporcionado</li>
     * <li>Retorna la vista inflada al sistema para su renderizacion</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param inflater           Objeto utilizado para inflar el layout XML en una vista
     * @param container          Vista padre donde se insertara el fragmento
     * @param savedInstanceState Bundle que contiene el estado anterior del fragmento
     * @return Vista inflada del fragmento
     * @throws InflateException         Si hay un error al inflar el layout
     * @throws IllegalArgumentException Si los parametros proporcionados son invalidos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //> 1. Cargamos el layout especifico de esta vista
        return inflater.inflate(
                R.layout.fragment_choose_a_country_view, container, false);
    }


    /**
     * <body style="color: WHITE">
     * <description>
     * Metodo del ciclo de vida del fragmento que se ejecuta inmediatamente despues de que
     * onCreateView() retorna su vista. Se encarga de inicializar los componentes visuales,
     * establecer los observadores necesarios y configurar la logica de navegacion del fragmento.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Carga y configura los parametros de la aplicacion mediante ViewModelProvider</li>
     * <li>Determina el tipo de dispositivo y su orientacion</li>
     * <li>Inicializa y configura los componentes visuales (RecyclerView y TextView)</li>
     * <li>Establece los observadores para el manejo de datos y navegacion</li>
     * <li>Configura el control de navegacion del boton de retroceso</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param view               Vista recien creada, nunca nula
     * @param savedInstanceState Estado anterior del fragmento, puede ser nulo
     * @throws IllegalStateException    Si el fragmento no esta adjunto a una actividad
     * @throws IllegalArgumentException Si los parametros de navegacion son invalidos
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //> 1. Cargamos todos los parametros de la app
        this.applicationDataPOJO =
                new ViewModelProvider(requireActivity()).get(ApplicationDataPOJO.class);

        //> 1.1 Determinamos si estamos en un tipo de tableta para hacer o no hacer el cambio
        // de pantalla
        applicationDataPOJO.updateMutableDeviceMode(requireContext());
        applicationDataPOJO.updateMutableDeviceOrientation(requireContext());
        if (applicationDataPOJO.getNavigationFromCountryToInfoHasAlreadyBeenDone().getValue() == null) {
            applicationDataPOJO.setNavigationFromCountryToInfoHasAlreadyBeenDone(false);
        }

        //> 1.2 Conectamos listeners para la forma y orientacion del dispositivo, esto nos
        // permite organizar los listeners dependiendo del tipo de ventana que tenemos
        this.setUpListenersForDeviceConfigurationAndNavigationInitialization();

        //> 1.3 Conectmaos listeners para manejar la seleccion de un pais, esto nos permite
        // manejar el proceso de eliminacion del pais seleccionado o las strings que se colocan
        // cuando no tenemos una seleccion
        this.setUpMutableSelectedCountryListenersForInformationDisplaying();


        //> 2. Tomamos los componentes visuales del layout y conectamos con listeners.
        TextView textHolder = view.findViewById(R.id.inChooseACountryView_TextForHeader);
        RecyclerView viewHolder =
                view.findViewById(R.id.inChooseACountryView_RecyclerViewForComponents);
        CountryRecyclerAdapter countryRecyclerAdapter =
                new CountryRecyclerAdapter(this.applicationDataPOJO,
                                           requireContext());

        //> 2.1 Conectamos el recycler view y text view con su respectivo adapter y manejo para
        // la seleccion de pais, etc
        this.setUpListenersOnRecyclerViewAndTextViewForUIControl(viewHolder, countryRecyclerAdapter,
                                                             textHolder);

        //> 2.2 Conectamos el texto del header con el valor del view model del continente
        // seleccioando para manejar cambios en la seleccion, eliminacion de los datos, etc.
        this.setUpListenersForSelectedContinentChangesAndUIUpdates(textHolder,
                                                                 countryRecyclerAdapter);

        //> 3. Conectamos un controlador para cuando el usuario presione el boton de ir hacia
        // atras, esto nos permite manejar el stack y eliminar errores de navegacion entre la
        // vista 1 a 3.
        this.setUpBackButtonPressNavigationControl();
    }


    /**
     * <body style="color: WHITE">
     * <description>
     * Configura los observadores necesarios para manejar los cambios en la seleccion del continente
     * y actualizar la interfaz de usuario en consecuencia. Establece la logica para actualizar el
     * texto del encabezado y notificar al adaptador del RecyclerView cuando cambia el continente
     * seleccionado.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Verifica la existencia del ViewModel y el LiveData del continente seleccionado</li>
     * <li>Configura un observador para actualizar el texto del encabezado segun el continente</li>
     * <li>Configura un observador adicional para notificar al adaptador sobre cambios en la seleccion</li>
     * <li>Maneja casos nulos y vacios en la seleccion del continente</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param textHolder             Componente TextView que muestra el encabezado con el continente
     *                               seleccionado
     * @param countryRecyclerAdapter Adaptador del RecyclerView que maneja la lista de paises
     * @throws IllegalStateException Si el fragmento no esta adjunto al contexto de la actividad
     * @throws NullPointerException  Si el ViewModel o sus LiveData no estan inicializados
     *                               correctamente
     */
    private void setUpListenersForSelectedContinentChangesAndUIUpdates(TextView textHolder,
                                                                       CountryRecyclerAdapter countryRecyclerAdapter) {
        if (applicationDataPOJO != null) {
            //> 1. Si el AndroidViewModel no es nulo, entonces observamos los cambios en los 
            // continentes, esto nos permite manejar la eliminacion de este o la seleccion de 
            // este en la vista geenral
            if (applicationDataPOJO.getMutableSelectedContinent() != null) {
                applicationDataPOJO.getMutableSelectedContinent().observe(getViewLifecycleOwner(),
                                                                          new Observer<String>() {
                                                                              @Override
                                                                              public void onChanged(String s) {
                                                                                  if (s != null && !s.isEmpty()) {
                                                                                      textHolder.setText("Countries in " + s);
                                                                                  } else {
                                                                                      textHolder.setText("Please Select a Continent First");
                                                                                  }
                                                                              }
                                                                          });
            }

            //> 2. Para separar los concerns de los observadores, se anade esete observador que 
            // indica que aqui tenemos que actualizar especificamente el recycler view para que 
            // la lista no se quede con datos anteriores
            if (applicationDataPOJO.getMutableSelectedContinent() != null) {
                applicationDataPOJO.getMutableSelectedContinent().observe(getViewLifecycleOwner(),
                                                                          new Observer<String>() {
                                                                              @Override
                                                                              public void onChanged(String s) {
                                                                                  countryRecyclerAdapter.notifyDataSetChanged();
                                                                              }
                                                                          });
            }
        }
    }

    /**
     * <body style="color: WHITE">
     * <description>
     * Metodo encargado de configurar y establecer los observadores necesarios para controlar la
     * interfaz de usuario del RecyclerView y TextView. Se ocupa de manejar la visualizacion de
     * paises por continente y actualizar la interfaz segun los cambios en la seleccion del
     * usuario.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Configura el LayoutManager del RecyclerView con un LinearLayoutManager</li>
     * <li>Establece el adaptador personalizado en el RecyclerView</li>
     * <li>Configura observadores para la lista de paises</li>
     * <li>Implementa la logica de filtrado de paises por continente</li>
     * <li>Actualiza el texto del encabezado segun la seleccion del continente</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param viewHolder             El RecyclerView que muestra la lista de paises
     * @param countryRecyclerAdapter El adaptador personalizado para manejar los datos de paises
     * @param textHolder             El TextView que muestra el encabezado de la seccion
     * @throws IllegalStateException Si el fragmento no esta adjunto a una actividad
     * @throws NullPointerException  Si alguno de los parametros es nulo
     */
    private void setUpListenersOnRecyclerViewAndTextViewForUIControl(RecyclerView viewHolder,
                                                                     CountryRecyclerAdapter countryRecyclerAdapter,
                                                                     TextView textHolder) {
        if (viewHolder != null) {
            //> 1. Si la vista no es nula, entonces asignamos el adapter para el recycler view 
            // luego de asignar un linear layout manager para las vistas
            viewHolder.setLayoutManager(new LinearLayoutManager(requireContext()));
            viewHolder.setAdapter(countryRecyclerAdapter);
            countryRecyclerAdapter.notifyDataSetChanged();
            //> 2  Conectamos un listener al adaptador para manejar cambios de seleccion de country
            applicationDataPOJO.constantCountryList.observe(getViewLifecycleOwner(),
                                                            new Observer<List<CountryInformation>>() {
                                                                @Override
                                                                public void onChanged(List<CountryInformation> countryInformations) {
                                                                    countryRecyclerAdapter.notifyDataSetChanged();
                                                                }
                                                            });
            applicationDataPOJO.constantCountryList.observe(getViewLifecycleOwner(),
                                                            new Observer<List<CountryInformation>>() {
                                                                @Override
                                                                public void onChanged(List<CountryInformation> countryInformations) {
                                                                    List<CountryInformation> informationPerContinent =
                                                                            countryInformations.stream().filter(new Predicate<CountryInformation>() {
                                                                                @Override
                                                                                public boolean test(CountryInformation countryInformation) {
                                                                                    return countryInformation.getCountryContinent()
                                                                                            .equalsIgnoreCase(
                                                                                                    applicationDataPOJO
                                                                                                            .getMutableSelectedContinent().
                                                                                                            getValue());
                                                                                }
                                                                            }).collect(Collectors.toList());

                                                                    if (informationPerContinent.isEmpty()) {
                                                                        textHolder.setText(
                                                                                "Select a " +
                                                                                        "Continent");
                                                                    }
                                                                }
                                                            });
        }
    }

    /**
     * <body style="color: WHITE">
     * <description>
     * Configura los observadores necesarios para controlar el estado de seleccion de paises y
     * continentes en la aplicacion. Este metodo establece la logica para manejar los cambios en la
     * seleccion y actualiza las banderas de navegacion correspondientes cuando se producen cambios
     * en la seleccion.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Configura un observador para el pais seleccionado que reinicia la bandera de navegacion
     * cuando se deselecciona un pais</li>
     * <li>Establece un observador para el continente seleccionado que reinicia la bandera de
     * navegacion cuando se deselecciona un continente</li>
     * <li>Maneja los estados nulos o vacios en las selecciones</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si el fragmento no esta adjunto a una actividad cuando se
     *                               intentan configurar los observadores
     * @throws NullPointerException  Si el ViewModel o alguno de sus LiveData no estan inicializados
     *                               correctamente
     */
    private void setUpMutableSelectedCountryListenersForInformationDisplaying() {
        //> 1. Conectamos un listener para el pais seleccionado, en este caso, si tenemos que se 
        // ha eliminado la seleccion (lo que nos llevaria a un null country) entonces marcamos 
        // que la navegacion no se ha hecho
        applicationDataPOJO.getMutableSelectedCountry().observe(getViewLifecycleOwner(),
                                                                new Observer<CountryInformation>() {
                                                                    @Override
                                                                    public void onChanged(CountryInformation countryInformation) {
                                                                        if (countryInformation.getCountryContinent().equalsIgnoreCase("null")) {
                                                                            applicationDataPOJO.setNavigationFromCountryToInfoHasAlreadyBeenDone(false);
                                                                        }
                                                                    }
                                                                });
        //> 2. Si el string del nombre es vacio o es nulo, entonces tambien marcamos que la 
        // navegacion hacia la vista no se ha hecho.
        applicationDataPOJO.getMutableSelectedContinent().observe(getViewLifecycleOwner(),
                                                                  new Observer<String>() {
                                                                      @Override
                                                                      public void onChanged(String s) {
                                                                          if (s == null || s.isEmpty()) {
                                                                              applicationDataPOJO.setNavigationFromContinentToCountryHasAlreadyBeenDone(false);
                                                                          }
                                                                      }
                                                                  });
    }

    /**
     * <body style="color: WHITE">
     * <description>
     * Configura los observadores necesarios para monitorear y responder a cambios en la
     * configuracion del dispositivo. Este metodo establece la logica para detectar si el
     * dispositivo es una tableta y su orientacion actual, permitiendo adaptar la interfaz de
     * usuario y la navegacion segun estas caracteristicas.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Configura un observador para detectar si el dispositivo es una tableta</li>
     * <li>Actualiza la bandera interna de modo tableta cuando cambia la configuracion</li>
     * <li>Configura un observador para detectar la orientacion del dispositivo</li>
     * <li>Actualiza la bandera interna de orientacion cuando cambia la configuracion</li>
     * <li>Reconfigura los listeners de navegacion cuando cambia cualquier parametro</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si el fragmento no esta adjunto a una actividad cuando se
     *                               intentan configurar los observadores
     * @throws NullPointerException  Si el ViewModel o sus LiveData no estan inicializados
     *                               correctamente
     */
    private void setUpListenersForDeviceConfigurationAndNavigationInitialization() {
        //> 1. Conectamos un listener, al igual que en la anterior clase del controller para 
        // eliminar o asignar los listeners dependiendo de la orientacion del dispositivo.
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
     * <body style="color: WHITE">
     * <description>
     * Configura los observadores de navegacion para controlar la transicion entre fragmentos basado
     * en la seleccion de pais. Este metodo maneja la logica de navegacion diferenciada para
     * dispositivos tablet y telefono, considerando tambien la orientacion de la pantalla para
     * determinar el comportamiento apropiado de la navegacion.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Elimina cualquier observador de navegacion existente para evitar duplicados</li>
     * <li>Verifica si el dispositivo es una tablet y su orientacion actual</li>
     * <li>Crea y configura un nuevo observador para la seleccion de pais si es necesario</li>
     * <li>Ejecuta la navegacion programatica cuando se selecciona un pais valido</li>
     * <li>Maneja las banderas de estado de navegacion para prevenir navegaciones duplicadas</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si se intenta navegar cuando el fragmento no esta adjunto a una
     *                               actividad
     * @throws NullPointerException  Si el ViewModel o sus LiveData no estan inicializados
     *                               correctamente
     */
    private void setUpNavigationListeners() {
        //> 1. Eliminamos el observer si el observer no es nulo, es decir si hubo un cambio de 
        // olrientacion, por ejemplo, eliminamos el observer (listener) para no tener el problema
        // de una navegacion infinita
        if (navigationObserver != null) {
            applicationDataPOJO.getMutableSelectedCountry().removeObserver(navigationObserver);
            navigationObserver = null;
        }

        //> 2. Conectamos un listener si estamos en un dispositivo que no es una tableta, o si es
        // una tableta pero si tiene una orientacion de portrait
        if (!this.areWeInATabletDevice.get() || (this.areWeInATabletDevice.get()
                && this.areWeInPortraitOrientation.get())) {
            navigationObserver = new Observer<CountryInformation>() {
                @Override
                public void onChanged(CountryInformation s) {
                    if (s == null || s.getCountryName().equalsIgnoreCase("null")
                            || Boolean.TRUE.equals(applicationDataPOJO.getIsNavigatingBack().getValue())) {
                        return;
                    }
                    if (Boolean.TRUE.equals(applicationDataPOJO.getNavigationFromCountryToInfoHasAlreadyBeenDone().getValue())) {
                        return;
                    }

                    try {
                        View currentView = getView();
                        if (currentView != null) {
                            if (isAdded() && getActivity() != null) {
                                NavController navController = Navigation.findNavController(
                                        getActivity(), R.id.nav_host_fragment);
                                if (navController.getCurrentDestination().getId() == R.id.chooseCountryFragment) {
                                    applicationDataPOJO.setNavigationFromCountryToInfoHasAlreadyBeenDone(true);
                                    navController.navigate(R.id.action_country_to_info);

                                }
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }

                }
            };
            //> 3. Cargamos el listener al sistema
            applicationDataPOJO.getMutableSelectedCountry().observe(getViewLifecycleOwner(),
                                                                    navigationObserver);
        }
    }


    /**
     * <body style="color: WHITE">
     * <description>
     * Metodo del ciclo de vida del fragmento que se ejecuta cuando ocurre un cambio en la
     * configuracion del dispositivo. Este metodo es responsable de actualizar el estado de la
     * aplicacion para reflejar la nueva configuracion del dispositivo, como cambios en la
     * orientacion de la pantalla o el modo de visualizacion.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Llama al metodo de la superclase para mantener el comportamiento estandar</li>
     * <li>Actualiza el modo del dispositivo en el ViewModel usando el contexto actual</li>
     * <li>Mantiene la consistencia del estado de la aplicacion tras los cambios de configuracion</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @param newConfig La nueva configuracion del dispositivo que contiene los cambios realizados
     * @throws IllegalStateException Si el fragmento no esta adjunto a una actividad
     * @throws NullPointerException  Si el parametro newConfig es nulo o el contexto no esta
     *                               disponible
     */
    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applicationDataPOJO.updateMutableDeviceMode(requireContext());
    }

    /**
     * <body style="color: WHITE">
     * <description>
     * Metodo del ciclo de vida que se ejecuta cuando el fragmento vuelve a estar visible para el
     * usuario. Se encarga principalmente de restablecer el estado de navegacion cuando el usuario
     * retorna a este fragmento desde otra vista. Es fundamental para mantener la consistencia en la
     * navegacion de la aplicacion.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Ejecuta el metodo onResume() de la clase padre para mantener el comportamiento base</li>
     * <li>Verifica si existe una bandera de navegacion hacia atras activa</li>
     * <li>Restablece la bandera de navegacion hacia atras si esta activa</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si el fragmento no esta adjunto a una actividad
     * @throws NullPointerException  Si el ViewModel no esta inicializado correctamente
     */
    @Override
    public void onResume() {
        super.onResume();
        if (Boolean.TRUE.equals(applicationDataPOJO.getIsNavigatingBack().getValue())) {
            applicationDataPOJO.setIsNavigatingBack(false);
        }

    }

    /**
     * <body style="color: WHITE">
     * <description>
     * Metodo del ciclo de vida del fragmento que se ejecuta cuando la vista del fragmento esta
     * siendo destruida. Se encarga de realizar la limpieza necesaria de los recursos y observadores
     * utilizados por el fragmento, especialmente los relacionados con la navegacion, para evitar
     * fugas de memoria y comportamientos indeseados.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Ejecuta el metodo onDestroyView de la clase padre</li>
     * <li>Verifica si existe un observador de navegacion activo</li>
     * <li>Elimina el observador de navegacion si existe</li>
     * <li>Establece la referencia del observador a null para liberar recursos</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si se intenta acceder al ViewModel cuando el fragmento ya no
     *                               esta adjunto a una actividad
     * @throws NullPointerException  Si el ViewModel o sus LiveData han sido destruidos
     *                               prematuramente
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (navigationObserver != null) {
            applicationDataPOJO.getMutableSelectedCountry().removeObserver(navigationObserver);
            navigationObserver = null;
        }
    }


    /**
     * <body style="color: WHITE">
     * <description>
     * Metodo del ciclo de vida que se ejecuta cuando el fragmento es desvinculado de su actividad
     * contenedora. Se encarga de limpiar los recursos y observadores asociados al fragmento,
     * asegurando una correcta liberacion de memoria y previniendo posibles fugas de memoria.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Ejecuta el metodo onDetach de la clase padre</li>
     * <li>Verifica si existe un observador de navegacion activo</li>
     * <li>Elimina el observador de navegacion si existe</li>
     * <li>Establece la referencia del observador a null</li>
     * <li>Elimina la seleccion del continente actual</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si se intenta acceder al ViewModel cuando el fragmento ya esta
     *                               desvinculado
     * @throws NullPointerException  Si el ViewModel o sus LiveData han sido destruidos
     *                               prematuramente
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (navigationObserver != null) {
            applicationDataPOJO.getMutableSelectedCountry().removeObserver(navigationObserver);
            navigationObserver = null;
            applicationDataPOJO.dropContinentSelection();
        }
    }

    /**
     * <body style="color: WHITE">
     * <description>
     * Configura el controlador para el manejo del boton de retroceso del dispositivo. Este metodo
     * establece un callback personalizado que intercepta la accion del boton de retroceso y
     * gestiona la navegacion entre fragmentos de manera controlada, asegurando una transicion
     * fluida entre vistas y manteniendo el estado de la aplicacion.
     * </description>
     *
     * <methodology>
     * <ol>
     * <li>Registra un callback personalizado en el dispatcher del boton de retroceso</li>
     * <li>Actualiza el estado del ViewModel para reflejar la navegacion hacia atras</li>
     * <li>Ejecuta la navegacion hacia atras utilizando el NavController</li>
     * <li>Maneja posibles errores durante la navegacion</li>
     * </ol>
     * </methodology>
     * </body>
     *
     * @throws IllegalStateException Si el fragmento no esta adjunto a una actividad o si el
     *                               NavController no esta disponible
     * @throws NullPointerException  Si el ViewModel o el contexto de la actividad son nulos
     */
    private void setUpBackButtonPressNavigationControl() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                                                                   new OnBackPressedCallback(true) {
                                                                       @Override
                                                                       public void handleOnBackPressed() {
                                                                           //? 1. Informamos al
                                                                           // view model que
                                                                           // estamos de regreso
                                                                           // desde la info hacia
                                                                           // country
                                                                           applicationDataPOJO.helperHandlerParaBackNavigationFromInfoToCountryViews();

                                                                           //? 2. Navegamos hacia
                                                                           // atras
                                                                           NavController controller =
                                                                                   Navigation.findNavController(requireActivity(),
                                                                                                                R.id.nav_host_fragment);
                                                                           try {
                                                                               controller.popBackStack();
                                                                           } catch (
                                                                                   Exception e) {
                                                                               e.printStackTrace();
                                                                           }

                                                                       }
                                                                   });
    }
}