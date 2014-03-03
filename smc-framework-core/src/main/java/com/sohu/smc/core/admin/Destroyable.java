package com.sohu.smc.core.admin;

/**
 * <p>A Destroyable is an object which can be destroyed.</p>
 * <p>Typically a Destroyable is a {@link LifeCycle} component that can hold onto
 * resources over multiple start/stop cycles.   A call to destroy will release all
 * resources and will prevent any further start/stop cycles from being successful.</p>
 * User: shijinkui
 * Date: 12-9-4
 * Time: 下午4:28
 */
public interface Destroyable {
    void destroy();
}
