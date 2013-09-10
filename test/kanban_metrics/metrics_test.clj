(ns kanban-metrics.metrics-test
  (:require [midje.sweet :refer :all]
            [kanban-metrics.metrics :refer :all]))

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