(ns kanban-metrics.datomic.board-store
  (:require [datomic.api :refer [q] :as d]
            [kanban-metrics.sources.excel :as excel]
            [clojure.pprint :refer :all]))

; (def uri "datomic:free://localhost:4334//kanban-metrics")
(def uri "datomic:mem://kanban-metrics")
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

(def => nil)

(defn prep-row [row]
  (let [mappings [["Sequence"    int           :card/sequence]
                  ["Project"     =>            :card/project]
                  ["Feature/bug" =>            :card/work-item-type]
                  ["Description" =>            :card/description]
                  ["Requests"    #(.toDate %)  :card/requested]
                  ["Backlog"     #(.toDate %)  :card/backlog]
                  ["In Progress" #(.toDate %)  :card/in-progress]
                  ["QA"          #(.toDate %)  :card/qa]
                  ["UAT"         #(.toDate %)  :card/uat]
                  ["Done"        #(.toDate %)  :card/done]]]
    (into {} (for [[src f tgt] mappings :let [result (get row src)]]
                [tgt  (if-not (nil? f)
                        (f result)
                        result)]))))

(defn ->trxn [row data-seq]
  (assoc row :db/id (d/tempid data-seq)))

(defn load-excel-file [file sheet]
  (d/transact conn
    (map #(->trxn (prep-row %1) %2) (open-excel-file file sheet) (range))))

(defn do-query [query]
  (q query (d/db conn)))