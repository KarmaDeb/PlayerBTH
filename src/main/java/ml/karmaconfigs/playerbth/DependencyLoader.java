package ml.karmaconfigs.playerbth;

import ml.karmaconfigs.playerbth.Utils.Server;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/*
Part of this code is copied
from:
https://www.nathanbak.com/?p=407

Please don't ask support for it
 */

/*
GNU LESSER GENERAL PUBLIC LICENSE
                       Version 2.1, February 1999

 Copyright (C) 1991, 1999 Free Software Foundation, Inc.
 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.

[This is the first released version of the Lesser GPL.  It also counts
 as the successor of the GNU Library Public License, version 2, hence
 the version number 2.1.]
 */

public final class DependencyLoader implements PlayerBTH {

    public DependencyLoader() {
        File dependenciesDir = new File(plugin.getDataFolder() + "/libs");
        if (!dependenciesDir.exists() && dependenciesDir.mkdirs()) {
            String dir = dependenciesDir.getAbsolutePath().replaceAll("\\\\", "/");
            Server.send("Created directory {0}", Server.AlertLevel.INFO, dir);
        }
    }

    public final boolean injectJodaTime() {
        File dependency = new File(plugin.getDataFolder() + "/libs/", "JodaTime.jar");
        if (!dependency.exists()) {
            Server.send("Trying to download JodaTime.jar from {0}", Server.AlertLevel.INFO, "https://github.com/JodaOrg/joda-time/releases/download/v2.10.6/joda-time-2.10.6.jar");
            if (download("https://github.com/JodaOrg/joda-time/releases/download/v2.10.6/joda-time-2.10.6.jar", dependency)) {
                return injectJar(dependency);
            }
            return false;
        }
        return injectJar(dependency);
    }

    /**
     * Downloads the specified URL to the specified file location. Maximum size
     * allowed is <code>Long.MAX_VALUE</code> bytes.
     *
     * @param url
     *            location to read
     * @param file
     *            location to write
     */
    private boolean download(final String url, final File file) {
        try {
            TrustManager [] trustManagers = new TrustManager [] { new NvbTrustManager() };
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustManagers, null);

            // Set connections to use lenient TrustManager and HostnameVerifier
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NvbHostnameVerifier());

            InputStream is = new URL(url).openStream();
            ReadableByteChannel rbc = Channels.newChannel(is);
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(file);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } finally {
                if (rbc != null) {
                    rbc.close();
                }
                if (fos != null) {
                    fos.close();
                }
                is.close();

                Server.send("Downloaded dependency JodaTime.jar from {0}", Server.AlertLevel.INFO, url);
            }

            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Simple <code>TrustManager</code> that allows unsigned certificates.
     */
    private static final class NvbTrustManager implements TrustManager, X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * Simple <code>HostnameVerifier</code> that allows any hostname and session.
     */
    private static final class NvbHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private boolean injectJar(final File jar) {
        try {
            Server.send("Trying to inject {0} into PlayerBTH", Server.AlertLevel.INFO, jar.getName());
            // Get the ClassLoader class
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Class<?> clazz = cl.getClass();

            // Get the protected addURL method from the parent URLClassLoader class
            Method method = clazz.getSuperclass().getDeclaredMethod("addURL", URL.class);

            // Run projected addURL method to add JAR to classpath
            method.setAccessible(true);
            method.invoke(cl, jar.toURI().toURL());

            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}