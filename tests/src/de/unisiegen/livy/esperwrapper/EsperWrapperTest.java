package de.unisiegen.livy.esperwrapper;

import android.test.AndroidTestCase;

public class EsperWrapperTest extends AndroidTestCase {
    EsperWrapper esperWrapper;

    @Override
    public void setUp() throws Exception {
        AsperLoader loader = new AsperLoader(getContext());
        esperWrapper = new EsperWrapper(loader);
    }

    public void testRegisterEpl() throws Exception {
        assertTrue(false);

    }
}