package com.sohu.smc.core.admin;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shijinkui
 * Date: 12-9-4
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
public interface Dumpable {

    String dump();

    void dump(Appendable out, String indent) throws IOException;
}
