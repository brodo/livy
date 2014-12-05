package de.unisiegen.LivyTestApp;

import android.test.AndroidTestCase;

public class LocationUtilsTest extends AndroidTestCase {
    public void testCalculatingSmallDistance() throws Exception {
        double lat1 = 50.875194;
        double lon1 = 8.025945;
        double lat2 = 50.872310;
        double lon2 = 8.018649;
        double distance = LocationUtils.distanceInKilometres(lat1,lon1, lat2, lon2);

        assertEquals(distance, 0.604, 0.0005);
    }

    public void testCalculatingBigDistance() throws Exception {
        double lat1 = 51.982097;
        double lon1 = 19.657033;
        double lat2 = 41.060786;
        double lon2 = -2.844211;
        double distance = LocationUtils.distanceInKilometres(lat1,lon1, lat2, lon2);

        assertEquals(distance, 2093.401, 1);
    }
}