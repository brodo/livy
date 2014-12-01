package de.unisiegen.livy.esperwrapper;

import android.content.Context;
import dalvik.system.DexClassLoader;

import java.io.*;
import java.lang.reflect.Method;

/**
 * Created by Julian Dax on 13/08/14.
 */
public class AsperLoader {
    private final String fileName = "asper_dexed.jar";
    private final String folderName = "dex";
    private final String optimizedFolderName = "dex_output";
    private final Context context;
    private DexClassLoader dexClassLoader;
    private EPRuntimeProxy epRuntime;
    private EplAdministratorProxy epAdministrator;

    public AsperLoader(Context context){
        this.context = context;
        if(!doesFileExistOnInternalStorage(fileName, folderName)){
            copyFileFromAssetsToInternalStorage(fileName, folderName);
        }
        dexFile(fileName, folderName, optimizedFolderName);
        try {
            Class epServiceProviderManagerClass =  dexClassLoader.loadClass("com.espertech.esper.client.EPServiceProviderManager");
            Method getDefaultProvider =  epServiceProviderManagerClass.getMethod("getDefaultProvider");
            Object epServiceProvider = getDefaultProvider.invoke(null);
            Method getEPRuntime = epServiceProvider.getClass().getMethod("getEPRuntime");

            epRuntime = new EPRuntimeProxy(getEPRuntime.invoke(epServiceProvider));
            Method getEPAdministrator = epServiceProvider.getClass().getMethod("getEPAdministrator");
            Class updateListenerClass = dexClassLoader.loadClass("com.espertech.esper.client.UpdateListener");
            epAdministrator = new EplAdministratorProxy(getEPAdministrator.invoke(epServiceProvider), updateListenerClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public EPRuntimeProxy getEPRuntime(){
       return epRuntime;
    }

    public Object getEPAdministrator() {
        return epAdministrator;
    }

    private boolean doesFileExistOnInternalStorage(String fileName, String folderName){
        final File dexInternalStoragePath = new File(context.getDir(folderName, Context.MODE_PRIVATE),
                fileName);
        return  dexInternalStoragePath.exists();
    }

    /*
     * Copy file from assets to internal storage. The file name on the internal storage will be the same as the one
     * in the assets.
     */
    private boolean copyFileFromAssetsToInternalStorage(String sourceFileName, String targetFolder) {
        final File dexInternalStoragePath = new File(context.getDir(targetFolder, Context.MODE_PRIVATE),
                sourceFileName);
        BufferedInputStream bis = null;
        OutputStream dexWriter = null;

        try {
            bis = new BufferedInputStream(context.getAssets().open(sourceFileName));
            dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
            final int BUF_SIZE = 8 * 1024;
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();
            return true;
        } catch (IOException e) {
            if (dexWriter != null) {
                try {
                    dexWriter.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            return false;
        }
    }

    private void dexFile(String inputFileName, String inputFolderName, String outputFolderName){
        final File optimizedDexOutputPath = context.getDir(outputFolderName, Context.MODE_PRIVATE);
        final File dexInternalStoragePath = new File(context.getDir(inputFolderName, Context.MODE_PRIVATE), inputFileName);
        // Initialize the class loader with the secondary dex file.
        dexClassLoader = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
            optimizedDexOutputPath.getAbsolutePath(),
            null,
            context.getClassLoader());

    }
}