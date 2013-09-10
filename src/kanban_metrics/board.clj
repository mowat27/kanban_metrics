(ns kanban-metrics.board
  (:require [kanban-metrics.time :refer [parse-dates]]))

(defn prepper
  [args]
  (fn [[columns data]]
    [columns
     (parse-dates (args :datetime-format) data)]))