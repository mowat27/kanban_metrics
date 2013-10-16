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
   :db/ident :card/work-type
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "Work item type"
   :db/fulltext true
   :db.install/_attribute :db.part/db}

  {:db/id #db/id[:db.part/db]
   :db/ident :card/description
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one
   :db/doc "Description of the task"
   :db/fulltext true
   :db.install/_attribute :db.part/db}
   ])

(defn run-trxn [connection transaction]
  (println "Running Transaction:")
  (pprint transaction)
  @(d/transact connection transaction))

(def transact (partial run-trxn conn))
(defn query
  ([x] (q x (d/db conn)))
  ([x & args] (apply q x (d/db conn) args)))

(defn entity [id]
  (d/entity (d/db conn) id))

(defn entity-retractions [coll]
  (->> coll
      (map #(concat [:db.fn/retractEntity] %))
      transact))

(defn load-schema [] (transact schema))

(def play-data [
  {:db/id #db/id[:db.part/user -1000001], :playpen/one "hello"}
  {:db/id #db/id[:db.part/user -1000002], :playpen/many ["foo" "bar"]}])

(def get-playpen-one  '[:find ?v :where [?e :playpen/one ?v]])
(def get-playpen-many '[:find ?v :where [?e :playpen/many ?v]])

(def board1-data [
  { :db/id #db/id[:db.part/user -1000001]
    :card/description "First card"
    :card/work-type   "Feature"}
  { :db/id #db/id[:db.part/user -1000002]
    :card/description "Second card"
    :card/work-type   "Feature"}
  { :db/id #db/id[:db.part/user -1000003]
    :card/description "Third card"
    :card/work-type   "Bug"}
  { :db/id #db/id[:db.part/user -1000004]
    :card/description "Fourth card"
    :card/work-type   "Feature"}

  { :db/id #db/id[:db.part/user -1000100],
    :board/name "Board 1"
    :board/cards [#db/id[:db.part/user -1000001]
                  #db/id[:db.part/user -1000002]
                  #db/id[:db.part/user -1000003]
                  #db/id[:db.part/user -1000004]]}])

(def board2-cards [
  { :db/id #db/id[:db.part/user -1000001]
    :card/description "Fix 1"
    :card/work-type   "Bug"}
  { :db/id #db/id[:db.part/user -1000002]
    :card/description "Fix 2"
    :card/work-type   "Bug"}])

(def board2-def [
  { :db/id #db/id[:db.part/user -1000100],
    :board/name "Board 2"
    :board/cards [#db/id[:db.part/user -1000001]
                  #db/id[:db.part/user -1000002]]}])

(def cards '[(cards ?board-id ?desc ?type)
             [?board-id :board/cards      ?c-id]
             [?c-id     :card/description ?desc]
             [?c-id     :card/work-type   ?type]])

(def board '[(board ?name) [?e :board/name ?name]])

(defn get-cards-for-board [board-name] (query '[:find ?card
                                                :in $ ?name
                                                :where
                                                [?board :board/name ?name]
                                                [?board :board/cards ?card]]
                                        board-name))

(defn card-names-for-board [] (query '[:find ?card ?a ?v :where [17592186045422 :board/cards ?card] [?card ?a ?v] [?a :card/description]]))