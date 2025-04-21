package com.evolvlabs.CountryInformationViewer.DataModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.evolvlabs.CountryInformationViewer.CountryInformationViewer;
import com.evolvlabs.CountryInformationViewer.Utilities.DeviceOrientationUtilities;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author: Paulo Cantos, Santiago Arellano
 * @date: 15-Apr-2025
 * @description: El presente archivo implementa un ViewModel, un nuevo modelo para el manejo de
 * datos que descubrimos mientras revisabamos informacion para trabajar con la transferencia de
 * datos dentro de una aplicacion con fragmentos pero sin menu. En nuestras apps anteriores,
 * manejabamos un Singleton directamente dado que teniamos la posibilidad de cargar y guardar los
 * datos dentro de la app, y los datos se requerian actualizar en pocas secciones. Como las
 * aplicaciones, y su fuerte no era el manejo de datos, no teniamos que manejar un ViewModel, pero
 * ahora al tener un fragmento que tiene que llamar a otros y vamos a trabajar con un nav_graph mas
 * complejo, consideramos correcto usar esta implementacion.
 * @see ViewModel
 */
public class ApplicationDataPOJO extends AndroidViewModel {

    /*! Parametros internos de la aplicaicon*/
    /**
     * @description: parametro de tipo LiveData que nos permite tener una lista de los continentes
     * que se han registrado en el sistema. Usamos en este caso {@link androidx.lifecycle.LiveData}
     * dado que tenemos que manejar un registro internoq ue es immutable, mientras que afuera usamos
     * un {@link androidx.lifecycle.MutableLiveData} para manejar informacion que debe cambiar en
     * base a lo que pide el usuario por ejemplo
     */
    private MutableLiveData<List<String>> mutableContinentList =
            new MutableLiveData<>(
                    new ArrayList<>() {{
                        addAll(List.of("Americas", "Asia", "Africa", "Europe",
                                       "Oceania"));
                    }});
    //? Aqui definimos el tipo de constante publica dado que es facil para todo el resto de
    // componentes de la aplicacion para suscribirse a este dato, este tipo de dato es parecido a
    // un ObservableValue de JavaFX
    public LiveData<List<String>> constantContinentList = mutableContinentList;

    /**
     * @description: parametro de tipo LiveData and MutableLiveData que se usa especificamente para
     * mantener un listado de todos los paises cargados dentro del sistema, mientras que las
     * imagenes no se han cargado. Aqui tenemos solo los datos para que la carga de imagenes se haga
     * en la UI.
     */
    private MutableLiveData<List<CountryInformation>> mutableCountryList =
            new MutableLiveData<>();
    public LiveData<List<CountryInformation>> constantCountryList = mutableCountryList;

    /**
     * @description: parametro de tipo liveData y MutableLiveData que se usa especificamente para
     * mantener un pais que se ha seleccionado.
     */
    private MutableLiveData<CountryInformation> mutableSelectedCountry =
            new MutableLiveData<>();
    public LiveData<CountryInformation> constantSelectedCountry = mutableSelectedCountry;

    /**
     * @description: parametro de tipo LiveData and MutableLiveDAta se usa especificamente para
     * mantener una instancia de un continente que se ha seleccionado
     */
    private MutableLiveData<String> mutableSelectedContinent =
            new MutableLiveData<>();
    public LiveData<String> constantSelectedContinent = mutableSelectedContinent;


    /**
     * @description: parametro de tipo LiveData y MutableLiveData que se usa especificamente para
     * tener una lista de valores de los continentes y sus iconos, esto nos ayuda a mostrar los
     * valores. Esta lista se conecta con la lista de continentes para tener un orden tal como se
     * establece en este parametro
     */
    private MutableLiveData<Map<String, Bitmap>> mutableContinentIconMap = new MutableLiveData<>();
    public LiveData<Map<String, Bitmap>> constantContinentIconMap = mutableContinentIconMap;


    /**
     * @description : parametro de tipo Mutable y Live Data de un mapa que comunica el path de
     * descarga de la imagen con el bitmap de la imagen basado en el pais cuya bandera se
     * descarga, esto es un cache rapido en memoria
     */
    private MutableLiveData<Map<String, Bitmap>> mutableCountryFlagMap = new MutableLiveData<>();
    public LiveData<Map<String, Bitmap>> constantCountryFlagMap = mutableCountryFlagMap;

    /**
     * @description : parametro de tipo Mutable y Live Data que maneja el booleano que determina
     * si hemos realizado una navegacion entre el continente y la vista del pais, esto nos
     * permite saber si debemos de permitir el movimiento hacia la vista o no.
     */
    private MutableLiveData<Boolean> navigationFromContinentToCountryHasAlreadyBeenDone =
            new MutableLiveData<>(false);
    public LiveData<Boolean> constantNavigationFromContinentToCountryHasAlreadyBeenDone =
            navigationFromContinentToCountryHasAlreadyBeenDone;
    /**
     * @description : parametro de tipo Mutable y Live Data que maneja el boolean que determina
     * si hemos realizado un movimiento desde la vista de selecciond el pais hacia la vista de
     * informacion.
     */
    public MutableLiveData<Boolean> navigationFromCountryToInfoHasAlreadyBeenDone =
            new MutableLiveData<>(false);
    public LiveData<Boolean> constantNavigationFromCountryToInfoHasAlreadyBeenDone =
            navigationFromCountryToInfoHasAlreadyBeenDone;
    /**
     * @description : parametro de tipo MutableData que permite conocer dentro del sistema si se
     * ha realizado, o se esta realizado para manejar el stack.
     */
    private MutableLiveData<Boolean> isNavigatingBack = new MutableLiveData<>(false);

    /**
     * @description : parametro interno que mantiene la lista de los continentes eliminados para 
     * el manejo de la informacion y serializacion
     */
    private List<String> removedContinentList = new ArrayList<>();
    /**
     * @description : parametro interno que mantiene una lista de los paises eliminados para la 
     * serializacion de los mismos
     */
    private List<CountryInformation> removedCountryList = new ArrayList<>();

    /**
     * @description : parametro Mutable Data que permite conocer si el dispositivo esta en table 
     * mode
     */
    private final MutableLiveData<Boolean> mutableBooleanIsDeviceInTabletMode =
            new MutableLiveData<>(false);

    /**
     * @description : parametro Mutable Data que permite conocer si el dispositivo esta en 
     * landscape o portrait
     */
    private final MutableLiveData<Boolean> mutableBooleanIsDeviceInPortraitMode =
            new MutableLiveData<>(true);

    private SharedPreferences preferencesFromApplication;

    private final String SHARED_PREFERENCES_NAME = "countryInformationViewerPreferences";
    private final String SP_REMOVED_CONTINENT_LIST = "removedContinentList";
    private final String SP_REMOVED_COUNTRIES_LIST = "removedCountriesList";
    private final Application applicationContext;
    private String selectedContinentPriorReloading;

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Constructor principal del modelo de datos de la aplicacion. Este constructor inicializa los
     * datos fundamentales del sistema, cargando las preferencias compartidas y realizando la carga
     * inicial de datos. Maneja la deserializacion de datos previamente guardados si existen.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     * <li>Inicializa la clase padre con la aplicacion proporcionada</li>
     * <li>Almacena el contexto de la aplicacion para uso posterior</li>
     * <li>Configura y obtiene las preferencias compartidas de la aplicacion</li>
     * <li>Ejecuta el metodo de carga de datos principal</li>
     * <li>Verifica y deserializa las preferencias guardadas si existen</li>
     * </ol>
     * </body>
     *
     * @param application La instancia de la aplicacion que se utilizara para obtener el contexto
     * @throws NullPointerException     Si el parametro application es nulo
     * @throws SecurityException        Si no se tienen permisos para acceder a las preferencias
     *                                  compartidas
     * @throws IllegalArgumentException Si hay un error en la configuracion de las preferencias
     */
    public ApplicationDataPOJO(@NonNull Application application) {
        super(application);
        this.applicationContext = application;
        //? 1. Llamamos al super method para la carga de datos en la aplicacion.
        this.preferencesFromApplication =
                getApplication().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        dataLoadingSuperMethod();
        if (this.isSharedPreferencesOnline()) {
            //? 2. Deserializamos los valores de esta vista para poder eliminar todos los valores
            // que el usuario haya borrado anteriormente.
            this.deserializeSharedPreferences();
        }
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo principal de carga de datos que inicializa y popula las estructuras de datos
     * fundamentales de la aplicacion. Este metodo se encarga de cargar las imagenes de banderas de
     * paises, iconos de continentes y establecer las relaciones entre los diferentes elementos del
     * modelo de datos.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     * <li>Obtiene el AssetManager de la aplicacion para acceder a los recursos</li>
     * <li>Carga las banderas de los paises y su informacion asociada</li>
     * <li>Carga los iconos de los continentes y establece sus relaciones</li>
     * <li>Actualiza los LiveData correspondientes con la informacion cargada</li>
     * </ol>
     * </body>
     *
     * @throws SecurityException     Si no se tienen los permisos necesarios para acceder a los
     *                               assets
     * @throws IOException           Si ocurre un error durante la lectura de los archivos de
     *                               recursos
     * @throws IllegalStateException Si el contexto de la aplicacion no esta disponible
     */
    private void dataLoadingSuperMethod() {
        AssetManager managerRequiredForImageExtraction =
                this.applicationContext.getAssets();
        //? 1. Habiendo registrado el managerRequiredForImageExtraction, si este no es nulo,
        // tenemos que cargar las imagenes y analizar los valores
        loadingAllCountriesAndCountriesFlagsHelper(managerRequiredForImageExtraction);
        //? 2. Ahora cargamos los iconos de cada uno de los paises
        loadingAllContinentNamesAndIcons(managerRequiredForImageExtraction);



    }


    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo encargado de recargar completamente el modelo de datos de la aplicacion. Reinicializa
     * las listas de continentes y paises a sus valores predeterminados, recarga todas las imagenes
     * y banderas, y limpia cualquier estado anterior de elementos eliminados.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     * <li>Obtiene el AssetManager para acceder a los recursos de la aplicacion</li>
     * <li>Reinicializa la lista de continentes a sus valores predeterminados</li>
     * <li>Recarga todas las banderas de paises y su informacion asociada</li>
     * <li>Recarga todos los iconos de continentes</li>
     * <li>Limpia las listas de elementos eliminados</li>
     * <li>Elimina las preferencias compartidas relacionadas</li>
     * </ol>
     * </body>
     *
     * @throws SecurityException     Si no se tienen los permisos necesarios para acceder a los
     *                               assets
     * @throws IllegalStateException Si el contexto de la aplicacion no esta disponible
     * @throws IOException           Si ocurre un error durante la lectura de los archivos de
     *                               recursos
     */
    public void reloadDataModel(){
        this.selectedContinentPriorReloading = this.getMutableSelectedContinent().getValue();
        AssetManager managerRequiredForImageExtraction =
                this.applicationContext.getAssets();
        this.mutableContinentList.setValue(List.of("Americas", "Asia", "Africa", "Europe",
                                                  "Oceania"));
        loadingAllCountriesAndCountriesFlagsHelper(managerRequiredForImageExtraction);
        //? 2. Ahora cargamos los iconos de cada uno de los paises
        loadingAllContinentNamesAndIcons(managerRequiredForImageExtraction);
        //? 3. Reseteamos el data cleanup
        this.removedContinentList.clear();
        this.removedCountryList.clear();
        this.cleanUpSharedPreferences();
    }

    public void reloadPriorSelectedContinent(){
        this.mutableSelectedContinent.setValue(this.selectedContinentPriorReloading);
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo encargado de limpiar las preferencias compartidas almacenadas en la aplicacion.
     * Elimina todos los registros de continentes y paises removidos previamente almacenados. Este
     * metodo es fundamental para resetear el estado de la aplicacion a sus valores
     * predeterminados.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     * <li>Verifica si las preferencias compartidas estan disponibles y activas</li>
     * <li>Obtiene un editor de preferencias compartidas</li>
     * <li>Elimina la lista de continentes removidos</li>
     * <li>Elimina la lista de paises removidos</li>
     * <li>Aplica los cambios de manera asincrona</li>
     * </ol>
     * </body>
     *
     * @throws SecurityException     Si no se tienen los permisos necesarios para acceder a las
     *                               preferencias compartidas
     * @throws IllegalStateException Si el editor de preferencias no esta disponible
     */
    private void cleanUpSharedPreferences(){
        if (this.isSharedPreferencesOnline()) {
            SharedPreferences.Editor editor = this.preferencesFromApplication.edit();
            editor.remove(SP_REMOVED_CONTINENT_LIST);
            editor.remove(SP_REMOVED_COUNTRIES_LIST);
            editor.apply();
        }
    }


    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo auxiliar encargado de cargar la informacion de todos los paises y sus banderas
     * correspondientes desde los assets de la aplicacion. Este proceso incluye la creacion de
     * objetos CountryInformation para cada pais y la carga de sus imagenes de banderas en formato
     * Bitmap.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Inicializa las estructuras de datos para almacenar paises e imagenes</li>
     *   <li>Itera sobre cada region/continente definido en el sistema</li>
     *   <li>Para cada region, lee y procesa los archivos de imagen de banderas</li>
     *   <li>Crea objetos CountryInformation con la informacion extraida</li>
     *   <li>Carga las imagenes de banderas como Bitmaps</li>
     *   <li>Actualiza los LiveData con la informacion procesada</li>
     * </ol>
     * </body>
     *
     * @param managerRequiredForImageExtraction AssetManager requerido para acceder a los recursos
     *                                          de la aplicacion
     * @throws SecurityException        Si no se tienen los permisos necesarios para acceder a los
     *                                  assets
     * @throws IOException              Si ocurre un error durante la lectura de archivos
     * @throws IllegalArgumentException Si el path de los assets es invalido
     * @throws NullPointerException     Si el AssetManager proporcionado es nulo
     */
    private void loadingAllCountriesAndCountriesFlagsHelper(
            AssetManager managerRequiredForImageExtraction) {
        List<CountryInformation> loadedCountries = new ArrayList<>();
        Map<String, Bitmap> imagesCountries = new ConcurrentHashMap<>();
        if (managerRequiredForImageExtraction != null) {
            //? 2. Leemos todos los components de las assets, buscando las carpetas e imagenes
            try {
                //? 2.1 Iteramos sobre todas las direcciones que tiene el path de la region
                for (String regionDefinedWithin : Objects.requireNonNull(
                        constantContinentList.getValue())) {
                    String[] pathsPerImage =
                            managerRequiredForImageExtraction.list(regionDefinedWithin);

                    //? 2.2 Creamos un objeto interno de cada pais por categoria
                    for (String path : pathsPerImage) {
                        CountryInformation information = new CountryInformation();
                        information.setCountryContinent(regionDefinedWithin);
                        String pathWithoutExtension = path.replace(".png", "");
                        information.setCountryName(pathWithoutExtension
                                                           .substring(regionDefinedWithin.length() + 1)
                                                           .replace('_', ' '));
                        information.setCountryImageAssetPath(regionDefinedWithin + "/" + path);

                        //? 2.3 Cargamos este objeto dentro del arreglo general
                        loadedCountries.add(information);
                        //? 2.4 Descargamos las imagenes para cargarlas al archivo con el path
                        ExecutorService service = Executors.newCachedThreadPool();
                        service.submit(() -> {
                            Bitmap imageForCountryBitmap = null;
                            try (InputStream streamForImage = managerRequiredForImageExtraction.open(
                                    information.getCountryImageAssetPath())) {
                                imageForCountryBitmap = BitmapFactory.decodeStream(streamForImage);
                                imagesCountries.put(information.getCountryImageAssetPath(), imageForCountryBitmap);
                            } catch (Exception e) {
                                Log.e("[ApplicationDataPOJO]", "Error Code 0x001 - [Raised] - ApplicationDataPOJO" +
                                        " - ApplicationDataPOJO - IOException - " + e.getMessage());
                                e.printStackTrace();
                            }
                        });

                    }
                }
            } catch (IOException e) {
                Log.e("[ApplicationDataPOJO]", "Error Code 0x001 - [Raised] - ApplicationDataPOJO" +
                        " - ApplicationDataPOJO - IOException - " + e.getMessage());
                e.printStackTrace();
            }

        }
        //? 3. Con la data cargada la colocamos en los LiveData internos
        this.mutableCountryFlagMap.setValue(new HashMap<>());
        this.mutableCountryFlagMap.setValue(imagesCountries);
        this.mutableCountryList.setValue(new ArrayList<>());
        mutableCountryList.setValue(loadedCountries);
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo auxiliar encargado de cargar los iconos asociados a cada continente desde los recursos
     * de la aplicacion. Este proceso incluye la lectura de archivos de imagen desde la carpeta de
     * Icons y su conversion a objetos Bitmap, los cuales son almacenados en un mapa para su uso
     * posterior en la interfaz de usuario.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Inicializa un mapa concurrente para almacenar los iconos cargados</li>
     *   <li>Lee el listado de archivos de iconos desde la carpeta Icons</li>
     *   <li>Procesa cada archivo de icono de forma asincrona utilizando un ExecutorService</li>
     *   <li>Convierte cada archivo de imagen a un objeto Bitmap</li>
     *   <li>Almacena los Bitmaps en el mapa utilizando el nombre del archivo sin extension</li>
     *   <li>Actualiza el LiveData con el mapa de iconos procesado</li>
     * </ol>
     * </body>
     *
     * @param managerRequiredForImageExtraction AssetManager necesario para acceder a los recursos
     * @throws SecurityException        Si no se tienen los permisos necesarios para acceder a los
     *                                  assets
     * @throws IOException              Si ocurre un error durante la lectura de archivos
     * @throws IllegalArgumentException Si el path de los assets es invalido
     * @throws NullPointerException     Si el AssetManager proporcionado es nulo
     */
    private void loadingAllContinentNamesAndIcons(AssetManager managerRequiredForImageExtraction) {
        Map<String, Bitmap> iconsLoaded = new ConcurrentHashMap<>();
        try {
            String[] icons = managerRequiredForImageExtraction.list("Icons");
            System.out.println(Arrays.toString(icons));
            for (String icon : icons) {
                String pathWithoutExtension = icon.replace(".png", "");
                try {
                    InputStream streamFromPath =
                            managerRequiredForImageExtraction.open("Icons/" + icon);
                    Bitmap bitmap = BitmapFactory.decodeStream(streamFromPath);
                    streamFromPath.close();
                    if (bitmap != null) {
                        iconsLoaded.put(pathWithoutExtension, bitmap);
                    }
                } catch (IOException e) {
                    Log.e("[ApplicationDataPOJO]", "Error Code 0x001 - [Raised] - ApplicationDataPOJO" +
                            " - ApplicationDataPOJO - IOException - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Log.e("[ApplicationDataPOJO]", "Error Code 0x001 - [Raised] - ApplicationDataPOJO" +
                    " - ApplicationDataPOJO - IOException - " + e.getMessage());
            e.printStackTrace();
        }

        //? 5. Cargamos los iconos y su mapa hacia el mutable live data
        this.mutableContinentIconMap.setValue(new HashMap<>());
        this.mutableContinentIconMap.setValue(iconsLoaded);
    }


    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo interno que verifica el estado y disponibilidad de las preferencias compartidas de la
     * aplicacion. Este metodo evalua si las preferencias estan inicializadas y si contienen datos
     * serializados de continentes o paises removidos previamente.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Verifica que el objeto preferencesFromApplication no sea nulo</li>
     *   <li>Comprueba la existencia de la clave para continentes removidos</li>
     *   <li>Comprueba la existencia de la clave para paises removidos</li>
     * </ol>
     * </body>
     *
     * @return boolean Verdadero si las preferencias estan disponibles y contienen datos, falso en
     * caso contrario
     * @throws SecurityException     Si no se tienen los permisos necesarios para acceder a las
     *                               preferencias
     * @throws IllegalStateException Si el contexto de la aplicacion no esta disponible
     */
    private boolean isSharedPreferencesOnline() {
        //? 2. Revisamos si no es nulo y existe contenido
        return this.preferencesFromApplication != null &&
                (this.preferencesFromApplication.contains(SP_REMOVED_CONTINENT_LIST) ||
                        this.preferencesFromApplication.contains(SP_REMOVED_COUNTRIES_LIST));
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo interno responsable de deserializar las preferencias compartidas de la aplicacion.
     * Este proceso recupera la informacion previamente guardada sobre continentes y paises
     * eliminados del sistema. La deserializacion permite restaurar el estado anterior de la
     * aplicacion en cuanto a elementos removidos.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Recupera la cadena de continentes eliminados desde las preferencias compartidas</li>
     *   <li>Procesa la cadena y elimina los continentes del sistema actual</li>
     *   <li>Recupera la cadena de paises eliminados desde las preferencias compartidas</li>
     *   <li>Procesa la cadena y elimina los paises del sistema actual</li>
     * </ol>
     * </body>
     *
     * @throws SecurityException        Si no hay permisos para acceder a las preferencias
     *                                  compartidas
     * @throws IllegalStateException    Si las preferencias compartidas no estan disponibles
     * @throws IllegalArgumentException Si el formato de los datos serializados es invalido
     */
    private void deserializeSharedPreferences() {
        //? 1. Cargamos al programa la lista de continentes eliminados directamente, dado que
        // estos nos eliminan mas rapidamente varios paises.
        String removedContinents = this.preferencesFromApplication
                .getString(SP_REMOVED_CONTINENT_LIST, "");
        if (!removedContinents.isEmpty()) {
            var newString = removedContinents.replace('[', ' ').replace(']', ' ');
            for (String continentName : newString.split(",")) {
                if (!continentName.isEmpty()) {
                    this.removeConstantContinentFromContinentList(
                            continentName.trim().strip());
                }
            }
        }

        //? 2. Cargamos al programa la lista de paises eliminados, en este contexto, solo
        // tendremos paises libres si su continente no se ha eliminado completamente, por
        // ejemplo, si eliminamos USA y Canada, pero no Americas, entonces estos quedan, pero si
        // eliminamos Americas, entonces eliminamos los paises eliminados y nos valemos de los
        // continentes para alivianara el shared preferences
        String removedCountries =
                this.preferencesFromApplication.getString(SP_REMOVED_COUNTRIES_LIST, "");
        if (!removedCountries.isEmpty()) {
            var newString = removedCountries.replace('[', ' ').replace(']', ' ');
            for (String countryName : newString.split(",")) {
                if (!countryName.isEmpty()) {
                    this.removeConstantCountryFromCountryList(countryName.trim().strip());
                }
            }
        }
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo interno responsable de serializar la eliminacion de un continente del sistema. Este
     * proceso incluye la actualizacion de las preferencias compartidas y la eliminacion de todos
     * los paises asociados a dicho continente. La serializacion asegura que los cambios persistan
     * entre sesiones de la aplicacion.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Verifica si el continente ya esta en la lista de continentes eliminados</li>
     *   <li>Anade el continente a la lista de continentes eliminados</li>
     *   <li>Filtra y elimina los paises asociados al continente eliminado</li>
     *   <li>Serializa la lista actualizada de paises eliminados</li>
     *   <li>Serializa la lista actualizada de continentes eliminados</li>
     * </ol>
     * </body>
     *
     * @param continentName El nombre del continente que se va a eliminar y serializar
     * @throws SecurityException        Si no se tienen los permisos necesarios para acceder a las
     *                                  preferencias compartidas
     * @throws IllegalStateException    Si las preferencias compartidas no estan disponibles
     * @throws IllegalArgumentException Si el nombre del continente es invalido o nulo
     */
    private void serializeSingleContinentRemoved(String continentName) {
        //? 1. Para manejar esto, tenemos que tomar la string ya cargada dentro del sistema, si
        // nosotros tenemos una string vacia entonces es el primer continente eliminado, pero por
        // seguridad intentamos eliminar cualquier pais asociado al continente para no tener
        // sobrecarga de memoria
        for (String continentsRemoved : this.removedContinentList) {
            if (continentsRemoved.equalsIgnoreCase(continentName)) {
                return; // Salimos rapidamente (no espero que eso suceda nunca) pero demostraria
                // que ya esta el continente a eliminar
            }
        }
        //> 1.1 Anadimos el continente a la lista de continentes eliminados

        this.removedContinentList.add(continentName);


        //> 1.2 Con el continente seleccionado, intentamos eliminar todos los paises dentro del
        // arreglo cuyo continent sea el continentName
        this.removedCountryList =
                this.removedCountryList.stream().filter(new Predicate<CountryInformation>() {
                    @Override
                    public boolean test(CountryInformation countryInformation) {
                        return countryInformation.getCountryContinent().equalsIgnoreCase(continentName);
                    }
                }).collect(Collectors.toList());


        //> 1.3 Serializamos con un editor los paises filtrados
        SharedPreferences.Editor editor = this.preferencesFromApplication.edit();
        editor.putString(SP_REMOVED_COUNTRIES_LIST, this.removedCountryList.toString());
        editor.commit();
        //> 1.4 Serializamos con un editor
        editor.putString(SP_REMOVED_CONTINENT_LIST, this.removedContinentList.toString());
        editor.commit();
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo interno responsable de serializar la eliminacion de un pais del sistema. Este proceso
     * incluye la verificacion de duplicados, el almacenamiento del pais en la lista de eliminados y
     * la actualizacion de las preferencias compartidas para mantener la persistencia de datos entre
     * sesiones de la aplicacion.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Verifica si el pais ya existe en la lista de paises eliminados</li>
     *   <li>Anade el pais a la lista de paises eliminados si no existe</li>
     *   <li>Convierte la lista de paises eliminados a formato String</li>
     *   <li>Actualiza las preferencias compartidas con la nueva lista serializada</li>
     * </ol>
     * </body>
     *
     * @param informationForCountryRemoved Objeto CountryInformation que contiene la informacion del
     *                                     pais a eliminar
     * @throws SecurityException        Si no hay permisos para acceder a las preferencias
     *                                  compartidas
     * @throws IllegalStateException    Si las preferencias compartidas no estan disponibles
     * @throws IllegalArgumentException Si el objeto CountryInformation es invalido o nulo
     */
    private void serializeSingleCountryRemoved(CountryInformation informationForCountryRemoved){
        //? Aqui no debemos filtrarpor los continentes eliminados, dado que si estos ya estan
        // eliminados es muy poco probable que estos vuelvan a aparecer vivos, y que se cargue
        // informacion incorrecta aqui. Ademas, estos paises se serializan rapidamente como una
        // String completa

        //> 1. Revisamos si el pais ya esta eliminado
        for(CountryInformation information: this.removedCountryList){
            if (information.getCountryName().equalsIgnoreCase(informationForCountryRemoved.getCountryName())){
                return;
            }
        }

        //> 2. Anadimos el pais a eliminar al listado de paises
        this.removedCountryList.add(informationForCountryRemoved);

        //> 3. Convertimos el listado a una lista de Strings
        List<String> stringList = new ArrayList<>();
        stringList.addAll(this.removedCountryList
                                  .stream()
                                  .map(CountryInformation::getCountryName)
                                  .collect(Collectors.toList()));

        //> 4. Serializamos la lista nueva
        SharedPreferences.Editor editor = this.preferencesFromApplication.edit();
        editor.putString(SP_REMOVED_COUNTRIES_LIST, stringList.toString());
        editor.commit();
    }

    /*? Metodos de trabajo planeados para el mutableContinentList*/
    public LiveData<List<String>> getConstantContinentList() {
        return constantContinentList;
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo responsable de eliminar un continente especifico del sistema y toda su informacion
     * asociada. Esta eliminacion incluye la actualizacion de las listas de continentes, paises,
     * iconos y banderas relacionadas con el continente especificado. Ademas, gestiona la
     * serializacion de los datos eliminados para mantener la persistencia entre sesiones.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Verifica la existencia del continente en las listas del sistema</li>
     *   <li>Elimina el continente de la lista de continentes y su icono asociado</li>
     *   <li>Remueve todos los paises asociados al continente</li>
     *   <li>Elimina las banderas e imagenes relacionadas con el continente</li>
     *   <li>Actualiza las selecciones actuales si estan relacionadas con el continente</li>
     *   <li>Serializa los cambios en las preferencias compartidas</li>
     * </ol>
     * </body>
     *
     * @param continentName El nombre del continente que se desea eliminar
     * @return boolean Verdadero si el continente fue eliminado exitosamente, falso en caso
     * contrario
     * @throws IllegalStateException    Si los LiveData no estan inicializados correctamente
     * @throws IllegalArgumentException Si el nombre del continente es invalido o nulo
     * @throws SecurityException        Si no hay permisos para acceder al almacenamiento
     */
    public boolean removeConstantContinentFromContinentList(String continentName) {

        if (this.constantContinentList.getValue() != null
                && this.constantContinentIconMap.getValue() != null) {
            if (this.constantContinentList.getValue().contains(continentName)
                    && this.constantContinentIconMap.getValue().containsKey(continentName)) {
                //? 1. Removemos la informacion directa del continente
                if (this.mutableContinentIconMap.getValue() != null) {
                    Map<String, Bitmap> updatedIconMap =
                            new HashMap<>(this.mutableContinentIconMap.getValue());
                    updatedIconMap.remove(continentName);
                    this.mutableContinentIconMap.setValue(updatedIconMap);
                }
                if (this.mutableContinentList.getValue() != null) {
                    List<String> updatedContinentList =
                            new ArrayList<>(this.mutableContinentList.getValue());
                    updatedContinentList.remove(continentName);
                    this.mutableContinentList.setValue(updatedContinentList);
                }

                //? 2. Removemos la informacion de los paisees asociados con este continente
                if (this.mutableCountryList.getValue() != null) {
                    List<CountryInformation> updatedCountryList = new ArrayList<>(this.mutableCountryList.getValue());
                    updatedCountryList.removeIf(countryInfo ->
                                                        countryInfo.getCountryContinent().equalsIgnoreCase(continentName)
                                               );
                    this.mutableCountryList.setValue(updatedCountryList);
                }

                //? 3. Removemos la informacion de las imagenes asociadas con este continente
                if (this.mutableContinentIconMap.getValue() != null) {
                    Map<String, Bitmap> updatedIconMap =
                            new HashMap<>(this.mutableContinentIconMap.getValue());
                    List<CountryInformation> countries_to_remove =
                            this.mutableCountryList.getValue();
                    for (CountryInformation information : countries_to_remove) {
                        updatedIconMap.remove((information.getCountryContinent()
                                .equalsIgnoreCase(continentName)) ?
                                                      information.getCountryImageAssetPath() :
                                                      "");
                    }
                    this.mutableContinentIconMap.setValue(updatedIconMap);
                }

                //? 4.  Removemos el continent selected
                if (this.mutableSelectedContinent.getValue() != null && this.getMutableSelectedContinent()
                        .getValue().equalsIgnoreCase(continentName)) {
                    this.mutableSelectedContinent.setValue("");
                }

                //? 5. Removemos el country selected
                if (this.mutableSelectedCountry.getValue() != null) {
                    this.mutableSelectedCountry.setValue(new CountryInformation());
                }

                //? 6. Serializamos hacia sharedPreferences
                serializeSingleContinentRemoved(continentName);
                return true;
            }
        }
        return false;
    }

    /*? Metodos de trabajo planeados para el mutableCountryList*/
    public LiveData<List<CountryInformation>> getConstantCountryList() {
        return constantCountryList;
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo responsable de eliminar un pais especifico del sistema y toda su informacion asociada.
     * Este proceso incluye la eliminacion del pais de la lista principal, la eliminacion de su
     * bandera del mapa de banderas y la actualizacion del pais seleccionado si corresponde. Tambien
     * gestiona la serializacion del pais eliminado para mantener la persistencia entre sesiones.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Verifica la existencia del pais en la lista del sistema</li>
     *   <li>Elimina el pais de la lista principal de paises</li>
     *   <li>Elimina la bandera asociada al pais del mapa de banderas</li>
     *   <li>Actualiza la seleccion actual si corresponde al pais eliminado</li>
     *   <li>Serializa el pais eliminado en las preferencias compartidas</li>
     * </ol>
     * </body>
     *
     * @param countryName El nombre del pais que se desea eliminar del sistema
     * @throws IllegalStateException    Si los LiveData no estan inicializados correctamente
     * @throws IllegalArgumentException Si el nombre del pais es invalido o nulo
     * @throws SecurityException        Si no hay permisos para acceder al almacenamiento
     */
    public void removeConstantCountryFromCountryList(String countryName) {
        if (this.mutableCountryList.getValue() != null) {
            //? 1. Removemos directmente el pais si este existe en el listado de paises
            if (this.mutableCountryList.getValue() != null) {
                CountryInformation information =
                        this.getConstantCountryFromCountryListPerName(countryName);
                List<CountryInformation> updatedCountryList =
                        new ArrayList<>(this.mutableCountryList.getValue());
                updatedCountryList.removeIf(countryInfo ->
                                                    countryInfo.getCountryName().equalsIgnoreCase(countryName)
                                           );
                this.mutableCountryList.setValue(updatedCountryList);
                //? 2. Renovemos directamente la imagen del pais
                if (this.mutableCountryFlagMap.getValue() != null) {
                    Map<String, Bitmap> updatedFlagMap =
                            new HashMap<>(this.mutableCountryFlagMap.getValue());
                    updatedFlagMap.remove(information.getCountryImageAssetPath());
                    this.mutableCountryFlagMap.setValue(updatedFlagMap);
                }

                //? 3. Removenmos directamente tambien si este es el pais seleccionado
                if (this.mutableSelectedCountry.getValue() != null) {
                    this.mutableSelectedCountry.setValue(new CountryInformation());
                }

                //? 4. Serializamos pais removido
                serializeSingleCountryRemoved(information);
            }

        }
    }

    /**
     * <body style="color: WHITE;">
     * <h3>Descripcion:</h3>
     * Metodo que realiza la busqueda de informacion de un pais especifico dentro de la lista de
     * paises manejada por el sistema. El metodo toma como entrada el nombre del pais y retorna un
     * objeto CountryInformation que contiene toda la informacion asociada a dicho pais, incluyendo
     * su continente y la ruta de su bandera.
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Verifica la existencia de datos en el LiveData de paises</li>
     *   <li>Filtra la lista de paises buscando coincidencias con el nombre proporcionado</li>
     *   <li>Retorna el primer pais que coincida o un objeto vacio si no hay coincidencias</li>
     * </ol>
     * </body>
     *
     * @param countryName Nombre del pais que se desea buscar en el sistema
     * @return CountryInformation Objeto con la informacion del pais o un objeto vacio si no se
     * se encuentra
     * @throws IllegalStateException Si el LiveData de paises no esta inicializado
     * @throws NullPointerException  Si el parametro countryName es nulo
     */
    public CountryInformation getConstantCountryFromCountryListPerName(String countryName) {
        //? 1. Si tenemos valores en el contant list buscamos por el valor de esta country absado
        // del nombre
        if (this.constantCountryList.getValue() != null) {
            CountryInformation information = this.constantCountryList
                    .getValue()
                    .stream()
                    .filter(new Predicate<CountryInformation>() {
                        @Override
                        public boolean test(CountryInformation countryInformation) {
                            return countryInformation.getCountryName().equalsIgnoreCase(countryName);
                        }
                    })
                    .findFirst()
                    .orElse(new CountryInformation());

            //? 2. Teniendo en cuenta si hay datos, tenemos que regresar la informacion.
            return information;
        }

        return new CountryInformation();
    }

    /*? Metodos de trabajo planeados para el SelectedContinent*/
    public void setSelectedContinentIntoMutableData(String selectedContinent) {
        this.mutableSelectedContinent.setValue(selectedContinent);
    }

    public MutableLiveData<String> getMutableSelectedContinent() {
        return mutableSelectedContinent;
    }

    /*? Metodos de trabajo planeados para el SelectedCountry*/
    public void setSelectedCountryIntoMutableData(CountryInformation selectedCountry) {
        this.mutableSelectedCountry.setValue(selectedCountry);
    }

    public MutableLiveData<CountryInformation> getMutableSelectedCountry() {
        return mutableSelectedCountry;
    }

    public void updateMutableDeviceMode(Context context) {
        if (context != null) {
            mutableBooleanIsDeviceInTabletMode.setValue(DeviceOrientationUtilities.determineIfDeviceIsTablet(context));
        }
    }

    public LiveData<Boolean> getMutableIsDeviceInTabletMode() {
        return this.mutableBooleanIsDeviceInTabletMode;
    }

    public void updateMutableDeviceOrientation(Context context) {
        if (context != null) {
            mutableBooleanIsDeviceInPortraitMode.setValue(DeviceOrientationUtilities.determineIfDeviceOrientationIsPortrait(context));
        }
    }

    public LiveData<Boolean> getMutableIsDeviceInPortraitMode() {
        return this.mutableBooleanIsDeviceInPortraitMode;
    }

    public void setNavigationFromContinentToCountryHasAlreadyBeenDone(boolean hasItBeenDone) {
        this.navigationFromContinentToCountryHasAlreadyBeenDone.setValue(hasItBeenDone);
    }

    public LiveData<Boolean> getNavigationFromContinentToCountryHasAlreadyBeenDone() {
        return this.navigationFromContinentToCountryHasAlreadyBeenDone;
    }

    public void setNavigationFromCountryToInfoHasAlreadyBeenDone(boolean hasItBeenDone) {
        this.navigationFromCountryToInfoHasAlreadyBeenDone.setValue(hasItBeenDone);
    }

    public LiveData<Boolean> getNavigationFromCountryToInfoHasAlreadyBeenDone() {
        return this.navigationFromCountryToInfoHasAlreadyBeenDone;
    }

    public void setIsNavigatingBack(boolean hasItBeenDone) {
        this.isNavigatingBack.setValue(hasItBeenDone);
    }

    public LiveData<Boolean> getIsNavigatingBack() {
        return this.isNavigatingBack;
    }

    public void dropCountrySelection() {
        //? 1. Cargamos un valor nulo o vacio para eliminar la seleccion anterior
        this.mutableSelectedCountry.setValue(new CountryInformation());
        //? 2. Reseteamos la flag de navegacion de Country a Info a falso de nuevo
        this.navigationFromCountryToInfoHasAlreadyBeenDone.setValue(false);
    }

    public void dropContinentSelection() {
        //? 1. Cargamos un valor nulo para eliminar el continente seleccionado anteriormente
        this.mutableSelectedContinent.setValue("");
        //? 2. Cargamos el valor correcto a la constante
        this.navigationFromContinentToCountryHasAlreadyBeenDone.setValue(false);
    }

    public void helperHandlerParaBackNavigationFromInfoToCountryViews() {
        //? 1. Marcamos que la navegacion es en reversa
        this.setIsNavigatingBack(true);
        this.dropCountrySelection();
    }

    public void helperHandlerParaBackNavigationFromCountryToContinentViews() {
        //? 1. Marcamos la navegacion en reversa aqui tambien
        this.setIsNavigatingBack(true);
        this.dropContinentSelection();
    }

}
