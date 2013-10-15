(ns kanban-metrics.examples.data-model-playground
  (:require [datomic.api :refer [q] :as d]
            [clojure.pprint :refer [pprint]]))

(def uri "datomic:mem://data-model-play")

(d/create-database uri)
(def conn (d/connect uri))

(def schema [
  ; playpen
  {:db/id #db/id[:db.part/db]
   :db/ident :playpen/one
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "One String"
   :db/fulltext true
   :db.install/_attribute :db.part/db}

  {:db/id #db/id[:db.part/db]
   :db/ident :playpen/many
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/many
   :db/doc "Many Strings"
   :db/fulltext true
   :db.install/_attribute :db.part/db}

  ; board
  {:db/id #db/id[:db.part/db]
   :db/ident :board/name
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "The board name"
   :db/fulltext true
   :db.install/_attribute :db.part/db}

  {:db/id #db/id[:db.part/db]
   :db/ident :board/cards
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/many
   :db/doc "The cards on the board"
   :db.install/_attribute :db.part/db}

  ; card
  {:db/id #db/id[:db.part/db]
   :db/ident :card/description
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "Description of the task"
   :db/fulltext true
   :db.install/_attribute :db.part/db}])

(defn run-trxn [connection transaction]
  (println "Running Transaction:")
  (pprint transaction)
  @(d/transact connection transaction))

(def transact (partial run-trxn conn))
(defn query [x]
  (q x (d/db conn)))

(defn load-schema [] (transact schema))

(def play-data [
  {:db/id #db/id[:db.part/user -1000001], :playpen/one "hello"}
  {:db/id #db/id[:db.part/user -1000002], :playpen/many ["foo" "bar"]}
])

(def get-playpen-one  '[:find ?v :where [?e :playpen/one ?v]])
(def get-playpen-many '[:find ?v :where [?e :playpen/many ?v]])









