(defproject another-porwigle "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [java-jdbc/dsl "0.1.3"]
                 [com.h2database/h2 "1.3.170"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 [http-kit "2.1.16"]
                 [org.clojure/data.json "0.2.6"]]
  :profiles { :dev {:source-paths ["dev"]
                    :dependencies [[org.clojure/tools.namespace "0.2.4"]]}})
