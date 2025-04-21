package com.evolvlabs.CountryInformationViewer.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.evolvlabs.countryinformationviewer.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import kotlin.Unit;

/**
 * @author : Santiago Arellano
 * @description : El presente archivo implementa un dialog builder disenado para mostrar un mensaje
 * de confirmacion al usuario en caso de realizar una accion que requiere trabajo extra como
 * recargar datos o borrar datos.
 */
public class ConfirmationDialogBuilder {
    public static void showConfirmationDialog(String title,
                                              String message,
                                              Runnable onConfirm,
                                              Context context,
                                              Resources resources) {
        //? 1. Creamos el builder base de nuestro dialogo, lo que hacemos es simplemente entrar y
        // pasar los componentes de nuestra vista principal rapidamente y cargamos lo estructural
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(resources.getString(R.string.remove_message_on_confirm_button_name),
                                   (dialog, which) -> onConfirm.run())
                .setNegativeButton(resources.getString(R.string.remove_message_on_reject_button_name), null)
                .setBackground(ResourcesCompat.getDrawable(resources,
                                                           R.drawable
                                                                   .rounded_corners_for_component_styling,
                                                           null));

        //? 2. En este punto tranformarmos el builder a un AlertDialog real, y le enchufamos la
        // el listener adecuado para cuando mostremos la UI. Hacemos esto ya que cuando se
        // muestre, al inicio se mostraba con el look de material UI y necesitabamos un look
        // parecido al de la App
        AlertDialog dialogForAlert = builder.create();
        dialogForAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //? 2.1 Ponemos el color del texto tanto de titulo, cuerpo y botones
                TextView title = dialogForAlert.findViewById(android.R.id.title);
                Typeface faceForInterBoldFont = ResourcesCompat.getFont(context, R.font.inter_bold);
                Typeface faceForInterFont = ResourcesCompat.getFont(context, R.font.inter);

                if (title != null) {
                    title.setTextColor(ResourcesCompat.getColor(resources,
                                                                R.color.palette_Brilliant_Cobalt
                            , null));
                    title.setTextSize(18);
                    title.setTypeface(faceForInterBoldFont);
                    title.setSingleLine(false);
                    title.setMaxLines(5);
                    title
                            .setLayoutParams(
                                    new DrawerLayout
                                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                          ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                TextView body = dialogForAlert.findViewById(android.R.id.message);
                if (body != null) {
                    body.setTextColor(ResourcesCompat.getColor(resources,
                                                               R.color.palette_Brilliant_Cobalt,
                                                               null));
                    body.setMaxLines(5);
                    body.setTypeface(faceForInterFont);
                    body.setTextSize(14);
                    body.setSingleLine(false);
                }
                Button positiveButton =
                        dialogForAlert.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton =
                        dialogForAlert.getButton(DialogInterface.BUTTON_NEGATIVE);

                positiveButton
                        .setAllCaps(false);
                negativeButton
                        .setAllCaps(false);
                negativeButton
                        .setBackgroundColor(resources.getColor(R.color.palette_Anakiwa,
                                                               null));
                positiveButton
                        .setBackgroundColor(resources.getColor(R.color.palette_Fog,
                                                               null));
                positiveButton
                        .setTextColor(resources.getColor(R.color.palette_Dodger_Blue,
                                                         null));
                negativeButton
                        .setTextColor(resources.getColor(R.color.palette_Dodger_Blue,
                                                         null));
            }
        });
        //? 3. Mostramos al final la alerta y continuamos normalmente.
        dialogForAlert.show();
    }

    public static void showConfirmationDialog(String title,
                                              String message,
                                              String confirmationButtonText,
                                              String negationButtonText,
                                              Runnable onConfirm,
                                              Context context,
                                              Resources resources) {
        //? 1. Creamos el builder base de nuestro dialogo, lo que hacemos es simplemente entrar y
        // pasar los componentes de nuestra vista principal rapidamente y cargamos lo estructural
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(confirmationButtonText,
                                   (dialog, which) -> onConfirm.run())
                .setNegativeButton(negationButtonText, null)
                .setBackground(ResourcesCompat.getDrawable(resources,
                                                           R.drawable
                                                                   .rounded_corners_for_component_styling,
                                                           null));

        //? 2. En este punto tranformarmos el builder a un AlertDialog real, y le enchufamos la
        // el listener adecuado para cuando mostremos la UI. Hacemos esto ya que cuando se
        // muestre, al inicio se mostraba con el look de material UI y necesitabamos un look
        // parecido al de la App
        AlertDialog dialogForAlert = builder.create();
        dialogForAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //? 2.1 Ponemos el color del texto tanto de titulo, cuerpo y botones
                TextView title = dialogForAlert.findViewById(android.R.id.title);
                Typeface faceForInterBoldFont = ResourcesCompat.getFont(context, R.font.inter_bold);
                Typeface faceForInterFont = ResourcesCompat.getFont(context, R.font.inter);

                if (title != null) {
                    title.setTextColor(ResourcesCompat.getColor(resources,
                                                                R.color.palette_Brilliant_Cobalt
                            , null));
                    title.setTextSize(18);
                    title.setTypeface(faceForInterBoldFont);
                    title.setSingleLine(false);
                    title.setMaxLines(5);
                    title
                            .setLayoutParams(
                                    new DrawerLayout
                                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                          ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                TextView body = dialogForAlert.findViewById(android.R.id.message);
                if (body != null) {
                    body.setTextColor(ResourcesCompat.getColor(resources,
                                                               R.color.palette_Brilliant_Cobalt,
                                                               null));
                    body.setMaxLines(5);
                    body.setTypeface(faceForInterFont);
                    body.setTextSize(14);
                    body.setSingleLine(false);
                }
                Button positiveButton =
                        dialogForAlert.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton =
                        dialogForAlert.getButton(DialogInterface.BUTTON_NEGATIVE);

                positiveButton
                        .setAllCaps(false);
                negativeButton
                        .setAllCaps(false);
                negativeButton
                        .setBackgroundColor(resources.getColor(R.color.palette_Anakiwa,
                                                               null));
                positiveButton
                        .setBackgroundColor(resources.getColor(R.color.palette_Fog,
                                                               null));
                positiveButton
                        .setTextColor(resources.getColor(R.color.palette_Dodger_Blue,
                                                         null));
                negativeButton
                        .setTextColor(resources.getColor(R.color.palette_Dodger_Blue,
                                                         null));
            }
        });
        //? 3. Mostramos al final la alerta y continuamos normalmente.
        dialogForAlert.show();
    }

}
