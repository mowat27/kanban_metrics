(ns kanban-metrics.web.core
  (:require [ring.adapter.jetty :refer :all]
            [compojure.core :refer :all]
            [kanban-metrics.web.views.board :as board]
            [kanban-metrics.datomic.board-store :refer [conn do-query]]))

(defn columns []
  (map first
    (do-query '[:find ?column :where [?n :card/sequence]
                                     [?n ?a]
                                     [?a :db/ident ?column]
                                     [(not= :db/txInstant ?column)]])))

(defn card-ids []
  (map first (do-query '[:find ?card :where [?card :card/sequence]])))

(defn card [id]
  (do-query '[:find ?attr ?value
              :in $ ?id
              :where [?id ?a ?value] [?a :db/ident ?attr]] id))

(defn cards [ids]
  (->> ids
       (map #(into {} (card %)))
       (sort-by :card/sequence)))

(def ordered-columns [:card/sequence
                      :card/project
                      :card/work-item-type
                      :card/description
                      :card/requested
                      :card/backlog
                      :card/in-progress
                      :card/qa
                      :card/uat
                      :card/done])

(defroutes app
  (GET "/"     [] (board/show ordered-columns (cards (card-ids))))
  (GET "/list" [] (board/list-cards ordered-columns (cards (card-ids)))))

(defn -main [port]
  (run-jetty #'app {:port (Integer. port) :join? false}))