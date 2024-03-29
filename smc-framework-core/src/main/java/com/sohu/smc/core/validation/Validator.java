package com.sohu.smc.core.validation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * A simple façade for Hibernate Validator.
 */
public class Validator {
    //private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * Validates the given object, and returns a list of error messages, if any. If the returned
     * list is empty, the object is valid.
     *
     * @param o      a potentially-valid object
     * @param <T>    the type of object to validate
     * @return a list of error messages, if any, regarding {@code o}'s validity
     */
    public <T> ImmutableList<String> validate(T o) {
        final Set<String> errors = Sets.newHashSet();
        /*final Set<ConstraintViolation<T>> violations = factory.getValidator().validate(o);
        for (ConstraintViolation<T> v : violations) {
            if (v.getConstraintDescriptor().getAnnotation() instanceof ValidationMethod) {
                final ImmutableList<Path.Node> nodes = ImmutableList.copyOf(v.getPropertyPath());
                final ImmutableList<Path.Node> usefulNodes = nodes.subList(0, nodes.size() - 1);
                final String msg = v.getMessage().startsWith(".") ? "%s%s" : "%s %s";
                errors.add(format(msg, Joiner.on('.').join(usefulNodes), v.getMessage()).trim());
            } else {
                errors.add(format("%s %s (was %s)",
                                  v.getPropertyPath(),
                                  v.getMessage(),
                                  v.getInvalidValue()));
            }
        }*/
        return ImmutableList.copyOf(Ordering.natural().sortedCopy(errors));
    }
}
