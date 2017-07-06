package de.wwu.muggl.instructions.invokespecial;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import de.wwu.muggl.configuration.Globals;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialClass;
import de.wwu.muggl.instructions.invokespecial.meta.InvokeSpecialMethod;
import de.wwu.muggl.vm.Frame;

/**
 * The manager of special invocation methods.
 * 
 * @author Andreas Fuchs
 */
public class InvokeSpecialManager {
	
	/**
	 * The special methods map.
	 */
	protected static Map<SpecialMethodEntry, Method> specialMethods = new HashMap<>();
	
	public static void initialize() {
		try (Stream<Path> paths = Files.walk(Paths.get("../muggl-core/src-invoke-special"))) {
		    paths
		        .filter(Files::isRegularFile)
		        .forEach(InvokeSpecialManager::checkForSpecialMethods);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static void checkForSpecialMethods(Path path) {
		String p = path.toFile().getPath().replaceAll("/", ".");
		if(File.separator.equals("\\")) {
			p = path.toFile().getPath().replace("\\", ".");
		}
		String className = p.substring("...muggl-core/src-invoke-special".length()+1, p.length()-".java".length());
		
		Globals.getInst().logger.info("Check class: [" + className + "] for special methods!");
		
		try {
			Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
			InvokeSpecialClass isc = clazz.getAnnotation(InvokeSpecialClass.class);
			if(isc != null) {
				for(Method m : clazz.getDeclaredMethods()) {
					if(!Modifier.isStatic(m.getModifiers())) {
						Globals.getInst().logger.info("  method " + m.getName() + " in class " + isc.className() + " is not static, thus not considered to be a special method!");
						continue;
					}
					InvokeSpecialMethod ism = m.getAnnotation(InvokeSpecialMethod.class);
					if(ism != null) {
						SpecialMethodEntry entry = new SpecialMethodEntry(isc.className(), ism.name(), ism.signature());
						specialMethods.put(entry, m);
						Globals.getInst().logger.info("  method " + ism.name() + " with signature " + ism.signature() + " in class " + isc.className() + " successfully added as special method");
					} else {
						Globals.getInst().logger.info("  method " + m.getName() + " in class " + isc.className() + " is not annoated with @InvokeSpecialMethod");
					}
				}
			} else {
				Globals.getInst().logger.info("Class " + className + " not annoated with @InvokeSpecialClass");
			}
		} catch (ClassNotFoundException e) {
			Globals.getInst().logger.error(e);
		}
	}

	public static void main(String[] args) {
		InvokeSpecialManager.initialize();
	}

	public static boolean isSpecial(SpecialMethodEntry entry) {
		return specialMethods.containsKey(entry);
	}

	public static void executeSpecialMethod(SpecialMethodEntry entry, Frame frame, Object[] parameters) {
		Method m = specialMethods.get(entry);
		try {
			m.invoke(m, frame, parameters);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
}
