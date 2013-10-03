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

(defn show [columns cards]
  (html [:body
          [:h1 "Kanban Board"]]))
