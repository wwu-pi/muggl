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
	 * The fully qualified name of the class of the JPA EntityManager.
	 */
	public static final String ENTITY_MANAGER_CLASS = "javax.persistence.EntityManager";
}
