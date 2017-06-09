package d.w.m.javax.persistence;

import d.w.m.annotations.MugglMock;
import d.w.m.annotations.SpecialMethodSignature;
import de.wwu.muggl.javaee.invoke.SpecialMethodInvokeException;
import de.wwu.muggl.vm.Frame;


@MugglMock("javax.persistence.EntityManager")
public class EntityManager {

	@SpecialMethodSignature(name="find", signature="(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;")
	public void find(Frame frame, Object[] parameters) throws SpecialMethodInvokeException {
	}
	
	
}
