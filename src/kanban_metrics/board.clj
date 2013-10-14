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

(defn invert-and-flatten
  ([[x coll]]
    (invert-and-flatten x coll))
  ([x coll]
    (interleave coll (repeat x))))

(defn board-on-date [f target-date cards]
  (let [card-dates   (map #(vector % (f % target-date)) cards)
        col-card-seq (reduce #(concat %1 (invert-and-flatten %2)) [] card-dates)]
    (reduce (fn [m [column card]]
                (assoc m column (conj (get m column []) card)))
            {} (partition 2 col-card-seq))))

(defn pad
  ([coll len]
    (pad coll len nil))
  ([coll len x]
    (take len (concat coll (repeat x)))))

(defn transpose [m]
  (apply mapv vector m))

(defn get-cards [ordered-columns m]
  (map #(get m % []) ordered-columns))

(defn get-cards-padded [columns m]
  (let [cards (get-cards columns m)
        len   (apply max (map count cards))]
    (map #(pad % len) cards)))

(defn map-to-matrix [columns m]
  (let [cards (get-cards-padded columns m)]
    (concat [columns] (transpose cards))))