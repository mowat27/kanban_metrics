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

(defn board-on-date [cards target-dt]
  (let [card (first cards)]
    (reduce #(if (= target-dt (get card %2))
                      (assoc %1 %2 cards)
                      (assoc %1 %2 [nil])) {} (keys card))))