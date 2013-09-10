(defproject kanban_metrics "0.1.0-SNAPSHOT"
  :description "Project for generating kanban metrics"
  :url "https://github.com/mowat27/kanban_metrics"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.6.0"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}})
