package me.illusion.skyblockcore.spigot.file.path;

import me.illusion.skyblockcore.shared.utilities.ExceptionLogger;
import me.illusion.skyblockcore.spigot.utilities.storage.YMLBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ExtendedYMLBase extends YMLBase {

    public ExtendedYMLBase(JavaPlugin plugin, String name) {
        super(plugin, name);
    }

    public ExtendedYMLBase(JavaPlugin plugin, File file, boolean existsOnSource) {
        super(plugin, file, existsOnSource);
    }

    protected <T> T load(T t, ConfigurationSection section) {
        Class<?> clazz = t.getClass();

        for(Field field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(TargetPath.class))
                continue;

            TargetPath target = field.getAnnotation(TargetPath.class);
            String path = target.path();

            if ("".equalsIgnoreCase(path))
                path = field.getName();

            Class<?> targetClass = field.getType();

            boolean accessible = field.isAccessible();

            if(!accessible)
                field.setAccessible(true);

            try {
                Object cast = section.get(path);

                if(cast == null)
                    continue;

                Class<?> primitive = PrimitiveUnboxer.unbox(cast.getClass());

                if (primitive.isPrimitive() && primitive.equals(targetClass))
                    cast = cast.getClass().getDeclaredMethod(primitive.getName() + "Value").invoke(cast);

                field.set(t, cast);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                ExceptionLogger.log(e);
            }

            field.setAccessible(!accessible);
        }

        return t;
    }
}
