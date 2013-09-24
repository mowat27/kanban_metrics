(ns kanban-metrics.time-test
  (:require [midje.sweet :refer :all]
            [kanban-metrics.time :refer :all]
            [clj-time.core  :as t])
  (:import java.util.Date))

(def date-times [(t/date-time 2013 2 1)
                 (t/date-time 2013 1 1)
                 (t/date-time 2013 3 1)])

(fact (apply max-dt date-times) => (t/date-time 2013 3 1))
(fact (apply min-dt date-times) => (t/date-time 2013 1 1))

(fact (->datetime (Date. 113 8 6 13 35 12)) => (t/date-time 2013 8 5 13 35 12))
