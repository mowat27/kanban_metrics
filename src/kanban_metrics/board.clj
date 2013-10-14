(ns kanban-metrics.board
  (:require [kanban-metrics.time :refer [parse-dates]]
            [clj-time.core :as t]
            [clojure.pprint :refer :all])
  (:import org.joda.time.DateTime))

(defn prepper
  [args]
  (fn [[columns data]]
    [columns
     (parse-dates (args :datetime-format) data)]))

(defn prepare
  [args board]
  (let [f (prepper args)]
    (f board)))

(defn invert-and-flatten
  "Converts a key followed by a list of values
  into list of value, key, value, key etc"
  ([[x coll]]
    (invert-and-flatten x coll))
  ([x coll]
    (interleave coll (repeat x))))

(defn on-or-after? [start date]
  (or (= start date) (.after date start)))

(defn on-or-before? [end date]
  (or (= end date) (.before date end)))

(defn within? [[start end] date]
  (and (on-or-after? start date)
       (on-or-before? end date)))

(defn rolling-window
  ([f coll]
    (partition 2 1 (map f coll))))

(defn get-bounds [[start next-date]]
  (if (= start next-date)
      [start start]
      [start (-> (DateTime. next-date)
                 (t/minus (t/days 1))
                 .toDate)]))

(defn columns-on-date [columns card target-date]
  "Finds the set of columns a card would have touched
  on the target date provided"
  (let [last-column (last columns)
        last-date   (get card last-column)]
    (if (.after target-date last-date)
        #{last-column}
        (->> (partition-all 2 1 columns)
             (filter #(within? (get-bounds (map (fn [c] (get card c)) %)) target-date))
             (map first)
             (reduce #(conj %1 %2) #{})))))

(defn board-on-date [f target-date cards]
  "Calculates which "
  (let [card-dates   (map #(vector % (f % target-date)) cards)
        col-card-seq (reduce #(concat %1 (invert-and-flatten %2)) [] card-dates)]
    (reduce (fn [m [column card]]
                (assoc m column (conj (get m column []) card)))
            {} (partition 2 col-card-seq))))

(defn pad
  "Pads a collection to the specified length with nil
  or a specified value."
  ([coll len]
    (pad coll len nil))
  ([coll len x]
    (take len (concat coll (repeat x)))))

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn get-cards [columns m]
  "Creates a sequence containing a list of cards for a
  each column given. Returns an empty list if nothing is found
  for a column"
  (map #(get m % []) columns))

(defn get-cards-padded [columns m]
  "Creates a sequence containing a list of cards for a
  each column given. The results are padded with nil
  to the length of the longest result."
  (let [cards (get-cards columns m)
        len   (apply max (map count cards))]
    (map #(pad % len) cards)))

(defn map-to-matrix [columns m]
  "Converts a map of columns to cards into a matrix
  of columns and rows"
  (let [cards (get-cards-padded columns m)]
    (concat [columns] (transpose cards))))
