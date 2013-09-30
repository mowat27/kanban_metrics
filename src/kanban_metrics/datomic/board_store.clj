(ns kanban-metrics.datomic.board-store
  (:require [datomic.api :only [q db] :as d]
            [kanban-metrics.sources.excel :as excel]
            [clojure.pprint :refer :all]))

(def uri "datomic:free://localhost:4334//kanban-metrics")
(d/create-database uri)

(def conn (d/connect uri))
(def schema-file "data/board_store_schema.dtm")

(defn apply-schema [f]
  (let [schema-tx (read-string (slurp f))])
    (println "Applying schema-tx:")
    (pprint schema-tx)
    @(d/transact conn schema-tx))





