(ns kanban-metrics.metrics
  (:require [kanban-metrics.time :refer :all]))


(defn get-column
  "Returns the values from a column on the board"
  [[columns data] column]
  (let [i (.indexOf columns column)
        rows (->> data
                  (partition-all (count columns))
                  (map vec))]
        (map #(get % i) rows)))

(defn counts-by-day
  "Returns the number of cards in each column per day"
  ([board]
    (let [[columns data] board]
      (reduce #(merge %1 {%2 (counts-by-day board %2)}) {} columns)))
  ([baord column]
    (frequencies (get-column baord column))))

(defn cards-per-day
  "Counts the average number of cards going into a column per day"
  [board column]
  (let [cards (->> (get-column board column)
                   (parse-dates "dd/MM")
                   vec)
        total-cards (count cards)
        total-days  (days-spanned cards)]
    (/ total-cards total-days)))
