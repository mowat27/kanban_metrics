(ns kanban-metrics.web.views.board
  (:require [hiccup.core :refer [html]]
            [kanban-metrics.datomic.board-store :refer [do-query cards card-ids]]))

(defn header-row [columns]
  [:thead
    [:tr (for [c columns]
                [:th c])]])

(defn list-cards [columns cards]
  (html [:body
          [:h1 "Card Details"]
          [:table
            (header-row columns)
            [:tbody
              (for [card cards]
                [:tr
                  (for [col columns] [:td (col card)])])]]]))

; (->> all-cards
;      (filter #(t/before? (DateTime. today) (DateTime. (:card/done %))))
;      count)

(def date-columns [:card/requested
                   :card/backlog
                   :card/in-progress
                   :card/qa
                   :card/uat
                   :card/done])

(def meta-columns [:card/sequence :card/description :card/work-item-type])

(def all-cards (cards (card-ids)))
(def card (first all-cards))

(defn dates-for [card]
  (reduce #(conj %1 (%2 card)) #{} date-columns))

(defn get-columns-for-date [card date]
  (filter #(= (%1 card) date) date-columns))

; temp
(def today (second (first (map dates-for all-cards))))
(def todays-cards (->> all-cards (map #(vector (last (get-columns-for-date % today)) %))))
(def cards-on-board (filter (fn [[col card]] (not (nil? col))) todays-cards))
(def cards-by-col (reduce (fn [m [col card]] (assoc m col (conj (m col []) card))) {} cards-on-board))

(def cards-in-cols (map #(vector % (cards-by-col % [])) date-columns))

(defn pad
  ([coll len]
    (pad coll len nil))
  ([coll len x]
    (take len (concat coll (repeat x)))))

(def rows (->> cards-in-cols
               (map #(pad (last %) (count date-columns)))
               (apply mapv vector)))


(defn get-card [card date]
  (-> (select-keys card meta-columns)
      (assoc :card/current (last (get-columns-for-date card date)))))


(defn show [columns cards]
  (html [:body
          [:h1 "Kanban Board"]
          [:table
            (header-row columns)
            [:tbody
              (for [row rows]
              [:tr
                (for [card row]
                    [:td (get card :card/description "")])])]]]))
