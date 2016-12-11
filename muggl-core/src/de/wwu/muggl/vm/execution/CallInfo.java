package de.wwu.muggl.vm.execution;

import de.wwu.muggl.vm.classfile.ClassFile;
import de.wwu.muggl.vm.classfile.structures.Method;

/**
 * CallInfo provides all the information gathered for a particular linked call site after resolving it. A link is any
 * reference made from within the bytecodes of a method to an object outside of that method. If the info is invalid, the
 * link has not been resolved successfully.
 * 
 *
 */
public class CallInfo {

	private ClassFile _resolved_klass; // static receiver klass, resolved from a symbolic reference
	private ClassFile _selected_klass; // dynamic receiver class (same as static, or subklass)
	private Method _resolved_method; // static target method
	private Method _selected_method; // dynamic (actual) target method
	private CallKind _call_kind; // kind of call (static(=bytecode static/special +
	// others inferred), vtable, itable)
	private int _call_index; // vtable or itable index of selected class method (if any)
	private Object _resolved_appendix; // extra argument in constant pool (if CPCE::has_appendix)
	private Object _resolved_method_type; // MethodType (for invokedynamic and invokehandle call sites)

	// BasicType result_type() const { return selected_method()->result_type(); }
	public int vtable_index() {
		// Even for interface calls the vtable index could be non-negative.
		// See CallInfo::set_interface.
		// assert(has_vtable_index() || is_statically_bound(), "");
		assert (_call_kind == CallKind.VTABLE_CALL || _call_kind == CallKind.DIRECT_CALL);
		// The returned value is < 0 if the call is statically bound.
		// But, the returned value may be >= 0 even if the kind is direct_call.
		// It is up to the caller to decide which way to go.
		return _call_index;
	}

	public int itable_index() {
		assert (_call_kind == CallKind.ITABLE_CALL);
		// The returned value is always >= 0, a valid itable index.
		return _call_index;
	}

	public void set_static(ClassFile resolved_klass, Method resolved_method) {
		int vtable_index = Method.NONVIRTUAL_VTABLE_INDEX;
		set_common(resolved_klass, resolved_klass, resolved_method, resolved_method, CallKind.DIRECT_CALL,
				vtable_index);
	}

	// set_interface(KlassHandle resolved_klass, KlassHandle selected_klass, methodHandle resolved_method,
	// methodHandle selected_method, int itable_index, TRAPS) {
	// // This is only called for interface methods. If the resolved_method
	// // comes from java/lang/Object, it can be the subject of a virtual call, so
	// // we should pick the vtable index from the resolved method.
	// // In that case, the caller must call set_virtual instead of set_interface.
	// assert(resolved_method->method_holder()->is_interface(), "");
	// assert(itable_index == resolved_method()->itable_index(), "");
	// set_common(resolved_klass, selected_klass, resolved_method, selected_method, CallInfo::itable_call,
	// itable_index, CHECK);
	// }
	//
	//
	//
	// set_virtual(KlassHandle resolved_klass, KlassHandle selected_klass, methodHandle resolved_method,
	// methodHandle selected_method, int vtable_index, TRAPS) {
	// assert(vtable_index >= 0 || vtable_index == Method::nonvirtual_vtable_index, "valid index");
	// assert(vtable_index < 0 || !resolved_method->has_vtable_index() || vtable_index ==
	// resolved_method->vtable_index(), "");
	// CallKind kind = (vtable_index >= 0 && !resolved_method->can_be_statically_bound() ? CallInfo::vtable_call :
	// CallInfo::direct_call);
	// set_common(resolved_klass, selected_klass, resolved_method, selected_method, kind, vtable_index, CHECK);
	// assert(!resolved_method->is_compiled_lambda_form(), "these must be handled via an invokehandle call");
	// }

	// void set_handle(methodHandle resolved_method, Handle resolved_appendix, Handle resolved_method_type, TRAPS) {
	// if (resolved_method==null) {
	//// THROW_MSG(vmSymbols::java_lang_InternalError(), "resolved method is null");
	// }
	// KlassHandle resolved_klass = SystemDictionary::MethodHandle_klass();
	// assert(resolved_method->intrinsic_id() == vmIntrinsics::_invokeBasic ||
	// resolved_method->is_compiled_lambda_form(),
	// "linkMethod must return one of these");
	// int vtable_index = Method::nonvirtual_vtable_index;
	// assert(!resolved_method->has_vtable_index(), "");
	// set_common(resolved_klass, resolved_klass, resolved_method, resolved_method, CallInfo::direct_call,
	// vtable_index, CHECK);
	// _resolved_appendix = resolved_appendix;
	// _resolved_method_type = resolved_method_type;
	// }

	public void set_common(ClassFile resolved_klass, ClassFile selected_klass, Method resolved_method,
			Method selected_method, CallKind kind, int index) {
		// assert(resolved_method.signature() == selected_method.signature()): "signatures must correspond";
		_resolved_klass = resolved_klass;
		_selected_klass = selected_klass;
		_resolved_method = resolved_method;
		_selected_method = selected_method;
		_call_kind = kind;
		_call_index = index;
		_resolved_appendix = null;
	}

	CallInfo(){
		
	}
	// utility query for unreflecting a method
	CallInfo(Method resolved_method, ClassFile resolved_klass) {

		ClassFile resolved_method_holder = resolved_method.getClassFile();
		if (resolved_klass == null) { // 2nd argument defaults to holder of 1st
			resolved_klass = resolved_method_holder;
		}
		_resolved_klass = resolved_klass;
		_selected_klass = resolved_klass;
		_resolved_method = resolved_method;
		_selected_method = resolved_method;
		// classify:
		CallKind kind = CallKind.UNKNOWN_KIND;
		int index = resolved_method.vtable_index();
		if (resolved_method.can_be_statically_bound()) {
			kind = CallKind.DIRECT_CALL;
		} else if (!resolved_method_holder.isAccInterface()) {
			// Could be an Object method inherited into an interface, but still a vtable call.
			kind = CallKind.VTABLE_CALL;
		} else if (!resolved_klass.isAccInterface()) {
			// A default or miranda method. Compute the vtable index.
			// ResourceMark rm;
			// klassVtable* vt = InstanceKlass::cast(resolved_klass)->vtable();
			// index = LinkResolver::vtable_index_of_interface_method(resolved_klass,
			// resolved_method);
			// assert(index >= 0 , "we should have valid vtable index at this point");

			kind = CallKind.VTABLE_CALL;
		} else if (resolved_method.has_vtable_index()) {
			// Can occur if an interface redeclares a method of Object.
			kind = CallKind.VTABLE_CALL;
		} else {
			// A regular interface call.
			kind = CallKind.ITABLE_CALL;
			index = resolved_method.itable_index();
		}
		// assert(index == Method.nonvirtual_vtable_index || index >= 0):"bad index";
		_call_kind = kind;
		_call_index = index;
		_resolved_appendix = null;
	}

	public ClassFile get_resolved_klass() {
		return _resolved_klass;
	}

	public ClassFile get_selected_klass() {
		return _selected_klass;
	}

	public Method get_resolved_method() {
		return _resolved_method;
	}

	public Method get_selected_method() {
		return _selected_method;
	}

	public CallKind get_call_kind() {
		return _call_kind;
	}

	public int get_call_index() {
		return _call_index;
	}

	public Object get_resolved_appendix() {
		return _resolved_appendix;
	}

	public Object get_resolved_method_type() {
		return _resolved_method_type;
	}

	public void set_virtual(ClassFile resolvedCF, ClassFile selectedCF, Method resolvedMethod, Method selectedMethod, int vtable_index) {		
		set_common(resolvedCF, selectedCF, selectedMethod, selectedMethod, CallKind.VTABLE_CALL, vtable_index);		
	}

}