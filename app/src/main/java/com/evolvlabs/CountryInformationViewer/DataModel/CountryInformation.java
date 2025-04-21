package com.evolvlabs.CountryInformationViewer.DataModel;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

/**
 * @author: Paulo Cantos, Santiago Arellano
 * @date: 15-Apr-2025
 * @description: El presente archivo define la implementacion base de una clase para el manejo de
 * los datos de la aplicacion. Si bien se podia manejar la informacion sin tener que manejar una
 * clase extra, por fines demostrativos y de separacion de responsabilidades, decidimos crear esta
 * clase para aislar el funcionamiento de los datos de la app
 */
public class CountryInformation implements Comparable<CountryInformation> {

    /*! Parametros internos*/
    private String countryContinent;
    private String countryName;
    private String countryImageAssetPath;

    /*! Constructores*/
    public CountryInformation(String countryContinent, String countryName,
                              String countryImageAssetPath) {
        // ? 1. Cargamos todos los setters para los valores externos
        this.setCountryContinent(countryContinent);
        this.setCountryName(countryName);
        this.setCountryImageAssetPath(countryImageAssetPath);
    }

    public CountryInformation() {
        // ? 1. Cargamos todos los setters para los valores externos
        this.setCountryContinent("null");
        this.setCountryName("null");
        this.setCountryImageAssetPath("null");
    }


    /*! Setters y Getters*/
    public void setCountryContinent(String countryContinent)
            throws IllegalArgumentException {
        if (!countryContinent.isEmpty()) {
            this.countryContinent = countryContinent;
        } else {
            throw new IllegalArgumentException("Error Code 0x001 - [Raised] - CountryInformation " +
                                                       "- setCountryContinent - La entrada de " +
                                                       "countryContinent no puede ser nula o " +
                                                       "vacia");
        }
    }

    public String getCountryContinent(){return this.countryContinent;}

    public void setCountryName(String countryName){
        if (!countryName.isEmpty()){
            this.countryName = countryName;
        }else {
            throw new IllegalArgumentException("Error Code 0x002 - [Raised] - CountryInformation " +
                                                       "- setCountryName - La entrada de " +
                                                       "countryName no puede ser nula o vacia");
        }
    }

    public String getCountryName(){return this.countryName;}

    public void setCountryImageAssetPath(String countryImageAssetPath){
        if (!countryImageAssetPath.isEmpty()){
            this.countryImageAssetPath = countryImageAssetPath;
        }else {
            throw new IllegalArgumentException("Error Code 0x003 - [Raised] - CountryInformation " +
                                                       "- setCountryImageAssetPath - La entrada de " +
                                                       "countryImageAssetPath no puede ser nula o " +
                                                       "vacia");
        }
    }

    public String getCountryImageAssetPath(){return this.countryImageAssetPath;}



    /*! Overrides y Comparable*/

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
       if (obj == null || !obj.getClass().equals(this.getClass())){return false;}
       else if (obj == this){return true;}
       else {
           CountryInformation countryInformation = (CountryInformation) obj;
           return (countryInformation.getCountryContinent().equals(this.getCountryContinent()) &&
                   countryInformation.getCountryName().equals(this.getCountryName()) &&
                   countryInformation.getCountryImageAssetPath().equals(this.getCountryImageAssetPath()));

       }
    }

    /**
     * Returns a string representation of the object. In general, the {@code toString} method
     * returns a string that "textually represents" this object. The result should be a concise but
     * informative representation that is easy for a person to read. It is recommended that all
     * subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object} returns a string consisting of the name
     * of the class of which the object is an instance, the at-sign character `{@code @}', and the
     * unsigned hexadecimal representation of the hash code of the object. In other words, this
     * method returns a string equal to the value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @NonNull
    @Override
    public @NotNull String toString() {
        return "CountryInformation{" +
                "countryContinent='" + countryContinent + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryImageAssetPath='" + countryImageAssetPath + '\'' +
                '}';
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero,
     * or a positive integer as this object is less than, equal to, or greater than the specified
     * object.
     *
     * <p>The implementor must ensure
     * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))} for all {@code x} and {@code y}.  (This
     * implies that {@code x.compareTo(y)} must throw an exception iff {@code y.compareTo(x)} throws
     * an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
     * implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for all {@code z}.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any class that implements
     * the {@code Comparable} interface and violates this condition should clearly indicate this
     * fact.  The recommended language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     * <i>signum</i> function, which is defined to return one of {@code -1},
     * {@code 0}, or {@code 1} according to whether the value of
     * <i>expression</i> is negative, zero, or positive, respectively.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal
     * to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it from being compared
     *                              to this object.
     */
    @Override
    public int compareTo(CountryInformation o) {
        //! Implementamos un comparable basado en el nombre y una comparacion lexicografica
        return o.getCountryName().compareTo(this.getCountryName());
    }
}
