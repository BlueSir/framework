package com.sohu.smc.core;

import com.sohu.smc.core.bundles.JavaBundle;
import com.sohu.smc.core.config.Configuration;

public abstract class Service<T extends Configuration> extends AbstractService<T> {
    protected Service() {
        addBundle(new JavaBundle(this));
        checkForScalaExtensions();
    }

    @Override
    protected final void subclassServiceInsteadOfThis() {

    }

    private void checkForScalaExtensions() {
        try {
            final Class<?> scalaObject = Class.forName("scala.ScalaObject");
            final Class<?> klass = getClass();
            if (scalaObject.isAssignableFrom(klass)) {
                throw new IllegalStateException(klass.getCanonicalName() + " is a Scala class. " +
                        "It should extend ScalaService, not Service.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
