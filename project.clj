; Local maven install for datomic
; Get datomic from http://downloads.datomic.com/0.8.4159/datomic-free-0.8.4159.zip
; Unzip file and cd to directory
; mvn install:install-file -DgroupId=com.datomic -DartifactId=datomic-free -Dfile=datomic-free-0.8.3343.jar -DpomFile=pom.xml
(defproject kanban_metrics "0.1.0-SNAPSHOT"
  :description "Project for generating kanban metrics"
  :url "https://github.com/mowat27/kanban_metrics"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.6.0"]
                 [incanter "1.5.4"]
                 [com.datomic/datomic-free "0.8.4159"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  :main kanban-metrics.core)


