(ns kanban-metrics.web.views.board
  (:require [hiccup.core :refer [html]]
            [kanban-metrics.datomic.board-store :refer [do-query cards card-ids]]
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

(def all-cards (cards (card-ids)))
(def card (first all-cards))

(defn dates-for [card]
  (reduce #(conj %1 (%2 card)) #{} date-columns))

(defn get-columns-for-date [card date]
  (filter #(= (%1 card) date) date-columns))

(defn pad
  ([coll len]
    (pad coll len nil))
  ([coll len x]
    (take len (concat coll (repeat x)))))

(defn get-card [card date]
  (-> (select-keys card meta-columns)
      (assoc :card/current (last (get-columns-for-date card date)))))

(defn add-to-column [m [col card]]
  (assoc m col (conj (m col []) card)))

(defn assign-column [target-date card]
  [(last (get-columns-for-date card (.toDate target-date)))
   card])

(defn show [columns cards dt]
  (let [target-date    (parse (formatters :date) dt)
        todays-cards   (map #(assign-column target-date %) cards)
        cards-on-board (filter (fn [[col card]] (not (nil? col))) todays-cards)
        cards-by-col   (reduce add-to-column {} cards-on-board)
        cards-in-cols  (map #(vector % (cards-by-col % [])) date-columns)
        rows           (->> cards-in-cols
                            (map #(pad (last %) (count date-columns)))
                            (apply mapv vector))]
    (html [:body
            [:h1 (str "Kanban Board on " dt)]
            [:ul
              [:li [:a {:href (str "/on/" (unparse (formatters :date) (t/minus target-date (t/days 1))))} "Previous Day"]]
              [:li [:a {:href (str "/on/" (unparse (formatters :date) (t/plus target-date  (t/days 1))))} "Next Day"]]
              [:li [:a {:href (str "/on/" (unparse (formatters :date) (t/today-at 0 0)))}                 "Today"]]]
            [:table
              (header-row columns)
              [:tbody
                (for [row rows]
                [:tr
                  (for [card row]
                      [:td (get card :card/description "")])])]]])))
