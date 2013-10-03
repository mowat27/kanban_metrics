(ns kanban-metrics.web.core
  (:require [ring.adapter.jetty :refer :all]
            [compojure.core :refer :all]
            [kanban-metrics.web.views.board :as board]
            [kanban-metrics.datomic.board-store :refer [conn do-query]]))

(def columns
  (map first
    (do-query '[:find ?column :where [?n :card/sequence]
                                     [?n ?a]
                                     [?a :db/ident ?column]
                                     [(not= :db/txInstant ?column)]])))

(def cards
  (do-query '[:find ?n ]))

(defn home-page [request]
  (board/show columns cards))

(defroutes app
  (GET "/" [] home-page))

(defn -main [port]
  (run-jetty #'app {:port (Integer. port) :join? false}))
