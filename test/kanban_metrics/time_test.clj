(ns kanban-metrics.time-test
  (:require [midje.sweet :refer :all]
            [kanban-metrics.time :refer :all]
            [clj-time.core  :as t]))

(def date-times [(t/date-time 2013 2 1)
                 (t/date-time 2013 1 1)
                 (t/date-time 2013 3 1)])

(fact (apply max-dt date-times) => (t/date-time 2013 3 1))
(fact (apply min-dt date-times) => (t/date-time 2013 1 1))
