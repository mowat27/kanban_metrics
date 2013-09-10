(ns kanban-metrics.time
  (:require [clj-time.core  :as t]
            [clj-time.format :as fmt]))

(defn min-dt
  "Finds the min datetime in a list"
  [& coll]
  (reduce #(if (t/before? %1 %2) %1 %2) coll))

(defn max-dt
  "Finds the max datetime in a list"
  [& coll]
  (reduce #(if (t/after? %1 %2) %1 %2) coll))

(defn days-spanned
  "Returned the number of days spanned by a list of dates"
  [cards]
  (inc (t/in-days (t/interval (apply min-dt cards) (apply max-dt cards)))))

(defn parse-dates
  "Parses a sequence of strings into dates"
  [dt-format coll]
  (map #(fmt/parse (fmt/formatter dt-format) %) coll))