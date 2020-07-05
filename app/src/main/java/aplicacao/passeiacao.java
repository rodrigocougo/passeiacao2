package aplicacao;

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;

/* Tudo que é instanciado dendro do Application, será enxergado em todo o projeto*/
public class passeiacao extends Application {

    //Constantes globais;
    public static int DONO = 0, PASSEADOR = 1;

    public static int IS_SEARCH = 0, IS_DOGS = 1, IS_SCHEDULED = 2, IS_HISTORICAL = 3, IS_DOG_PROFILE = 4;
    public static int IS_OWNER = 0, IS_WALKER = 1;
    public static int IS_INSERT = 0, IS_REMOVE = 1;
    public static double SET_LON=0;
    public static double SET_LAT=0;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext(),
                ImagePipelineConfig.newBuilder(getApplicationContext())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        //Fresco.initialize(this);
    }

}
