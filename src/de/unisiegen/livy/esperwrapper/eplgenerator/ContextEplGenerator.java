package de.unisiegen.livy.esperwrapper.eplgenerator;

import com.google.gson.Gson;

/**
 * Created by Julian Dax on 06/05/14.
 */
public class ContextEplGenerator {
    private Gson gson = new Gson();

    public String generateEplFromJson(String json){
        Context context=  gson.fromJson(json, Context.class);
        return generateEplFromContext(context);
    }

    public String generateEplFromContext(Context context){
        return String.format("CREATE CONTEXT %d START (%s) END (%s)", context.id, context.start, context.stop);
    }
}
