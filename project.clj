(defproject schema.contrib "0.1.0-SNAPSHOT"
  :description "A collection of namespaces that operate on prismatic/schema
metadata and functions"
  :url "https://github.com/cddr/schema.contrib"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dev-dependencies [[leiningen "2.3.4"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [prismatic/schema "0.2.1"]
                 [com.taoensso/tower "2.0.2"]]
  :plugins [[codox "0.6.7"]])
