/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bmuschko.gradle.docker

import org.apache.commons.io.FileUtils
import spock.lang.Specification

class AbstractIntegrationTest extends Specification {
    static final String SERVER_URL = 'http://localhost:2375'
    File projectDir = new File('build/integTest')

    def setup() {
        deleteProjectDir()
        projectDir = createDir(projectDir)
    }

    def cleanup() {
        deleteProjectDir()
    }

    protected File createDir(File dir) {
        if(!dir.exists()) {
            boolean success = dir.mkdirs()

            if(!success) {
                throw new IOException("Failed to create directory '$dir.canonicalPath'")
            }
        }

        dir
    }

    protected File createNewFile(File parent, String filename) {
        File file = new File(parent, filename)

        if(!file.exists()) {
            if(!file.createNewFile()) {
                throw new IOException("Unable to create new test file $file.canonicalPath.")
            }
        }

        file
    }

    private void deleteProjectDir() {
        if(projectDir.exists()) {
            FileUtils.deleteDirectory(projectDir)
        }
    }

    static boolean isDockerServerInfoUrlReachable() {
        URL url = new URL("$SERVER_URL/info")
        isUrlReachable(url)
    }

    static boolean isUrlReachable(URL url) {
        try {
            HttpURLConnection connection = url.openConnection()
            connection.requestMethod = 'GET'
            connection.connectTimeout = 3000
            return connection.responseCode == HttpURLConnection.HTTP_OK
        }
        catch(IOException e) {
            return false
        }
    }

    static boolean hasDockerHubCredentials() {
        File gradlePropsFile = new File(System.getProperty('user.home'), '.gradle/gradle.properties')

        if(!gradlePropsFile.exists()) {
            return false
        }

        Properties properties = new Properties()

        gradlePropsFile.withInputStream {
            properties.load(it)
        }

        properties['dockerHubUsername'] != null && properties['dockerHubPassword'] != null && properties['dockerHubEmail'] != null
    }
}
