package it.eng.parer.slite.gen.util;

import java.io.File;

import it.eng.util.SpringLiteTool;
import java.nio.charset.Charset;

public class ParerTool extends SpringLiteTool {

    private static final String GEN_PACKAGE = "it.eng.parer.slite.gen";
    private static final String FORM_PACKAGE = GEN_PACKAGE + ".form";
    private static final String ACTION_PACKAGE = GEN_PACKAGE + ".action";
    private static final String USER_PACKAGE = "it.eng.spagoLite.security";

    public ParerTool(String actionPath, String actionRerPath, String genPackage, String actionPackage,
            String formPackage) {
        super(actionPath, actionRerPath, genPackage, actionPackage, formPackage);
    }

    public static void main(String[] args)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        System.setProperty("file.encoding", "UTF-8");
        java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null, null);

        if (args.length == 0) {
            throw new IllegalArgumentException();
        }
        String basedir = args[0];
        for (String arg : args) {
            System.out.println("Parametro passato al main:" + arg);
        }

        System.out.println("Percorso corrente : " + new File(".").getAbsolutePath());
        String actionPath = basedir.replaceAll("\\\\", "/") + "/../sacer-web/src/main/java/it/eng/parer/web/action";

        ParerTool myParerTool = new ParerTool(actionPath, null, GEN_PACKAGE, ACTION_PACKAGE, FORM_PACKAGE);

        System.out.println("Charset.defaultCharset().name() :" + Charset.defaultCharset().name());

        myParerTool.setSrcPath(basedir + "/target/generated-sources/slite");
        myParerTool.setFormPath(basedir + "/src/main/resources/forms");
        myParerTool.setJspPath(basedir + "/../sacer-web/src/main/webapp/jsp");
        myParerTool.setUserPackage(USER_PACKAGE);
        myParerTool.run();
    }
}
