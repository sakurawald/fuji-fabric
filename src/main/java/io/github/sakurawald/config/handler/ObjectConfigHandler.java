package io.github.sakurawald.config.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import io.github.sakurawald.Fuji;
import lombok.Cleanup;

import java.io.*;
import java.lang.reflect.InvocationTargetException;


public class ObjectConfigHandler<T> extends ConfigHandler<T> {

    Class<T> configClass;

    public ObjectConfigHandler(File file, Class<T> configClass) {
        super(file);
        this.file = file;
        this.configClass = configClass;
    }

    public ObjectConfigHandler(String child, Class<T> configClass) {
        this(new File(Fuji.CONFIG_PATH.toString(), child), configClass);
    }

    public void loadFromDisk() {
        // Does the file exist?
        try {
            if (!file.exists()) {
                saveToDisk();
            } else {
                // read older json from disk
                @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
                JsonElement olderJsonElement = JsonParser.parseReader(reader);

                // merge older json with newer json
                if (!this.merged) {
                    this.merged = true;
                    T newerJsonInstance = configClass.getDeclaredConstructor().newInstance();
                    JsonElement newerJsonElement = gson.toJsonTree(newerJsonInstance, configClass);
                    mergeJson(olderJsonElement, newerJsonElement);
                }

                // read merged json
                model = gson.fromJson(olderJsonElement, configClass);

                this.saveToDisk();
            }

        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            Fuji.LOGGER.error("Load config failed: " + e.getMessage());
        }
    }


    public void saveToDisk() {
        try {
            // Should we generate a default config instance ?
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                this.file.getParentFile().mkdirs();
                this.model = configClass.getDeclaredConstructor().newInstance();
            }

            // Save.
            JsonWriter jsonWriter = gson.newJsonWriter(new BufferedWriter(new FileWriter(this.file)));
            gson.toJson(this.model, configClass, jsonWriter);
            jsonWriter.close();
        } catch (IOException | InstantiationException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
