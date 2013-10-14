(ns kanban-metrics.board
  (:require [kanban-metrics.time :refer [parse-dates]]
            [clojure.pprint :refer :all]))

(defn prepper
  [args]
  (fn [[columns data]]
    [columns
     (parse-dates (args :datetime-format) data)]))

(defn prepare
  [args board]
  (let [f (prepper args)]
    (f board)))

(defn columns-on-date [ordered-cols card target-date]
  (let [last-column (last ordered-cols)
        last-date   (get card last-column)]
    (if (.after target-date last-date)
        #{last-column}
        (set (filter #(= target-date (get card %)) ordered-cols)))))

(defn reverse-map
  ([[x coll]]
    (reverse-map x coll))
  ([x coll]
    (interleave coll (repeat x))))

(defn board-on-date [f target-date cards]
  (let [ card-dates (map #(vector % (f % target-date)) cards)
         reversals  (map #(reverse-map %) card-dates)]
    (apply hash-map (reduce concat reversals))))

