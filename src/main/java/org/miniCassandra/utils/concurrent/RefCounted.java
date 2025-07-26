package org.miniCassandra.utils.concurrent;

/**
 * An object that needs ref counting does the two following:
 *   - defines a Tidy object that will cleanup once it's gone,
 *     (this must retain no references to the object we're tracking (only its resources and how to clean up))
 * Then, one of two options:
 * 1) Construct a Ref directly pointing to it, and always use this Ref; or
 * 2)
 *   - implements RefCounted
 *   - encapsulates a Ref, we'll call selfRef, to which it proxies all calls to RefCounted behaviours
 *   - users must ensure no references to the selfRef leak, or are retained outside of a method scope.
 *     (to ensure the selfRef is collected with the object, so that leaks may be detected and corrected)
 */
public interface RefCounted<T>
{
    /**
     * @return the a new Ref() to the managed object, incrementing its refcount, or null if it is already released
     */
    public Ref<T> tryRef();

    public Ref<T> ref();

    public static interface Tidy
    {
        void tidy() throws Exception;
        String name();
    }
}
