package de.wwu.muggl.vm.execution;

public enum CallKind {
    DIRECT_CALL,                        // jump into resolved_method (must be concrete)
    VTABLE_CALL,                        // select recv.klass.method_at_vtable(index)
    ITABLE_CALL,                        // select recv.klass.method_at_itable(resolved_method.holder, index)
    UNKNOWN_KIND; //=-1
}
