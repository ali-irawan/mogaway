package com.wenresearch.mogawe.core;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class NullCallable implements Callable
{
    @Override
    public Object call(Context context, Scriptable scope, Scriptable holdable, Object[] objects)
    {
        return objects[1];
    }
}
