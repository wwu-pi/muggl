package de.wwu.muggl.configuration;

public final class JavaEEConstants {

	/**
	 * The Java String representation of the JPA annotation @javax.persistence.PersistenceContext.
	 */
	public static final String ANNOTATION_PERSISTENCE_CONTEXT = "Ljavax/persistence/PersistenceContext;";
	
	/**
	 * The Java String representation of the JPA annotation @javax.inject.Inject.
	 */
	public static final String ANNOTATION_INJECT = "Ljavax/inject/Inject;";
	
	/**
	 * The Java String representation of the JPA annotation @javax.annotation.PostConstruct.
	 */
	public static final String ANNOTATION_POST_CONSTRUCT = "Ljavax/annotation/PostConstruct;";
	
	/**
	 * The Java String representation of the JPA annotation @javax.persistence.Entity.
	 */
	public static final String ANNOTATION_PERSISTENCE_ENTITY = "Ljavax/persistence/Entity;";
	
	// -------------------------------------------------------------
	// JPA Entity Attributes
	
	/**
	 * The Java String representation of the JPA annotation @javax.persistence.Id.
	 */
	public static final String ANNOTATION_PERSISTENCE_ID = "Ljavax/persistence/Id;";
	
	/**
	 * The Java String representation of the JPA annotation javax.validation.constraints.NotNull.
	 */
	public static final String ANNOTATION_VALIDATION_CONSTRAINTS_NOT_NULL = "Ljavax/validation/constraints/NotNull;";
	
	/**
	 * The Java String representation of the JPA annotation javax.validation.constraints.Min.
	 */
	public static final String ANNOTATION_VALIDATION_CONSTRAINTS_MIN = "Ljavax/validation/constraints/Min;";
	
	/**
	 * The Java String representation of the JPA annotation javax.validation.constraints.Min.
	 */
	public static final String ANNOTATION_VALIDATION_CONSTRAINTS_MAX = "Ljavax/validation/constraints/Max;";
	
	// -------------------------------------------------------------
		
	/**
	 * The fully qualified name of the class of the JPA EntityManager.
	 */
	public static final String ENTITY_MANAGER_CLASS = "javax.persistence.EntityManager";
}
