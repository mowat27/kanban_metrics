(ns kanban-metrics.web.views.board
  (:require [hiccup.core :refer [html]]))

(defn list-cards [columns cards]
  (html [:body
          [:h1 "Card Details"]
          [:table
            [:thead
              [:tr
                (for [c columns] [:th c])]]
            [:tbody
              (for [card cards]
                [:tr
                  (for [col columns] [:td (col card)])])]]]))

; (->> all-cards
;      (filter #(t/before? (DateTime. today) (DateTime. (:card/done %))))
;      count)

(def date-columns [:card/requested
                   :card/in-progress
                   :card/uat
                   :card/done
                   :card/backlog
                   :card/qa])

(def meta-columns [:card/sequence :card/description :card/work-item-type])

(def all-cards (cards (card-ids)))
(def card (first all-cards))

(defn dates-for [card]
  (reduce #(conj %1 (%2 card)) #{} date-columns))

(defn get-columns-for-date [card date]
  (filter #(= (%1 card) date) date-columns))

(defn get-card [card date]
  (-> (select-keys card meta-columns)
      (assoc :card/current (last (get-columns-for-date card date)))))

(defn show [columns cards]
  (html [:body
          [:h1 "Kanban Board"]]))
