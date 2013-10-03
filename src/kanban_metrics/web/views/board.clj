(ns kanban-metrics.web.views.board
  (:require [hiccup.core :refer [html]]))

(defn show [columns cards]
  (html [:body
          [:h1 "A Kanban Board"]
          [:table
            [:thead
              [:tr (for [c columns] [:th c])]]
            [:tbody ]]
          [:p cards]]))
