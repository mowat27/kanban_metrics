(ns kanban-metrics.board-test
  (:require [midje.sweet :refer :all]
            [kanban-metrics.board :refer :all]
            [clj-time.core  :as t]))

(def board
  [[:backlog  :dev  :done]
   ["1/6"     "2/6" "3/6"]])

(def expected-rows
  [(t/date-time 2000 6 1)
   (t/date-time 2000 6 2)
   (t/date-time 2000 6 3)])

(def prep
  (prepper {:datetime-format "dd/MM"}))

(fact (prep board) => [(first board) expected-rows])
