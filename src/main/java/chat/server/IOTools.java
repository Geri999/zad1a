package chat.server;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
public class IOTools {
    String separator = File.separator; //todo for unix & Win

    private static String mainPath = (new File("").getAbsolutePath() + "\\src\\main\\resources\\");

    public static HashMap<String, String> loadConfigFile() {

        HashMap<String, String> configMap = new HashMap<>();
        File file = new File(mainPath + "config_file.cfg");
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] array = line.split("=");
                try {
                    configMap.put(array[0], array[1]);
                } catch (Exception e) {
                    log.info("Empty line or no \"=\" sign in config_file.cfg");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Configfile loaded. {}", configMap
                .entrySet()
                .stream()
                .map(s -> (s.getKey() + "=" + s.getValue()))
                .collect(Collectors.joining(" | ")));

        return configMap;
    }
}