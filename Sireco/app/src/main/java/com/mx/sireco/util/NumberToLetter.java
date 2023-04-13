package com.mx.sireco.util;

import android.util.Log;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/* renamed from: galileosolutions.com.mx.sireco.galileosolutions.com.mx.sireco.utils.NumberToLetter */
public abstract class NumberToLetter {
    private static final String[] CENTENAS = {"CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};
    private static final String[] DECENAS = {"VENTI", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA ", "CIEN "};
    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE"};

    public static String convertNumberToLetter(String number) throws NumberFormatException {
        return convertNumberToLetter(Double.parseDouble(number));
    }

    public static String convertNumberToLetter(double doubleNumber) throws NumberFormatException {
        Log.v("Number to letra", "" + doubleNumber);
        StringBuilder converted = new StringBuilder();
        DecimalFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.DOWN);
        double doubleNumber2 = Double.parseDouble(format.format(doubleNumber));
        if (doubleNumber2 > 9.99999999E8d) {
            throw new NumberFormatException("El numero es mayor de 999'999.999, no es posible convertirlo");
        } else if (doubleNumber2 < 0.0d) {
            throw new NumberFormatException("El numero debe ser positivo");
        } else {
            String[] splitNumber = String.valueOf(doubleNumber2).replace('.', '#').split("#");
            int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0], 8)) + String.valueOf(getDigitAt(splitNumber[0], 7)) + String.valueOf(getDigitAt(splitNumber[0], 6)));
            if (millon == 1) {
                converted.append("UN MILLON ");
            } else if (millon > 1) {
                converted.append(convertNumber(String.valueOf(millon)) + "MILLONES ");
            }
            int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0], 5)) + String.valueOf(getDigitAt(splitNumber[0], 4)) + String.valueOf(getDigitAt(splitNumber[0], 3)));
            if (miles == 1) {
                converted.append("MIL ");
            } else if (miles > 1) {
                converted.append(convertNumber(String.valueOf(miles)) + "MIL ");
            }
            int cientos = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0], 2)) + String.valueOf(getDigitAt(splitNumber[0], 1)) + String.valueOf(getDigitAt(splitNumber[0], 0)));
            if (cientos == 1) {
                converted.append("UN");
            }
            if (millon + miles + cientos == 0) {
                converted.append("CERO");
            }
            if (cientos > 1) {
                converted.append(convertNumber(String.valueOf(cientos)));
            }
            converted.append("PESOS");
            int centavos = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[1], 2)) + String.valueOf(getDigitAt(splitNumber[1], 1)) + String.valueOf(getDigitAt(splitNumber[1], 0)));
            Log.v("Number to Letra", "" + centavos);
            String centavosStr = String.valueOf(centavos);
            if (centavos < 10) {
                centavosStr = centavosStr + "0";
            }
            if ("".equals(centavosStr.trim()) || "00".equals(centavosStr.trim())) {
                converted.append(" 00/100 ");
            } else {
                converted.append(" " + centavosStr + "/100 ");
            }
            return converted.toString();
        }
    }

    private static String convertNumber(String number) {
        if (number.length() > 3) {
            throw new NumberFormatException("La longitud maxima debe ser 3 digitos");
        } else if (number.equals("100")) {
            return "CIEN";
        } else {
            StringBuilder output = new StringBuilder();
            if (getDigitAt(number, 2) != 0) {
                output.append(CENTENAS[getDigitAt(number, 2) - 1]);
            }
            int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1)) + String.valueOf(getDigitAt(number, 0)));
            if (k <= 20) {
                output.append(UNIDADES[k]);
            } else if (k <= 30 || getDigitAt(number, 0) == 0) {
                output.append(DECENAS[getDigitAt(number, 1) - 2] + UNIDADES[getDigitAt(number, 0)]);
            } else {
                output.append(DECENAS[getDigitAt(number, 1) - 2] + "Y " + UNIDADES[getDigitAt(number, 0)]);
            }
            return output.toString();
        }
    }

    private static int getDigitAt(String origin, int position) {
        if (origin.length() <= position || position < 0) {
            return 0;
        }
        return origin.charAt((origin.length() - position) - 1) - '0';
    }
}
