(ns kanban-metrics.datomic.board-store
  (:require [datomic.api :refer [q] :as d]
            [kanban-metrics.sources.excel :as excel]
            [clojure.pprint :refer :all]))

(def uri "datomic:free://localhost:4334//kanban-metrics")
; (def uri "datomic:mem://kanban-metrics")
(d/create-database uri)

(def conn (d/connect uri))
(def schema-file "data/board_store_schema.dtm")

(defn apply-schema [f]
  (let [schema-tx (read-string (slurp f))]
    (println "Applying schema-tx:")
    (pprint schema-tx)
    @(d/transact conn schema-tx)))

(defn open-excel-file [file sheet]
  (let [xls  (excel/load-sheet file sheet)
        cols (excel/all-cols xls)]
    (filter #(excel/complete? cols %) xls)))

(defn prep-row [row mappings]
  (into {}
    (for [[src f tgt] mappings
          :let [result (get row src)]]
      [tgt (if-not (nil? f) (f result) result)])))

(defn ->trxn [row data-seq]
  (assoc row :db/id (d/tempid data-seq)))

(defn load-excel-file [file sheet mappings]
  (d/transact conn
    (map #(->trxn (prep-row %1 mappings) %2) (open-excel-file file sheet) (range))))

(defn do-query
  ([query]
    (q query (d/db conn)))
  ([query & params]
    (apply q query (d/db conn) params)))

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