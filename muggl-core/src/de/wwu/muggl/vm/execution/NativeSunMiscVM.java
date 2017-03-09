package de.wwu.muggl.vm.execution;

import de.wwu.muggl.vm.Frame;
import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.ClassFileException;
import de.wwu.muggl.vm.classfile.structures.Field;
import de.wwu.muggl.vm.initialization.InitializedClass;
import de.wwu.muggl.vm.loading.MugglClassLoader;

import java.lang.invoke.MethodType;

/**
 * Created by j_dage01 on 09.03.17.
 */
public class NativeSunMiscVM extends NativeMethodProvider {
    private static final String handledClassFQ = sun.misc.VM.class.getCanonicalName();
    private static ClassFile CLASS_VM = null;
    private static ClassFile CLASS_VMPROPERTIESWRAPPER = null;

    public static void initialiseAndRegister(MugglClassLoader classLoader) throws ClassFileException {
        CLASS_VM = classLoader.getClassAsClassFile(handledClassFQ);
        CLASS_VMPROPERTIESWRAPPER = classLoader.getClassAsClassFile(
                de.wwu.muggl.vm.execution.nativeWrapping.VMPropertiesWrapper.class.getCanonicalName());
        registerNatives();
    }

    public static void initialize(Frame frame) {
        InitializedClass sunMiscVm = CLASS_VM.getTheInitializedClass(frame.getVm());
        Field savedProps = CLASS_VM.getFieldByName("savedProps");
        sunMiscVm.putField(savedProps, frame.getVm().getAnObjectref(CLASS_VMPROPERTIESWRAPPER));
    }

    public static void registerNatives() {
        NativeWrapper.registerNativeMethod(NativeSunMiscVM.class, handledClassFQ, "initialize",
                MethodType.methodType(void.class, Frame.class),
                MethodType.methodType(void.class));
    }

}
