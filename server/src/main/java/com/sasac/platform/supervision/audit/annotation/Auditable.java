package com.sasac.platform.supervision.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marker for methods that should be audited.
 * <p>
 * When applied to a service or controller method, the
 * {@link com.sasac.platform.supervision.audit.aspect.OperationLogAspect}
 * intercepts the invocation and writes an {@code OperationLog} entry
 * capturing the before/after state of the operation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The action being performed (e.g. CREATE, UPDATE, DELETE, APPROVE, SUBMIT).
     */
    String action();

    /**
     * The type of the target entity (e.g. "Asset", "InspectionTask").
     */
    String targetType();
}
