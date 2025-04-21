package com.evolvlabs.CountryInformationViewer.DataModel;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.evolvlabs.CountryInformationViewer.Controllers.ChooseAContinentController;
import com.evolvlabs.CountryInformationViewer.Controllers.ChooseACountryController;
import com.evolvlabs.CountryInformationViewer.Utilities.ConfirmationDialogBuilder;
import com.evolvlabs.countryinformationviewer.R;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

public class CountryRecyclerAdapter extends RecyclerView.Adapter<CountryRecyclerAdapter.CountryViewHolder> {

    private final ApplicationDataPOJO viewModelPassedFromOutside;
    private CountryInformation[] countriesFiltered;
    private AssetManager assetManagerToDownloadImagesFrom;

    public CountryRecyclerAdapter(ApplicationDataPOJO applicationDataPOJOFromTheOutside,
                                  Context contextFromTheOutside) {
        this.viewModelPassedFromOutside = applicationDataPOJOFromTheOutside;

        this.assetManagerToDownloadImagesFrom = contextFromTheOutside.getAssets();

    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {

        private ImageView inHereIsYourInfo_ImageViewForFlag;
        private TextView inHereIsYourInfo_TextViewForCountryName;

        public CountryViewHolder(@NonNull @NotNull View itemView) {
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

        public void bindOnLongClickListenr(View.OnLongClickListener listener) {
            if (listener != null) {
                this.inHereIsYourInfo_TextViewForCountryName.setOnLongClickListener(listener);
                this.inHereIsYourInfo_ImageViewForFlag.setOnLongClickListener(listener);
                this.itemView.setOnLongClickListener(listener);
            }
        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an
     * item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items of the
     * given type. You can either create a new View manually or inflate it from an XML layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of the
     * View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an
     *                 adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public @NotNull CountryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflatedViewForLayout =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_recycler_view_layout,
                                 parent, false);
        return new CountryViewHolder(inflatedViewForLayout);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method again if the
     * position of the item changes in the data set unless the item itself is invalidated or the new
     * position cannot be determined. For this reason, you should only use the <code>position</code>
     * parameter while acquiring the related data item inside this method and should not keep a copy
     * of it. If you need the position of an item later on (e.g. in a click listener), use
     * {@link ViewHolder#getAdapterPosition()} which will have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can handle
     * efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item
     *                 at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull CountryRecyclerAdapter.CountryViewHolder holder, int position) {
        this.countriesFiltered = this.getFilteredCountriesBasedOnContinentSelection();
        if (viewModelPassedFromOutside.constantCountryList.getValue() != null) {
            String countryName = countriesFiltered[position].getCountryName();
            //? 2. Tenemos que ahora cargar la imagen desde el sistema
            String countryAssetPath = countriesFiltered[position].getCountryImageAssetPath();
            Bitmap countryIconBitmap = null;
            if (viewModelPassedFromOutside.constantCountryFlagMap.getValue() != null){
                 countryIconBitmap =
                         viewModelPassedFromOutside.constantCountryFlagMap.getValue().get(countryAssetPath);
            } else {
                try {
                    InputStream countryIconStream =
                            assetManagerToDownloadImagesFrom.open(countryAssetPath);
                    countryIconBitmap = BitmapFactory.decodeStream(countryIconStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            holder.bind(countryName, countryIconBitmap);
            holder.bindOnClickListener(new OnCountryClickHandler());
            holder.bindOnLongClickListenr(new OnCountryLongClickListener());
        }
    }

    private CountryInformation[] getFilteredCountriesBasedOnContinentSelection() {
        return (countriesFiltered =
                viewModelPassedFromOutside.constantCountryList
                        .getValue()
                        .stream()
                        .filter(new Predicate<CountryInformation>() {
                            @Override
                            public boolean test(CountryInformation countryInformation) {
                                return countryInformation
                                        .getCountryContinent()
                                        .equalsIgnoreCase(
                                                viewModelPassedFromOutside
                                                        .constantSelectedContinent
                                                        .getValue());
                            }
                        }).toArray(CountryInformation[]::new));

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return this.getFilteredCountriesBasedOnContinentSelection().length;
    }



    private class OnCountryClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //? 1. Para cargar el listener, debemos de cargar la informacion de la vista, es
            // decir la informacion asociada con el nombre del texto para buscar en base de este
            // nombre el CountryInformation y luego el resto de cargas
            if (v.findViewById(R.id.inHereIsYourInfo_TextViewForCountryName) != null) {
                String textFromView = ((TextView) v.findViewById(
                        R.id.inHereIsYourInfo_TextViewForCountryName)).getText().toString();
                CountryInformation informationFromView =
                        viewModelPassedFromOutside
                                .getConstantCountryFromCountryListPerName(textFromView);
                System.out.println(informationFromView);
                if (!informationFromView.getCountryName().isEmpty()
                        && !informationFromView.getCountryImageAssetPath().isEmpty()) {
                    viewModelPassedFromOutside.setSelectedCountryIntoMutableData(informationFromView);
                }
            }

        }
    }

    private class OnCountryLongClickListener implements View.OnLongClickListener {

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {
            //? 1. Aqui debemos manejar la seleccion de datos de la vista para poder eliminar el
            // valor directamente del view model interno. Para hacer esto debemos de tomar el
            // valor de la vista, y si este existe tenemos que eliminarlo de la lista.
            if (v.findViewById(R.id.inHereIsYourInfo_TextViewForCountryName) != null) {
                String textFromView = ((TextView) v.findViewById(
                        R.id.inHereIsYourInfo_TextViewForCountryName)).getText().toString();

                //? 2. Conectamos con el view model para eliminar los datos directamente
                if (viewModelPassedFromOutside.constantCountryList.getValue() != null) {
                    if (!textFromView.isEmpty()) {
                        ConfirmationDialogBuilder.showConfirmationDialog(
                                v.getResources().getString(R.string.removing_country_dialog_header, textFromView),
                                v.getResources().getString(R.string.removing_country_confirmation_message),
                                () -> {
                                    viewModelPassedFromOutside.removeConstantCountryFromCountryList(textFromView);
                                }, v.getContext(), v.getResources());
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
