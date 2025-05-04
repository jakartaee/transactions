package jakarta.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an unchecked exception type is recoverable,
 * so that it does not result in the transaction being
 * {@linkplain Transaction#setRollbackOnly marked for
 * rollback} when thrown by a {@link Transactional}
 * managed bean.
 * <p>
 * The annotated exception type must be a subclass of
 * {@link RuntimeException}.
 * <p>
 * The effect of this annotation may be suppressed for
 * a given managed bean by explicitly listing the
 * exception type in {@link Transactional#rollbackOn}.
 *
 * @since JTA 2.1
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Recoverable {}
