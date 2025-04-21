package com.evolvlabs.CountryInformationViewer.Controllers;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.CustomFloatAttributes;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.evolvlabs.CountryInformationViewer.DataModel.ApplicationDataPOJO;
import com.evolvlabs.CountryInformationViewer.DataModel.CountryInformation;
import com.evolvlabs.countryinformationviewer.R;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : Paulo Cantos, Santiago Arellano
 * @date : 19-Apr-2025
 * @description : El presente archivo implementa el controlador de la carga de la vista de la
 * informacion de un pais seleccionado en el sistema.
 */
public class HereIsTheirInfoController extends Fragment {

    private ApplicationDataPOJO viewModelForApp;
    private TextView inHereIsYourInfo_TextViewForCountryName;
    private ImageView inHereIsYourInfo_ImageViewForFlag;
    private WebView inHereIsYourInfo_WebViewForWikipediaPage;
    private AtomicBoolean areWeInTabletMode = new AtomicBoolean(false);
    private AtomicBoolean areWeInPortraitOrientation = new AtomicBoolean(false);

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo responsable de crear y retornar la vista del fragmento. Este metodo es llamado por el
     * sistema Android cuando es momento de dibujar la interfaz de usuario del fragmento. Infla el
     * layout definido para este fragmento y lo prepara para su visualizacion.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Recibe el LayoutInflater del sistema</li>
     *   <li>Obtiene el contenedor padre donde se colocara el fragmento</li>
     *   <li>Infla el layout XML correspondiente a este fragmento</li>
     *   <li>Retorna la vista inflada al sistema</li>
     * </ol>
     * </body>
     *
     * @param inflater           objeto usado para inflar el layout XML
     * @param container          vista padre donde se adjuntara el fragmento
     * @param savedInstanceState estado previo del fragmento si esta siendo recreado
     * @return View la vista raiz del fragmento
     * @throws IllegalArgumentException si el layout no puede ser inflado
     * @throws NullPointerException     si el inflater es nulo
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_here_is_their_info_view, container, false);
    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo que se ejecuta despues de que la vista del fragmento ha sido creada. Es responsable de
     * inicializar los componentes de la interfaz de usuario, establecer los observadores necesarios
     * y configurar la navegacion. Este metodo es fundamental para la configuracion inicial del
     * fragmento y sus interacciones.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Inicializa y configura el ViewModel de la aplicacion</li>
     *   <li>Configura los observadores para el tipo de dispositivo y orientacion</li>
     *   <li>Recupera y almacena referencias a los componentes de la UI</li>
     *   <li>Establece el observador para la seleccion de paises</li>
     *   <li>Configura el control de navegacion para el boton de retroceso</li>
     * </ol>
     * </body>
     *
     * @param view               La vista raiz del fragmento
     * @param savedInstanceState Bundle que contiene el estado anterior del fragmento
     * @throws IllegalStateException si el fragmento no esta adjunto a una actividad
     * @throws NullPointerException  si la vista es nula o no se puede inicializar el ViewModel
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //> 1. Cargamos el AndroidViewModel desde la apliicacion en general
        this.viewModelForApp = new ViewModelProvider(requireActivity())
                .get(ApplicationDataPOJO.class);
        this.viewModelForApp.updateMutableDeviceMode(requireContext());
        this.viewModelForApp.updateMutableDeviceOrientation(requireContext());


        //> 1.1 Conectamos listeners hacia los valores especificos del tipo de dispositivo y
        // orientacion que tenemos
        this.setUpOrientationAndDeviceTypeListeners();

        //? 2. Cargamos los componentes desde el layout
        this.retrieveComponentsAndStoreReferenceFromUI();


        //? 3. Conectamos un listener de cambio de pais (seleccion en la pestana anterior) al
        // texto e image view
        this.connectOnCountrySelectedObservableListener();

        //? 3. Definimos el control para regresar de vista a vista.
        this.setUpBackButtonPressNavigationControl();

    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo responsable de establecer los observadores que monitorizan el tipo de dispositivo y su
     * orientacion. Mantiene actualizadas las variables atomicas que indican si el dispositivo es
     * una tableta y si esta en modo retrato, permitiendo que la aplicacion responda a cambios en
     * tiempo real.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Establece un observador para el modo tableta del dispositivo</li>
     *   <li>Actualiza el valor atomico areWeInTabletMode segun los cambios</li>
     *   <li>Establece un observador para la orientacion del dispositivo</li>
     *   <li>Actualiza el valor atomico areWeInPortraitOrientation segun los cambios</li>
     * </ol>
     * </body>
     *
     * @throws IllegalStateException si el fragmento no esta adjunto a un ciclo de vida valido
     * @throws NullPointerException  si viewModelForApp no esta inicializado correctamente
     */
    private void setUpOrientationAndDeviceTypeListeners() {
        this.viewModelForApp.getMutableIsDeviceInTabletMode().observe(getViewLifecycleOwner(),
             aBoolean -> areWeInTabletMode.set(aBoolean));
        this.viewModelForApp.getMutableIsDeviceInPortraitMode().observe(getViewLifecycleOwner(),
             aBoolean -> areWeInPortraitOrientation.set(aBoolean));
    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo encargado de establecer y gestionar el observador para los cambios en la seleccion de
     * paises. Actualiza la interfaz de usuario cuando se selecciona un nuevo pais, mostrando su
     * nombre, bandera y pagina de Wikipedia correspondiente. Tambien maneja los casos donde no hay
     * pais seleccionado o cuando se elimina la seleccion.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Establece un observador para el LiveData del pais seleccionado</li>
     *   <li>Verifica si existe informacion valida del pais seleccionado</li>
     *   <li>Actualiza el TextView con el nombre del pais</li>
     *   <li>Carga y muestra la bandera del pais desde el cache o los assets</li>
     *   <li>Carga la pagina de Wikipedia correspondiente al pais</li>
     * </ol>
     * </body>
     *
     * @throws IllegalStateException si el fragmento no esta adjunto a una actividad
     * @throws NullPointerException  si los componentes de la UI no estan inicializados
     * @throws IOException           si hay problemas al cargar los recursos de imagen
     */
    private void connectOnCountrySelectedObservableListener() {
        //? 1. Cargamos un observer a los datos principales de la seleccion de country
            viewModelForApp.getMutableSelectedCountry().observe(getViewLifecycleOwner(),
                                                            new Observer<CountryInformation>() {
                                                                @Override
                                                                public void onChanged(CountryInformation countryInformation) {
                                                                    //? 1.1 Revisamos el tipo de
                                                                    // informacion que tenemos,
                                                                    // si es nula, entonces
                                                                    // significa que se ha
                                                                    // eliminado el contenido de
                                                                    // este pais
                                                                    if (countryInformation
                                                                            .getCountryName()
                                                                            .equalsIgnoreCase("null")){
                                                                        //? 1.1.1 Trabajamos para
                                                                        // conectar un mensaje al
                                                                        // listener de nuestros
                                                                        // datos
                                                                        inHereIsYourInfo_TextViewForCountryName.setText("No Country Selected");
                                                                        displayPageNotFoundErrorPage(inHereIsYourInfo_WebViewForWikipediaPage,
                                                                                                     viewModelForApp.constantSelectedCountry
                                                                                                             .getValue().getCountryName(),
                                                                                                     "https://en.wikipedia.org/wiki/Null");
                                                                        inHereIsYourInfo_ImageViewForFlag.setImageBitmap(null);
                                                                    } else {
                                                                        //? 1.1.2 Cargamos los
                                                                        // datos que tengamos
                                                                        inHereIsYourInfo_TextViewForCountryName.setText(
                                                                                countryInformation.getCountryName());
                                                                        Bitmap imageBitmap = null;
                                                                        if (viewModelForApp.constantCountryFlagMap.getValue() != null){
                                                                            if (viewModelForApp.constantCountryFlagMap.getValue().containsKey(
                                                                                    countryInformation.getCountryImageAssetPath())){
                                                                                imageBitmap =
                                                                                        viewModelForApp.constantCountryFlagMap
                                                                                                .getValue()
                                                                                                .get(countryInformation.getCountryImageAssetPath());
                                                                            } else {
                                                                                downloadBitmap(countryInformation, imageBitmap);
                                                                            }
                                                                        }  else {
                                                                            downloadBitmap(countryInformation, imageBitmap);
                                                                        }

                                                                        if (imageBitmap != null){
                                                                            inHereIsYourInfo_ImageViewForFlag.setImageBitmap(imageBitmap);
                                                                        }
                                                                        //? 1.1.3 Cargamos la
                                                                        // conexion a la red
                                                                        attemptToLoadWikipediaPageForCountry();
                                                                    }
                                                                }
                                                            });

    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo encargado de descargar y decodificar una imagen de bandera desde los assets de la
     * aplicacion. Procesa el archivo de imagen almacenado localmente y lo convierte en un objeto
     * Bitmap para su visualizacion en la interfaz de usuario.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Abre un InputStream desde los assets usando la ruta proporcionada</li>
     *   <li>Decodifica el stream de datos en un objeto Bitmap</li>
     *   <li>Maneja posibles errores de entrada/salida durante la operacion</li>
     * </ol>
     * </body>
     *
     * @param countryInformation objeto que contiene la informacion del pais, incluyendo la ruta del
     *                           archivo de la bandera
     * @param imageBitmap        objeto Bitmap que almacenara la imagen decodificada
     * @throws IOException              si hay un error al acceder o leer el archivo de imagen
     * @throws IllegalArgumentException si la ruta del archivo es invalida
     * @throws IllegalStateException    si el contexto de la actividad no esta disponible
     */
    private void downloadBitmap(CountryInformation countryInformation, Bitmap imageBitmap) {
        try (InputStream stream = getActivity().getAssets().
                open(countryInformation.getCountryImageAssetPath())) {
            imageBitmap = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo responsable de inicializar y almacenar las referencias a los componentes de la
     * interfaz de usuario desde el layout. Obtiene las referencias de los elementos visuales
     * necesarios para mostrar la informacion del pais seleccionado, incluyendo el nombre, la
     * bandera y la pagina de Wikipedia.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Obtiene la referencia al TextView para mostrar el nombre del pais</li>
     *   <li>Obtiene la referencia al ImageView para mostrar la bandera del pais</li>
     *   <li>Obtiene la referencia al WebView para mostrar la pagina de Wikipedia</li>
     * </ol>
     * </body>
     *
     * @throws IllegalStateException si el fragmento no esta adjunto a una vista
     * @throws NullPointerException  si no se puede encontrar alguno de los componentes en el
     *                               layout
     */
    private void retrieveComponentsAndStoreReferenceFromUI() {
        this.inHereIsYourInfo_TextViewForCountryName =
                this.getView().findViewById(R.id.inHereIsYourInfo_TextViewForCountryName);
        this.inHereIsYourInfo_ImageViewForFlag =
                this.getView().findViewById(R.id.inHereIsYourInfo_ImageViewForFlag);
        this.inHereIsYourInfo_WebViewForWikipediaPage =
                this.getView().findViewById(R.id.inHereIsYourInfo_WebViewForWikipediaPage);
    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo responsable de cargar la pagina de Wikipedia correspondiente al pais seleccionado en
     * el WebView de la aplicacion. Gestiona la carga de contenido web y maneja posibles errores
     * durante el proceso de carga.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Obtiene el pais seleccionado del ViewModel</li>
     *   <li>Construye la URL de Wikipedia basada en el nombre del pais</li>
     *   <li>Configura el WebView con soporte JavaScript</li>
     *   <li>Implementa un WebViewClient para manejar errores de carga</li>
     *   <li>Inicia la carga de la pagina web</li>
     * </ol>
     * </body>
     *
     * @throws IllegalStateException si el WebView no esta inicializado correctamente
     * @throws NullPointerException  si no hay un pais seleccionado en el ViewModel
     */
    private void attemptToLoadWikipediaPageForCountry() {
        //? 1. Obtenemos el valor del pais seleccionado una vez en la vista
        if (this.viewModelForApp.constantSelectedCountry.getValue() != null) {
            String httpAccessLinkToWikipedia =
                    "https://en.wikipedia.org/wiki/" + viewModelForApp.constantSelectedCountry.
                            getValue().getCountryName().replace(" ","_");
            this.inHereIsYourInfo_WebViewForWikipediaPage =
                    this.getView().findViewById(R.id.inHereIsYourInfo_WebViewForWikipediaPage);
            //? 2. Por motivos de la pagina de wikipedia conectamos y encendemos el manejo de
            // JavaScript
            if (this.inHereIsYourInfo_WebViewForWikipediaPage != null){
                this.inHereIsYourInfo_WebViewForWikipediaPage.getSettings().setJavaScriptEnabled(true);

                //? 3. Creamos un webclient para manejar cualquier error internamente
                this.inHereIsYourInfo_WebViewForWikipediaPage.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        //? 4. Si la pagina no se carga correctamente, mostramos un error
                        if (view.getTitle() == null || view.getTitle().contains("Error")) {
                            displayPageNotFoundErrorPage(view, viewModelForApp.constantSelectedCountry.getValue().getCountryName(),
                                                         httpAccessLinkToWikipedia);
                        }

                    }
                });

                //? 4. Cargamos el link
                this.inHereIsYourInfo_WebViewForWikipediaPage.loadUrl(httpAccessLinkToWikipedia);
            }

        }
    }


    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo encargado de mostrar una pagina de error personalizada cuando no se puede cargar la
     * informacion de Wikipedia para un pais especifico. Genera una pagina HTML con un mensaje de
     * error informativo y presenta detalles sobre el intento fallido de carga.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Genera una pagina HTML con estilos y estructura definida</li>
     *   <li>Incluye el nombre del pais y la URL que causo el error</li>
     *   <li>Muestra un mensaje explicativo sobre posibles causas del error</li>
     *   <li>Carga la pagina generada en el WebView proporcionado</li>
     * </ol>
     * </body>
     *
     * @param webViewFromMainMethod El WebView donde se mostrara la pagina de error
     * @param countryNameToDisplay  Nombre del pais que causo el error
     * @param urlForCountry         URL de Wikipedia que no pudo ser cargada
     * @throws IllegalArgumentException si alguno de los parametros es nulo
     * @throws IllegalStateException    si el WebView no esta correctamente inicializado
     */
    private void displayPageNotFoundErrorPage(WebView webViewFromMainMethod,
                                              String countryNameToDisplay,
                                              String urlForCountry) {
        //> 1. Cargamos una hardcoded webpage que cree para que se vea bonito en el caso de que 
        // elimines un pais o un continente en el layout de tableta, las paginas de wikipedia 
        // generalmente no fallan, lo que puede fallar es que elimines el pais o el continente.
        String webpage = "" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body style=\"background-color: #e4c8a0\">\n" +
                "<div style=\"background-color: #7a5c43;\n" +
                "border-radius: 15px;\n" +
                "border: #503b2b 5px solid;\n" +
                "padding: 20px;\n" +
                "margin: 20px;\n" +
                "font-family: Inter, sans-serif;\n" +
                "text-align: center\">\n" +
                "    <h1>Ha occurido un error al cargar la pagina para " + ((countryNameToDisplay.equalsIgnoreCase("null")) ? " un pais eliminado" : (" "+ countryNameToDisplay)) + " </h1>\n" +
                "</div>\n" +
                "<blockquote style=\"font-family: sans-serif, Inter; background-color: #f6e5d5; padding: 20px; border: #7a5c43 1px solid;\n" +
                "border-radius: 15px\">\n" +
                "    <p>\n" +
                "        Intentamos arreglar el problema y buscar la pagina en base a este " + urlForCountry +
                " url generado automáticamente, pero al intentar cargar\n" +
                "        el contenido hemos encontrado problemas de carga y lo reportamos en esta pagina.\n" +
                "    </p>\n" +
                "    <p>Dentro de Wikipedia, algunos paises cuyo nombre es similar a una pronvicia, o estado, cargaran una pagina que te permitira\n" +
                "    navegar desde ella hacia la pagina especifica. No obstante, si un pais no existe en wikipedia, por cualquier motivo, el error es grave\n" +
                "    y no tenemos forma de enviarte hacia esa pagina</p>\n" +
                "</blockquote>\n" +
                "<div style=\"background-color: #7a5c43;\n" +
                "border-radius: 15px;\n" +
                "border: #503b2b 5px solid;\n" +
                "padding: 20px;\n" +
                "margin: 20px;\n" +
                "font-family: Inter, sans-serif;\n" +
                "text-align: center\">\n" +
                "    <h3>Para salir de aqui, solo regresa al anterior menu y selecciona otro pais</h3>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
        webViewFromMainMethod.loadDataWithBaseURL(null, webpage,
                                                  "text/html", "UTF-8",
                                                  null);
    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo que gestiona los cambios de configuracion del dispositivo, especialmente durante
     * cambios de orientacion o modo de visualizacion. Este metodo se activa automaticamente cuando
     * ocurre un cambio en la configuracion del dispositivo y actualiza el estado de la aplicacion
     * para reflejar la nueva configuracion.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Ejecuta la implementacion base de onConfigurationChanged</li>
     *   <li>Actualiza el modo del dispositivo en el ViewModel</li>
     *   <li>Actualiza la orientacion del dispositivo en el ViewModel</li>
     * </ol>
     * </body>
     *
     * @param newConfig objeto Configuration que contiene la nueva configuracion del dispositivo
     * @throws IllegalStateException si el fragmento no esta adjunto a un contexto
     */
    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewModelForApp.updateMutableDeviceMode(requireContext());
        viewModelForApp.updateMutableDeviceOrientation(requireContext());
    }

    /**
     * <body style="color: WHITE">
     * <h3>Descripcion:</h3>
     * <p>
     * Metodo encargado de configurar el control de navegacion hacia atras en la aplicacion. Este
     * componente permite a los usuarios regresar a la vista anterior cuando presionan el boton de
     * retroceso del dispositivo. La implementacion garantiza una transicion suave entre fragmentos
     * y actualiza el estado de la aplicacion segun sea necesario.
     * </p>
     *
     * <h3>Metodologia:</h3>
     * <ol>
     *   <li>Registra un callback en el dispatcher de botones de retroceso de la actividad</li>
     *   <li>Notifica al ViewModel sobre la navegacion de regreso</li>
     *   <li>Obtiene el controlador de navegacion de la actividad actual</li>
     *   <li>Ejecuta la navegacion hacia atras mediante popBackStack</li>
     * </ol>
     * </body>
     *
     * @throws IllegalStateException si el fragmento no esta asociado a una actividad
     * @throws NullPointerException  si el controlador de navegacion no puede ser encontrado
     */
    private void setUpBackButtonPressNavigationControl(){
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                                                                   new OnBackPressedCallback(true) {
                                                                       @Override
                                                                       public void handleOnBackPressed() {
                                                                           //? 1. Informamos al
                                                                           // view model que
                                                                           // estamos de regreso
                                                                           // desde la info hacia
                                                                           // country
                                                                           viewModelForApp.helperHandlerParaBackNavigationFromInfoToCountryViews();

                                                                           //? 2. Navegamos hacia
                                                                           // atras
                                                                           NavController controller =
                                                                                   Navigation.findNavController(requireActivity(),
                                                                                                                R.id.nav_host_fragment);
                                                                           if (controller != null){
                                                                               try{
                                                                                   controller.popBackStack();
                                                                               } catch (
                                                                                       Exception e) {
                                                                                   e.printStackTrace();
                                                                               }
                                                                           } else {
                                                                               System.out.println("Controller was null");
                                                                           }

                                                                       }
                                                                   });
    }
}