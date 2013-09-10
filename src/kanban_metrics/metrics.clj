(ns kanban-metrics.metrics)

(defn counts-by-day
  "Returns the number of cards in each column per day"
  ([board]
    (let [[columns data] board]
      (reduce #(merge %1 {%2 (counts-by-day board %2)}) {} columns)))
  ([[columns data] column]
    (let [i (.indexOf columns column)
        rows (->> data
                  (partition-all (count columns))
                  (map vec))]
    (frequencies (map #(get % i) rows)))))
