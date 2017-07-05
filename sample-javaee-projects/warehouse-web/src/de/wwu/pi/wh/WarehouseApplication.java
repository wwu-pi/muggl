package de.wwu.pi.wh;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import de.wwu.pi.wh.web.WarehouseService;


@ApplicationPath("/webapi")
public class WarehouseApplication extends Application {
	
	@Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(WarehouseService.class);
        return classes;
    }
}
