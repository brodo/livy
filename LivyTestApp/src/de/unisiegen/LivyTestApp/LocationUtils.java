package de.unisiegen.LivyTestApp;

import static java.lang.Math.*;

/**
 * Created by Julian Dax on 04.12.14.
 */
public class LocationUtils {
    public static double distanceInKilometres(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = sin(decimalDegreesToRadians(lat1)) * sin(decimalDegreesToRadians(lat2)) +
                cos(decimalDegreesToRadians(lat1)) * cos(decimalDegreesToRadians(lat2)) *
                        cos(decimalDegreesToRadians(theta));
        return decimalDegreesToKilometres(radiansToDecimalDegrees(acos(dist)));
    }

    private static double decimalDegreesToKilometres(double dist) {
        return dist * 60 * 1.1515 * 1.609344;
    }

    private static double decimalDegreesToRadians(double deg) {
        return deg * PI / 180.0;
    }

    private static double radiansToDecimalDegrees(double rad) {
        return rad * 180 / PI;
    }


}
