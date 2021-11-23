package com.gonzalez.serialize;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigurationList implements Externalizable {

    public static String BASE_CONFIG_SAVE_FILE = "_config";

    public static String baseDir = new File(System.getProperty("user.home"), ".autoClicker").getAbsolutePath();
    private Map<String, Configuration> configurationList;

    public ConfigurationList() {
        new File(this.baseDir).mkdirs();
        configurationList = new HashMap<>();
    }

    public Set<String> getAllConfigurationsName() {
        return configurationList.keySet();
    }

    public Configuration getConfigurationByName(String name) {
        return configurationList.get(name);
    }

    public void addConfiguration(Configuration configuration) {
        configurationList.put(configuration.getName(), configuration);
    }

    public void saveAll() {
        File file = new File(baseDir, BASE_CONFIG_SAVE_FILE);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        System.out.println("Save " + configurationList.size() + " configurations");
        out.writeInt(configurationList.size());
        for (String configurationName : getAllConfigurationsName()) {
            out.writeUTF(configurationName);
            saveConfiguration(configurationList.get(configurationName));
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int nbConfiguration = in.readInt();
        System.out.println("Load " + nbConfiguration + " configurations");
        for(int i=0; i<nbConfiguration; i++) {
            String nameConfig = in.readUTF();
            loadConfiguration(nameConfig);
        }
    }

    public void saveConfiguration(Configuration configuration) {
        File file = new File(baseDir, configuration.getName());

        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfiguration(String name) {
        File file = new File(baseDir, name);
        if(file.exists()) {
            ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                Configuration configuration = (Configuration) ois.readObject();
                configurationList.put(name, configuration);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
