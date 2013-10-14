(ns kanban-metrics.web.views.board
  (:require [hiccup.core :refer [html]]
            [kanban-metrics.datomic.board-store :refer [do-query cards card-ids]]
            [kanban-metrics.board :refer :all]
            [clj-time.core :as t]
            [clj-time.format :refer [formatters formatter parse unparse]]))

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

(def date-columns [:card/requested
                   :card/backlog
                   :card/in-progress
                   :card/qa
                   :card/uat
                   :card/done])

(def meta-columns [:card/sequence :card/description :card/work-item-type])

(defn calculate-board [columns cards dt]
  (let [target-date (.toDate (parse (formatters :date) dt))
        card-fn     (fn [card target-date] (columns-on-date columns card target-date))]
    (map-to-matrix columns (board-on-date card-fn target-date cards))))

(defn show [columns cards dt]
  (let [target-date (parse (formatters :date) dt)
        board       (calculate-board columns cards dt)]
    (html [:body
            [:h1 (str "Kanban Board on " dt)]
            [:ul
              [:li [:a {:href (str "/on/" (unparse (formatters :date) (t/minus target-date (t/days 1))))} "Previous Day"]]
              [:li [:a {:href (str "/on/" (unparse (formatters :date) (t/plus target-date  (t/days 1))))} "Next Day"]]
              [:li [:a {:href (str "/on/" (unparse (formatters :date) (t/today-at 0 0)))}                 "Today"]]]
            [:table
              (header-row (first board))
              [:tbody
                (for [row (rest board)]
                [:tr
                  (for [card row]
                      [:td (get card :card/description "")])])]]])))
