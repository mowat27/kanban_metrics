(ns kanban-metrics.web.core
  (:require [ring.adapter.jetty :refer :all]
            [compojure.core :refer :all]
            [kanban-metrics.web.views.board :as board]
            [kanban-metrics.datomic.board-store :refer [conn do-query cards card-ids]]))

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