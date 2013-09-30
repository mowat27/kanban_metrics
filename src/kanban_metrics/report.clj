(ns kanban-metrics.report
  (:require [kanban-metrics.metrics :as m]
            [clj-time.format :as fmt]))

(defn format-counts [counts col]
  {col (reduce (fn [m [dt v]] (assoc m (fmt/unparse (fmt/formatter "MMdd") dt) v)) {} (col counts))})


