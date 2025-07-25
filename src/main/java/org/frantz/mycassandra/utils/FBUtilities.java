package org.frantz.mycassandra.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.*;

public class FBUtilities {
    public static final int MAX_UNSIGNED_SHORT = 0xFFFF;

    public static Collection<InetAddress> getAllLocalAddresses()
    {
        Set<InetAddress> localAddresses = new HashSet<InetAddress>();
        try
        {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            if (nets != null)
            {
                while (nets.hasMoreElements())
                    localAddresses.addAll(Collections.list(nets.nextElement().getInetAddresses()));
            }
        }
        catch (SocketException e)
        {
            throw new AssertionError(e);
        }
        return localAddresses;
    }

    public static void updateWithInt(MessageDigest digest, int val)
    {
        digest.update((byte) ((val >>> 24) & 0xFF));
        digest.update((byte) ((val >>> 16) & 0xFF));
        digest.update((byte) ((val >>>  8) & 0xFF));
        digest.update((byte) ((val >>> 0) & 0xFF));
    }

    public static void updateWithLong(MessageDigest digest, long val)
    {
        digest.update((byte) ((val >>> 56) & 0xFF));
        digest.update((byte) ((val >>> 48) & 0xFF));
        digest.update((byte) ((val >>> 40) & 0xFF));
        digest.update((byte) ((val >>> 32) & 0xFF));
        digest.update((byte) ((val >>> 24) & 0xFF));
        digest.update((byte) ((val >>> 16) & 0xFF));
        digest.update((byte) ((val >>>  8) & 0xFF));
        digest.update((byte)  ((val >>> 0) & 0xFF));
    }
}
