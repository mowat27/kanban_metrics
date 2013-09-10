(ns kanban-metrics.metrics-test
  (:require [midje.sweet :refer :all]
            [kanban-metrics.metrics :refer :all]
            [clj-time.core  :as t]))

(def kanban-board
  [[:requests  :dev   :live]
   ["1/6"      "2/6"  "3/6"
    "1/6"      "1/6"  "2/6"]])

(facts "about counts-by-day for a column"
  (counts-by-day kanban-board :requests) => {"1/6" 2}
  (counts-by-day kanban-board :dev) => {"1/6" 1 "2/6" 1})

(facts "about counts-by-day for all columns"
  (counts-by-day kanban-board) => {:requests {"1/6" 2}
                                   :dev      {"1/6" 1 "2/6" 1}
                                   :live     {"3/6" 1  "2/6" 1}})

(def board2
  [[:dev  :live]
   ["1/6" "9/6"
    "2/6" "9/6"
    "3/6" "11/6"
    "4/6" "11/6"
    "5/6" "12/6"
    "6/6" "12/6"]])

(facts "about cards-per-day"
  (cards-per-day board2 :dev) => 1
  (cards-per-day board2 :live) => (/ 6 4))

(fact (cycle-time board2 :live 2) => (/ 2 (/ 6 4)))

